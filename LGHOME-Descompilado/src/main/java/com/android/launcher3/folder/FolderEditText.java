package com.android.launcher3.folder;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class FolderEditText extends EditText {
    private final int MAX_INPUT_TEXT_SIZE;
    private Folder mFolder;
    private Toast mToast;

    public FolderEditText(Context context) {
        super(context);
        this.MAX_INPUT_TEXT_SIZE = 100;
    }

    public FolderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.MAX_INPUT_TEXT_SIZE = 100;
    }

    public FolderEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.MAX_INPUT_TEXT_SIZE = 100;
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
        setFilters(new InputFilter[]{new LGLengthFilter(getContext(), this, 100), new InputFilter.LengthFilter(100)});
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() != 4) {
            return false;
        }
        this.mFolder.dismissEditingName();
        return true;
    }

    class LGLengthFilter implements InputFilter {
        private static final String TAG = "LGLengthFilter";
        private EditText mEditText;
        private TextView mMaxError;
        private int mMaxInputTextSize;

        public LGLengthFilter() {
            this.mEditText = null;
            this.mMaxInputTextSize = 0;
            this.mMaxError = null;
        }

        public LGLengthFilter(Context context, EditText pInput, int nMaxInputTextSize) {
            this.mEditText = null;
            this.mMaxInputTextSize = 0;
            this.mMaxError = null;
            this.mEditText = pInput;
            this.mMaxInputTextSize = nMaxInputTextSize;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int length = (dest.length() - dest.subSequence(dstart, dend).length()) + source.subSequence(start, end).length();
            int i = this.mMaxInputTextSize;
            if (length > i) {
                LGLog.i(TAG, length + " is greater than " + i);
                int selectionStart = this.mEditText.getSelectionStart();
                int selectionEnd = this.mEditText.getSelectionEnd();
                EditText editText = this.mEditText;
                editText.setText(editText.getText());
                this.mEditText.setSelection(selectionStart, selectionEnd);
                this.mEditText.clearComposingText();
                if (this.mEditText != null) {
                    FolderEditText folderEditText = FolderEditText.this;
                    folderEditText.mToast = Toast.makeText(folderEditText.getContext(), FolderEditText.this.getResources().getString(R.string.sp_overflow_textsize_max_NORMAL), 1);
                    FolderEditText.this.mToast.show();
                }
            } else if (FolderEditText.this.mToast != null) {
                FolderEditText.this.mToast.cancel();
            }
            return source;
        }
    }
}
