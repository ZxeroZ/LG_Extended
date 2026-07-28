package com.android.launcher3.dragndrop;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetHost;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.InstallShortcutReceiver;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.PinItemRequestCompat;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetHostViewLoader;
import com.android.launcher3.widget.WidgetImageView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.wing.AppListAdapter;

/* JADX INFO: loaded from: classes.dex */
public class AddItemActivity extends BaseActivity implements View.OnLongClickListener, View.OnTouchListener {
    private static final int REQUEST_BIND_APPWIDGET = 1;
    private static final int SHADOW_SIZE = 10;
    private static final String STATE_EXTRA_WIDGET_ID = "state.widget.id";
    private static final String TAG = "AddItemActivity";
    private LauncherAppState mApp;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetManagerCompat mAppWidgetManager;
    private InvariantDeviceProfile mIdp;
    private int mPendingBindWidgetId;
    private PendingAddWidgetInfo mPendingWidgetInfo;
    private PinItemRequestCompat mRequest;
    private LivePreviewWidgetCell mWidgetCell;
    private Bundle mWidgetOptions;
    private final PointF mLastTouchPos = new PointF();
    private boolean mFinishOnPause = false;
    CheckBox mCheckBoxHome = null;
    CheckBox mCheckBoxSwivelHome = null;

    private void logCommand(int command) {
    }

    @Override // com.android.launcher3.views.ActivityContext
    public BaseDragLayer getDragLayer() {
        return null;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        AppListAdapter adapter;
        super.onCreate(savedInstanceState);
        PinItemRequestCompat pinItemRequest = PinItemRequestCompat.getPinItemRequest(getIntent());
        this.mRequest = pinItemRequest;
        if (pinItemRequest == null || !isLGHome()) {
            LGLog.i(TAG, "AddItemActivity cannot onCreate because mRequest = " + this.mRequest + ", isLGHome = " + isLGHome());
            finish();
            return;
        }
        LauncherAppState launcherAppState = LauncherAppState.getInstance(BaseActivity.fromContext(this));
        this.mApp = launcherAppState;
        InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
        this.mIdp = invariantDeviceProfile;
        this.mDeviceProfile = invariantDeviceProfile.getDeviceProfile(getApplicationContext());
        setupOrientation();
        setContentView(LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() ? R.layout.add_item_confirmation_activity_swivel : R.layout.add_item_confirmation_activity);
        this.mWidgetCell = (LivePreviewWidgetCell) findViewById(R.id.widget_cell);
        if (this.mRequest.getRequestType() == 1) {
            setupShortcut();
        } else if (!setupWidget()) {
            finish();
        }
        this.mWidgetCell.setOnTouchListener(this);
        if (!WindowUtils.isWideMode(this) && !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            this.mWidgetCell.setOnLongClickListener(this);
        } else if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            ((TextView) findViewById(R.id.add_item_guide_text)).setText(R.string.add_item_request_drag_hint_swivel_mode);
            findViewById(R.id.widget_cell_frame_layout).setBackground(null);
        } else {
            findViewById(R.id.add_item_guide_text).setVisibility(4);
        }
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            this.mCheckBoxHome = (CheckBox) findViewById(R.id.checkbox_basic_home);
            this.mCheckBoxSwivelHome = (CheckBox) findViewById(R.id.checkbox_swivel_home);
            this.mCheckBoxSwivelHome.setText(String.format(getApplicationContext().getString(R.string.add_item_request_select_home_swing_home, getApplicationContext().getString(R.string.sp_swivel_homescreen_category_NORMAL), getApplicationContext().getString(R.string.swing_main_screen)), new Object[0]));
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                this.mCheckBoxHome.setChecked(false);
                this.mCheckBoxSwivelHome.setChecked(true);
                ((Button) findViewById(R.id.place_Automatically_button)).setText(R.string.menu_add);
            } else {
                this.mCheckBoxHome.setChecked(true);
                this.mCheckBoxSwivelHome.setChecked(false);
            }
            LauncherModel model = this.mApp.getModel();
            if (model != null && ((Launcher) model.getCallback()).getCarouselLayout() != null && (adapter = ((Launcher) model.getCallback()).getCarouselLayout().getAdapter()) != null && adapter.isCarouselItemMax()) {
                LGLog.i(TAG, "no space on swivel home, so skip this shortcut for swivel home ");
                findViewById(R.id.no_space_on_swivel_home).setVisibility(0);
                TextView textView = (TextView) findViewById(R.id.text_no_space_on_swivel_home);
                textView.setText(getString(R.string.swivel_home_maximum_items));
                textView.setVisibility(0);
                this.mCheckBoxSwivelHome.setChecked(false);
                this.mCheckBoxSwivelHome.setEnabled(false);
            }
            this.mCheckBoxHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.launcher3.dragndrop.AddItemActivity.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AddItemActivity.this.updatePlaceAutomaticallyButton();
                }
            });
            this.mCheckBoxSwivelHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.launcher3.dragndrop.AddItemActivity.2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AddItemActivity.this.updatePlaceAutomaticallyButton();
                }
            });
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.mLastTouchPos.set(motionEvent.getX(), motionEvent.getY());
        return false;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        WidgetImageView widgetView = this.mWidgetCell.getWidgetView();
        if (widgetView.getBitmap() == null) {
            return false;
        }
        Rect bitmapBounds = widgetView.getBitmapBounds();
        bitmapBounds.offset(widgetView.getLeft() - ((int) this.mLastTouchPos.x), widgetView.getTop() - ((int) this.mLastTouchPos.y));
        PinItemDragListener pinItemDragListener = new PinItemDragListener(this.mRequest, bitmapBounds, widgetView.getBitmap().getWidth(), widgetView.getWidth());
        Intent intentPutExtra = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setFlags(270532608).putExtra(PinItemDragListener.EXTRA_PIN_ITEM_DRAG_LISTENER, pinItemDragListener);
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_LANDSCAPE.getValue() && !Utilities.isAllowRotationPrefEnabled(this) && getResources().getConfiguration().orientation == 2 && !isInMultiWindowMode()) {
            intentPutExtra.addFlags(32768);
        }
        view.startDragAndDrop(new ClipData(new ClipDescription("addItem", new String[]{pinItemDragListener.getMimeType()}), new ClipData.Item("")), new View.DragShadowBuilder(view) { // from class: com.android.launcher3.dragndrop.AddItemActivity.3
            @Override // android.view.View.DragShadowBuilder
            public void onDrawShadow(Canvas canvas) {
            }

            @Override // android.view.View.DragShadowBuilder
            public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                outShadowSize.set(10, 10);
                outShadowTouchPoint.set(5, 5);
            }
        }, null, 256);
        startActivity(intentPutExtra, ActivityOptions.makeCustomAnimation(this, 0, android.R.anim.fade_out).toBundle());
        this.mFinishOnPause = true;
        return false;
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        if (this.mFinishOnPause) {
            finish();
        }
    }

    private void setupShortcut() {
        PinShortcutRequestActivityInfo pinShortcutRequestActivityInfo = new PinShortcutRequestActivityInfo(this.mRequest, this);
        WidgetItem widgetItem = new WidgetItem(pinShortcutRequestActivityInfo);
        this.mWidgetCell.getWidgetView().setTag(new PendingAddShortcutInfo(pinShortcutRequestActivityInfo));
        this.mWidgetCell.applyFromCellItem(widgetItem, this.mApp.getWidgetCache());
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() && ((getDeviceProfile().mDisplayId == 0 && Resources.getSystem().getConfiguration().orientation == 2) || (getDeviceProfile().mDisplayId == 4 && Resources.getSystem().getConfiguration().orientation == 1))) {
            this.mWidgetCell.setWidgetCellSize(getResources().getDimensionPixelOffset(R.dimen.add_item_activity_popup_widget_cell_image));
            ViewGroup.LayoutParams layoutParams = this.mWidgetCell.getLayoutParams();
            layoutParams.height = this.mWidgetCell.mCellSize;
            this.mWidgetCell.setLayoutParams(layoutParams);
        } else {
            this.mWidgetCell.setWidgetCellSize(getResources().getDimensionPixelOffset(R.dimen.add_item_activity_popup_widget_cell_width));
            ViewGroup.LayoutParams layoutParams2 = this.mWidgetCell.getLayoutParams();
            layoutParams2.width = this.mWidgetCell.mCellSize;
            this.mWidgetCell.setLayoutParams(layoutParams2);
        }
        this.mWidgetCell.ensurePreview();
    }

    private boolean setupWidget() {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfoFromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this, this.mRequest.getAppWidgetProviderInfo(this));
        if (launcherAppWidgetProviderInfoFromProviderInfo.minSpanX > this.mIdp.numColumns || launcherAppWidgetProviderInfoFromProviderInfo.minSpanY > this.mIdp.numRows) {
            return false;
        }
        if (!(getBaseContext() instanceof Launcher)) {
            LGLog.i(TAG, "setupWidget() Base Context is not Launcher");
            return false;
        }
        this.mWidgetCell.setPreview(PinItemDragListener.getPreview(this.mRequest));
        this.mAppWidgetManager = AppWidgetManagerCompat.getInstance(this);
        this.mAppWidgetHost = new AppWidgetHost(this, 1024);
        PendingAddWidgetInfo pendingAddWidgetInfo = new PendingAddWidgetInfo(Launcher.getLauncher(this), launcherAppWidgetProviderInfoFromProviderInfo, null);
        this.mPendingWidgetInfo = pendingAddWidgetInfo;
        pendingAddWidgetInfo.spanX = Math.min(this.mIdp.numColumns, launcherAppWidgetProviderInfoFromProviderInfo.spanX);
        this.mPendingWidgetInfo.spanY = Math.min(this.mIdp.numRows, launcherAppWidgetProviderInfoFromProviderInfo.spanY);
        this.mWidgetOptions = WidgetHostViewLoader.getDefaultOptionsForWidget(this, this.mPendingWidgetInfo);
        WidgetItem widgetItem = new WidgetItem(launcherAppWidgetProviderInfoFromProviderInfo, getPackageManager(), this.mIdp, Launcher.getLauncher(this));
        this.mWidgetCell.getWidgetView().setTag(this.mPendingWidgetInfo);
        this.mWidgetCell.applyFromCellItem(widgetItem, this.mApp.getWidgetCache());
        this.mWidgetCell.ensurePreview();
        return true;
    }

    public void onCancelClick(View v) {
        finish();
    }

    public void onPlaceAutomaticallyClick(View v) {
        CheckBox checkBox;
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            this.mCheckBoxHome = (CheckBox) findViewById(R.id.checkbox_basic_home);
            this.mCheckBoxSwivelHome = (CheckBox) findViewById(R.id.checkbox_swivel_home);
        }
        if (this.mRequest.getRequestType() == 1) {
            if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || ((checkBox = this.mCheckBoxHome) != null && checkBox.isChecked())) {
                InstallShortcutReceiver.queueShortcut(new ShortcutInfoCompat(this.mRequest.getShortcutInfo()), this);
            }
            CheckBox checkBox2 = this.mCheckBoxSwivelHome;
            if (checkBox2 != null && checkBox2.isEnabled() && this.mCheckBoxSwivelHome.isChecked()) {
                InstallShortcutReceiver.queueShortcutSwivel(new ShortcutInfoCompat(this.mRequest.getShortcutInfo()), this);
            }
            this.mRequest.accept();
            finish();
            return;
        }
        int iAllocateAppWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        this.mPendingBindWidgetId = iAllocateAppWidgetId;
        if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, this.mRequest.getAppWidgetProviderInfo(this), this.mWidgetOptions)) {
            acceptWidget(this.mPendingBindWidgetId);
            return;
        }
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_BIND");
        intent.putExtra("appWidgetId", this.mPendingBindWidgetId);
        intent.putExtra(LauncherSettings.Favorites.APPWIDGET_PROVIDER, this.mPendingWidgetInfo.componentName);
        intent.putExtra("appWidgetProviderProfile", this.mRequest.getAppWidgetProviderInfo(this).getProfile());
        startActivityForResult(intent, 1);
    }

    private void acceptWidget(int widgetId) {
        InstallShortcutReceiver.queueWidget(this.mRequest.getAppWidgetProviderInfo(this), widgetId, this);
        this.mWidgetOptions.putInt("appWidgetId", widgetId);
        this.mRequest.accept(this.mWidgetOptions);
        finish();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int intExtra;
        if (requestCode != 1) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (data != null) {
            intExtra = data.getIntExtra("appWidgetId", this.mPendingBindWidgetId);
        } else {
            intExtra = this.mPendingBindWidgetId;
        }
        if (resultCode == -1) {
            acceptWidget(intExtra);
        } else {
            this.mAppWidgetHost.deleteAppWidgetId(intExtra);
            this.mPendingBindWidgetId = -1;
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_EXTRA_WIDGET_ID, this.mPendingBindWidgetId);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mPendingBindWidgetId = savedInstanceState.getInt(STATE_EXTRA_WIDGET_ID, this.mPendingBindWidgetId);
    }

    private boolean isLGHome() {
        ResolveInfo resolveInfoResolveActivity;
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN);
        intent.addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME);
        return (packageManager == null || (resolveInfoResolveActivity = packageManager.resolveActivity(intent, 0)) == null || resolveInfoResolveActivity.activityInfo == null || !"com.lge.launcher3".equals(resolveInfoResolveActivity.activityInfo.packageName)) ? false : true;
    }

    private void setupOrientation() {
        if (this.mDeviceProfile.allowRotation) {
            return;
        }
        setRequestedOrientation(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlaceAutomaticallyButton() {
        if (this.mCheckBoxHome == null || this.mCheckBoxSwivelHome == null) {
            return;
        }
        Button button = (Button) findViewById(R.id.place_Automatically_button);
        String str = TAG;
        LGLog.d(str, "updatePlaceAutomaticallyButton() button = " + button + ", mCheckBoxHome.isChecked() = " + this.mCheckBoxHome.isChecked() + ", mCheckBoxSwivelHome.isChecked() = " + this.mCheckBoxSwivelHome.isChecked());
        if (button != null && !this.mCheckBoxHome.isChecked() && !this.mCheckBoxSwivelHome.isChecked()) {
            button.setEnabled(false);
            LGLog.d(str, "updatePlaceAutomaticallyButton() PlaceAutomaticallyButton is disable");
        } else {
            button.setEnabled(true);
            LGLog.d(str, "updatePlaceAutomaticallyButton() PlaceAutomaticallyButton is enable");
        }
    }
}
