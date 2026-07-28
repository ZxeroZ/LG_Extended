package com.lge.launcher3;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import com.android.launcher3.LauncherAppState;
import com.lge.launcher3.receiver.PendingIntentObjectList;

/* JADX INFO: loaded from: classes.dex */
public class CustomUIActivity extends AppCompatActivity implements View.OnUnhandledKeyEventListener {
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    EditText editText6;
    EditText editText7;
    EditText editText8;
    Switch mSwitch1;
    Switch mSwitch2;
    Spinner spinner1;
    TestView testview1;
    TextView tv1;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_ui_main);
        initViews();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void initViews() {
        Resources resources = getResources();
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        this.spinner1 = spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.lge.launcher3.CustomUIActivity.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof AppCompatTextView) {
                    CharSequence text = ((AppCompatTextView) view).getText();
                    if ("black".equals(text)) {
                        LauncherAppState.getIDP(CustomUIActivity.this.getApplicationContext()).getDeviceProfile(CustomUIActivity.this.getApplicationContext()).inv.mColorOfLetterBox = CustomUIActivity.this.getApplicationContext().getColor(R.color.letterbox_color_for_thumbnail_black);
                    } else if ("white".equals(text)) {
                        LauncherAppState.getIDP(CustomUIActivity.this.getApplicationContext()).getDeviceProfile(CustomUIActivity.this.getApplicationContext()).inv.mColorOfLetterBox = CustomUIActivity.this.getApplicationContext().getColor(R.color.letterbox_color_for_thumbnail_white);
                    }
                }
            }
        });
        Switch r1 = (Switch) findViewById(R.id.switch1);
        this.mSwitch1 = r1;
        r1.setChecked(CustomUIManager.getInstance(getApplicationContext()).getUseToastOfAssistant());
        this.mSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lge.launcher3.-$$Lambda$CustomUIActivity$aaEdQdRK11n4ykzRjWWd243XIVw
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initViews$0$CustomUIActivity(compoundButton, z);
            }
        });
        Switch r12 = (Switch) findViewById(R.id.switch2);
        this.mSwitch2 = r12;
        r12.setChecked(CustomUIManager.getInstance(getApplicationContext()).getUseFlingOfAssistant());
        this.mSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lge.launcher3.-$$Lambda$CustomUIActivity$0uj3MPG9xmjVAA-Y7ZhYjShEQtA
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$initViews$1$CustomUIActivity(compoundButton, z);
            }
        });
        this.editText1 = (EditText) findViewById(R.id.edit1);
        float f = resources.getDisplayMetrics().xdpi;
        int i = (int) ((resources.getFloat(R.dimen.config_touchslop_modified_init_distance) * f) / 25.4f);
        EditText editText = this.editText1;
        editText.setHint(((Object) editText.getHint()) + " ori: " + i + "px");
        this.editText1.addOnUnhandledKeyEventListener(this);
        this.editText2 = (EditText) findViewById(R.id.edit2);
        int iMin = (int) Math.min(f * 0.01968504f, (float) resources.getInteger(R.integer.config_dropTouchSlop));
        EditText editText2 = this.editText2;
        editText2.setHint(((Object) editText2.getHint()) + " ori: " + iMin + "px");
        this.editText2.addOnUnhandledKeyEventListener(this);
        EditText editText3 = (EditText) findViewById(R.id.edit3);
        this.editText3 = editText3;
        CharSequence hint = editText3.getHint();
        editText3.setHint(((Object) hint) + " ori: " + resources.getDimensionPixelSize(R.dimen.device_profile_workspace_left_right_margin) + "px");
        this.editText3.addOnUnhandledKeyEventListener(this);
        this.editText5 = (EditText) findViewById(R.id.edit5);
        float dimension = resources.getDimension(R.dimen.gestures_assistant_drag_threshold);
        float f2 = dimension / resources.getDisplayMetrics().density;
        EditText editText4 = this.editText5;
        editText4.setHint(((Object) editText4.getHint()) + " ori: " + f2 + "dp(" + dimension + "px)");
        this.editText5.addOnUnhandledKeyEventListener(this);
        this.editText6 = (EditText) findViewById(R.id.edit6);
        float dimension2 = resources.getDimension(R.dimen.gestures_assistant_fling_threshold);
        float f3 = dimension2 / resources.getDisplayMetrics().density;
        EditText editText5 = this.editText6;
        editText5.setHint(((Object) editText5.getHint()) + " ori: " + f3 + "dp(" + dimension2 + "px)");
        this.editText6.addOnUnhandledKeyEventListener(this);
        EditText editText6 = (EditText) findViewById(R.id.edit7);
        this.editText7 = editText6;
        CharSequence hint2 = editText6.getHint();
        editText6.setHint(((Object) hint2) + " ori: " + resources.getInteger(R.integer.assistant_gesture_corner_deg_threshold) + " degree");
        this.editText7.addOnUnhandledKeyEventListener(this);
        Button button = (Button) findViewById(R.id.button1);
        this.btn1 = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.CustomUIActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CustomUIActivity.this.updateAllValue(v);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        this.btn2 = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.CustomUIActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (CustomUIActivity.this.editText1 != null) {
                    CustomUIActivity.this.editText1.setText("");
                }
                if (CustomUIActivity.this.editText2 != null) {
                    CustomUIActivity.this.editText2.setText("");
                }
                if (CustomUIActivity.this.editText3 != null) {
                    CustomUIActivity.this.editText3.setText("");
                }
                if (CustomUIActivity.this.editText5 != null) {
                    CustomUIActivity.this.editText5.setText("");
                }
                if (CustomUIActivity.this.editText6 != null) {
                    CustomUIActivity.this.editText6.setText("");
                }
                if (CustomUIActivity.this.editText7 != null) {
                    CustomUIActivity.this.editText7.setText("");
                }
                CustomUIManager.getInstance(CustomUIActivity.this.getApplicationContext()).setUseToastOfAssistant(false);
                CustomUIManager.getInstance(CustomUIActivity.this.getApplicationContext()).setUseFlingOfAssistant(true);
                if (CustomUIActivity.this.mSwitch1 != null) {
                    CustomUIActivity.this.mSwitch1.setChecked(CustomUIManager.getInstance(CustomUIActivity.this.getApplicationContext()).getUseToastOfAssistant());
                }
                if (CustomUIActivity.this.mSwitch2 != null) {
                    CustomUIActivity.this.mSwitch2.setChecked(CustomUIManager.getInstance(CustomUIActivity.this.getApplicationContext()).getUseFlingOfAssistant());
                }
                CustomUIActivity.this.updateAllValue(v);
            }
        });
        Button button3 = (Button) findViewById(R.id.button3);
        this.btn3 = button3;
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.CustomUIActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CustomUIActivity.this.sendBroadcast(new Intent(PendingIntentObjectList.KillProcessHandler.KILL_PROCESS_INTENT));
            }
        });
        float f4 = getResources().getDisplayMetrics().density;
        TestView testView = (TestView) findViewById(R.id.testview1);
        this.testview1 = testView;
        testView.setLineW(f4 * 10.0f);
        EditText editText7 = (EditText) findViewById(R.id.edit4);
        this.editText4 = editText7;
        editText7.setShowSoftInputOnFocus(true);
        this.editText4.addOnUnhandledKeyEventListener(new View.OnUnhandledKeyEventListener() { // from class: com.lge.launcher3.CustomUIActivity.5
            @Override // android.view.View.OnUnhandledKeyEventListener
            public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
                if (event.getKeyCode() != 66 || event.getAction() != 1) {
                    return false;
                }
                CustomUIActivity.this.drawTestView();
                return false;
            }
        });
        Button button4 = (Button) findViewById(R.id.button4);
        this.btn4 = button4;
        button4.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.CustomUIActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CustomUIActivity.this.drawTestView();
            }
        });
        EditText editText8 = (EditText) findViewById(R.id.edit8);
        this.editText8 = editText8;
        editText8.setShowSoftInputOnFocus(true);
        this.editText8.addOnUnhandledKeyEventListener(new View.OnUnhandledKeyEventListener() { // from class: com.lge.launcher3.CustomUIActivity.7
            @Override // android.view.View.OnUnhandledKeyEventListener
            public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
                if (event.getKeyCode() != 66) {
                    return false;
                }
                event.getAction();
                return false;
            }
        });
        Button button5 = (Button) findViewById(R.id.button5);
        this.btn5 = button5;
        button5.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.CustomUIActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
            }
        });
    }

    public /* synthetic */ void lambda$initViews$0$CustomUIActivity(CompoundButton compoundButton, boolean z) {
        CustomUIManager.getInstance(getApplicationContext()).setUseToastOfAssistant(z);
    }

    public /* synthetic */ void lambda$initViews$1$CustomUIActivity(CompoundButton compoundButton, boolean z) {
        CustomUIManager.getInstance(getApplicationContext()).setUseFlingOfAssistant(z);
    }

    private void hideKeyboard(View v) {
        v.clearFocus();
        ((InputMethodManager) getApplicationContext().getSystemService("input_method")).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void drawTestView() {
        if (this.testview1 != null) {
            EditText editText = this.editText4;
            int iIntValue = (editText == null || editText.getText() == null || this.editText4.getText().toString().isEmpty()) ? 0 : Integer.decode(this.editText4.getText().toString()).intValue();
            float f = getResources().getDisplayMetrics().density;
            float f2 = getResources().getDisplayMetrics().xdpi;
            float f3 = iIntValue * f;
            float f4 = getResources().getDisplayMetrics().ydpi;
            this.testview1.setLineW(f3);
            this.testview1.invalidate();
            Button button = this.btn4;
            if (button != null) {
                button.setText(String.format("x: %sdp, %spx, %smm(y:%smm)", Integer.valueOf(iIntValue), Float.valueOf(f3), Float.valueOf(f3 / (f2 / 25.4f)), Float.valueOf(f3 / (f4 / 25.4f))));
            }
            hideKeyboard(this.editText4);
        }
    }

    private void updateInfoText() {
        this.tv1 = (TextView) findViewById(R.id.text1);
        float f = getResources().getDisplayMetrics().xdpi;
        float f2 = getResources().getDisplayMetrics().density;
        float f3 = getResources().getDisplayMetrics().scaledDensity;
        float f4 = f / 25.4f;
        float f5 = getResources().getDisplayMetrics().ydpi;
        float f6 = f5 / 25.4f;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getApplicationContext());
        this.tv1.setText(String.format(" Touch slop (Scaled: %s, Paging: %s, Window: %s), \n xdpi : %s \n xdpm : %s(%s) \n ydpi : %s \n ydpm : %s(%s) \n density : %s, scaledDensity : %s \n 100 pixel = x: %smm y : %smm\n 1dp = x: %smm y : %smm\n **xdpi(The exact physical pixels per inch of the screen in the X dimension.)", Integer.valueOf(viewConfiguration.getScaledTouchSlop()), Integer.valueOf(viewConfiguration.getScaledPagingTouchSlop()), Integer.valueOf(viewConfiguration.getScaledWindowTouchSlop()), Float.valueOf(f), Float.valueOf(f4), Float.valueOf(1.0f / f4), Float.valueOf(f5), Float.valueOf(f6), Float.valueOf(1.0f / f6), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(100.0f / f4), Float.valueOf(100.0f / f6), Float.valueOf(f2 / f4), Float.valueOf(f2 / f6)));
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        updateInfoText();
    }

    @Override // android.view.View.OnUnhandledKeyEventListener
    public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
        if (event.getKeyCode() != 66 || event.getAction() != 1 || !(v instanceof EditText)) {
            return false;
        }
        updateValue(v);
        hideKeyboard(v);
        return false;
    }

    public void updateAllValue(View v) {
        updateValue(this.editText1);
        updateValue(this.editText2);
        updateValue(this.editText3);
        updateValue(this.editText5);
        updateValue(this.editText6);
        updateValue(this.editText7);
        hideKeyboard(v);
    }

    public void updateValue(View v) {
        if (v instanceof EditText) {
            EditText editText = (EditText) v;
            int iIntValue = (editText == null || editText.getText() == null || editText.getText().toString().isEmpty()) ? 0 : Integer.decode(editText.getText().toString()).intValue();
            switch (v.getId()) {
                case R.id.edit1 /* 2131296471 */:
                    CustomUIManager.getInstance(getApplicationContext()).setTouchSlop(iIntValue);
                    break;
                case R.id.edit2 /* 2131296472 */:
                    CustomUIManager.getInstance(getApplicationContext()).setScaledTouchSlop(iIntValue);
                    break;
                case R.id.edit3 /* 2131296473 */:
                    CustomUIManager.getInstance(getApplicationContext()).setWorkspacePageSpacing(iIntValue);
                    break;
                case R.id.edit5 /* 2131296475 */:
                    CustomUIManager.getInstance(getApplicationContext()).setDragDistThresholdOfAssistant(iIntValue * getResources().getDisplayMetrics().density);
                    break;
                case R.id.edit6 /* 2131296476 */:
                    CustomUIManager.getInstance(getApplicationContext()).setFlingDistThresholdOfAssistant(iIntValue * getResources().getDisplayMetrics().density);
                    break;
                case R.id.edit7 /* 2131296477 */:
                    CustomUIManager.getInstance(getApplicationContext()).setAngleThresholdOfAssistant(iIntValue);
                    break;
            }
        }
    }
}
