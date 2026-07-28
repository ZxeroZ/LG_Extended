package com.lge.launcher3.sortappsby;

import com.lge.launcher3.R;
import com.lge.launcher3.util.AppNameComparator;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.UserUtils;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class SortAppsByConst {
    public static final Comparator<SortAppsByItemInfo> NAME_COMPARATOR = new Comparator<SortAppsByItemInfo>() { // from class: com.lge.launcher3.sortappsby.SortAppsByConst.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(SortAppsByItemInfo lhs, SortAppsByItemInfo rhs) {
            return AppNameComparator.compare(lhs.getName(), rhs.getName());
        }
    };
    public static final Comparator<SortAppsByItemInfo> INSTALL_TIME_COMPARATOR = new Comparator<SortAppsByItemInfo>() { // from class: com.lge.launcher3.sortappsby.SortAppsByConst.2
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(SortAppsByItemInfo lhs, SortAppsByItemInfo rhs) {
            int iCompare = UserUtils.compare(lhs.getUserHandle(), lhs.getUserHandle());
            if (iCompare != 0) {
                return iCompare;
            }
            return Long.compare(lhs.getLauncherActivityInfo() == null ? 0L : lhs.getLauncherActivityInfo().getFirstInstallTime(), rhs.getLauncherActivityInfo() != null ? rhs.getLauncherActivityInfo().getFirstInstallTime() : 0L);
        }
    };
    public static final Comparator<SortAppsByItemInfo> POSITION_COMPARATOR = new Comparator<SortAppsByItemInfo>() { // from class: com.lge.launcher3.sortappsby.SortAppsByConst.3
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(SortAppsByItemInfo lhs, SortAppsByItemInfo rhs) {
            int iCompare = Integer.compare(lhs.mScreenRank, rhs.mScreenRank);
            if (iCompare != 0) {
                return iCompare;
            }
            int iCompare2 = Integer.compare(lhs.mCellY, rhs.mCellY);
            if (iCompare2 != 0) {
                return iCompare2;
            }
            int iCompare3 = Integer.compare(lhs.mCellX, rhs.mCellX);
            if (iCompare3 != 0) {
                return iCompare3;
            }
            return 0;
        }
    };

    public enum SortType {
        NAME(R.string.sortappsby_name, R.string.sortappsby_name_dialog_desc, R.string.sortappsby_name_dialog_except_default_screen_desc, R.string.sortappsby_name_toast_desc),
        DOWNLOAD_DATE(R.string.sortappsby_download_date, R.string.sortappsby_download_date_dialog_desc, R.string.sortappsby_download_date_dialog_except_default_screen_desc, R.string.sortappsby_download_date_toast_desc);

        private int mDialogDescResId;
        private int mDialogTitleResId;
        private int mToastDescResId;

        SortType(int dialogTitle, int dialogDesc, int dialogDescExceptDefault, int toastDesc) {
            this.mDialogTitleResId = -1;
            this.mDialogDescResId = -1;
            this.mToastDescResId = -1;
            this.mDialogTitleResId = dialogTitle;
            this.mDialogDescResId = LGHomeFeature.Config.FEATURE_SORT_APPS_EXCEPT_DEFAULT_SCREEN.getValue() ? dialogDescExceptDefault : dialogDesc;
            this.mToastDescResId = toastDesc;
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
