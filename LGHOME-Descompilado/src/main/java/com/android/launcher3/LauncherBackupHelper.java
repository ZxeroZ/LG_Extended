package com.android.launcher3;

import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.backup.nano.BackupProtos;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.ItemInfo;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.CRC32;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class LauncherBackupHelper implements BackupHelper {
    private static final int APPWIDGET_ID_INDEX = 4;
    private static final int APPWIDGET_PROVIDER_INDEX = 3;
    private static final int BACKUP_VERSION = 3;
    private static final int CELLX_INDEX = 5;
    private static final int CELLY_INDEX = 6;
    private static final int CONTAINER_INDEX = 7;
    private static final boolean DEBUG = false;
    private static final int ICON_INDEX = 8;
    private static final int ICON_PACKAGE_INDEX = 9;
    private static final int ICON_RESOURCE_INDEX = 10;
    private static final int ICON_TYPE_INDEX = 11;
    private static final int ID_INDEX = 0;
    private static final int ID_MODIFIED = 1;
    private static final int INTENT_INDEX = 2;
    private static final int ITEM_TYPE_INDEX = 12;
    private static final String JOURNAL_KEY = "#";
    private static final int MAX_ICONS_PER_PASS = 10;
    private static final int MAX_JOURNAL_SIZE = 1000000;
    private static final int MAX_WIDGETS_PER_PASS = 5;
    private static final int SCREEN_INDEX = 13;
    private static final int SCREEN_RANK_INDEX = 2;
    private static final int SPANX_INDEX = 14;
    private static final int SPANY_INDEX = 15;
    private static final String TAG = "LauncherBackupHelper";
    private static final int TITLE_INDEX = 16;
    private static final boolean VERBOSE = false;
    private boolean mBackupDataWasUpdated;
    private BackupManager mBackupManager;
    final Context mContext;
    private BackupProtos.DeviceProfieData mDeviceProfileData;
    private IconCache mIconCache;
    private InvariantDeviceProfile mIdp;
    private long mLastBackupRestoreTime;
    private final long mUserSerial;
    private static final String[] FAVORITE_PROJECTION = {"_id", LauncherSettings.ChangeLogColumns.MODIFIED, LauncherSettings.BaseLauncherColumns.INTENT, LauncherSettings.Favorites.APPWIDGET_PROVIDER, "appWidgetId", LauncherSettings.Favorites.CELLX, LauncherSettings.Favorites.CELLY, LauncherSettings.Favorites.CONTAINER, "icon", LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, LauncherSettings.BaseLauncherColumns.ICON_TYPE, LauncherSettings.BaseLauncherColumns.ITEM_TYPE, "screen", "spanX", "spanY", "title", "profileId"};
    private static final String[] SCREEN_PROJECTION = {"_id", LauncherSettings.ChangeLogColumns.MODIFIED, LauncherSettings.WorkspaceScreens.SCREEN_RANK};
    private byte[] mBuffer = new byte[512];
    int restoredBackupVersion = 1;
    private final HashSet<String> mExistingKeys = new HashSet<>();
    private final ArrayList<BackupProtos.Key> mKeys = new ArrayList<>();
    boolean restoreSuccessful = true;
    private final ItemTypeMatcher[] mItemTypeMatchers = new ItemTypeMatcher[7];

    public LauncherBackupHelper(Context context) {
        this.mContext = context;
        this.mUserSerial = UserManagerCompat.getInstance(context).getSerialNumberForUser(Process.myUserHandle());
    }

    private void dataChanged() {
        if (this.mBackupManager == null) {
            this.mBackupManager = new BackupManager(this.mContext);
        }
        this.mBackupManager.dataChanged();
    }

    private void applyJournal(BackupProtos.Journal journal) {
        this.mLastBackupRestoreTime = journal.t;
        this.mExistingKeys.clear();
        if (journal.key != null) {
            for (BackupProtos.Key key : journal.key) {
                this.mExistingKeys.add(keyToBackupKey(key));
            }
        }
        this.restoredBackupVersion = journal.backupVersion;
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        BackupProtos.Journal journal = readJournal(oldState);
        if (!launcherIsReady()) {
            dataChanged();
            writeJournal(newState, journal);
            return;
        }
        if (this.mDeviceProfileData == null) {
            LauncherAppState launcherAppState = LauncherAppState.getInstance(this.mContext);
            InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
            this.mIdp = invariantDeviceProfile;
            this.mDeviceProfileData = initDeviceProfileData(invariantDeviceProfile);
            this.mIconCache = launcherAppState.getIconCache();
        }
        Log.v(TAG, "lastBackupTime = " + journal.t);
        this.mKeys.clear();
        applyJournal(journal);
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mBackupDataWasUpdated = false;
        try {
            backupFavorites(data);
            backupScreens(data);
            backupIcons(data);
            backupWidgets(data);
            HashSet hashSet = new HashSet();
            Iterator<BackupProtos.Key> it = this.mKeys.iterator();
            while (it.hasNext()) {
                hashSet.add(keyToBackupKey(it.next()));
            }
            this.mExistingKeys.removeAll(hashSet);
            Iterator<String> it2 = this.mExistingKeys.iterator();
            while (it2.hasNext()) {
                data.writeEntityHeader(it2.next(), -1);
                this.mBackupDataWasUpdated = true;
            }
            this.mExistingKeys.clear();
            if (!this.mBackupDataWasUpdated) {
                this.mBackupDataWasUpdated = (journal.profile != null && Arrays.equals(BackupProtos.DeviceProfieData.toByteArray(journal.profile), BackupProtos.DeviceProfieData.toByteArray(this.mDeviceProfileData)) && journal.backupVersion == 3 && journal.appVersion == getAppVersion()) ? false : true;
            }
            if (this.mBackupDataWasUpdated) {
                this.mLastBackupRestoreTime = jCurrentTimeMillis;
                writeRowToBackup(JOURNAL_KEY, getCurrentStateJournal(), data);
            }
        } catch (IOException e) {
            Log.e(TAG, "launcher backup has failed", e);
        }
        writeNewStateDescription(newState);
    }

    private boolean isBackupCompatible(BackupProtos.Journal oldState) {
        BackupProtos.DeviceProfieData deviceProfieData = this.mDeviceProfileData;
        BackupProtos.DeviceProfieData deviceProfieData2 = oldState.profile;
        if (deviceProfieData2 == null || deviceProfieData2.desktopCols == 0.0f) {
            return true;
        }
        boolean z = ((float) deviceProfieData.allappsRank) >= deviceProfieData2.hotseatCount;
        if (deviceProfieData.hotseatCount >= deviceProfieData2.hotseatCount && deviceProfieData.allappsRank == deviceProfieData2.allappsRank) {
            z = true;
        }
        return z && deviceProfieData.desktopCols >= deviceProfieData2.desktopCols && deviceProfieData.desktopRows >= deviceProfieData2.desktopRows;
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream data) {
        if (this.restoreSuccessful) {
            if (this.mDeviceProfileData == null) {
                InvariantDeviceProfile invariantDeviceProfile = new InvariantDeviceProfile(this.mContext);
                this.mIdp = invariantDeviceProfile;
                this.mDeviceProfileData = initDeviceProfileData(invariantDeviceProfile);
                this.mIconCache = new IconCache(this.mContext, this.mIdp);
            }
            int size = data.size();
            if (this.mBuffer.length < size) {
                this.mBuffer = new byte[size];
            }
            try {
                data.read(this.mBuffer, 0, size);
                String key = data.getKey();
                if (JOURNAL_KEY.equals(key)) {
                    if (!this.mKeys.isEmpty()) {
                        Log.wtf(TAG, keyToBackupKey(this.mKeys.get(0)) + " received after #");
                        this.restoreSuccessful = false;
                        return;
                    }
                    BackupProtos.Journal journal = new BackupProtos.Journal();
                    MessageNano.mergeFrom(journal, readCheckedBytes(this.mBuffer, size));
                    applyJournal(journal);
                    this.restoreSuccessful = isBackupCompatible(journal);
                    return;
                }
                if (this.mExistingKeys.isEmpty() || this.mExistingKeys.contains(key)) {
                    BackupProtos.Key keyBackupKeyToKey = backupKeyToKey(key);
                    this.mKeys.add(keyBackupKeyToKey);
                    int i = keyBackupKeyToKey.type;
                    if (i == 1) {
                        restoreFavorite(keyBackupKeyToKey, this.mBuffer, size);
                        return;
                    }
                    if (i == 2) {
                        restoreScreen(keyBackupKeyToKey, this.mBuffer, size);
                        return;
                    }
                    if (i == 3) {
                        restoreIcon(keyBackupKeyToKey, this.mBuffer, size);
                        return;
                    }
                    if (i == 4) {
                        restoreWidget(keyBackupKeyToKey, this.mBuffer, size);
                        return;
                    }
                    Log.w(TAG, "unknown restore entity type: " + keyBackupKeyToKey.type);
                    this.mKeys.remove(keyBackupKeyToKey);
                }
            } catch (IOException e) {
                Log.w(TAG, "ignoring unparsable backup entry", e);
            }
        }
    }

    @Override // android.app.backup.BackupHelper
    public void writeNewStateDescription(ParcelFileDescriptor newState) {
        writeJournal(newState, getCurrentStateJournal());
    }

    private BackupProtos.Journal getCurrentStateJournal() {
        BackupProtos.Journal journal = new BackupProtos.Journal();
        journal.t = this.mLastBackupRestoreTime;
        ArrayList<BackupProtos.Key> arrayList = this.mKeys;
        journal.key = (BackupProtos.Key[]) arrayList.toArray(new BackupProtos.Key[arrayList.size()]);
        journal.appVersion = getAppVersion();
        journal.backupVersion = 3;
        journal.profile = this.mDeviceProfileData;
        return journal;
    }

    private int getAppVersion() {
        try {
            return this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException unused) {
            return 0;
        }
    }

    private BackupProtos.DeviceProfieData initDeviceProfileData(InvariantDeviceProfile profile) {
        BackupProtos.DeviceProfieData deviceProfieData = new BackupProtos.DeviceProfieData();
        deviceProfieData.desktopRows = profile.numRows;
        deviceProfieData.desktopCols = profile.numColumns;
        deviceProfieData.hotseatCount = profile.numHotseatIcons;
        deviceProfieData.allappsRank = profile.hotseatAllAppsRank;
        return deviceProfieData;
    }

    private void backupFavorites(BackupDataOutput data) throws IOException {
        Cursor cursorQuery = this.mContext.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, FAVORITE_PROJECTION, getUserSelectionArg(), null, null);
        try {
            cursorQuery.moveToPosition(-1);
            while (cursorQuery.moveToNext()) {
                long j = cursorQuery.getLong(0);
                long j2 = cursorQuery.getLong(1);
                BackupProtos.Key key = getKey(1, j);
                this.mKeys.add(key);
                if (!this.mExistingKeys.contains(keyToBackupKey(key)) || j2 >= this.mLastBackupRestoreTime) {
                    writeRowToBackup(key, packFavorite(cursorQuery), data);
                }
            }
        } finally {
            cursorQuery.close();
        }
    }

    private void restoreFavorite(BackupProtos.Key key, byte[] buffer, int dataSize) throws IOException {
        this.mContext.getContentResolver().insert(LauncherSettings.Favorites.CONTENT_URI, unpackFavorite(buffer, dataSize));
    }

    private void backupScreens(BackupDataOutput data) throws IOException {
        Cursor cursorQuery = this.mContext.getContentResolver().query(LauncherSettings.WorkspaceScreens.CONTENT_URI, SCREEN_PROJECTION, null, null, null);
        try {
            cursorQuery.moveToPosition(-1);
            while (cursorQuery.moveToNext()) {
                long j = cursorQuery.getLong(0);
                long j2 = cursorQuery.getLong(1);
                BackupProtos.Key key = getKey(2, j);
                this.mKeys.add(key);
                if (!this.mExistingKeys.contains(keyToBackupKey(key)) || j2 >= this.mLastBackupRestoreTime) {
                    writeRowToBackup(key, packScreen(cursorQuery), data);
                }
            }
        } finally {
            cursorQuery.close();
        }
    }

    private void restoreScreen(BackupProtos.Key key, byte[] buffer, int dataSize) throws IOException {
        this.mContext.getContentResolver().insert(LauncherSettings.WorkspaceScreens.CONTENT_URI, unpackScreen(buffer, dataSize));
    }

    private void backupIcons(BackupDataOutput data) throws IOException {
        BackupProtos.Key key;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int i = this.mContext.getResources().getDisplayMetrics().densityDpi;
        UserHandle userHandleMyUserHandle = Process.myUserHandle();
        Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI, FAVORITE_PROJECTION, "(itemType=0 OR itemType=1) AND " + getUserSelectionArg(), null, null);
        try {
            cursorQuery.moveToPosition(-1);
            int i2 = 0;
            while (cursorQuery.moveToNext()) {
                long j = cursorQuery.getLong(0);
                try {
                    Intent uri = Intent.parseUri(cursorQuery.getString(2), 0);
                    ComponentName component = uri.getComponent();
                    String strKeyToBackupKey = null;
                    if (component != null) {
                        BackupProtos.Key key2 = getKey(3, component.flattenToShortString());
                        strKeyToBackupKey = keyToBackupKey(key2);
                        key = key2;
                    } else {
                        Log.w(TAG, "empty intent on application favorite: " + j);
                        key = null;
                    }
                    if (this.mExistingKeys.contains(strKeyToBackupKey)) {
                        this.mKeys.add(key);
                    } else if (strKeyToBackupKey != null) {
                        if (i2 < 10) {
                            Bitmap icon = this.mIconCache.getIcon(uri, userHandleMyUserHandle);
                            if (icon != null && !this.mIconCache.isDefaultIcon(icon, userHandleMyUserHandle)) {
                                writeRowToBackup(key, packIcon(i, icon), data);
                                this.mKeys.add(key);
                                i2++;
                            }
                        } else {
                            dataChanged();
                        }
                    }
                } catch (IOException unused) {
                    Log.e(TAG, "unable to save application icon for favorite: " + j);
                } catch (URISyntaxException unused2) {
                    Log.e(TAG, "invalid URI on application favorite: " + j);
                }
            }
        } finally {
            cursorQuery.close();
        }
    }

    private void restoreIcon(BackupProtos.Key key, byte[] buffer, int dataSize) throws IOException {
        BackupProtos.Resource resource = (BackupProtos.Resource) unpackProto(new BackupProtos.Resource(), buffer, dataSize);
        Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(resource.data, 0, resource.data.length);
        if (bitmapDecodeByteArray == null) {
            Log.w(TAG, "failed to unpack icon for " + key.name);
        }
        this.mIconCache.preloadIcon(ComponentName.unflattenFromString(key.name), bitmapDecodeByteArray, resource.dpi, "", this.mUserSerial);
    }

    private void backupWidgets(BackupDataOutput data) throws IOException {
        BackupProtos.Key key;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int i = this.mContext.getResources().getDisplayMetrics().densityDpi;
        Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI, FAVORITE_PROJECTION, "itemType=4 AND " + getUserSelectionArg(), null, null);
        try {
            cursorQuery.moveToPosition(-1);
            int i2 = 0;
            while (cursorQuery.moveToNext()) {
                long j = cursorQuery.getLong(0);
                String string = cursorQuery.getString(3);
                ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(string);
                String strKeyToBackupKey = null;
                if (componentNameUnflattenFromString != null) {
                    BackupProtos.Key key2 = getKey(4, string);
                    strKeyToBackupKey = keyToBackupKey(key2);
                    key = key2;
                } else {
                    Log.w(TAG, "empty intent on appwidget: " + j);
                    key = null;
                }
                if (this.mExistingKeys.contains(strKeyToBackupKey) && this.restoredBackupVersion >= 3) {
                    this.mKeys.add(key);
                } else if (strKeyToBackupKey != null) {
                    if (i2 < 5) {
                        writeRowToBackup(key, packWidget(i, componentNameUnflattenFromString, Process.myUserHandle()), data);
                        this.mKeys.add(key);
                        i2++;
                    } else {
                        dataChanged();
                    }
                }
            }
        } finally {
            cursorQuery.close();
        }
    }

    private void restoreWidget(BackupProtos.Key key, byte[] buffer, int dataSize) throws IOException {
        BackupProtos.Widget widget = (BackupProtos.Widget) unpackProto(new BackupProtos.Widget(), buffer, dataSize);
        if (widget.icon.data != null) {
            Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(widget.icon.data, 0, widget.icon.data.length);
            if (bitmapDecodeByteArray == null) {
                Log.w(TAG, "failed to unpack widget icon for " + key.name);
                return;
            }
            this.mIconCache.preloadIcon(ComponentName.unflattenFromString(widget.provider), bitmapDecodeByteArray, widget.icon.dpi, widget.label, this.mUserSerial);
        }
    }

    private BackupProtos.Key getKey(int type, long id) {
        BackupProtos.Key key = new BackupProtos.Key();
        key.type = type;
        key.id = id;
        key.checksum = checkKey(key);
        return key;
    }

    private BackupProtos.Key getKey(int type, String name) {
        BackupProtos.Key key = new BackupProtos.Key();
        key.type = type;
        key.name = name;
        key.checksum = checkKey(key);
        return key;
    }

    private String keyToBackupKey(BackupProtos.Key key) {
        return Base64.encodeToString(BackupProtos.Key.toByteArray(key), 2);
    }

    private BackupProtos.Key backupKeyToKey(String backupKey) throws InvalidBackupException {
        try {
            BackupProtos.Key from = BackupProtos.Key.parseFrom(Base64.decode(backupKey, 0));
            if (from.checksum == checkKey(from)) {
                return from;
            }
            throw new InvalidBackupException("invalid key read from stream" + backupKey);
        } catch (InvalidProtocolBufferNanoException e) {
            throw new InvalidBackupException(e);
        } catch (IllegalArgumentException e2) {
            throw new InvalidBackupException(e2);
        }
    }

    private long checkKey(BackupProtos.Key key) {
        CRC32 crc32 = new CRC32();
        crc32.update(key.type);
        crc32.update((int) (key.id & 65535));
        crc32.update((int) ((key.id >> 32) & 65535));
        if (!TextUtils.isEmpty(key.name)) {
            crc32.update(key.name.getBytes());
        }
        return crc32.getValue();
    }

    private boolean isReplaceableHotseatItem(BackupProtos.Favorite favorite) {
        return favorite.container == -101 && favorite.intent != null && (favorite.itemType == 0 || favorite.itemType == 1);
    }

    private BackupProtos.Favorite packFavorite(Cursor c) {
        Intent uri;
        BackupProtos.Favorite favorite = new BackupProtos.Favorite();
        int i = 0;
        favorite.id = c.getLong(0);
        favorite.screen = c.getInt(13);
        favorite.container = c.getInt(7);
        favorite.cellX = c.getInt(5);
        favorite.cellY = c.getInt(6);
        favorite.spanX = c.getInt(14);
        favorite.spanY = c.getInt(15);
        favorite.iconType = c.getInt(11);
        String string = c.getString(16);
        if (!TextUtils.isEmpty(string)) {
            favorite.title = string;
        }
        String string2 = c.getString(2);
        ActivityInfo activityInfo = null;
        if (TextUtils.isEmpty(string2)) {
            uri = null;
        } else {
            try {
                uri = Intent.parseUri(string2, 0);
                try {
                    uri.removeExtra(ItemInfo.EXTRA_PROFILE);
                    favorite.intent = uri.toUri(0);
                } catch (URISyntaxException e) {
                    e = e;
                    Log.e(TAG, "Invalid intent", e);
                }
            } catch (URISyntaxException e2) {
                e = e2;
                uri = null;
            }
        }
        favorite.itemType = c.getInt(12);
        if (favorite.itemType == 4) {
            favorite.appWidgetId = c.getInt(4);
            String string3 = c.getString(3);
            if (!TextUtils.isEmpty(string3)) {
                favorite.appWidgetProvider = string3;
            }
        } else if (favorite.itemType == 1) {
            if (favorite.iconType == 0) {
                String string4 = c.getString(9);
                if (!TextUtils.isEmpty(string4)) {
                    favorite.iconPackage = string4;
                }
                String string5 = c.getString(10);
                if (!TextUtils.isEmpty(string5)) {
                    favorite.iconResource = string5;
                }
            }
            byte[] blob = c.getBlob(8);
            if (blob != null && blob.length > 0) {
                favorite.icon = blob;
            }
        }
        if (isReplaceableHotseatItem(favorite) && uri != null && uri.getComponent() != null) {
            PackageManager packageManager = this.mContext.getPackageManager();
            try {
                activityInfo = packageManager.getActivityInfo(uri.getComponent(), 0);
            } catch (PackageManager.NameNotFoundException e3) {
                Log.e(TAG, "Target not found", e3);
            }
            if (activityInfo == null) {
                return favorite;
            }
            while (true) {
                ItemTypeMatcher[] itemTypeMatcherArr = this.mItemTypeMatchers;
                if (i >= itemTypeMatcherArr.length) {
                    break;
                }
                if (itemTypeMatcherArr[i] == null) {
                    itemTypeMatcherArr[i] = new ItemTypeMatcher(CommonAppTypeParser.getResourceForItemType(i));
                }
                if (this.mItemTypeMatchers[i].matches(activityInfo, packageManager)) {
                    favorite.targetType = i;
                    break;
                }
                i++;
            }
        }
        return favorite;
    }

    private ContentValues unpackFavorite(byte[] buffer, int dataSize) throws IOException {
        BackupProtos.Favorite favorite = (BackupProtos.Favorite) unpackProto(new BackupProtos.Favorite(), buffer, dataSize);
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", Long.valueOf(favorite.id));
        contentValues.put("screen", Integer.valueOf(favorite.screen));
        contentValues.put(LauncherSettings.Favorites.CONTAINER, Integer.valueOf(favorite.container));
        contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(favorite.cellX));
        contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(favorite.cellY));
        contentValues.put("spanX", Integer.valueOf(favorite.spanX));
        contentValues.put("spanY", Integer.valueOf(favorite.spanY));
        if (favorite.itemType == 1) {
            contentValues.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, Integer.valueOf(favorite.iconType));
            if (favorite.iconType == 0) {
                contentValues.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, favorite.iconPackage);
                contentValues.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, favorite.iconResource);
            }
            contentValues.put("icon", favorite.icon);
        }
        if (!TextUtils.isEmpty(favorite.title)) {
            contentValues.put("title", favorite.title);
        } else {
            contentValues.put("title", "");
        }
        if (!TextUtils.isEmpty(favorite.intent)) {
            contentValues.put(LauncherSettings.BaseLauncherColumns.INTENT, favorite.intent);
        }
        contentValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(favorite.itemType));
        contentValues.put("profileId", Long.valueOf(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle())));
        BackupProtos.DeviceProfieData deviceProfieData = this.mDeviceProfileData;
        if (favorite.itemType == 4) {
            if (!TextUtils.isEmpty(favorite.appWidgetProvider)) {
                contentValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, favorite.appWidgetProvider);
            }
            contentValues.put("appWidgetId", Integer.valueOf(favorite.appWidgetId));
            contentValues.put(LauncherSettings.Favorites.RESTORED, (Integer) 7);
            if (favorite.cellX + favorite.spanX > deviceProfieData.desktopCols || favorite.cellY + favorite.spanY > deviceProfieData.desktopRows) {
                this.restoreSuccessful = false;
                throw new InvalidBackupException("Widget not in screen bounds, aborting restore");
            }
        } else {
            if (isReplaceableHotseatItem(favorite) && favorite.targetType != 0 && favorite.targetType < 7) {
                Log.e(TAG, "Added item type flag");
                contentValues.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(1 | CommonAppTypeParser.encodeItemTypeToFlag(favorite.targetType)));
            } else {
                contentValues.put(LauncherSettings.Favorites.RESTORED, (Integer) 1);
            }
            if (favorite.container == -101) {
                if (favorite.screen >= deviceProfieData.hotseatCount || favorite.screen == deviceProfieData.allappsRank) {
                    this.restoreSuccessful = false;
                    throw new InvalidBackupException("Item not in hotseat bounds, aborting restore");
                }
            } else if (favorite.cellX >= deviceProfieData.desktopCols || favorite.cellY >= deviceProfieData.desktopRows) {
                this.restoreSuccessful = false;
                throw new InvalidBackupException("Item not in desktop bounds, aborting restore");
            }
        }
        return contentValues;
    }

    private BackupProtos.Screen packScreen(Cursor c) {
        BackupProtos.Screen screen = new BackupProtos.Screen();
        screen.id = c.getLong(0);
        screen.rank = c.getInt(2);
        return screen;
    }

    private ContentValues unpackScreen(byte[] buffer, int dataSize) throws InvalidProtocolBufferNanoException {
        BackupProtos.Screen screen = (BackupProtos.Screen) unpackProto(new BackupProtos.Screen(), buffer, dataSize);
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", Long.valueOf(screen.id));
        contentValues.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(screen.rank));
        return contentValues;
    }

    private BackupProtos.Resource packIcon(int dpi, Bitmap icon) {
        BackupProtos.Resource resource = new BackupProtos.Resource();
        resource.dpi = dpi;
        resource.data = Utilities.flattenBitmap(icon);
        return resource;
    }

    private BackupProtos.Widget packWidget(int dpi, ComponentName provider, UserHandle user) {
        LauncherAppWidgetProviderInfo providerInfo = LauncherModel.getProviderInfo(this.mContext, provider, user);
        BackupProtos.Widget widget = new BackupProtos.Widget();
        widget.provider = provider.flattenToShortString();
        widget.label = providerInfo.label;
        widget.configure = providerInfo.configure != null;
        if (providerInfo.icon != 0) {
            widget.icon = new BackupProtos.Resource();
            widget.icon.data = Utilities.flattenBitmap(Utilities.createIconBitmap(this.mIconCache.getFullResIcon(provider.getPackageName(), providerInfo.icon), this.mContext));
            widget.icon.dpi = dpi;
        }
        int[] iArrRectToCell = CellLayout.rectToCell(this.mIdp.portraitProfile, this.mContext, providerInfo.minResizeWidth, providerInfo.minResizeHeight, null);
        widget.minSpanX = (providerInfo.resizeMode & 1) != 0 ? iArrRectToCell[0] : -1;
        widget.minSpanY = (providerInfo.resizeMode & 2) != 0 ? iArrRectToCell[1] : -1;
        return widget;
    }

    private <T extends MessageNano> T unpackProto(T proto, byte[] buffer, int dataSize) throws InvalidProtocolBufferNanoException {
        MessageNano.mergeFrom(proto, readCheckedBytes(buffer, dataSize));
        return proto;
    }

    private BackupProtos.Journal readJournal(ParcelFileDescriptor oldState) {
        int i;
        BackupProtos.Journal journal = new BackupProtos.Journal();
        if (oldState == null) {
            return journal;
        }
        FileInputStream fileInputStream = new FileInputStream(oldState.getFileDescriptor());
        try {
            try {
                try {
                    int iAvailable = fileInputStream.available();
                    if (iAvailable < 1000000) {
                        byte[] bArr = new byte[iAvailable];
                        InvalidProtocolBufferNanoException e = null;
                        boolean z = false;
                        int i2 = 0;
                        while (iAvailable > 0) {
                            try {
                                i = fileInputStream.read(bArr, i2, 1);
                            } catch (IOException unused) {
                                bArr = null;
                            }
                            if (i <= 0) {
                                Log.w(TAG, "unexpected end of file while reading journal.");
                                iAvailable = 0;
                                MessageNano.mergeFrom(journal, readCheckedBytes(bArr, i2));
                                z = true;
                                iAvailable = 0;
                            } else {
                                iAvailable -= i;
                                i2 += i;
                                try {
                                    MessageNano.mergeFrom(journal, readCheckedBytes(bArr, i2));
                                    z = true;
                                    iAvailable = 0;
                                } catch (InvalidProtocolBufferNanoException e2) {
                                    e = e2;
                                    journal.clear();
                                }
                            }
                        }
                        if (!z) {
                            Log.w(TAG, "could not find a valid journal", e);
                        }
                    }
                    fileInputStream.close();
                } catch (IOException e3) {
                    Log.w(TAG, "failed to close the journal", e3);
                    fileInputStream.close();
                }
            } catch (IOException e4) {
                Log.w(TAG, "failed to close the journal", e4);
            }
            return journal;
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (IOException e5) {
                Log.w(TAG, "failed to close the journal", e5);
            }
            throw th;
        }
    }

    private void writeRowToBackup(BackupProtos.Key key, MessageNano proto, BackupDataOutput data) throws IOException {
        writeRowToBackup(keyToBackupKey(key), proto, data);
    }

    private void writeRowToBackup(String backupKey, MessageNano proto, BackupDataOutput data) throws IOException {
        byte[] bArrWriteCheckedBytes = writeCheckedBytes(proto);
        data.writeEntityHeader(backupKey, bArrWriteCheckedBytes.length);
        data.writeEntityData(bArrWriteCheckedBytes, bArrWriteCheckedBytes.length);
        this.mBackupDataWasUpdated = true;
    }

    private void writeJournal(ParcelFileDescriptor newState, BackupProtos.Journal journal) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newState.getFileDescriptor());
            fileOutputStream.write(writeCheckedBytes(journal));
            fileOutputStream.close();
        } catch (IOException e) {
            Log.w(TAG, "failed to write backup journal", e);
        }
    }

    private byte[] writeCheckedBytes(MessageNano proto) {
        BackupProtos.CheckedMessage checkedMessage = new BackupProtos.CheckedMessage();
        checkedMessage.payload = MessageNano.toByteArray(proto);
        CRC32 crc32 = new CRC32();
        crc32.update(checkedMessage.payload);
        checkedMessage.checksum = crc32.getValue();
        return MessageNano.toByteArray(checkedMessage);
    }

    private static byte[] readCheckedBytes(byte[] buffer, int dataSize) throws InvalidProtocolBufferNanoException {
        BackupProtos.CheckedMessage checkedMessage = new BackupProtos.CheckedMessage();
        MessageNano.mergeFrom(checkedMessage, buffer, 0, dataSize);
        CRC32 crc32 = new CRC32();
        crc32.update(checkedMessage.payload);
        if (checkedMessage.checksum != crc32.getValue()) {
            throw new InvalidProtocolBufferNanoException("checksum does not match");
        }
        return checkedMessage.payload;
    }

    private boolean launcherIsReady() {
        Cursor cursorQuery = this.mContext.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, FAVORITE_PROJECTION, null, null, null);
        if (cursorQuery == null) {
            return false;
        }
        cursorQuery.close();
        return LauncherAppState.getInstanceNoCreate() != null;
    }

    private String getUserSelectionArg() {
        return "profileId=" + UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle());
    }

    class InvalidBackupException extends IOException {
        private static final long serialVersionUID = 8931456637211665082L;

        InvalidBackupException(Throwable cause) {
            super(cause);
        }

        InvalidBackupException(String reason) {
            super(reason);
        }
    }

    private class ItemTypeMatcher {
        private final ArrayList<Intent> mIntents;

        ItemTypeMatcher(int xml_res) {
            this.mIntents = xml_res == 0 ? new ArrayList<>() : parseIntents(xml_res);
        }

        private ArrayList<Intent> parseIntents(int xml_res) {
            ArrayList<Intent> arrayList = new ArrayList<>();
            XmlResourceParser xml = LauncherBackupHelper.this.mContext.getResources().getXml(xml_res);
            try {
                try {
                    DefaultLayoutParser.beginDocument(xml, "resolve");
                    int depth = xml.getDepth();
                    while (true) {
                        int next = xml.next();
                        if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                            break;
                        }
                        if (next == 2 && "favorite".equals(xml.getName())) {
                            arrayList.add(Intent.parseUri(DefaultLayoutParser.getAttributeValue(xml, "uri"), 0));
                        }
                    }
                } catch (IOException | URISyntaxException | XmlPullParserException e) {
                    Log.e(LauncherBackupHelper.TAG, "Unable to parse " + xml_res, e);
                }
                return arrayList;
            } finally {
                xml.close();
            }
        }

        public boolean matches(ActivityInfo activity, PackageManager pm) {
            for (Intent intent : this.mIntents) {
                intent.setPackage(activity.packageName);
                ResolveInfo resolveInfoResolveActivity = pm.resolveActivity(intent, 0);
                if (resolveInfoResolveActivity != null && (resolveInfoResolveActivity.activityInfo.name.equals(activity.name) || resolveInfoResolveActivity.activityInfo.name.equals(activity.targetActivity))) {
                    return true;
                }
            }
            return false;
        }
    }
}
