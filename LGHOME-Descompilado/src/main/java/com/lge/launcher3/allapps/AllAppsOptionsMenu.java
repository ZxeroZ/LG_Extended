package com.lge.launcher3.allapps;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsOptionsMenu {
    private final String TAG;
    private boolean mIsShowing;
    private final Menu mMenu;
    private final PopupMenu mPopupMenu;

    public AllAppsOptionsMenu(Context context, View button, PopupMenu.OnMenuItemClickListener listener) {
        String simpleName = AllAppsOptionsMenu.class.getSimpleName();
        this.TAG = simpleName;
        int identifier = context.getResources().getIdentifier("actionOverflowMenuStyle", "attr", LauncherConst.PACKAGE_NAME_NATIVE);
        if (identifier != 0) {
            this.mPopupMenu = new PopupMenu(context, button, GravityCompat.END, identifier, 0);
        } else {
            LGLog.d(simpleName, "Show default popup menu because the resId of attr is 0");
            this.mPopupMenu = new PopupMenu(context, button);
        }
        this.mMenu = this.mPopupMenu.getMenu();
        this.mPopupMenu.setOnMenuItemClickListener(listener);
        this.mPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() { // from class: com.lge.launcher3.allapps.AllAppsOptionsMenu.1
            @Override // android.widget.PopupMenu.OnDismissListener
            public void onDismiss(PopupMenu menu) {
                AllAppsOptionsMenu.this.mIsShowing = false;
            }
        });
    }

    public void addItems(String lablel, int itemID) {
        Menu menu = this.mMenu;
        menu.add(0, itemID, menu.size(), 0);
    }

    public void dismiss() {
        this.mPopupMenu.dismiss();
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public void clear() {
        this.mMenu.clear();
    }

    public void show() {
        if (this.mIsShowing) {
            return;
        }
        this.mPopupMenu.show();
        this.mIsShowing = true;
    }
}
