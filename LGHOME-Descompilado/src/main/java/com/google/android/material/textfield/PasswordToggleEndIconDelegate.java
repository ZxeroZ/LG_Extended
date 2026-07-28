package com.google.android.material.textfield;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.content.res.AppCompatResources;
import com.google.android.material.R;
import com.google.android.material.textfield.TextInputLayout;

/* JADX INFO: loaded from: classes.dex */
class PasswordToggleEndIconDelegate extends EndIconDelegate {
    private final TextInputLayout.OnEndIconChangedListener passwordToggleEndIconChangedListener;
    private final TextInputLayout.OnEditTextAttachedListener passwordToggleOnEditTextAttachedListener;

    PasswordToggleEndIconDelegate(TextInputLayout textInputLayout) {
        super(textInputLayout);
        this.passwordToggleOnEditTextAttachedListener = new TextInputLayout.OnEditTextAttachedListener() { // from class: com.google.android.material.textfield.PasswordToggleEndIconDelegate.1
            @Override // com.google.android.material.textfield.TextInputLayout.OnEditTextAttachedListener
            public void onEditTextAttached() {
                PasswordToggleEndIconDelegate.this.textInputLayout.setEndIconVisible(PasswordToggleEndIconDelegate.this.hasPasswordTransformation());
            }
        };
        this.passwordToggleEndIconChangedListener = new TextInputLayout.OnEndIconChangedListener() { // from class: com.google.android.material.textfield.PasswordToggleEndIconDelegate.2
            @Override // com.google.android.material.textfield.TextInputLayout.OnEndIconChangedListener
            public void onEndIconChanged(int i) {
                EditText editText = PasswordToggleEndIconDelegate.this.textInputLayout.getEditText();
                if (editText == null || i != 1) {
                    return;
                }
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        };
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void initialize() {
        this.textInputLayout.setEndIconDrawable(AppCompatResources.getDrawable(this.context, R.drawable.design_password_eye));
        this.textInputLayout.setEndIconContentDescription(this.textInputLayout.getResources().getText(R.string.password_toggle_content_description));
        this.textInputLayout.setEndIconOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.textfield.PasswordToggleEndIconDelegate.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditText editText = PasswordToggleEndIconDelegate.this.textInputLayout.getEditText();
                if (editText == null) {
                    return;
                }
                int selectionEnd = editText.getSelectionEnd();
                if (PasswordToggleEndIconDelegate.this.hasPasswordTransformation()) {
                    editText.setTransformationMethod(null);
                    PasswordToggleEndIconDelegate.this.endIconView.setChecked(true);
                } else {
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    PasswordToggleEndIconDelegate.this.endIconView.setChecked(false);
                }
                editText.setSelection(selectionEnd);
            }
        });
        this.textInputLayout.addOnEditTextAttachedListener(this.passwordToggleOnEditTextAttachedListener);
        this.textInputLayout.addOnEndIconChangedListener(this.passwordToggleEndIconChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasPasswordTransformation() {
        EditText editText = this.textInputLayout.getEditText();
        return editText != null && (editText.getTransformationMethod() instanceof PasswordTransformationMethod);
    }
}
