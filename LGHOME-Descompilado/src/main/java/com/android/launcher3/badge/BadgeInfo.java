package com.android.launcher3.badge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BadgeInfo {
    public static final int MAX_COUNT = 1000;
    private Shader mNotificationIcon;
    private NotificationInfo mNotificationInfo;
    private PackageUserKey mPackageUserKey;
    private int mTotalCount;
    private NotificationKeyData mNotificationWithIcon = null;
    private List<NotificationKeyData> mNotificationKeys = new ArrayList();

    public NotificationKeyData getNotificationWithIcon() {
        return this.mNotificationWithIcon;
    }

    public void setNotificationWithIcon(NotificationKeyData notificationData) {
        this.mNotificationWithIcon = notificationData;
    }

    public BadgeInfo(PackageUserKey packageUserKey) {
        this.mPackageUserKey = packageUserKey;
    }

    public boolean addOrUpdateNotificationKey(NotificationKeyData notificationKey) {
        int iIndexOf = this.mNotificationKeys.indexOf(notificationKey);
        NotificationKeyData notificationKeyData = iIndexOf == -1 ? null : this.mNotificationKeys.get(iIndexOf);
        if (notificationKeyData != null) {
            int i = this.mTotalCount - notificationKeyData.count;
            this.mTotalCount = i;
            this.mTotalCount = i + notificationKey.count;
            notificationKeyData.count = notificationKey.count;
            return true;
        }
        boolean zAdd = this.mNotificationKeys.add(notificationKey);
        if (zAdd) {
            this.mTotalCount += notificationKey.count;
        }
        return zAdd;
    }

    private void removeGroupHeaderHasChild() {
        ArrayList<NotificationKeyData> arrayList = new ArrayList();
        for (NotificationKeyData notificationKeyData : this.mNotificationKeys) {
            if ((notificationKeyData.flag & 512) != 0) {
                arrayList.add(notificationKeyData);
            }
        }
        ArrayList<NotificationKeyData> arrayList2 = new ArrayList();
        for (NotificationKeyData notificationKeyData2 : arrayList) {
            Iterator<NotificationKeyData> it = this.mNotificationKeys.iterator();
            while (true) {
                if (it.hasNext()) {
                    NotificationKeyData next = it.next();
                    if ((next.flag & 512) == 0 && notificationKeyData2.group != null && notificationKeyData2.group.equals(next.group)) {
                        arrayList2.add(notificationKeyData2);
                        break;
                    }
                }
            }
        }
        for (NotificationKeyData notificationKeyData3 : arrayList2) {
            this.mTotalCount -= notificationKeyData3.count;
            this.mNotificationKeys.remove(notificationKeyData3);
        }
    }

    public boolean removeNotificationKey(NotificationKeyData notificationKey) {
        if (this.mNotificationWithIcon != null && notificationKey.notificationKey.equals(this.mNotificationWithIcon.notificationKey)) {
            this.mNotificationWithIcon = null;
        }
        boolean zRemove = this.mNotificationKeys.remove(notificationKey);
        if (zRemove) {
            this.mTotalCount -= notificationKey.count;
        }
        return zRemove;
    }

    public List<NotificationKeyData> getNotificationKeys() {
        return this.mNotificationKeys;
    }

    public int getNotificationCount() {
        return Math.min(this.mTotalCount, 1000);
    }

    public void setNotificationToShow(NotificationInfo notificationInfo) {
        this.mNotificationInfo = notificationInfo;
        this.mNotificationIcon = null;
    }

    public boolean hasNotificationToShow() {
        return this.mNotificationInfo != null;
    }

    public Shader getNotificationIconForBadge(Context context, int badgeColor, int badgeSize, int badgePadding) {
        NotificationInfo notificationInfo = this.mNotificationInfo;
        if (notificationInfo == null) {
            return null;
        }
        if (this.mNotificationIcon == null) {
            Drawable drawableNewDrawable = notificationInfo.getIconForBackground(context, badgeColor).getConstantState().newDrawable();
            int i = badgeSize - (badgePadding * 2);
            drawableNewDrawable.setBounds(0, 0, i, i);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(badgeSize, badgeSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            float f = badgePadding;
            canvas.translate(f, f);
            drawableNewDrawable.draw(canvas);
            this.mNotificationIcon = new BitmapShader(bitmapCreateBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        return this.mNotificationIcon;
    }

    public boolean isIconLarge() {
        NotificationInfo notificationInfo = this.mNotificationInfo;
        return notificationInfo != null && notificationInfo.isIconLarge();
    }

    public boolean shouldBeInvalidated(BadgeInfo newBadge) {
        return this.mPackageUserKey.equals(newBadge.mPackageUserKey) && getNotificationCount() != newBadge.getNotificationCount();
    }
}
