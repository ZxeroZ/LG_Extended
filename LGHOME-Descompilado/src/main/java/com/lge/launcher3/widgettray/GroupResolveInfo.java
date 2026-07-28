package com.lge.launcher3.widgettray;

import android.content.Context;
import android.content.pm.ResolveInfo;
import com.lge.launcher3.util.PackageUtils;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GroupResolveInfo extends ResolveInfo {
    private List<Object> mGroupList;
    private String mLabel;

    public GroupResolveInfo(Context context, List<Object> groupList) {
        this.mGroupList = groupList;
        if (groupList == null || groupList.size() <= 0) {
            return;
        }
        this.activityInfo = ((ResolveInfo) this.mGroupList.get(0)).activityInfo;
        this.mLabel = PackageUtils.getApplicationLabel(context, this.activityInfo.packageName);
    }

    public String getLabel() {
        return this.mLabel;
    }

    public List<Object> getGroupList() {
        return this.mGroupList;
    }
}
