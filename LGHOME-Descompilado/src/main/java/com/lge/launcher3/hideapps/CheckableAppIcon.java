package com.lge.launcher3.hideapps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.launcher3.FastBitmapDrawable;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class CheckableAppIcon extends FrameLayout implements Checkable {
    private CheckBox mCheckBox;
    private int mIconSize;
    private String mStrCheckBox;
    private String mStrChecked;
    private String mStrUnchecked;
    private TextView mTextView;

    public CheckableAppIcon(Context context) {
        this(context, null);
    }

    public CheckableAppIcon(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public CheckableAppIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIconSize = getResources().getDimensionPixelSize(R.dimen.checkable_app_icon_size);
        this.mTextView = (TextView) findViewById(R.id.checkable_icon_view);
        this.mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        Resources resources = getResources();
        this.mStrCheckBox = resources.getString(R.string.talkback_checkbox);
        this.mStrChecked = resources.getString(R.string.talkback_checked);
        this.mStrUnchecked = resources.getString(R.string.talkback_not_checked);
        setLayoutDirection(3);
        initCheckBox();
    }

    public void initCheckBox() {
        int identifier = getResources().getIdentifier("btn_blocked_check_selector", LauncherConst.RESOURCE_IMAGE_TYPE, "com.lge");
        if (identifier != 0) {
            this.mCheckBox.setButtonDrawable(identifier);
        }
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.mCheckBox.isChecked();
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean checked) {
        this.mCheckBox.setChecked(checked);
        setContentDescription(getCheckableAppTalkbackString(checked));
        announceForAccessibility(getCheckableAppTalkbackString(checked));
    }

    public CharSequence getCheckableAppTalkbackString(boolean checked) {
        StringBuilder sbAppend = new StringBuilder(this.mTextView.getText()).append(',').append(this.mStrCheckBox).append(' ');
        sbAppend.append(checked ? this.mStrChecked : this.mStrUnchecked);
        return sbAppend;
    }

    @Override // android.widget.Checkable
    public void toggle() {
        this.mCheckBox.toggle();
    }

    public void setIcon(Drawable icon) {
        if (icon == null) {
            return;
        }
        int i = this.mIconSize;
        icon.setBounds(0, 0, i, i);
        this.mTextView.setCompoundDrawables(null, icon, null, null);
    }

    public void setIcon(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable(bitmap);
        int i = this.mIconSize;
        fastBitmapDrawable.setBounds(0, 0, i, i);
        fastBitmapDrawable.setFilterBitmap(true);
        this.mTextView.setCompoundDrawables(null, fastBitmapDrawable, null, null);
    }

    public void setText(CharSequence text) {
        this.mTextView.setText(text);
    }
}
