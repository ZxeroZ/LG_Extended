package com.android.launcher3.notification;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.service.notification.StatusBarNotification;
import android.view.View;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.PackageUserKey;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class NotificationInfo implements View.OnClickListener {
    private static final String TAG = "NotificationInfo";
    public final boolean autoCancel;
    public final boolean dismissable;
    public final PendingIntent intent;
    private int mIconColor;
    private Drawable mIconDrawable;
    private boolean mIsIconLarge;
    private final ItemInfo mItemInfo;
    public final String notificationKey;
    public final PackageUserKey packageUserKey;
    public final CharSequence text;
    public final CharSequence title;

    public NotificationInfo(Context context, StatusBarNotification statusBarNotification, ItemInfo itemInfo) {
        this.packageUserKey = PackageUserKey.fromNotification(statusBarNotification);
        this.notificationKey = statusBarNotification.getKey();
        Notification notification = statusBarNotification.getNotification();
        this.title = notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE);
        this.text = notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
        Icon largeIcon = notification.getBadgeIconType() == 1 ? null : notification.getLargeIcon();
        if (largeIcon == null) {
            Icon smallIcon = notification.getSmallIcon();
            this.mIconDrawable = smallIcon != null ? smallIcon.loadDrawable(context) : null;
            this.mIconColor = statusBarNotification.getNotification().color;
            this.mIsIconLarge = false;
        } else {
            this.mIconDrawable = largeIcon.loadDrawable(context);
            this.mIsIconLarge = true;
        }
        if (this.mIconDrawable == null) {
            this.mIconDrawable = new BitmapDrawable(context.getResources(), LauncherAppState.getInstance(context).getIconCache().getDefaultIcon(statusBarNotification.getUser()));
        }
        this.intent = notification.contentIntent;
        this.autoCancel = (notification.flags & 16) != 0;
        this.dismissable = (notification.flags & 2) == 0;
        this.mItemInfo = itemInfo;
    }

    private void notiViewIconNullCheck(Context context, Icon icon) {
        if (icon == null) {
            LGLog.i(TAG, "notiViewIcon is null, Can't get icon from Notification");
            this.mIconDrawable = null;
        } else {
            this.mIconDrawable = icon.loadDrawable(context);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.intent == null) {
            return;
        }
        Launcher launcher = Launcher.getLauncher(view.getContext());
        try {
            this.intent.send(null, 0, null, null, null, null, ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle());
            launcher.getUserEventDispatcher().logNotificationLaunch(view, this.intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        if (this.autoCancel) {
            launcher.getPopupDataProvider().cancelNotification(this.notificationKey);
        }
        AbstractFloatingView.closeOpenContainer(launcher, 2);
    }

    public Drawable getIconForBackground(Context context, int background) {
        if (this.mIsIconLarge) {
            return this.mIconDrawable;
        }
        this.mIconColor = IconPalette.resolveContrastColor(context, this.mIconColor, background);
        Drawable drawableMutate = this.mIconDrawable.mutate();
        drawableMutate.setTintList(null);
        drawableMutate.setTint(this.mIconColor);
        return drawableMutate;
    }

    public boolean isIconLarge() {
        return this.mIsIconLarge;
    }
}
