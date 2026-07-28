package com.android.launcher3.util;

import android.util.Log;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.ShortcutAndWidgetContainer;
import java.lang.reflect.Array;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class FocusLogic {
    public static final int CURRENT_PAGE_FIRST_ITEM = -6;
    public static final int CURRENT_PAGE_LAST_ITEM = -7;
    private static final boolean DEBUG = false;
    public static final int EMPTY = -1;
    public static final int NEXT_PAGE_FIRST_ITEM = -8;
    public static final int NEXT_PAGE_LEFT_COLUMN = -9;
    public static final int NEXT_PAGE_RIGHT_COLUMN = -10;
    public static final int NOOP = -1;
    public static final int PIVOT = 100;
    public static final int PREVIOUS_PAGE_FIRST_ITEM = -3;
    public static final int PREVIOUS_PAGE_LAST_ITEM = -4;
    public static final int PREVIOUS_PAGE_LEFT_COLUMN = -5;
    public static final int PREVIOUS_PAGE_RIGHT_COLUMN = -2;
    private static final String TAG = "FocusLogic";

    private static int handleMoveEnd() {
        return -7;
    }

    private static int handleMoveHome() {
        return -6;
    }

    private static int handlePageDown(int pageIndex, int pageCount) {
        return pageIndex < pageCount + (-1) ? -8 : -7;
    }

    private static int handlePageUp(int pageIndex) {
        return pageIndex > 0 ? -3 : -6;
    }

    private static boolean isValid(int xPos, int yPos, int countX, int countY) {
        return xPos >= 0 && xPos < countX && yPos >= 0 && yPos < countY;
    }

    public static boolean shouldConsume(int keyCode) {
        return keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20 || keyCode == 122 || keyCode == 123 || keyCode == 92 || keyCode == 93 || keyCode == 67 || keyCode == 112;
    }

    public static int handleKeyEvent(int keyCode, int cntX, int cntY, int[][] map, int iconIdx, int pageIndex, int pageCount, boolean isRtl) {
        int iHandleDpadHorizontal;
        if (keyCode == 92) {
            return handlePageUp(pageIndex);
        }
        if (keyCode == 93) {
            return handlePageDown(pageIndex, pageCount);
        }
        if (keyCode == 122) {
            return handleMoveHome();
        }
        if (keyCode != 123) {
            switch (keyCode) {
                case 19:
                    return handleDpadVertical(iconIdx, cntX, cntY, map, -1);
                case 20:
                    return handleDpadVertical(iconIdx, cntX, cntY, map, 1);
                case 21:
                    iHandleDpadHorizontal = handleDpadHorizontal(iconIdx, cntX, cntY, map, -1);
                    if (!isRtl && iHandleDpadHorizontal == -1 && pageIndex > 0) {
                        return -2;
                    }
                    if (isRtl && iHandleDpadHorizontal == -1 && pageIndex < pageCount - 1) {
                        return -10;
                    }
                    break;
                case 22:
                    iHandleDpadHorizontal = handleDpadHorizontal(iconIdx, cntX, cntY, map, 1);
                    if (!isRtl && iHandleDpadHorizontal == -1 && pageIndex < pageCount - 1) {
                        return -9;
                    }
                    if (isRtl && iHandleDpadHorizontal == -1 && pageIndex > 0) {
                        return -5;
                    }
                    break;
                default:
                    return -1;
            }
            return iHandleDpadHorizontal;
        }
        return handleMoveEnd();
    }

    private static int[][] createFullMatrix(int m, int n) {
        int[] iArr = {m, n};
        int[][] iArr2 = (int[][]) Array.newInstance((Class<?>) int.class, iArr);
        for (int i = 0; i < m; i++) {
            Arrays.fill(iArr2[i], -1);
        }
        return iArr2;
    }

    public static int[][] createSparseMatrix(CellLayout layout) {
        ShortcutAndWidgetContainer shortcutsAndWidgets = layout.getShortcutsAndWidgets();
        int countX = layout.getCountX();
        int countY = layout.getCountY();
        boolean zInvertLayoutHorizontally = shortcutsAndWidgets.invertLayoutHorizontally();
        int[][] iArrCreateFullMatrix = createFullMatrix(countX, countY);
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            int i2 = ((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams()).cellX;
            int i3 = ((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams()).cellY;
            if (isShortCutItem((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams())) {
                if (zInvertLayoutHorizontally) {
                    i2 = (countX - i2) - 1;
                }
                iArrCreateFullMatrix[i2][i3] = i;
            }
        }
        return iArrCreateFullMatrix;
    }

    public static int[][] createSparseMatrix(CellLayout iconLayout, CellLayout hotseatLayout, boolean isHorizontal, int allappsiconRank, boolean includeAllappsicon) {
        int countX;
        int iMax;
        ShortcutAndWidgetContainer shortcutsAndWidgets = iconLayout.getShortcutsAndWidgets();
        ShortcutAndWidgetContainer shortcutsAndWidgets2 = hotseatLayout.getShortcutsAndWidgets();
        if (isHorizontal) {
            countX = Math.max(iconLayout.getCountX(), hotseatLayout.getMaxCount());
            iMax = iconLayout.getCountY() + hotseatLayout.getCountY();
        } else {
            countX = iconLayout.getCountX() + hotseatLayout.getCountX();
            iMax = Math.max(iconLayout.getCountY(), hotseatLayout.getMaxCount());
        }
        int[][] iArrCreateFullMatrix = createFullMatrix(countX, iMax);
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            int i2 = ((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams()).cellX;
            int i3 = ((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams()).cellY;
            if (isShortCutItem((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams())) {
                iArrCreateFullMatrix[i2][i3] = i;
            }
        }
        for (int childCount = shortcutsAndWidgets2.getChildCount() - 1; childCount >= 0; childCount--) {
            if (isHorizontal) {
                iArrCreateFullMatrix[((CellLayout.LayoutParams) shortcutsAndWidgets2.getChildAt(childCount).getLayoutParams()).cellX + 0][iconLayout.getCountY()] = shortcutsAndWidgets.getChildCount() + childCount;
            } else {
                iArrCreateFullMatrix[iconLayout.getCountX()][((CellLayout.LayoutParams) shortcutsAndWidgets2.getChildAt(childCount).getLayoutParams()).cellY + 0] = shortcutsAndWidgets.getChildCount() + childCount;
            }
        }
        return iArrCreateFullMatrix;
    }

    public static int[][] createSparseMatrix(CellLayout iconLayout, int pivotX, int pivotY) {
        ShortcutAndWidgetContainer shortcutsAndWidgets = iconLayout.getShortcutsAndWidgets();
        int[][] iArrCreateFullMatrix = createFullMatrix(iconLayout.getCountX() + 1, iconLayout.getCountY());
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            int i2 = ((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams()).cellX;
            int i3 = ((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams()).cellY;
            if (isShortCutItem((CellLayout.LayoutParams) shortcutsAndWidgets.getChildAt(i).getLayoutParams())) {
                if (pivotX < 0) {
                    iArrCreateFullMatrix[i2 - pivotX][i3] = i;
                } else {
                    iArrCreateFullMatrix[i2][i3] = i;
                }
            }
        }
        if (pivotX < 0) {
            iArrCreateFullMatrix[0][pivotY] = 100;
        } else {
            iArrCreateFullMatrix[pivotX][pivotY] = 100;
        }
        return iArrCreateFullMatrix;
    }

    private static int handleDpadHorizontal(int iconIdx, int cntX, int cntY, int[][] matrix, int increment) {
        if (matrix == null) {
            throw new IllegalStateException("Dpad navigation requires a matrix.");
        }
        int i = -1;
        int i2 = -1;
        for (int i3 = 0; i3 < cntX; i3++) {
            for (int i4 = 0; i4 < cntY; i4++) {
                if (matrix[i3][i4] == iconIdx) {
                    i = i3;
                    i2 = i4;
                }
            }
        }
        int i5 = i + increment;
        int iInspectMatrix = -1;
        int i6 = i5;
        while (i6 >= 0 && i6 < cntX) {
            iInspectMatrix = inspectMatrix(i6, i2, cntX, cntY, matrix);
            if (iInspectMatrix != -1) {
                return iInspectMatrix;
            }
            i6 += increment;
        }
        for (int i7 = 1; i7 < cntY; i7++) {
            int i8 = i7 * increment;
            int i9 = i2 + i8;
            int i10 = i2 - i8;
            int i11 = i5;
            while (i11 >= 0 && i11 < cntX) {
                int iInspectMatrix2 = inspectMatrix(i11, i9, cntX, cntY, matrix);
                if (iInspectMatrix2 != -1) {
                    return iInspectMatrix2;
                }
                iInspectMatrix = inspectMatrix(i11, i10, cntX, cntY, matrix);
                if (iInspectMatrix != -1) {
                    return iInspectMatrix;
                }
                i11 += increment;
            }
        }
        return iInspectMatrix;
    }

    private static int handleDpadVertical(int iconIndex, int cntX, int cntY, int[][] matrix, int increment) {
        if (matrix == null) {
            throw new IllegalStateException("Dpad navigation requires a matrix.");
        }
        int i = -1;
        int i2 = -1;
        for (int i3 = 0; i3 < cntX; i3++) {
            for (int i4 = 0; i4 < cntY; i4++) {
                if (matrix[i3][i4] == iconIndex) {
                    i2 = i3;
                    i = i4;
                }
            }
        }
        int i5 = i + increment;
        int iInspectMatrix = -1;
        int i6 = i5;
        while (i6 >= 0 && i6 < cntY && i6 >= 0) {
            iInspectMatrix = inspectMatrix(i2, i6, cntX, cntY, matrix);
            if (iInspectMatrix != -1) {
                return iInspectMatrix;
            }
            i6 += increment;
        }
        for (int i7 = 1; i7 < cntX; i7++) {
            int i8 = i7 * increment;
            int i9 = i2 + i8;
            int i10 = i2 - i8;
            int i11 = i5;
            while (i11 >= 0 && i11 < cntY) {
                int iInspectMatrix2 = inspectMatrix(i9, i11, cntX, cntY, matrix);
                if (iInspectMatrix2 != -1) {
                    return iInspectMatrix2;
                }
                iInspectMatrix = inspectMatrix(i10, i11, cntX, cntY, matrix);
                if (iInspectMatrix != -1) {
                    return iInspectMatrix;
                }
                i11 += increment;
            }
        }
        return iInspectMatrix;
    }

    private static int inspectMatrix(int x, int y, int cntX, int cntY, int[][] matrix) {
        if (!isValid(x, y, cntX, cntY) || matrix[x][y] == -1) {
            return -1;
        }
        return matrix[x][y];
    }

    private static String getStringIndex(int index) {
        switch (index) {
            case NEXT_PAGE_LEFT_COLUMN /* -9 */:
                return "NEXT_PAGE_LEFT_COLUMN";
            case NEXT_PAGE_FIRST_ITEM /* -8 */:
                return "NEXT_PAGE_FIRST";
            case CURRENT_PAGE_LAST_ITEM /* -7 */:
                return "CURRENT_PAGE_LAST";
            case -6:
                return "CURRENT_PAGE_FIRST";
            case PREVIOUS_PAGE_LEFT_COLUMN /* -5 */:
            default:
                return Integer.toString(index);
            case -4:
                return "PREVIOUS_PAGE_LAST";
            case -3:
                return "PREVIOUS_PAGE_FIRST";
            case -2:
                return "PREVIOUS_PAGE_RIGHT_COLUMN";
            case -1:
                return "NOOP";
        }
    }

    private static void printMatrix(int[][] matrix) {
        Log.v(TAG, "\tprintMap:");
        int length = matrix[0].length;
        for (int i = 0; i < length; i++) {
            String str = "\t\t";
            for (int[] iArr : matrix) {
                str = str + String.format("%3d", Integer.valueOf(iArr[i]));
            }
            Log.v(TAG, str);
        }
    }

    public static View getAdjacentChildInNextPage(ShortcutAndWidgetContainer nextPage, View oldView, int edgeColumn) {
        int i = ((CellLayout.LayoutParams) oldView.getLayoutParams()).cellY;
        for (int countX = (edgeColumn == -9) ^ nextPage.invertLayoutHorizontally() ? 0 : ((CellLayout) nextPage.getParent()).getCountX() - 1; countX >= 0; countX--) {
            for (int i2 = i; i2 >= 0; i2--) {
                View childAt = nextPage.getChildAt(countX, i2);
                if (childAt != null) {
                    return childAt;
                }
            }
        }
        return null;
    }

    public static boolean isShortCutItem(CellLayout.LayoutParams lp) {
        return lp.cellHSpan == 1 && lp.cellVSpan == 1;
    }
}
