package com.lge.launcher3.homesettings;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class SBSwitchPreference extends SwitchPreference {
    @Override // android.preference.TwoStatePreference, android.preference.Preference
    protected void onClick() {
    }

    public SBSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SBSwitchPreference(Context context) {
        this(context, null);
    }

    @Override // android.preference.SwitchPreference, android.preference.Preference
    protected void onBindView(View view) {
        super.onBindView(view);
        Resources resources = getContext().getResources();
        ImageView imageView = (ImageView) view.findViewById(resources.getIdentifier("switchBullet", "id", "com.lge"));
        if (imageView != null) {
            imageView.setVisibility(0);
        }
        ImageView imageView2 = (ImageView) view.findViewById(resources.getIdentifier("switchDivider", "id", "com.lge"));
        if (imageView2 != null) {
            imageView2.setVisibility(0);
        }
        int identifier = resources.getIdentifier("switch_widget", "id", LauncherConst.PACKAGE_NAME_NATIVE);
        if (identifier == 0) {
            identifier = resources.getIdentifier("switchWidget", "id", LauncherConst.PACKAGE_NAME_NATIVE);
        }
        View viewFindViewById = view.findViewById(identifier);
        if (viewFindViewById != null && (viewFindViewById instanceof Checkable) && (viewFindViewById instanceof Switch)) {
            Switch r6 = (Switch) viewFindViewById;
            r6.setOnCheckedChangeListener(null);
            r6.setChecked(isChecked());
            r6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lge.launcher3.homesettings.SBSwitchPreference.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    if (!SBSwitchPreference.this.callChangeListener(Boolean.valueOf(isChecked))) {
                        buttonView.setChecked(!isChecked);
                    } else {
                        buttonView.setChecked(isChecked);
                        new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.homesettings.SBSwitchPreference.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                SBSwitchPreference.this.setChecked(isChecked);
                                if (isChecked) {
                                    SBHomeDataBaseUtil.turnOnSmartBulletin(buttonView.getContext());
                                } else {
                                    SBHomeDataBaseUtil.turnOffSmartBulletin(buttonView.getContext());
                                }
                            }
                        }, 200L);
                    }
                }
            });
            if (Build.VERSION.SDK_INT <= 21) {
                r6.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.homesettings.SBSwitchPreference.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                    }
                });
            }
        }
    }
}
