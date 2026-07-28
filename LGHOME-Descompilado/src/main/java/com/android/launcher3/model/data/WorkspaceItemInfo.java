package com.android.launcher3.model.data;

import android.app.Person;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.text.TextUtils;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.uioverrides.ApiWrapper;
import com.android.launcher3.util.ContentWriter;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

/* JADX INFO: loaded from: classes.dex */
public class WorkspaceItemInfo extends ItemInfoWithIcon {
    public static final int DEFAULT = 0;
    public static final int FLAG_AUTOINSTALL_ICON = 2;
    public static final int FLAG_INSTALL_SESSION_ACTIVE = 4;
    public static final int FLAG_RESTORED_ICON = 1;
    public static final int FLAG_RESTORE_STARTED = 8;
    public static final int FLAG_SUPPORTS_WEB_UI = 16;
    public CharSequence disabledMessage;
    public Intent.ShortcutIconResource iconResource;
    public Intent intent;
    private int mInstallProgress;
    private String[] personKeys;
    public int status;

    public WorkspaceItemInfo() {
        this.personKeys = Utilities.EMPTY_STRING_ARRAY;
        this.itemType = 1;
    }

    public WorkspaceItemInfo(WorkspaceItemInfo info) {
        super(info);
        this.personKeys = Utilities.EMPTY_STRING_ARRAY;
        this.title = info.title;
        this.intent = new Intent(info.intent);
        this.iconResource = info.iconResource;
        this.status = info.status;
        this.mInstallProgress = info.mInstallProgress;
        this.personKeys = (String[]) info.personKeys.clone();
    }

    public WorkspaceItemInfo(AppInfo info) {
        super(info);
        this.personKeys = Utilities.EMPTY_STRING_ARRAY;
        this.title = Utilities.trim(info.title);
        this.intent = new Intent(info.getIntent());
    }

    public WorkspaceItemInfo(ShortcutInfo shortcutInfo, Context context) {
        this.personKeys = Utilities.EMPTY_STRING_ARRAY;
        this.user = shortcutInfo.getUserHandle();
        this.itemType = 6;
        updateFromDeepShortcutInfo(shortcutInfo, context);
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToDatabase(ContentWriter writer) {
        super.onAddToDatabase(writer);
        writer.put("title", this.title).put(LauncherSettings.BaseLauncherColumns.INTENT, getIntent()).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.status));
        Intent.ShortcutIconResource shortcutIconResource = this.iconResource;
        if (shortcutIconResource != null) {
            writer.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, shortcutIconResource.packageName).put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.iconResource.resourceName);
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public Intent getIntent() {
        return this.intent;
    }

    public boolean hasStatusFlag(int flag) {
        return (flag & this.status) != 0;
    }

    public final boolean isPromise() {
        return hasStatusFlag(3);
    }

    public boolean hasPromiseIconUi() {
        return isPromise() && !hasStatusFlag(16);
    }

    public int getInstallProgress() {
        return this.mInstallProgress;
    }

    public void setInstallProgress(int progress) {
        this.mInstallProgress = progress;
        this.status |= 4;
    }

    public void updateFromDeepShortcutInfo(ShortcutInfo shortcutInfo, Context context) {
        this.intent = ShortcutKey.makeIntent(shortcutInfo);
        this.title = shortcutInfo.getShortLabel();
        CharSequence longLabel = shortcutInfo.getLongLabel();
        if (TextUtils.isEmpty(longLabel)) {
            longLabel = shortcutInfo.getShortLabel();
        }
        this.contentDescription = context.getPackageManager().getUserBadgedLabel(longLabel, this.user);
        if (shortcutInfo.isEnabled()) {
            this.runtimeStatusFlags &= -17;
        } else {
            this.runtimeStatusFlags |= 16;
        }
        this.disabledMessage = shortcutInfo.getDisabledMessage();
        Person[] persons = ApiWrapper.getPersons(shortcutInfo);
        this.personKeys = persons.length == 0 ? Utilities.EMPTY_STRING_ARRAY : (String[]) Arrays.stream(persons).map(new Function() { // from class: com.android.launcher3.model.data.-$$Lambda$WorkspaceItemInfo$NLQcSJEFKBLqD04iK_mFvrZ6-l0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Person) obj).getKey();
            }
        }).sorted().toArray(new IntFunction() { // from class: com.android.launcher3.model.data.-$$Lambda$WorkspaceItemInfo$eh4-QKIvhsXJbkoPvNfKYpNPADI
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return WorkspaceItemInfo.lambda$updateFromDeepShortcutInfo$0(i);
            }
        });
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: NEW_ARRAY (r0v0 int A[IMMUTABLE_TYPE]) (LINE:200) type: java.lang.String[] */
    static /* synthetic */ String[] lambda$updateFromDeepShortcutInfo$0(int i) {
        return new String[i];
    }

    public String getDeepShortcutId() {
        if (this.itemType == 6) {
            return getIntent().getStringExtra("shortcut_id");
        }
        return null;
    }

    public String[] getPersonKeys() {
        return this.personKeys;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public ComponentName getTargetComponent() {
        ComponentName targetComponent = super.getTargetComponent();
        if (targetComponent != null) {
            return targetComponent;
        }
        if (this.itemType != 1 && !hasStatusFlag(19)) {
            return targetComponent;
        }
        String str = this.intent.getPackage();
        if (str == null) {
            return null;
        }
        return new ComponentName(str, ".");
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public ItemInfoWithIcon m211clone() {
        return new WorkspaceItemInfo(this);
    }
}
