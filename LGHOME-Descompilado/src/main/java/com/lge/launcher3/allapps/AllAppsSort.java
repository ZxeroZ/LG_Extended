package com.lge.launcher3.allapps;

import com.android.launcher3.model.data.AppInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.util.AppNameComparator;
import com.lge.launcher3.util.UserUtils;
import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsSort {
    public static final Comparator<AppInfo> NAME_COMPARATOR = new Comparator<AppInfo>() { // from class: com.lge.launcher3.allapps.AllAppsSort.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(AppInfo a, AppInfo b) {
            if (a.title == null || b.title == null) {
                return 0;
            }
            return AppNameComparator.compare(a.title.toString(), b.title.toString());
        }
    };
    public static final Comparator<AllAppsItemInfo> FOLDER_NAME_COMPARATOR = new Comparator<AllAppsItemInfo>() { // from class: com.lge.launcher3.allapps.AllAppsSort.2
        private final Collator mCollator = Collator.getInstance();

        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(AllAppsItemInfo a, AllAppsItemInfo b) {
            if (a.mFolderInfo == null || b.mFolderInfo == null) {
                return 0;
            }
            return this.mCollator.compare(a.mFolderInfo.title.toString(), b.mFolderInfo.title.toString());
        }
    };
    public static final Comparator<AllAppsItemInfo> INSTALL_TIME_COMPARATOR = new Comparator<AllAppsItemInfo>() { // from class: com.lge.launcher3.allapps.AllAppsSort.3
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(AllAppsItemInfo a, AllAppsItemInfo b) {
            int iCompare = UserUtils.compare(a.user, b.user);
            return iCompare != 0 ? iCompare : Long.compare(a.firstInstallTime, b.firstInstallTime);
        }
    };
    public static final Comparator<AllAppsItemInfo> POSITION_COMPARATOR = new Comparator<AllAppsItemInfo>() { // from class: com.lge.launcher3.allapps.AllAppsSort.4
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(AllAppsItemInfo a, AllAppsItemInfo b) {
            int iCompare = Long.compare(a.screenId, b.screenId);
            if (iCompare != 0) {
                return iCompare;
            }
            int iCompare2 = Integer.compare(a.cellY, b.cellY);
            if (iCompare2 != 0) {
                return iCompare2;
            }
            int iCompare3 = Integer.compare(a.cellX, b.cellX);
            if (iCompare3 != 0) {
                return iCompare3;
            }
            return 0;
        }
    };

    public enum SortType {
        NAME(R.string.allapps_sortappsby_name, LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") ? R.string.allapps_sortappsby_name_dialog_desc_vzw : R.string.allapps_sortappsby_name_dialog_desc, R.string.sortappsby_name_toast_desc),
        DOWNLOAD_DATE(R.string.allapps_sortappsby_download_date, LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") ? R.string.allapps_sortappsby_download_date_dialog_desc_vzw : R.string.allapps_sortappsby_download_date_dialog_desc, R.string.allapps_sortappsby_download_date_toast_desc);

        private int mDialogDescResId;
        private int mDialogTitleResId;
        private int mToastDescResId;

        SortType(int dialogTitleResId, int dialogDescResId, int toastDescResId) {
            this.mDialogTitleResId = -1;
            this.mDialogDescResId = -1;
            this.mToastDescResId = -1;
            this.mDialogTitleResId = dialogTitleResId;
            this.mDialogDescResId = dialogDescResId;
            this.mToastDescResId = toastDescResId;
        }

        public int getDialogTitle() {
            return this.mDialogTitleResId;
        }

        public int getDialogDesc() {
            return this.mDialogDescResId;
        }

        public int getToastDesc() {
            return this.mToastDescResId;
        }
    }
}
