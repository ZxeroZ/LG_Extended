package androidx.core.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

/* JADX INFO: loaded from: classes.dex */
public final class InputConnectionCompat {
    private static final String COMMIT_CONTENT_ACTION = "androidx.core.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
    private static final String COMMIT_CONTENT_CONTENT_URI_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI";
    private static final String COMMIT_CONTENT_CONTENT_URI_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_URI";
    private static final String COMMIT_CONTENT_DESCRIPTION_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
    private static final String COMMIT_CONTENT_DESCRIPTION_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
    private static final String COMMIT_CONTENT_FLAGS_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
    private static final String COMMIT_CONTENT_FLAGS_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
    private static final String COMMIT_CONTENT_INTEROP_ACTION = "android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
    private static final String COMMIT_CONTENT_LINK_URI_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
    private static final String COMMIT_CONTENT_LINK_URI_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
    private static final String COMMIT_CONTENT_OPTS_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
    private static final String COMMIT_CONTENT_OPTS_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
    private static final String COMMIT_CONTENT_RESULT_INTEROP_RECEIVER_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
    private static final String COMMIT_CONTENT_RESULT_RECEIVER_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
    public static final int INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;

    public interface OnCommitContentListener {
        boolean onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts);
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:42:0x0077 */
    /* JADX DEBUG: Multi-variable search result rejected for r2v2, resolved type: android.os.ResultReceiver */
    /* JADX DEBUG: Multi-variable search result rejected for r2v3, resolved type: android.os.ResultReceiver */
    /* JADX DEBUG: Multi-variable search result rejected for r2v5, resolved type: android.os.ResultReceiver */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v3, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    static boolean handlePerformPrivateCommand(String str, Bundle bundle, OnCommitContentListener onCommitContentListener) throws Throwable {
        boolean z;
        ResultReceiver resultReceiver;
        ?? OnCommitContent = 0;
        OnCommitContent = 0;
        if (bundle == null) {
            return false;
        }
        if (TextUtils.equals(COMMIT_CONTENT_ACTION, str)) {
            z = false;
        } else {
            if (!TextUtils.equals(COMMIT_CONTENT_INTEROP_ACTION, str)) {
                return false;
            }
            z = true;
        }
        try {
            resultReceiver = (ResultReceiver) bundle.getParcelable(z ? COMMIT_CONTENT_RESULT_INTEROP_RECEIVER_KEY : COMMIT_CONTENT_RESULT_RECEIVER_KEY);
        } catch (Throwable th) {
            th = th;
            resultReceiver = 0;
        }
        try {
            Uri uri = (Uri) bundle.getParcelable(z ? COMMIT_CONTENT_CONTENT_URI_INTEROP_KEY : COMMIT_CONTENT_CONTENT_URI_KEY);
            ClipDescription clipDescription = (ClipDescription) bundle.getParcelable(z ? COMMIT_CONTENT_DESCRIPTION_INTEROP_KEY : COMMIT_CONTENT_DESCRIPTION_KEY);
            Uri uri2 = (Uri) bundle.getParcelable(z ? COMMIT_CONTENT_LINK_URI_INTEROP_KEY : COMMIT_CONTENT_LINK_URI_KEY);
            int i = bundle.getInt(z ? COMMIT_CONTENT_FLAGS_INTEROP_KEY : COMMIT_CONTENT_FLAGS_KEY);
            Bundle bundle2 = (Bundle) bundle.getParcelable(z ? COMMIT_CONTENT_OPTS_INTEROP_KEY : COMMIT_CONTENT_OPTS_KEY);
            if (uri != null && clipDescription != null) {
                OnCommitContent = onCommitContentListener.onCommitContent(new InputContentInfoCompat(uri, clipDescription, uri2), i, bundle2);
            }
            if (resultReceiver != 0) {
                resultReceiver.send(OnCommitContent, null);
            }
            return OnCommitContent;
        } catch (Throwable th2) {
            th = th2;
            if (resultReceiver != 0) {
                resultReceiver.send(0, null);
            }
            throw th;
        }
    }

    public static boolean commitContent(InputConnection inputConnection, EditorInfo editorInfo, InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
        boolean z;
        ClipDescription description = inputContentInfo.getDescription();
        String[] contentMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        int length = contentMimeTypes.length;
        boolean z2 = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            }
            if (description.hasMimeType(contentMimeTypes[i])) {
                z = true;
                break;
            }
            i++;
        }
        if (!z) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 25) {
            return inputConnection.commitContent((InputContentInfo) inputContentInfo.unwrap(), flags, opts);
        }
        int protocol = EditorInfoCompat.getProtocol(editorInfo);
        if (protocol == 2) {
            z2 = true;
        } else if (protocol != 3 && protocol != 4) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(z2 ? COMMIT_CONTENT_CONTENT_URI_INTEROP_KEY : COMMIT_CONTENT_CONTENT_URI_KEY, inputContentInfo.getContentUri());
        bundle.putParcelable(z2 ? COMMIT_CONTENT_DESCRIPTION_INTEROP_KEY : COMMIT_CONTENT_DESCRIPTION_KEY, inputContentInfo.getDescription());
        bundle.putParcelable(z2 ? COMMIT_CONTENT_LINK_URI_INTEROP_KEY : COMMIT_CONTENT_LINK_URI_KEY, inputContentInfo.getLinkUri());
        bundle.putInt(z2 ? COMMIT_CONTENT_FLAGS_INTEROP_KEY : COMMIT_CONTENT_FLAGS_KEY, flags);
        bundle.putParcelable(z2 ? COMMIT_CONTENT_OPTS_INTEROP_KEY : COMMIT_CONTENT_OPTS_KEY, opts);
        return inputConnection.performPrivateCommand(z2 ? COMMIT_CONTENT_INTEROP_ACTION : COMMIT_CONTENT_ACTION, bundle);
    }

    public static InputConnection createWrapper(InputConnection inputConnection, EditorInfo editorInfo, final OnCommitContentListener onCommitContentListener) {
        if (inputConnection == null) {
            throw new IllegalArgumentException("inputConnection must be non-null");
        }
        if (editorInfo == null) {
            throw new IllegalArgumentException("editorInfo must be non-null");
        }
        if (onCommitContentListener == null) {
            throw new IllegalArgumentException("onCommitContentListener must be non-null");
        }
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 25) {
            return new InputConnectionWrapper(inputConnection, z) { // from class: androidx.core.view.inputmethod.InputConnectionCompat.1
                @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
                public boolean commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts) {
                    if (onCommitContentListener.onCommitContent(InputContentInfoCompat.wrap(inputContentInfo), flags, opts)) {
                        return true;
                    }
                    return super.commitContent(inputContentInfo, flags, opts);
                }
            };
        }
        return EditorInfoCompat.getContentMimeTypes(editorInfo).length == 0 ? inputConnection : new InputConnectionWrapper(inputConnection, z) { // from class: androidx.core.view.inputmethod.InputConnectionCompat.2
            @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
            public boolean performPrivateCommand(String action, Bundle data) {
                if (InputConnectionCompat.handlePerformPrivateCommand(action, data, onCommitContentListener)) {
                    return true;
                }
                return super.performPrivateCommand(action, data);
            }
        };
    }

    @Deprecated
    public InputConnectionCompat() {
    }
}
