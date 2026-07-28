package androidx.core.app;

import android.app.Dialog;
import android.os.Build;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class DialogCompat {
    private DialogCompat() {
    }

    public static View requireViewById(Dialog dialog, int id) {
        if (Build.VERSION.SDK_INT >= 28) {
            return dialog.requireViewById(id);
        }
        View viewFindViewById = dialog.findViewById(id);
        if (viewFindViewById != null) {
            return viewFindViewById;
        }
        throw new IllegalArgumentException("ID does not reference a View inside this Dialog");
    }
}
