package com.lge.launcher3.widgettray;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetCell;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsViewPagerAdapter extends PagerAdapter {
    private static final boolean DEBUG = false;
    private static final int GROUP_WIDGET_SIZE_HOLDER = 2131296547;
    private static final int MAXPREVIEW_SIZE = 4;
    private static final String TAG = "WidgetsViewPagerAdapter";
    private int mCol;
    private View.OnClickListener mIconClickListener;
    private View.OnLongClickListener mIconLongClickListener;
    private LayoutInflater mInflater;
    private boolean mIsRtL;
    private Launcher mLauncher;
    private final PowerManager mPowerManager;
    private int mRow;
    private WidgetsUninstallButton mUninstallBtn;
    private WidgetPreviewLoader mWidgetPreviewLoader;
    private WidgetsModelExtension mWidgetsModel;
    protected boolean mNeedRefresh = false;
    protected ArrayList<LGSearchedWidgetsInfo> mSearchWidget = new ArrayList<>();

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View pager, Object obj) {
        return pager == obj;
    }

    public ArrayList<LGSearchedWidgetsInfo> getSearchWidget() {
        return this.mSearchWidget;
    }

    public void setSearchWidget(ArrayList<LGSearchedWidgetsInfo> searchWidget) {
        this.mSearchWidget = searchWidget;
    }

    private boolean isSearchState() {
        ArrayList<LGSearchedWidgetsInfo> arrayList;
        return LGHomeFeature.Config.FEATURE_USE_WIDGET_SEARCH.getValue() && (arrayList = this.mSearchWidget) != null && arrayList.size() > 0;
    }

    class Data {
        ComponentName componentName;
        int size;
        String sourceDirPath;
        int spanX;
        int spanY;

        Data() {
        }
    }

    public WidgetsViewPagerAdapter(Context c, View.OnClickListener iconClickListener, View.OnLongClickListener iconLongClickListener, Launcher launcher) {
        this.mIsRtL = false;
        this.mInflater = LayoutInflater.from(c);
        this.mLauncher = launcher;
        this.mIconClickListener = iconClickListener;
        this.mIconLongClickListener = iconLongClickListener;
        Resources resources = launcher.getResources();
        this.mRow = getRow();
        this.mCol = getCol();
        this.mIsRtL = Utilities.isRtl(resources);
        this.mPowerManager = (PowerManager) c.getApplicationContext().getSystemService("power");
    }

    public void setWidgetsModel(WidgetsModel w) {
        this.mWidgetsModel = (WidgetsModelExtension) w;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        double dCeil;
        if (isSearchState()) {
            dCeil = Math.ceil(((double) this.mSearchWidget.size()) / ((double) (getRow() * getCol())));
        } else {
            WidgetsModelExtension widgetsModelExtension = this.mWidgetsModel;
            if (widgetsModelExtension == null || widgetsModelExtension.mWidgetsList == null) {
                return 0;
            }
            dCeil = Math.ceil(((double) this.mWidgetsModel.mWidgetsList.size()) / ((double) (getRow() * getCol())));
        }
        return (int) dCeil;
    }

    private boolean isAvailableUninstallItems(int flags, UninstallBadgeUtils.UninstallType uninstallType) {
        return (flags & 1) != 0 && (uninstallType == UninstallBadgeUtils.UninstallType.DISABLE || uninstallType == UninstallBadgeUtils.UninstallType.UNINSTALL);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v10, resolved type: com.lge.launcher3.badge.uninstall.UninstallBadgeUtils$UninstallType */
    /* JADX DEBUG: Multi-variable search result rejected for r2v11, resolved type: com.lge.launcher3.badge.uninstall.UninstallBadgeUtils$UninstallType */
    /* JADX DEBUG: Multi-variable search result rejected for r2v33, resolved type: com.lge.launcher3.badge.uninstall.UninstallBadgeUtils$UninstallType */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00fc  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0111  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x014e  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x017e  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x01af  */
    @Override // androidx.viewpager.widget.PagerAdapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object instantiateItem(android.view.View r17, int r18) {
        /*
            r16 = this;
            r6 = r16
            r0 = r18
            int r7 = r6.getPositionAsLayoutDirection(r0)
            int r0 = r16.getRow()
            r6.mRow = r0
            int r0 = r16.getCol()
            r6.mCol = r0
            int r1 = r6.mRow
            int r8 = r1 * r0
            android.view.LayoutInflater r0 = r6.mInflater
            r1 = 2131493140(0x7f0c0114, float:1.8609752E38)
            r9 = 0
            android.view.View r0 = r0.inflate(r1, r9)
            r10 = r0
            android.widget.GridLayout r10 = (android.widget.GridLayout) r10
            java.lang.Integer r0 = java.lang.Integer.valueOf(r7)
            r10.setTag(r0)
            int r0 = r6.mRow
            int r1 = r6.mCol
            r6.initLayout(r0, r1, r10)
            r11 = 1
            r10.setImportantForAccessibility(r11)
            java.lang.String r0 = com.lge.launcher3.widgettray.WidgetsViewPagerAdapter.TAG
            com.lge.launcher3.widgettray.WidgetsModelExtension r1 = r6.mWidgetsModel
            java.util.HashMap<com.android.launcher3.model.PackageItemInfo, java.util.ArrayList<java.lang.Object>> r1 = r1.mWidgetsList
            int r1 = r1.size()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "instantiateItem [pos= "
            r2.append(r3)
            r2.append(r7)
            java.lang.String r3 = " WidgetSize = "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r1 = " ] : "
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            com.lge.launcher3.util.LGLog.d(r0, r1)
            com.android.launcher3.WidgetPreviewLoader r0 = r16.getWidgetPreviewLoader()
            if (r0 != 0) goto L69
            return r9
        L69:
            r12 = 0
            r0 = r9
            r13 = r12
        L6c:
            if (r13 >= r8) goto L223
            int r1 = r7 * r8
            int r1 = r1 + r13
            boolean r2 = r16.isSearchState()
            if (r2 == 0) goto Laf
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r2 = r6.mSearchWidget
            int r2 = r2.size()
            if (r1 < r2) goto L81
            goto L223
        L81:
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r2 = r6.mSearchWidget
            java.lang.Object r2 = r2.get(r1)
            com.lge.launcher3.widgettray.LGSearchedWidgetsInfo r2 = (com.lge.launcher3.widgettray.LGSearchedWidgetsInfo) r2
            android.appwidget.AppWidgetProviderInfo r2 = r2.appWidgetProvideInfo
            if (r2 == 0) goto L98
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r0 = r6.mSearchWidget
            java.lang.Object r0 = r0.get(r1)
            com.lge.launcher3.widgettray.LGSearchedWidgetsInfo r0 = (com.lge.launcher3.widgettray.LGSearchedWidgetsInfo) r0
            android.appwidget.AppWidgetProviderInfo r0 = r0.appWidgetProvideInfo
            goto Lc9
        L98:
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r2 = r6.mSearchWidget
            java.lang.Object r2 = r2.get(r1)
            com.lge.launcher3.widgettray.LGSearchedWidgetsInfo r2 = (com.lge.launcher3.widgettray.LGSearchedWidgetsInfo) r2
            android.content.pm.ResolveInfo r2 = r2.resolveInfo
            if (r2 == 0) goto Lc9
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r0 = r6.mSearchWidget
            java.lang.Object r0 = r0.get(r1)
            com.lge.launcher3.widgettray.LGSearchedWidgetsInfo r0 = (com.lge.launcher3.widgettray.LGSearchedWidgetsInfo) r0
            android.content.pm.ResolveInfo r0 = r0.resolveInfo
            goto Lc9
        Laf:
            com.lge.launcher3.widgettray.WidgetsModelExtension r2 = r6.mWidgetsModel
            int r2 = r2.getPackageSize()
            if (r1 < r2) goto Lb9
            goto L223
        Lb9:
            com.lge.launcher3.widgettray.WidgetsModelExtension r2 = r6.mWidgetsModel
            java.util.List r2 = r2.getSortedWidgets(r1)
            int r3 = r2.size()
            if (r3 != r11) goto Lcb
            java.lang.Object r0 = r2.get(r12)
        Lc9:
            r14 = r0
            goto Lf1
        Lcb:
            int r3 = r2.size()
            if (r3 <= r11) goto L21e
            java.lang.Object r3 = r2.get(r12)
            boolean r3 = r3 instanceof com.android.launcher3.LauncherAppWidgetProviderInfo
            if (r3 == 0) goto Le1
            com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo r0 = new com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo
            com.android.launcher3.Launcher r3 = r6.mLauncher
            r0.<init>(r3, r2)
            goto Lc9
        Le1:
            java.lang.Object r3 = r2.get(r12)
            boolean r3 = r3 instanceof android.content.pm.ResolveInfo
            if (r3 == 0) goto Lc9
            com.lge.launcher3.widgettray.GroupResolveInfo r0 = new com.lge.launcher3.widgettray.GroupResolveInfo
            com.android.launcher3.Launcher r3 = r6.mLauncher
            r0.<init>(r3, r2)
            goto Lc9
        Lf1:
            android.view.View r0 = r10.getChildAt(r13)
            r15 = r0
            com.lge.launcher3.widgettray.LGWidgetCell r15 = (com.lge.launcher3.widgettray.LGWidgetCell) r15
            boolean r0 = r14 instanceof com.android.launcher3.LauncherAppWidgetProviderInfo
            if (r0 == 0) goto L111
            r0 = r14
            com.android.launcher3.LauncherAppWidgetProviderInfo r0 = (com.android.launcher3.LauncherAppWidgetProviderInfo) r0
            com.android.launcher3.widget.PendingAddWidgetInfo r2 = new com.android.launcher3.widget.PendingAddWidgetInfo
            com.android.launcher3.Launcher r3 = r6.mLauncher
            r2.<init>(r3, r0, r9)
            r15.setTag(r2)
            com.android.launcher3.WidgetPreviewLoader r3 = r6.mWidgetPreviewLoader
            r15.applyFromAppWidgetProviderInfo(r0, r3)
            int r0 = r2.flags
            goto L133
        L111:
            boolean r0 = r14 instanceof android.content.pm.ResolveInfo
            if (r0 == 0) goto L132
            r0 = r14
            android.content.pm.ResolveInfo r0 = (android.content.pm.ResolveInfo) r0
            com.android.launcher3.widget.PendingAddShortcutInfo r2 = new com.android.launcher3.widget.PendingAddShortcutInfo
            android.content.pm.ActivityInfo r3 = r0.activityInfo
            com.android.launcher3.Launcher r4 = r6.mLauncher
            r2.<init>(r3, r4)
            r15.setTag(r2)
            com.android.launcher3.Launcher r3 = r6.mLauncher
            android.content.pm.PackageManager r3 = r3.getPackageManager()
            com.android.launcher3.WidgetPreviewLoader r4 = r6.mWidgetPreviewLoader
            r15.applyFromResolveInfo(r3, r0, r4)
            int r0 = r2.flags
            goto L133
        L132:
            r0 = r12
        L133:
            java.lang.Object r2 = r15.getTag()
            boolean r2 = r2 instanceof com.android.launcher3.model.data.ItemInfo
            if (r2 == 0) goto L14e
            android.content.Context r2 = r17.getContext()
            com.lge.launcher3.uninstallmode.UninstallModeManager r2 = com.lge.launcher3.uninstallmode.UninstallModeManager.getInstance(r2)
            java.lang.Object r3 = r15.getTag()
            com.android.launcher3.model.data.ItemInfo r3 = (com.android.launcher3.model.data.ItemInfo) r3
            com.lge.launcher3.badge.uninstall.UninstallBadgeUtils$UninstallType r2 = r2.getUninstallType(r3)
            goto L14f
        L14e:
            r2 = r9
        L14f:
            boolean r3 = r6.isAvailableUninstallItems(r0, r2)
            android.view.View$OnClickListener r0 = r6.mIconClickListener
            r15.setOnClickListener(r0)
            r0 = 2131296984(0x7f0902d8, float:1.82119E38)
            android.view.View r0 = r15.findViewById(r0)
            android.widget.ImageView r0 = (android.widget.ImageView) r0
            r4 = 2131296973(0x7f0902cd, float:1.8211878E38)
            android.view.View r4 = r15.findViewById(r4)
            r5 = r4
            com.lge.launcher3.widgettray.WidgetsImageView r5 = (com.lge.launcher3.widgettray.WidgetsImageView) r5
            com.android.launcher3.Launcher r4 = r6.mLauncher
            r9 = 2131296983(0x7f0902d7, float:1.8211898E38)
            android.view.View r4 = r4.findViewById(r9)
            com.lge.launcher3.widgettray.WidgetsUninstallButton r4 = (com.lge.launcher3.widgettray.WidgetsUninstallButton) r4
            r6.mUninstallBtn = r4
            boolean r4 = r16.isSearchState()
            if (r4 == 0) goto L1af
            android.view.View$OnLongClickListener r0 = r6.mIconLongClickListener
            r15.setOnLongClickListener(r0)
            r15.ensurePreview()
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r0 = r6.mSearchWidget
            if (r0 == 0) goto L1a7
            java.lang.Object r0 = r0.get(r1)
            com.lge.launcher3.widgettray.LGSearchedWidgetsInfo r0 = (com.lge.launcher3.widgettray.LGSearchedWidgetsInfo) r0
            java.lang.String r0 = r0.searchPrefix
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r2 = r6.mSearchWidget
            java.lang.Object r2 = r2.get(r1)
            com.lge.launcher3.widgettray.LGSearchedWidgetsInfo r2 = (com.lge.launcher3.widgettray.LGSearchedWidgetsInfo) r2
            java.lang.String r2 = r2.searchBody
            java.util.ArrayList<com.lge.launcher3.widgettray.LGSearchedWidgetsInfo> r3 = r6.mSearchWidget
            java.lang.Object r1 = r3.get(r1)
            com.lge.launcher3.widgettray.LGSearchedWidgetsInfo r1 = (com.lge.launcher3.widgettray.LGSearchedWidgetsInfo) r1
            java.lang.String r1 = r1.searchPostfix
            goto L1aa
        L1a7:
            r0 = 0
            r1 = 0
            r2 = 0
        L1aa:
            r15.applyFromSearchedInfo(r0, r2, r1)
            goto L21a
        L1af:
            com.lge.launcher3.widgettray.WidgetsUninstallButton r1 = r6.mUninstallBtn
            if (r1 == 0) goto L1c5
            boolean r1 = r1.isUninstallMode()
            if (r1 == 0) goto L1c5
            com.lge.launcher3.widgettray.WidgetsUninstallButton r1 = r6.mUninstallBtn
            boolean r1 = r1.isUninstallMode()
            r5.setUninstallBadge(r0, r1, r3, r2)
            r5.invalidate()
        L1c5:
            boolean r0 = com.lge.launcher3.util.Utilities.isLGUI8_0()
            if (r0 == 0) goto L1d8
            r0 = 2131296963(0x7f0902c3, float:1.8211858E38)
            android.view.View r0 = r15.findViewById(r0)
            r1 = 2131231237(0x7f080205, float:1.807855E38)
            r0.setBackgroundResource(r1)
        L1d8:
            boolean r0 = checkGroupWidget(r14)
            if (r0 == 0) goto L203
            r6.makeGroupItemPreview(r14, r15)
            r0 = 8
            r5.setVisibility(r0)
            r0 = 2131296969(0x7f0902c9, float:1.821187E38)
            android.view.View r0 = r15.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r0.setVisibility(r12)
            com.lge.launcher3.widgettray.WidgetsUninstallButton r0 = r6.mUninstallBtn
            if (r0 == 0) goto L1fe
            boolean r0 = r0.isUninstallMode()
            if (r0 == 0) goto L1fe
            r0 = r11
            goto L1ff
        L1fe:
            r0 = r12
        L1ff:
            r6.setGroupView(r15, r11, r0, r3)
            goto L20b
        L203:
            android.view.View$OnLongClickListener r0 = r6.mIconLongClickListener
            r15.setOnLongClickListener(r0)
            r15.ensurePreview()
        L20b:
            com.lge.launcher3.widgettray.WidgetsUninstallButton r0 = r6.mUninstallBtn
            if (r0 == 0) goto L21a
            boolean r2 = r0.isUninstallMode()
            r0 = r16
            r1 = r15
            r4 = r14
            r0.uninstallBadgeAnimation(r1, r2, r3, r4, r5)
        L21a:
            r15.setVisibility(r12)
            r0 = r14
        L21e:
            int r13 = r13 + 1
            r9 = 0
            goto L6c
        L223:
            r0 = r17
            androidx.viewpager.widget.ViewPager r0 = (androidx.viewpager.widget.ViewPager) r0
            r0.addView(r10, r12)
            java.lang.String r1 = com.lge.launcher3.widgettray.WidgetsViewPagerAdapter.TAG
            int r0 = r0.getChildCount()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "((ViewPager)pager) : "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            com.lge.launcher3.util.LGLog.d(r1, r0)
            r6.mNeedRefresh = r12
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.widgettray.WidgetsViewPagerAdapter.instantiateItem(android.view.View, int):java.lang.Object");
    }

    private void initLayout(int row, int col, GridLayout glList) {
        WidgetCell widgetCell;
        int i = row * col;
        glList.setColumnCount(col);
        glList.setRowCount(row);
        for (int i2 = 0; i2 < i; i2++) {
            if (com.lge.launcher3.util.Utilities.isLGUI8_0()) {
                widgetCell = (WidgetCell) this.mInflater.inflate(R.layout.widget_cell_ui8, (ViewGroup) glList, false);
                LinearLayout linearLayout = (LinearLayout) widgetCell.findViewById(R.id.widget_group_preview_top_layout);
                LinearLayout linearLayout2 = (LinearLayout) widgetCell.findViewById(R.id.widget_group_preview_bottom_layout);
                GroupItemPreview groupItemPreview = (GroupItemPreview) linearLayout.findViewById(R.id.widget_preview0);
                GroupItemPreview groupItemPreview2 = (GroupItemPreview) linearLayout.findViewById(R.id.widget_preview1);
                GroupItemPreview groupItemPreview3 = (GroupItemPreview) linearLayout2.findViewById(R.id.widget_preview2);
                GroupItemPreview groupItemPreview4 = (GroupItemPreview) linearLayout2.findViewById(R.id.widget_preview3);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -1);
                if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
                    layoutParams.width = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.group_widget_preview_width);
                } else {
                    layoutParams.width = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.group_widget_preview_width);
                    layoutParams.weight = 1.0f;
                    layoutParams.height = -1;
                }
                int dimensionPixelSize = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.group_widget_item_margin);
                layoutParams.setMargins(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
                groupItemPreview.setLayoutParams(layoutParams);
                groupItemPreview2.setLayoutParams(layoutParams);
                groupItemPreview3.setLayoutParams(layoutParams);
                groupItemPreview4.setLayoutParams(layoutParams);
            } else {
                widgetCell = (WidgetCell) this.mInflater.inflate(R.layout.widget_cell, (ViewGroup) glList, false);
            }
            glList.addView(widgetCell);
        }
    }

    public static boolean checkGroupWidget(Object itemInfo) {
        return (itemInfo instanceof GroupLauncherAppWidgetProviderInfo) || (itemInfo instanceof GroupResolveInfo);
    }

    private void makeGroupItemPreview(Object info, LGWidgetCell widgetCell) {
        List<Object> groupList;
        String label;
        if (info instanceof GroupLauncherAppWidgetProviderInfo) {
            GroupLauncherAppWidgetProviderInfo groupLauncherAppWidgetProviderInfo = (GroupLauncherAppWidgetProviderInfo) info;
            groupList = groupLauncherAppWidgetProviderInfo.getGroupList();
            label = groupLauncherAppWidgetProviderInfo.getLabel();
        } else if (info instanceof GroupResolveInfo) {
            GroupResolveInfo groupResolveInfo = (GroupResolveInfo) info;
            groupList = groupResolveInfo.getGroupList();
            label = groupResolveInfo.getLabel();
        } else {
            groupList = null;
            label = "";
        }
        int size = groupList != null ? groupList.size() : 0;
        GroupWidgetSizeHolder groupWidgetSizeHolder = new GroupWidgetSizeHolder();
        groupWidgetSizeHolder.size = size;
        widgetCell.setTag(R.id.group_widget_size_holder, groupWidgetSizeHolder);
        widgetCell.setWidgetName(label);
        widgetCell.setWidgetDims("");
        if (size > 4) {
            size = 4;
        }
        for (int i = 0; i < size; i++) {
            widgetCell.ensureGroupItemPreview(i, groupList.get(i));
        }
    }

    class GroupWidgetSizeHolder {
        private int size;

        GroupWidgetSizeHolder() {
        }
    }

    public void setUninstallBadge(ViewPager viewpager, boolean turnOn) {
        int i;
        int i2 = this.mRow * this.mCol;
        long jCurrentTimeMillis = UninstallModeManager.DEBUG_RESTRICT_PACKAGE ? System.currentTimeMillis() : 0L;
        for (int i3 = 0; i3 < viewpager.getChildCount(); i3++) {
            GridLayout gridLayout = (GridLayout) viewpager.getChildAt(i3);
            for (int i4 = 0; i4 < i2; i4++) {
                LGWidgetCell lGWidgetCell = (LGWidgetCell) gridLayout.getChildAt(i4);
                if (lGWidgetCell == null) {
                    break;
                }
                Object tag = lGWidgetCell.getTag();
                if (tag instanceof PendingAddWidgetInfo) {
                    i = ((PendingAddWidgetInfo) tag).flags;
                } else {
                    i = tag instanceof PendingAddShortcutInfo ? ((PendingAddShortcutInfo) tag).flags : 0;
                }
                UninstallBadgeUtils.UninstallType uninstallType = tag instanceof ItemInfo ? UninstallModeManager.getInstance(viewpager.getContext()).getUninstallType((ItemInfo) tag) : null;
                boolean zIsAvailableUninstallItems = isAvailableUninstallItems(i, uninstallType);
                Object obj = lGWidgetCell.mInfo;
                WidgetsImageView widgetsImageView = (WidgetsImageView) lGWidgetCell.findViewById(R.id.widget_preview);
                if (widgetsImageView != null) {
                    widgetsImageView.setUninstallBadge((ImageView) lGWidgetCell.findViewById(R.id.widget_uninstall), this.mUninstallBtn.isUninstallMode(), zIsAvailableUninstallItems, uninstallType);
                    widgetsImageView.invalidate();
                    startUpdate((ViewGroup) lGWidgetCell);
                    if (checkGroupWidget(obj)) {
                        setGroupView(lGWidgetCell, true, this.mUninstallBtn.isUninstallMode(), zIsAvailableUninstallItems);
                    }
                    uninstallBadgeAnimation(lGWidgetCell, this.mUninstallBtn.isUninstallMode(), zIsAvailableUninstallItems, obj, widgetsImageView);
                }
            }
        }
        if (UninstallModeManager.DEBUG_RESTRICT_PACKAGE) {
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            LGLog.d(TAG, "setUninstallBadge() : finish. time = " + jCurrentTimeMillis2);
        }
    }

    public void setGroupView(LGWidgetCell widgetCell, boolean isGroupWidget, boolean isUninstallMode, boolean isDownloaded) {
        ImageView imageView = (ImageView) widgetCell.findViewById(R.id.widget_uninstall);
        ImageView imageView2 = (ImageView) widgetCell.findViewById(R.id.widget_group_btn);
        if (imageView2 != null) {
            if (isGroupWidget) {
                if (this.mIsRtL) {
                    imageView2.setScaleX(-1.0f);
                }
                imageView2.setVisibility(0);
                imageView.setVisibility(4);
                if (!isUninstallMode) {
                    imageView2.setImageResource(R.drawable.btn_homescreen_enlarged_ink_normal);
                    return;
                } else if (isDownloaded) {
                    imageView2.setImageResource(R.drawable.btn_homescreen_close_normal);
                    return;
                } else {
                    imageView2.setImageResource(R.drawable.btn_homescreen_enlarged_ink_disable);
                    return;
                }
            }
            imageView2.setVisibility(8);
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object object) {
        LGLog.d(TAG, "getItemPosition object: " + object);
        return (this.mNeedRefresh || hasDiffrentData(object)) ? -2 : -1;
    }

    private boolean hasDiffrentData(Object object) {
        if (!(object instanceof GridLayout)) {
            return false;
        }
        GridLayout gridLayout = (GridLayout) object;
        int iIntValue = ((Integer) gridLayout.getTag()).intValue();
        if (iIntValue == -1) {
            return false;
        }
        int row = getRow() * getCol();
        for (int i = 0; i < row; i++) {
            Data data = new Data();
            Data data2 = new Data();
            WidgetCell widgetCell = (WidgetCell) gridLayout.getChildAt(i);
            if (widgetCell != null) {
                Object tag = widgetCell.getTag();
                if (checkGroupWidget(widgetCell.mInfo)) {
                    data.size = ((GroupWidgetSizeHolder) widgetCell.getTag(R.id.group_widget_size_holder)).size;
                } else {
                    data.size = 1;
                }
                if (tag instanceof PendingAddItemInfo) {
                    PendingAddItemInfo pendingAddItemInfo = (PendingAddItemInfo) tag;
                    data.componentName = pendingAddItemInfo.componentName;
                    data.spanX = pendingAddItemInfo.spanX;
                    data.spanY = pendingAddItemInfo.spanY;
                    if (tag instanceof PendingAddWidgetInfo) {
                        PendingAddWidgetInfo pendingAddWidgetInfo = (PendingAddWidgetInfo) tag;
                        if (pendingAddWidgetInfo.info != null && pendingAddWidgetInfo.info.providerInfo != null && pendingAddWidgetInfo.info.providerInfo.applicationInfo != null) {
                            data.sourceDirPath = pendingAddWidgetInfo.info.providerInfo.applicationInfo.getCodePath();
                        }
                    }
                }
            }
            int i2 = (iIntValue * row) + i;
            if (i2 < this.mWidgetsModel.getPackageSize()) {
                List<Object> sortedWidgets = this.mWidgetsModel.getSortedWidgets(i2);
                Object obj = sortedWidgets.get(0);
                data2.size = sortedWidgets.size();
                if (obj instanceof LauncherAppWidgetProviderInfo) {
                    LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) obj;
                    PendingAddWidgetInfo pendingAddWidgetInfo2 = new PendingAddWidgetInfo(this.mLauncher, launcherAppWidgetProviderInfo, null);
                    data2.componentName = launcherAppWidgetProviderInfo.provider;
                    data2.spanX = launcherAppWidgetProviderInfo.getSpanX(this.mLauncher);
                    data2.spanY = launcherAppWidgetProviderInfo.getSpanY(this.mLauncher);
                    if (pendingAddWidgetInfo2.info != null && pendingAddWidgetInfo2.info.providerInfo != null && pendingAddWidgetInfo2.info.providerInfo.applicationInfo != null) {
                        data2.sourceDirPath = pendingAddWidgetInfo2.info.providerInfo.applicationInfo.getCodePath();
                    }
                    if (widgetCell != null) {
                        widgetCell.setTag(pendingAddWidgetInfo2);
                    }
                } else if (obj instanceof ResolveInfo) {
                    ResolveInfo resolveInfo = (ResolveInfo) obj;
                    PendingAddShortcutInfo pendingAddShortcutInfo = new PendingAddShortcutInfo(resolveInfo.activityInfo, this.mLauncher);
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    if (activityInfo != null) {
                        data2.componentName = new ComponentName(((ComponentInfo) activityInfo).packageName, ((ComponentInfo) activityInfo).name);
                        data2.spanX = 1;
                        data2.spanY = 1;
                    }
                    if (widgetCell != null) {
                        widgetCell.setTag(pendingAddShortcutInfo);
                    }
                }
            }
            if (compareItems(data, data2)) {
                LGLog.d(TAG, "hasDiffrentComponentNameData position:" + iIntValue + " true");
                return true;
            }
        }
        LGLog.i(TAG, "hasDiffrentData position:" + iIntValue + " false");
        return false;
    }

    private boolean compareItems(Data existItem, Data expectedItem) {
        if (existItem.componentName == null && expectedItem.componentName == null) {
            return false;
        }
        if ((existItem.componentName == null && expectedItem.componentName != null) || ((existItem.componentName != null && expectedItem.componentName == null) || !existItem.componentName.equals(expectedItem.componentName))) {
            return true;
        }
        if ((!existItem.componentName.equals(expectedItem.componentName) || (existItem.spanX == expectedItem.spanX && existItem.spanY == expectedItem.spanY)) && existItem.size == expectedItem.size) {
            return (existItem.sourceDirPath == null || expectedItem.sourceDirPath == null || existItem.sourceDirPath.equals(expectedItem.sourceDirPath)) ? false : true;
        }
        return true;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
        GridLayout gridLayout = (GridLayout) view;
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            LGWidgetCell lGWidgetCell = (LGWidgetCell) gridLayout.getChildAt(i);
            for (int i2 = 0; i2 < 4; i2++) {
                GroupItemPreview groupItemPreview = (GroupItemPreview) lGWidgetCell.findViewById(this.mLauncher.getResources().getIdentifier("widget_preview" + i2, "id", this.mLauncher.getPackageName()));
                if (groupItemPreview != null && groupItemPreview.getDrawable() != null) {
                    ((BitmapDrawable) groupItemPreview.getDrawable()).getBitmap().recycle();
                    groupItemPreview.setImageDrawable(null);
                }
            }
        }
    }

    private WidgetPreviewLoader getWidgetPreviewLoader() {
        if (this.mWidgetPreviewLoader == null) {
            this.mWidgetPreviewLoader = LauncherAppState.getInstance(this.mLauncher).getWidgetCache();
        }
        return this.mWidgetPreviewLoader;
    }

    public int getPositionAsLayoutDirection(int position) {
        return !this.mIsRtL ? position : (getCount() - 1) - position;
    }

    public int getFirstPageIndex() {
        if (this.mIsRtL) {
            return getCount() - 1;
        }
        return 0;
    }

    public int getLastPageIndex() {
        if (this.mIsRtL) {
            return 0;
        }
        return getCount() - 1;
    }

    private void uninstallBadgeAnimation(LGWidgetCell widget, boolean isUninstallMode, boolean isDownloaded, Object info, WidgetsImageView preview) {
        ImageView imageView = (ImageView) widget.findViewById(R.id.widget_uninstall);
        ImageView imageView2 = (ImageView) widget.findViewById(R.id.widget_group_btn);
        if (this.mUninstallBtn.isUninstallMode() && isDownloaded && !checkGroupWidget(info) && !this.mIsRtL && !this.mPowerManager.isPowerSaveMode()) {
            imageView.startAnimation(getScaleAnimation(imageView, preview));
        } else {
            imageView.clearAnimation();
        }
        if (imageView2 != null) {
            if (checkGroupWidget(info) && isUninstallMode && isDownloaded && !this.mPowerManager.isPowerSaveMode()) {
                imageView2.startAnimation(getScaleAnimation(imageView2, preview));
            } else {
                imageView2.clearAnimation();
            }
        }
    }

    private ScaleAnimation getScaleAnimation(ImageView view, WidgetsImageView preview) {
        int intrinsicHeight;
        int intrinsicWidth = 0;
        if (view == null || view.getDrawable() == null) {
            intrinsicHeight = 0;
        } else {
            intrinsicWidth = view.getDrawable().getIntrinsicWidth() / 2;
            intrinsicHeight = view.getDrawable().getIntrinsicHeight() / 2;
            if (preview != null && this.mIsRtL) {
                preview.setUninstallBadgePositionX();
                intrinsicWidth += preview.getUninstallBadgePositionX();
            }
        }
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.17f, 1.0f, 1.17f, 0, intrinsicWidth, 0, intrinsicHeight);
        scaleAnimation.setDuration(600L);
        scaleAnimation.setRepeatMode(2);
        scaleAnimation.setRepeatCount(-1);
        return scaleAnimation;
    }

    public void clear(ViewPager viewpager) {
        int row = getRow() * getCol();
        for (int i = 0; i < viewpager.getChildCount(); i++) {
            GridLayout gridLayout = (GridLayout) viewpager.getChildAt(i);
            for (int i2 = 0; i2 < row; i2++) {
                LGWidgetCell lGWidgetCell = (LGWidgetCell) gridLayout.getChildAt(i2);
                if (lGWidgetCell != null) {
                    lGWidgetCell.clear();
                }
            }
        }
    }

    private int getRow() {
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            return this.mLauncher.getResources().getInteger(R.integer.widget_tray_row_land);
        }
        return this.mLauncher.getResources().getInteger(R.integer.widget_tray_row_port);
    }

    private int getCol() {
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            return this.mLauncher.getResources().getInteger(R.integer.widget_tray_col_land);
        }
        return this.mLauncher.getResources().getInteger(R.integer.widget_tray_col_port);
    }
}
