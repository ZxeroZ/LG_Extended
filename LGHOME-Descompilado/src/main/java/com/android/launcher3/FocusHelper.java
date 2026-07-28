package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderPagedView;
import com.android.launcher3.util.FocusLogic;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsHost;
import com.lge.launcher3.wing.CarouselLayout;

/* JADX INFO: loaded from: classes.dex */
public class FocusHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "FocusHelper";

    public static class PagedFolderKeyEventListener implements View.OnKeyListener {
        private final Folder mFolder;

        public PagedFolderKeyEventListener(Folder folder) {
            this.mFolder = folder;
        }

        @Override // android.view.View.OnKeyListener
        public boolean onKey(View v, int keyCode, KeyEvent e) {
            boolean zShouldConsume = FocusLogic.shouldConsume(keyCode);
            if (e.getAction() == 1) {
                return zShouldConsume;
            }
            if (!(v.getParent() instanceof ShortcutAndWidgetContainer)) {
                if (LauncherAppState.isDogfoodBuild()) {
                    throw new IllegalStateException("Parent of the focused item is not supported.");
                }
                return false;
            }
            ShortcutAndWidgetContainer shortcutAndWidgetContainer = (ShortcutAndWidgetContainer) v.getParent();
            CellLayout cellLayout = (CellLayout) shortcutAndWidgetContainer.getParent();
            int countX = cellLayout.getCountX();
            int countY = cellLayout.getCountY();
            int iIndexOfChild = shortcutAndWidgetContainer.indexOfChild(v);
            FolderPagedView folderPagedView = (FolderPagedView) cellLayout.getParent();
            int iIndexOfChild2 = folderPagedView.indexOfChild(cellLayout);
            int iHandleKeyEvent = FocusLogic.handleKeyEvent(keyCode, countX, countY, FocusLogic.createSparseMatrix(cellLayout), iIndexOfChild, iIndexOfChild2, folderPagedView.getPageCount(), Utilities.isRtl(v.getResources()));
            if (iHandleKeyEvent == -1) {
                handleNoopKey(keyCode, v);
                return zShouldConsume;
            }
            View adjacentChildInNextPage = null;
            switch (iHandleKeyEvent) {
                case FocusLogic.NEXT_PAGE_RIGHT_COLUMN /* -10 */:
                case FocusLogic.NEXT_PAGE_LEFT_COLUMN /* -9 */:
                    int i = iIndexOfChild2 + 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i);
                    if (cellLayoutChildrenForIndex != null) {
                        folderPagedView.snapToPage(i);
                        adjacentChildInNextPage = FocusLogic.getAdjacentChildInNextPage(cellLayoutChildrenForIndex, v, iHandleKeyEvent);
                    }
                    break;
                case FocusLogic.NEXT_PAGE_FIRST_ITEM /* -8 */:
                    int i2 = iIndexOfChild2 + 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex2 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i2);
                    if (cellLayoutChildrenForIndex2 != null) {
                        folderPagedView.snapToPage(i2);
                        adjacentChildInNextPage = cellLayoutChildrenForIndex2.getChildAt(0, 0);
                    }
                    break;
                case FocusLogic.CURRENT_PAGE_LAST_ITEM /* -7 */:
                    adjacentChildInNextPage = folderPagedView.getLastItem();
                    break;
                case -6:
                    adjacentChildInNextPage = cellLayout.getChildAt(0, 0);
                    break;
                case FocusLogic.PREVIOUS_PAGE_LEFT_COLUMN /* -5 */:
                case -2:
                    int i3 = iIndexOfChild2 - 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex3 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i3);
                    if (cellLayoutChildrenForIndex3 != null) {
                        int i4 = ((CellLayout.LayoutParams) v.getLayoutParams()).cellY;
                        folderPagedView.snapToPage(i3);
                        adjacentChildInNextPage = cellLayoutChildrenForIndex3.getChildAt((iHandleKeyEvent == -5) ^ cellLayoutChildrenForIndex3.invertLayoutHorizontally() ? 0 : countX - 1, i4);
                    }
                    break;
                case -4:
                    int i5 = iIndexOfChild2 - 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex4 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i5);
                    if (cellLayoutChildrenForIndex4 != null) {
                        folderPagedView.snapToPage(i5);
                        adjacentChildInNextPage = cellLayoutChildrenForIndex4.getChildAt(countX - 1, countY - 1);
                    }
                    break;
                case -3:
                    int i6 = iIndexOfChild2 - 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex5 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i6);
                    if (cellLayoutChildrenForIndex5 != null) {
                        folderPagedView.snapToPage(i6);
                        adjacentChildInNextPage = cellLayoutChildrenForIndex5.getChildAt(0, 0);
                    }
                    break;
                default:
                    adjacentChildInNextPage = shortcutAndWidgetContainer.getChildAt(iHandleKeyEvent);
                    break;
            }
            if (adjacentChildInNextPage != null) {
                adjacentChildInNextPage.requestFocus();
                FocusHelper.playSoundEffect(keyCode, v);
            } else {
                handleNoopKey(keyCode, v);
            }
            return zShouldConsume;
        }

        public void handleNoopKey(int keyCode, View v) {
            if (keyCode == 20) {
                this.mFolder.mFolderName.requestFocus();
                FocusHelper.playSoundEffect(keyCode, v);
            } else if (keyCode == 19) {
                this.mFolder.mFolderPlusButton.requestFocus();
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00f8  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0116  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x011d  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0128  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static boolean handleHotseatButtonKeyEvent(android.view.View r18, int r19, android.view.KeyEvent r20) {
        /*
            r0 = r18
            r1 = r19
            boolean r2 = com.android.launcher3.util.FocusLogic.shouldConsume(r19)
            int r3 = r20.getAction()
            r4 = 1
            if (r3 == r4) goto L138
            if (r2 != 0) goto L13
            goto L138
        L13:
            android.content.Context r3 = r18.getContext()
            com.android.launcher3.Launcher r3 = (com.android.launcher3.Launcher) r3
            com.android.launcher3.DeviceProfile r3 = r3.getDeviceProfile()
            android.view.ViewParent r5 = r18.getParent()
            com.android.launcher3.ShortcutAndWidgetContainer r5 = (com.android.launcher3.ShortcutAndWidgetContainer) r5
            android.view.ViewParent r6 = r5.getParent()
            com.android.launcher3.CellLayout r6 = (com.android.launcher3.CellLayout) r6
            android.view.ViewParent r7 = r6.getParent()
            com.android.launcher3.Hotseat r7 = (com.android.launcher3.Hotseat) r7
            android.view.View r7 = r18.getRootView()
            r8 = 2131297001(0x7f0902e9, float:1.8211935E38)
            android.view.View r7 = r7.findViewById(r8)
            com.android.launcher3.Workspace r7 = (com.android.launcher3.Workspace) r7
            int r15 = r7.getNextPage()
            int r14 = r7.getChildCount()
            int r8 = r5.indexOfChild(r0)
            com.android.launcher3.ShortcutAndWidgetContainer r9 = r6.getShortcutsAndWidgets()
            android.view.View r9 = r9.getChildAt(r8)
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r9 = (com.android.launcher3.CellLayout.LayoutParams) r9
            int r9 = r9.cellX
            android.view.View r10 = r7.getChildAt(r15)
            com.android.launcher3.CellLayout r10 = (com.android.launcher3.CellLayout) r10
            if (r10 != 0) goto L61
            return r2
        L61:
            com.android.launcher3.ShortcutAndWidgetContainer r13 = r10.getShortcutsAndWidgets()
            r11 = 19
            r12 = -1
            r16 = 0
            r4 = 0
            if (r1 != r11) goto L94
            boolean r11 = r3.isVerticalBarLayout()
            if (r11 != 0) goto L94
            com.android.launcher3.InvariantDeviceProfile r5 = r3.inv
            int r5 = r5.hotseatAllAppsRank
            com.android.launcher3.InvariantDeviceProfile r3 = r3.inv
            int r3 = r3.hotseatAllAppsRank
            if (r9 != r3) goto L7f
            r3 = 1
            goto L80
        L7f:
            r3 = r4
        L80:
            r9 = 1
            int[][] r3 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r10, r6, r9, r5, r3)
            int r5 = r13.getChildCount()
            int r8 = r8 + r5
            int r5 = r3.length
            r6 = r3[r4]
            int r6 = r6.length
        L8e:
            r11 = r3
            r9 = r5
            r10 = r6
            r12 = r8
            r5 = r13
            goto Le3
        L94:
            r11 = 21
            if (r1 != r11) goto Lc2
            boolean r11 = r3.isVerticalBarLayout()
            if (r11 == 0) goto Lc2
            com.android.launcher3.InvariantDeviceProfile r5 = r3.inv
            int r5 = r5.hotseatAllAppsRank
            com.android.launcher3.InvariantDeviceProfile r3 = r3.inv
            int r3 = r3.hotseatAllAppsRank
            if (r9 != r3) goto Laa
            r9 = 1
            goto Lab
        Laa:
            r9 = r4
        Lab:
            int[][] r3 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r10, r6, r4, r5, r9)
            int r5 = r13.getChildCount()
            int r8 = r8 + r5
            int r5 = r10.getCountX()
            int r6 = r6.getCountX()
            int r5 = r5 + r6
            int r6 = r10.getCountY()
            goto L8e
        Lc2:
            r9 = 22
            if (r1 != r9) goto Ld4
            boolean r3 = r3.isVerticalBarLayout()
            if (r3 == 0) goto Ld4
            r1 = 93
            r9 = r12
            r10 = r9
            r5 = r16
            r11 = r5
            goto Le2
        Ld4:
            int[][] r3 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r6)
            int r9 = r6.getCountX()
            int r6 = r6.getCountY()
            r11 = r3
            r10 = r6
        Le2:
            r12 = r8
        Le3:
            android.content.res.Resources r3 = r18.getResources()
            boolean r3 = com.android.launcher3.Utilities.isRtl(r3)
            r8 = r1
            r6 = r13
            r13 = r15
            r17 = r15
            r15 = r3
            int r3 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r8, r9, r10, r11, r12, r13, r14, r15)
            r8 = -8
            if (r3 != r8) goto L105
            int r15 = r17 + 1
            com.android.launcher3.ShortcutAndWidgetContainer r5 = getCellLayoutChildrenForIndex(r7, r15)
            android.view.View r16 = r5.getChildAt(r4)
            r7.snapToPage(r15)
        L105:
            if (r5 != r6) goto L112
            int r4 = r6.getChildCount()
            if (r3 < r4) goto L112
            int r4 = r6.getChildCount()
            int r3 = r3 - r4
        L112:
            r4 = -9
            if (r3 != r4) goto L11d
            r4 = 1
            int r15 = r17 + 1
            r7.snapToPage(r15)
            goto L126
        L11d:
            r4 = 1
            r6 = -2
            if (r3 != r6) goto L126
            int r15 = r17 + (-1)
            r7.snapToPage(r15)
        L126:
            if (r5 == 0) goto L138
            if (r16 != 0) goto L130
            if (r3 < 0) goto L130
            android.view.View r16 = r5.getChildAt(r3)
        L130:
            if (r16 == 0) goto L138
            r16.requestFocus()
            playSoundEffect(r1, r0)
        L138:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FocusHelper.handleHotseatButtonKeyEvent(android.view.View, int, android.view.KeyEvent):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:62:0x01fc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static boolean handleIconKeyEvent(android.view.View r20, int r21, android.view.KeyEvent r22) {
        /*
            r0 = r20
            r9 = r21
            boolean r10 = com.android.launcher3.util.FocusLogic.shouldConsume(r21)
            int r1 = r22.getAction()
            r11 = 1
            if (r1 == r11) goto L205
            if (r10 != 0) goto L13
            goto L205
        L13:
            android.content.Context r1 = r20.getContext()
            com.android.launcher3.Launcher r1 = (com.android.launcher3.Launcher) r1
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            android.view.ViewParent r2 = r20.getParent()
            r12 = r2
            com.android.launcher3.ShortcutAndWidgetContainer r12 = (com.android.launcher3.ShortcutAndWidgetContainer) r12
            android.view.ViewParent r2 = r12.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            android.view.ViewParent r3 = r2.getParent()
            r13 = r3
            com.android.launcher3.Workspace r13 = (com.android.launcher3.Workspace) r13
            android.view.ViewParent r3 = r13.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            r4 = 2131296803(0x7f090223, float:1.8211533E38)
            android.view.View r4 = r3.findViewById(r4)
            r14 = r4
            android.view.ViewGroup r14 = (android.view.ViewGroup) r14
            r4 = 2131296562(0x7f090132, float:1.8211044E38)
            android.view.View r3 = r3.findViewById(r4)
            r15 = r3
            com.android.launcher3.Hotseat r15 = (com.android.launcher3.Hotseat) r15
            int r5 = r12.indexOfChild(r0)
            int r16 = r13.indexOfChild(r2)
            int r17 = r13.getChildCount()
            int r3 = r2.getCountX()
            int r4 = r2.getCountY()
            r8 = 0
            android.view.View r6 = r15.getChildAt(r8)
            com.android.launcher3.CellLayout r6 = (com.android.launcher3.CellLayout) r6
            com.android.launcher3.ShortcutAndWidgetContainer r7 = r6.getShortcutsAndWidgets()
            r8 = 20
            if (r9 != r8) goto L89
            boolean r8 = r1.isVerticalBarLayout()
            if (r8 != 0) goto L89
            com.android.launcher3.InvariantDeviceProfile r1 = r1.inv
            int r1 = r1.hotseatAllAppsRank
            boolean r8 = r15.hasIcons()
            r8 = r8 ^ r11
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2, r6, r11, r1, r8)
            int r4 = r4 + 1
            r18 = r3
            r19 = r4
            r11 = 0
            goto Lb7
        L89:
            r8 = 22
            if (r9 != r8) goto La4
            boolean r8 = r1.isVerticalBarLayout()
            if (r8 == 0) goto La4
            com.android.launcher3.InvariantDeviceProfile r1 = r1.inv
            int r1 = r1.hotseatAllAppsRank
            boolean r8 = r15.hasIcons()
            r8 = r8 ^ r11
            r11 = 0
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2, r6, r11, r1, r8)
            int r3 = r3 + 1
            goto Lb3
        La4:
            r11 = 0
            r1 = 67
            if (r9 == r1) goto L205
            r1 = 112(0x70, float:1.57E-43)
            if (r9 != r1) goto Laf
            goto L205
        Laf:
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2)
        Lb3:
            r18 = r3
            r19 = r4
        Lb7:
            r4 = r1
            android.content.res.Resources r1 = r20.getResources()
            boolean r8 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r21
            r2 = r18
            r3 = r19
            r6 = r16
            r22 = r7
            r7 = r17
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6, r7, r8)
            r2 = 0
            switch(r1) {
                case -10: goto L186;
                case -9: goto L12f;
                case -8: goto L11f;
                case -7: goto L113;
                case -6: goto L10d;
                case -5: goto L12f;
                case -4: goto Lf8;
                case -3: goto Le8;
                case -2: goto L186;
                case -1: goto Le2;
                default: goto Ld4;
            }
        Ld4:
            if (r1 < 0) goto L1df
            int r3 = r12.getChildCount()
            if (r1 >= r3) goto L1df
            android.view.View r14 = r12.getChildAt(r1)
            goto L1fd
        Le2:
            r1 = 19
            if (r9 != r1) goto L1fc
            goto L1fd
        Le8:
            r3 = 1
            int r1 = r16 + (-1)
            com.android.launcher3.ShortcutAndWidgetContainer r2 = getCellLayoutChildrenForIndex(r13, r1)
            android.view.View r14 = r2.getChildAt(r11)
            r13.snapToPage(r1)
            goto L1fd
        Lf8:
            r3 = 1
            int r1 = r16 + (-1)
            com.android.launcher3.ShortcutAndWidgetContainer r2 = getCellLayoutChildrenForIndex(r13, r1)
            int r4 = r2.getChildCount()
            int r4 = r4 - r3
            android.view.View r14 = r2.getChildAt(r4)
            r13.snapToPage(r1)
            goto L1fd
        L10d:
            android.view.View r14 = r12.getChildAt(r11)
            goto L1fd
        L113:
            r3 = 1
            int r1 = r12.getChildCount()
            int r1 = r1 - r3
            android.view.View r14 = r12.getChildAt(r1)
            goto L1fd
        L11f:
            r3 = 1
            int r1 = r16 + 1
            com.android.launcher3.ShortcutAndWidgetContainer r2 = getCellLayoutChildrenForIndex(r13, r1)
            android.view.View r14 = r2.getChildAt(r11)
            r13.snapToPage(r1)
            goto L1fd
        L12f:
            r3 = 1
            int r4 = r16 + 1
            r5 = -5
            if (r1 != r5) goto L137
            int r4 = r16 + (-1)
        L137:
            r11 = r4
            r13.snapToPage(r11)
            android.view.ViewGroup$LayoutParams r1 = r20.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r1 = (com.android.launcher3.CellLayout.LayoutParams) r1
            int r1 = r1.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r12 = getCellLayoutChildrenForIndex(r13, r11)
            if (r12 == 0) goto L1fc
            r13.snapToPage(r11)
            android.view.ViewParent r2 = r12.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            r3 = -1
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2, r3, r1)
            r1 = 1
            int r2 = r18 + 1
            r5 = 100
            android.content.res.Resources r1 = r20.getResources()
            boolean r8 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r21
            r3 = r19
            r6 = r11
            r7 = r17
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6, r7, r8)
            android.view.View r14 = r12.getChildAt(r1)
            if (r14 != 0) goto L1fd
            r1 = 2
            r15.requestFocus(r1)
            android.view.View r1 = r15.findFocus()
            if (r1 != 0) goto L1fd
            r3 = 1
            int r11 = r11 - r3
            r13.snapToPage(r11)
            goto L1fd
        L186:
            r3 = 1
            int r4 = r16 + (-1)
            r5 = -10
            if (r1 != r5) goto L18f
            int r4 = r16 + 1
        L18f:
            r11 = r4
            android.view.ViewGroup$LayoutParams r1 = r20.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r1 = (com.android.launcher3.CellLayout.LayoutParams) r1
            int r1 = r1.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r12 = getCellLayoutChildrenForIndex(r13, r11)
            r13.snapToPage(r11)
            if (r12 == 0) goto L1fc
            r13.snapToPage(r11)
            android.view.ViewParent r2 = r12.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            int r3 = r2.getCountX()
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2, r3, r1)
            r1 = 1
            int r2 = r18 + 1
            r5 = 100
            android.content.res.Resources r1 = r20.getResources()
            boolean r8 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r21
            r3 = r19
            r6 = r11
            r7 = r17
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6, r7, r8)
            android.view.View r14 = r12.getChildAt(r1)
            if (r14 != 0) goto L1fd
            r1 = 1
            r15.requestFocus(r1)
            android.view.View r2 = r15.findFocus()
            if (r2 != 0) goto L1fd
            int r11 = r11 + r1
            r13.snapToPage(r11)
            goto L1fd
        L1df:
            int r3 = r12.getChildCount()
            if (r3 > r1) goto L1fc
            int r3 = r12.getChildCount()
            int r4 = r22.getChildCount()
            int r3 = r3 + r4
            if (r1 >= r3) goto L1fc
            int r2 = r12.getChildCount()
            int r1 = r1 - r2
            r2 = r22
            android.view.View r14 = r2.getChildAt(r1)
            goto L1fd
        L1fc:
            r14 = r2
        L1fd:
            if (r14 == 0) goto L205
            r14.requestFocus()
            playSoundEffect(r9, r0)
        L205:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FocusHelper.handleIconKeyEvent(android.view.View, int, android.view.KeyEvent):boolean");
    }

    public static boolean handleCarouselIconKeyEvent(View v, int keyCode, KeyEvent e) {
        boolean zShouldConsume = FocusLogic.shouldConsume(keyCode);
        if (e.getAction() == 1 || !zShouldConsume) {
            return zShouldConsume;
        }
        CarouselLayout carouselLayout = ((Launcher) v.getContext()).getCarouselLayout();
        if (carouselLayout == null) {
            return false;
        }
        View focusedChild = carouselLayout.getCarouselView().getLayoutManager().getFocusedChild();
        int position = focusedChild != null ? carouselLayout.getCarouselView().getLayoutManager().getPosition(focusedChild) : -1;
        if (keyCode != 92 && keyCode != 93 && keyCode != 122 && keyCode != 123) {
            switch (keyCode) {
                case 21:
                    if (position != -1 && position != 0) {
                        position--;
                    }
                    v.playSoundEffect(1);
                    break;
                case 22:
                    if (position != -1 && position != carouselLayout.getCarouselView().getLayoutManager().getItemCount() - 1) {
                        position++;
                    }
                    v.playSoundEffect(3);
                    break;
            }
            if (position == -1) {
                position = carouselLayout.getCarouselView().getLayoutManager().getItemCount() / 2;
            }
            View viewFindViewByPosition = carouselLayout.getCarouselView().getLayoutManager().findViewByPosition(position);
            if (viewFindViewByPosition != null) {
                carouselLayout.getCarouselView().smoothScrollToPosition(position);
                viewFindViewByPosition.requestFocus();
                playSoundEffect(keyCode, v);
            }
            return zShouldConsume;
        }
        return false;
    }

    static ShortcutAndWidgetContainer getCellLayoutChildrenForIndex(ViewGroup container, int i) {
        return ((CellLayout) container.getChildAt(i)).getShortcutsAndWidgets();
    }

    static void playSoundEffect(int keyCode, View v) {
        if (keyCode != 92) {
            if (keyCode != 93) {
                if (keyCode != 122) {
                    if (keyCode != 123) {
                        switch (keyCode) {
                            case 21:
                                v.playSoundEffect(1);
                                break;
                            case 22:
                                v.playSoundEffect(3);
                                break;
                        }
                    }
                }
            }
            v.playSoundEffect(4);
            return;
        }
        v.playSoundEffect(2);
    }

    public static boolean handleAppsCustomizeTabKeyEvent(View v, int keyCode, KeyEvent e) {
        AllAppsHost allAppsHost = ((Launcher) v.getContext()).getAllAppsHost();
        View viewFindViewById = allAppsHost.findViewById(R.id.lg_page_menu_search_btn);
        View viewFindViewById2 = allAppsHost.findViewById(R.id.lg_pagemenu_optionsmenu_button);
        boolean z = true;
        boolean z2 = e.getAction() != 1;
        switch (keyCode) {
            case 20:
                if (!z2 || !v.equals(viewFindViewById2)) {
                    z = false;
                } else if (viewFindViewById.getVisibility() == 0 && viewFindViewById.isFocusable()) {
                    viewFindViewById.requestFocus();
                }
                break;
            case 21:
                if (!z2 || !v.equals(viewFindViewById2)) {
                    z = false;
                } else if (viewFindViewById.getVisibility() == 0 && viewFindViewById.isFocusable()) {
                    viewFindViewById.requestFocus();
                }
                break;
            case 22:
                if (!z2) {
                    z = false;
                } else {
                    if (viewFindViewById2 == null) {
                        return false;
                    }
                    if (v == viewFindViewById2) {
                        return true;
                    }
                    if (v != viewFindViewById && viewFindViewById.getVisibility() == 0 && viewFindViewById.isFocusable()) {
                        z = false;
                    } else {
                        viewFindViewById2.setFocusable(true);
                        viewFindViewById2.requestFocus();
                    }
                }
                break;
            default:
                z = false;
                break;
        }
        if (z && z2) {
            v.playSoundEffect(0);
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x009a  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x009d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean handleAppsCustomizeKeyEvent(android.view.View r20, int r21, android.view.KeyEvent r22) {
        /*
            r0 = r20
            r9 = r21
            boolean r10 = com.android.launcher3.util.FocusLogic.shouldConsume(r21)
            int r1 = r22.getAction()
            r11 = 1
            if (r1 == r11) goto L1af
            if (r10 != 0) goto L13
            goto L1af
        L13:
            android.content.Context r1 = r20.getContext()
            com.android.launcher3.Launcher r1 = (com.android.launcher3.Launcher) r1
            r1.getDeviceProfile()
            com.lge.launcher3.allapps.AllAppsHost r1 = r1.getAllAppsHost()
            android.view.ViewParent r2 = r20.getParent()
            r12 = r2
            com.android.launcher3.ShortcutAndWidgetContainer r12 = (com.android.launcher3.ShortcutAndWidgetContainer) r12
            android.view.ViewParent r2 = r12.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            android.view.ViewParent r3 = r2.getParent()
            r13 = r3
            com.lge.launcher3.allapps.AllAppsPagedView r13 = (com.lge.launcher3.allapps.AllAppsPagedView) r13
            r3 = 2131296629(0x7f090175, float:1.821118E38)
            android.view.View r14 = r1.findViewById(r3)
            r3 = 2131296631(0x7f090177, float:1.8211184E38)
            android.view.View r15 = r1.findViewById(r3)
            int r5 = r12.indexOfChild(r0)
            int r16 = r13.indexOfChild(r2)
            int r17 = r13.getChildCount()
            int r18 = r2.getCountX()
            int r19 = r2.getCountY()
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2)
            android.content.res.Resources r1 = r20.getResources()
            boolean r8 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r21
            r2 = r18
            r3 = r19
            r6 = r16
            r7 = r17
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6, r7, r8)
            r2 = 0
            r3 = 0
            switch(r1) {
                case -10: goto L141;
                case -9: goto Le9;
                case -8: goto Lda;
                case -7: goto Lcf;
                case -6: goto Lc9;
                case -5: goto Le9;
                case -4: goto Lb5;
                case -3: goto La6;
                case -2: goto L141;
                case -1: goto L83;
                default: goto L75;
            }
        L75:
            if (r1 < 0) goto L19d
            int r3 = r12.getChildCount()
            if (r1 >= r3) goto L19d
            android.view.View r2 = r12.getChildAt(r1)
            goto L19d
        L83:
            r1 = 19
            if (r9 != r1) goto L9a
            if (r14 == 0) goto L90
            int r1 = r14.getVisibility()
            if (r1 != 0) goto L90
            goto L9b
        L90:
            if (r15 == 0) goto L9a
            int r1 = r15.getVisibility()
            if (r1 != 0) goto L9a
            r14 = r15
            goto L9b
        L9a:
            r14 = r2
        L9b:
            if (r14 == 0) goto La3
            r14.requestFocus()
            playSoundEffect(r9, r0)
        La3:
            r2 = r14
            goto L1a7
        La6:
            int r1 = r16 + (-1)
            com.android.launcher3.ShortcutAndWidgetContainer r2 = getCellLayoutChildrenForIndex(r13, r1)
            android.view.View r2 = r2.getChildAt(r3)
            r13.snapToPage(r1)
            goto L1a7
        Lb5:
            int r1 = r16 + (-1)
            com.android.launcher3.ShortcutAndWidgetContainer r2 = getCellLayoutChildrenForIndex(r13, r1)
            int r3 = r2.getChildCount()
            int r3 = r3 - r11
            android.view.View r2 = r2.getChildAt(r3)
            r13.snapToPage(r1)
            goto L1a7
        Lc9:
            android.view.View r2 = r12.getChildAt(r3)
            goto L1a7
        Lcf:
            int r1 = r12.getChildCount()
            int r1 = r1 - r11
            android.view.View r2 = r12.getChildAt(r1)
            goto L1a7
        Lda:
            int r1 = r16 + 1
            com.android.launcher3.ShortcutAndWidgetContainer r2 = getCellLayoutChildrenForIndex(r13, r1)
            android.view.View r2 = r2.getChildAt(r3)
            r13.snapToPage(r1)
            goto L1a7
        Le9:
            int r3 = r16 + 1
            r4 = -5
            if (r1 != r4) goto Lf3
            int r16 = r16 + (-1)
            r6 = r16
            goto Lf4
        Lf3:
            r6 = r3
        Lf4:
            r13.snapToPage(r6)
            android.view.ViewGroup$LayoutParams r1 = r20.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r1 = (com.android.launcher3.CellLayout.LayoutParams) r1
            int r1 = r1.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r12 = getCellLayoutChildrenForIndex(r13, r6)
            if (r12 == 0) goto L1a7
            r13.snapToPage(r6)
            android.view.ViewParent r2 = r12.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            r3 = -1
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2, r3, r1)
            int r2 = r18 + 1
            r5 = 100
            android.content.res.Resources r1 = r20.getResources()
            boolean r8 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r21
            r3 = r19
            r7 = r17
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6, r7, r8)
            android.view.View r2 = r12.getChildAt(r1)
            if (r12 == 0) goto L1a7
            if (r2 != 0) goto L138
            if (r1 < 0) goto L138
            android.view.View r1 = r13.getChildAt(r1)
            r2 = r1
        L138:
            if (r2 == 0) goto L1a7
            r2.requestFocus()
            playSoundEffect(r9, r0)
            goto L1a7
        L141:
            int r3 = r16 + (-1)
            r4 = -10
            if (r1 != r4) goto L14c
            int r16 = r16 + 1
            r6 = r16
            goto L14d
        L14c:
            r6 = r3
        L14d:
            android.view.ViewGroup$LayoutParams r1 = r20.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r1 = (com.android.launcher3.CellLayout.LayoutParams) r1
            int r1 = r1.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r12 = getCellLayoutChildrenForIndex(r13, r6)
            r13.snapToPage(r6)
            if (r12 == 0) goto L1a7
            r13.snapToPage(r6)
            android.view.ViewParent r2 = r12.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            int r3 = r2.getCountX()
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2, r3, r1)
            int r2 = r18 + 1
            r5 = 100
            android.content.res.Resources r1 = r20.getResources()
            boolean r8 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r21
            r3 = r19
            r7 = r17
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6, r7, r8)
            android.view.View r2 = r12.getChildAt(r1)
            if (r12 == 0) goto L1a7
            if (r2 != 0) goto L194
            if (r1 < 0) goto L194
            android.view.View r1 = r13.getChildAt(r1)
            r2 = r1
        L194:
            if (r2 == 0) goto L1a7
            r2.requestFocus()
            playSoundEffect(r9, r0)
            goto L1a7
        L19d:
            if (r12 == 0) goto L1a7
            if (r2 != 0) goto L1a7
            if (r1 < 0) goto L1a7
            android.view.View r2 = r13.getChildAt(r1)
        L1a7:
            if (r2 == 0) goto L1af
            r2.requestFocus()
            playSoundEffect(r9, r0)
        L1af:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FocusHelper.handleAppsCustomizeKeyEvent(android.view.View, int, android.view.KeyEvent):boolean");
    }
}
