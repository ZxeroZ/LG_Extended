package com.lge.launcher3.wallpaperpicker;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.wallpaperpicker.utils.LGWallpaperManagerUtil;
import com.lge.launcher3.wallpaperpicker.utils.LGWallpaperSwitchUtil;
import com.lge.launcher3.wallpaperpicker.utils.WallpaperValueCheckProvider;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperChooserDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final int INVALID_NUMBER = -1;
    private static final String TAG = "Launcher.WallpaperChooserDialogFragment";
    protected boolean mEmbedded;
    private ImageView mImageView;
    private boolean mIsClickItem;
    protected List<LGWallpaperManagerUtil.LGWallpaperItem> mList;
    private int mPreviewW = 0;
    private int mPreviewH = 0;
    private int mCurrentPos = -1;
    private LinkedHashMap<String, String> mOperatorWallpaperTable = null;
    private Context mContext = null;
    private Activity mActivity = null;
    private int mCurSelectedPosition = -1;
    private int mDestClickItemPostion = -1;

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public static WallpaperChooserDialogFragment newInstance() {
        WallpaperChooserDialogFragment wallpaperChooserDialogFragment = new WallpaperChooserDialogFragment();
        wallpaperChooserDialogFragment.setCancelable(true);
        return wallpaperChooserDialogFragment;
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity().getApplicationContext();
        this.mActivity = getActivity();
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        LGWallpaperCarrierHashClear();
        List<LGWallpaperManagerUtil.LGWallpaperItem> list = this.mList;
        if (list != null) {
            Iterator<LGWallpaperManagerUtil.LGWallpaperItem> it = list.iterator();
            while (it.hasNext()) {
                it.next().destoryItem();
            }
            this.mList.clear();
            this.mList = null;
        }
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        findWallpapers();
        return null;
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findWallpapers();
        View viewInflate = inflater.inflate(R.layout.wallpaper_chooser, container, false);
        this.mImageView = (ImageView) viewInflate.findViewById(R.id.background_imageview);
        final WallpaperChooserGallery wallpaperChooserGallery = (WallpaperChooserGallery) viewInflate.findViewById(R.id.gallery);
        wallpaperChooserGallery.setCallbackDuringFling(false);
        wallpaperChooserGallery.setOnItemSelectedListener(this);
        wallpaperChooserGallery.setOnItemClickListener(this);
        wallpaperChooserGallery.setAdapter((SpinnerAdapter) new ImageAdapter(getActivity()));
        if (this.mCurrentPos == -1) {
            restoreSelection();
        }
        wallpaperChooserGallery.setSelection(this.mCurrentPos);
        ((Button) viewInflate.findViewById(R.id.set)).setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.wallpaperpicker.WallpaperChooserDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WallpaperChooserDialogFragment.this.selectWallpaper(wallpaperChooserGallery.getSelectedItemPosition());
            }
        });
        return viewInflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectWallpaper(int position) {
        if (position < 0) {
            return;
        }
        List<LGWallpaperManagerUtil.LGWallpaperItem> list = this.mList;
        if (list != null && list.get(position) != null) {
            LGWallpaperManagerUtil.LGWallpaperItem lGWallpaperItem = this.mList.get(position);
            Context wallpaperPackageContext = getWallpaperPackageContext(lGWallpaperItem);
            WallpaperValueCheckProvider.getDefaultSharedPreferences(this.mContext).edit().putInt("wallpaper_index", position).apply();
            WallpaperValueCheckProvider.getDefaultSharedPreferences(this.mContext).edit().putBoolean("wallpaper_check_launched", true).apply();
            if (lGWallpaperItem.isExternalItem || wallpaperPackageContext == null) {
                LGWallpaperSwitchUtil.changeWallpaper(getActivity(), lGWallpaperItem.bitmapDrawableRes);
            } else {
                LGWallpaperSwitchUtil.changeWallpaper(wallpaperPackageContext, lGWallpaperItem.resImage);
            }
            LGUserLog.send(this.mContext, LGUserLog.FEATURENAME_WALLPAPERPICKER);
            Activity activity = getActivity();
            activity.setResult(-1);
            activity.finish();
            return;
        }
        LGLog.e(TAG, "Failed at selectWallpaper() : wci is null");
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (LGFeatureConfig.isFolderPhone(getActivity().getApplicationContext())) {
            selectWallpaper(position);
            return;
        }
        int selectedItemPosition = parent.getSelectedItemPosition();
        this.mCurSelectedPosition = selectedItemPosition;
        if (this.mDestClickItemPostion == position || selectedItemPosition == position) {
            this.mIsClickItem = false;
            this.mDestClickItemPostion = -1;
        } else {
            this.mIsClickItem = true;
            this.mDestClickItemPostion = position;
            this.mActivity.runOnUiThread(new WallpaperLoadRunnable(position));
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = this.mDestClickItemPostion;
        if (i == -1) {
            this.mIsClickItem = false;
        } else if (i == position) {
            this.mIsClickItem = false;
            this.mDestClickItemPostion = -1;
        }
        if (this.mIsClickItem) {
            return;
        }
        this.mActivity.runOnUiThread(new WallpaperLoadRunnable(position));
    }

    private void findWallpapers() {
        List<LGWallpaperManagerUtil.LGWallpaperItem> list = new LGWallpaperManagerUtil(getActivity()).getList();
        this.mList = list;
        for (int size = list.size() - 1; size >= 0; size--) {
            if (this.mList.get(size).resImage == 0 && this.mList.get(size).bitmapDrawableRes == null) {
                this.mList.remove(size);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getWallpaperPackageContext(LGWallpaperManagerUtil.LGWallpaperItem item) {
        try {
            Activity activity = getActivity();
            if (activity == null || item.mPackageName == null) {
                return null;
            }
            return activity.createPackageContext(item.mPackageName, 2);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private final LayoutInflater mLayoutInflater;

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public ImageAdapter(Activity activity) {
            this.mLayoutInflater = activity.getLayoutInflater();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return WallpaperChooserDialogFragment.this.mList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.wallpaper_image);
            if (imageView == null) {
                return convertView;
            }
            imageView.getBackground().setColorFilter(DDTUtils.getLGEColor(WallpaperChooserDialogFragment.this.mContext, "color_accent_ui"), PorterDuff.Mode.SRC_ATOP);
            LGWallpaperManagerUtil.LGWallpaperItem lGWallpaperItem = WallpaperChooserDialogFragment.this.mList.get(position);
            if (!lGWallpaperItem.isExternalItem) {
                Context wallpaperPackageContext = WallpaperChooserDialogFragment.this.getWallpaperPackageContext(lGWallpaperItem);
                if (wallpaperPackageContext != null) {
                    Drawable drawable = wallpaperPackageContext.getResources().getDrawable(lGWallpaperItem.resThumb);
                    imageView.setImageDrawable(drawable);
                    imageView.setContentDescription(WallpaperChooserDialogFragment.this.mContext.getResources().getString(R.string.menu_wallpaper) + (position + 1));
                    if (drawable != null) {
                        drawable.setDither(true);
                    } else {
                        LGLog.e(WallpaperChooserDialogFragment.TAG, "Error decoding thumbnail resId=" + lGWallpaperItem.resThumb + " for wallpaper #" + position);
                    }
                }
            } else if (imageView != null) {
                if (WallpaperChooserDialogFragment.this.mPreviewW == 0) {
                    findWallpaperSize(imageView.getPaddingStart());
                }
                if (WallpaperChooserDialogFragment.this.mPreviewW > 0) {
                    imageView.setLayoutParams(new Gallery.LayoutParams(WallpaperChooserDialogFragment.this.mPreviewW, WallpaperChooserDialogFragment.this.mPreviewH));
                }
                if (lGWallpaperItem.bitmapDrawableThumbRes != null) {
                    imageView.setImageBitmap(lGWallpaperItem.bitmapDrawableThumbRes);
                }
                imageView.setContentDescription(WallpaperChooserDialogFragment.this.mContext.getResources().getString(R.string.menu_wallpaper) + (position + 1));
                return imageView;
            }
            return convertView;
        }

        private void findWallpaperSize(int padding) {
            Context wallpaperPackageContext;
            Drawable drawable;
            for (LGWallpaperManagerUtil.LGWallpaperItem lGWallpaperItem : WallpaperChooserDialogFragment.this.mList) {
                if (!lGWallpaperItem.isExternalItem && (wallpaperPackageContext = WallpaperChooserDialogFragment.this.getWallpaperPackageContext(lGWallpaperItem)) != null && (drawable = wallpaperPackageContext.getResources().getDrawable(lGWallpaperItem.resThumb)) != null) {
                    int i = padding * 2;
                    WallpaperChooserDialogFragment.this.mPreviewW = drawable.getIntrinsicWidth() + i;
                    WallpaperChooserDialogFragment.this.mPreviewH = drawable.getIntrinsicHeight() + i;
                    return;
                }
            }
        }
    }

    private void restoreSelection() {
        if (this.mCurrentPos >= this.mList.size() || this.mList.size() <= 0) {
            this.mCurrentPos = -1;
            return;
        }
        int i = WallpaperValueCheckProvider.getDefaultSharedPreferences(this.mContext).getInt("wallpaper_index", 0);
        this.mCurrentPos = i;
        if (i >= this.mList.size()) {
            LGLog.i(TAG, "the Current Wallpaper position (" + this.mCurrentPos + ") is bigger than Wallpaper list size (" + this.mList.size() + ")");
            this.mCurrentPos = 0;
            WallpaperValueCheckProvider.getDefaultSharedPreferences(this.mContext).edit().putInt("wallpaper_index", this.mCurrentPos).apply();
            LGLog.i(TAG, "the Current Wallpaper position is changed as first position.");
        }
        LGLog.i(TAG, "restoreSelection posistion:" + this.mCurrentPos);
    }

    private void LGWallpaperCarrierHashClear() {
        LinkedHashMap<String, String> linkedHashMap = this.mOperatorWallpaperTable;
        if (linkedHashMap == null) {
            return;
        }
        linkedHashMap.clear();
        this.mOperatorWallpaperTable = null;
    }

    public class WallpaperLoadRunnable implements Runnable {
        private int mPosition;

        public WallpaperLoadRunnable(int poision) {
            this.mPosition = poision;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (WallpaperChooserDialogFragment.this.mList == null || WallpaperChooserDialogFragment.this.mList.size() <= 0) {
                return;
            }
            LGWallpaperManagerUtil.LGWallpaperItem lGWallpaperItem = WallpaperChooserDialogFragment.this.mList.get(this.mPosition);
            if (lGWallpaperItem.isExternalItem) {
                WallpaperChooserDialogFragment.this.mImageView.setImageBitmap(lGWallpaperItem.bitmapDrawableRes);
                return;
            }
            Context wallpaperPackageContext = WallpaperChooserDialogFragment.this.getWallpaperPackageContext(lGWallpaperItem);
            if (wallpaperPackageContext != null) {
                if ("com.lge.launcher2.theme.optimus".equals(lGWallpaperItem.mPackageName)) {
                    WallpaperChooserDialogFragment.this.mImageView.setImageDrawable(wallpaperPackageContext.getResources().getDrawable(lGWallpaperItem.resImage));
                } else {
                    WallpaperChooserDialogFragment.this.mImageView.setImageResource(lGWallpaperItem.resImage);
                }
            }
        }
    }
}
