package com.android.launcher3;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class AppListUtils {
    public static final Comparator<AppEntry> NAME_COMPARATOR = new Comparator<AppEntry>() { // from class: com.android.launcher3.AppListUtils.1
        Collator mCollator = Collator.getInstance();

        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(AppEntry lhs, AppEntry rhs) {
            String label = lhs.getLabel();
            String label2 = rhs.getLabel();
            boolean z = false;
            boolean z2 = label.length() > 0 && Character.isLetterOrDigit(label.codePointAt(0));
            if (label2.length() > 0 && Character.isLetterOrDigit(label2.codePointAt(0))) {
                z = true;
            }
            if (z2 && !z) {
                return -1;
            }
            if (z2 || !z) {
                return this.mCollator.compare(label, label2);
            }
            return 1;
        }
    };

    public static void launch(Context context, Intent launchIntent) {
        launchIntent.addFlags(268435456);
        try {
            context.startActivity(launchIntent);
        } catch (Exception e) {
            new AlertDialog.Builder(context, R.style.Theme.Material.Dialog.Alert).setTitle(com.lge.launcher3.R.string.couldnt_launch).setMessage(e.getLocalizedMessage()).setIcon(R.drawable.ic_dialog_alert).show();
        }
    }
}
