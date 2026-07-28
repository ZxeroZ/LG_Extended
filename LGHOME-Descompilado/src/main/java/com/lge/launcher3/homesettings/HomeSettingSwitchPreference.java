package com.lge.launcher3.homesettings;

import android.content.Context;
import android.content.res.Resources;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class HomeSettingSwitchPreference extends SwitchPreference {
    private Switch mSwitch;

    public HomeSettingSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeSettingSwitchPreference(Context context) {
        this(context, null);
    }

    @Override // android.preference.TwoStatePreference, android.preference.Preference
    protected void onClick() {
        this.mSwitch.toggle();
    }

    @Override // android.preference.SwitchPreference, android.preference.Preference
    protected void onBindView(View view) {
        super.onBindView(view);
        Resources resources = getContext().getResources();
        int identifier = resources.getIdentifier("switch_widget", "id", LauncherConst.PACKAGE_NAME_NATIVE);
        if (identifier == 0) {
            identifier = resources.getIdentifier("switchWidget", "id", LauncherConst.PACKAGE_NAME_NATIVE);
        }
        View viewFindViewById = view.findViewById(identifier);
        if (viewFindViewById != null && (viewFindViewById instanceof Checkable) && (viewFindViewById instanceof Switch)) {
            Switch r5 = (Switch) viewFindViewById;
            this.mSwitch = r5;
            r5.setOnCheckedChangeListener(null);
            this.mSwitch.setChecked(isChecked());
            this.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lge.launcher3.homesettings.HomeSettingSwitchPreference.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    if (buttonView != null) {
                        if (!HomeSettingSwitchPreference.this.callChangeListener(Boolean.valueOf(isChecked))) {
                            buttonView.setChecked(!isChecked);
                        } else {
                            buttonView.setChecked(isChecked);
                        }
                    }
                }
            });
        }
    }
}
