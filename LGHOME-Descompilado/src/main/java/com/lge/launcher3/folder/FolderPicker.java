package com.lge.launcher3.folder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.TalkBackUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class FolderPicker extends ArrayAdapter<Drawable> {
    private String[] colorList;
    private int mColorMax;
    private final LayoutInflater mInflater;
    private LGFolderPickerListener mListener;

    public interface LGFolderPickerListener {
        void requestFocusOnPalette();

        void resetFocusedColorView();

        void setFocusedColorView(View v);

        void setSelectedColorView(View v);
    }

    public FolderPicker(Context context, ArrayList<Drawable> apps) {
        super(context, 0, apps);
        this.mColorMax = FolderColorUtil.getColorMax();
        if (context != null) {
            setTalkbackResString(context);
        }
        this.mInflater = LayoutInflater.from(context);
    }

    public void setPickerListener(LGFolderPickerListener listener) {
        this.mListener = listener;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.mColorMax;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Drawable item = getItem(position);
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.lg_folder_color_view, parent, false);
        }
        String[] strArr = this.colorList;
        if (strArr != null) {
            TalkBackUtils.setTalkBack(convertView, strArr[position]);
        }
        ((ImageView) convertView.findViewById(R.id.folder_color_image)).setImageDrawable(item);
        convertView.setTag(Integer.valueOf(position));
        convertView.setFocusable(true);
        Drawable background = ((ImageView) convertView.findViewById(R.id.folder_color_focus)).getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setStroke(4, FolderColorUtil.getFolderBGColor(getContext(), position), 0.0f, 0.0f);
        }
        convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.lge.launcher3.folder.FolderPicker.1
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    FolderPicker.this.mListener.setFocusedColorView(view);
                } else {
                    FolderPicker.this.mListener.resetFocusedColorView();
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.folder.FolderPicker.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FolderPicker.this.mListener != null) {
                    FolderPicker.this.mListener.setSelectedColorView(view);
                    FolderPicker.this.mListener.requestFocusOnPalette();
                }
            }
        });
        return convertView;
    }

    private void setTalkbackResString(Context context) {
        Resources resources = context.getResources();
        String[] stringArray = resources.getStringArray(R.array.lg_folder_color_list);
        this.colorList = new String[stringArray.length];
        String[] stringArray2 = resources.getStringArray(R.array.lg_folder_color_list);
        for (int i = 0; i < stringArray.length; i++) {
            int identifier = resources.getIdentifier(stringArray[i], "string", context.getPackageName());
            if (identifier > 0) {
                this.colorList[i] = resources.getString(identifier);
            } else {
                this.colorList[i] = stringArray2[i];
            }
        }
    }
}
