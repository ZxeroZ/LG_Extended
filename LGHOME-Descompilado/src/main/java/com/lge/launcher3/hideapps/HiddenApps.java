package com.lge.launcher3.hideapps;

import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.lge.launcher3.util.LGLog;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class HiddenApps implements LauncherAppsCompat.OnAppsChangedCallbackCompat {
    private static final boolean DEBUG = true;
    private static final String TAG = "HideApps.hidden";
    private static HiddenApps sInstance;
    private Context mContext;
    private HashSet<HiddenApp> mHiddenApps = new HashSet<>();

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageAdded(String packageName, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageChanged(String packageName, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesSuspended(String[] packageNames, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesUnsuspended(String[] packageNames, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onShortcutsChanged(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user) {
    }

    private HiddenApps(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        LauncherAppsCompat.getInstance(applicationContext).addOnAppsChangedCallback(this);
        context.getContentResolver().registerContentObserver(LauncherSettings.HideApps.CONTENT_URI, true, new ContentObserver(new Handler()) { // from class: com.lge.launcher3.hideapps.HiddenApps.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                HiddenApps.this.loadHiddenApps();
            }
        });
        loadHiddenApps();
    }

    public static HiddenApps getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HiddenApps(context);
        }
        return sInstance;
    }

    protected void loadHiddenApps() {
        LGLog.i(TAG, "Load hidden apps..");
        this.mHiddenApps.clear();
        AppFilterImpl.clearList();
        for (UserHandle userHandle : UserManagerCompat.getInstance(this.mContext).getUserProfiles()) {
            Iterator<ComponentName> it = HideAppsStorage.getAllItems(this.mContext, userHandle).iterator();
            while (it.hasNext()) {
                HiddenApp hiddenApp = new HiddenApp(it.next(), userHandle);
                this.mHiddenApps.add(hiddenApp);
                LGLog.d(TAG, " # " + hiddenApp);
            }
        }
    }

    public boolean contains(ComponentName cn, UserHandle userHandle) {
        return this.mHiddenApps.contains(new HiddenApp(cn, userHandle));
    }

    public static class HiddenApp implements Parcelable {
        public static final Parcelable.Creator<HiddenApp> CREATOR = new Parcelable.Creator<HiddenApp>() { // from class: com.lge.launcher3.hideapps.HiddenApps.HiddenApp.1
            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public HiddenApp createFromParcel(Parcel in) {
                return new HiddenApp(in);
            }

            /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public HiddenApp[] newArray(int size) {
                return new HiddenApp[size];
            }
        };
        private ComponentName componentName;
        private UserHandle userHandle;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public HiddenApp(ComponentName cn, UserHandle userHandle) {
            this.componentName = cn;
            this.userHandle = userHandle;
        }

        public HiddenApp(Parcel in) {
            this.componentName = (ComponentName) in.readParcelable(null);
            this.userHandle = (UserHandle) in.readParcelable(null);
        }

        public ComponentName getComponentName() {
            return this.componentName;
        }

        public UserHandle getUserHandle() {
            return this.userHandle;
        }

        public boolean equals(Object object) {
            if (!(object instanceof HiddenApp)) {
                return false;
            }
            HiddenApp hiddenApp = (HiddenApp) object;
            return this.componentName.equals(hiddenApp.getComponentName()) && this.userHandle.equals(hiddenApp.userHandle);
        }

        public int hashCode() {
            return this.componentName.hashCode() + this.userHandle.hashCode();
        }

        public String toString() {
            return "{Name: " + this.componentName.toShortString() + ", userHandle: " + this.userHandle + "}";
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.componentName, flags);
            dest.writeParcelable(this.userHandle, flags);
        }
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageRemoved(String packageName, UserHandle user) {
        HideAppsStorage.removeItemForPkg(this.mContext, packageName, user);
    }
}
