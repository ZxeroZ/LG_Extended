package com.android.launcher3.allapps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.lge.launcher3.R;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class AllAppsGridAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final boolean DEBUG = false;
    public static final int EMPTY_SEARCH_VIEW_TYPE = 3;
    public static final int ICON_VIEW_TYPE = 1;
    public static final int PREDICTION_ICON_VIEW_TYPE = 2;
    public static final int SECTION_BREAK_VIEW_TYPE = 0;
    public static final String TAG = "AppsGridAdapter";
    AlphabeticalAppsList mApps;
    int mAppsPerRow;
    final Rect mBackgroundPadding = new Rect();
    private String mEmptySearchText;
    private GridLayoutManager mGridLayoutMgr;
    private GridSpanSizer mGridSizer;
    private View.OnClickListener mIconClickListener;
    private View.OnLongClickListener mIconLongClickListener;
    boolean mIsRtl;
    private GridItemDecoration mItemDecoration;
    private LayoutInflater mLayoutInflater;
    Paint mPredictedAppsDividerPaint;
    int mPredictionBarDividerOffset;
    int mSectionHeaderOffset;
    int mSectionNamesMargin;
    Paint mSectionTextPaint;
    private View.OnTouchListener mTouchListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mContent;

        public ViewHolder(View v) {
            super(v);
            this.mContent = v;
        }
    }

    public class GridSpanSizer extends GridLayoutManager.SpanSizeLookup {
        public GridSpanSizer() {
            setSpanIndexCacheEnabled(true);
        }

        @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
        public int getSpanSize(int position) {
            if (AllAppsGridAdapter.this.mApps.hasNoFilteredResults()) {
                return AllAppsGridAdapter.this.mAppsPerRow;
            }
            int i = AllAppsGridAdapter.this.mApps.getAdapterItems().get(position).viewType;
            if (i == 1 || i == 2) {
                return 1;
            }
            return AllAppsGridAdapter.this.mAppsPerRow;
        }
    }

    public class GridItemDecoration extends RecyclerView.ItemDecoration {
        private static final boolean DEBUG_SECTION_MARGIN = false;
        private static final boolean FADE_OUT_SECTIONS = false;
        private HashMap<String, PointF> mCachedSectionBounds = new HashMap<>();
        private Rect mTmpBounds = new Rect();

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        }

        public GridItemDecoration() {
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x0041  */
        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void onDraw(android.graphics.Canvas r24, androidx.recyclerview.widget.RecyclerView r25, androidx.recyclerview.widget.RecyclerView.State r26) {
            /*
                r23 = this;
                r0 = r23
                r1 = r25
                com.android.launcher3.allapps.AllAppsGridAdapter r2 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                com.android.launcher3.allapps.AlphabeticalAppsList r2 = r2.mApps
                boolean r2 = r2.hasFilter()
                if (r2 != 0) goto L1ae
                com.android.launcher3.allapps.AllAppsGridAdapter r2 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r2 = r2.mAppsPerRow
                if (r2 != 0) goto L16
                goto L1ae
            L16:
                com.android.launcher3.allapps.AllAppsGridAdapter r2 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                com.android.launcher3.allapps.AlphabeticalAppsList r2 = r2.mApps
                java.util.List r2 = r2.getAdapterItems()
                com.android.launcher3.allapps.AllAppsGridAdapter r3 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r3 = r3.mSectionNamesMargin
                r4 = 0
                if (r3 <= 0) goto L27
                r3 = 1
                goto L28
            L27:
                r3 = r4
            L28:
                int r6 = r25.getChildCount()
                r7 = r4
                r8 = r7
                r9 = r8
            L2f:
                if (r4 >= r6) goto L1ae
                android.view.View r10 = r1.getChildAt(r4)
                androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r1.getChildViewHolder(r10)
                com.android.launcher3.allapps.AllAppsGridAdapter$ViewHolder r11 = (com.android.launcher3.allapps.AllAppsGridAdapter.ViewHolder) r11
                boolean r12 = r0.isValidHolderAndChild(r11, r10, r2)
                if (r12 != 0) goto L4d
            L41:
                r10 = r24
                r16 = r3
                r20 = r4
                r17 = r6
                r18 = r7
                goto L1a0
            L4d:
                boolean r12 = r0.shouldDrawItemDivider(r11, r2)
                if (r12 == 0) goto L8d
                if (r7 != 0) goto L8d
                int r7 = r10.getTop()
                int r10 = r10.getHeight()
                int r7 = r7 + r10
                com.android.launcher3.allapps.AllAppsGridAdapter r10 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r10 = r10.mPredictionBarDividerOffset
                int r7 = r7 + r10
                com.android.launcher3.allapps.AllAppsGridAdapter r10 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                android.graphics.Rect r10 = r10.mBackgroundPadding
                int r10 = r10.left
                float r12 = (float) r10
                float r15 = (float) r7
                int r7 = r25.getWidth()
                com.android.launcher3.allapps.AllAppsGridAdapter r10 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                android.graphics.Rect r10 = r10.mBackgroundPadding
                int r10 = r10.right
                int r7 = r7 - r10
                float r14 = (float) r7
                com.android.launcher3.allapps.AllAppsGridAdapter r7 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                android.graphics.Paint r7 = r7.mPredictedAppsDividerPaint
                r11 = r24
                r13 = r15
                r16 = r7
                r11.drawLine(r12, r13, r14, r15, r16)
                r10 = r24
                r16 = r3
                r17 = r6
                r1 = 1
                r7 = 1
                goto L1a5
            L8d:
                if (r3 == 0) goto L41
                boolean r12 = r0.shouldDrawItemSection(r11, r4, r2)
                if (r12 == 0) goto L41
                int r12 = r10.getPaddingTop()
                int r12 = r12 * 2
                int r11 = r11.getPosition()
                java.lang.Object r13 = r2.get(r11)
                com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r13 = (com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem) r13
                com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo r14 = r13.sectionInfo
                java.lang.String r15 = r13.sectionName
                int r5 = r13.sectionAppIndex
            Lab:
                int r1 = r14.numApps
                if (r5 >= r1) goto L188
                java.lang.Object r1 = r2.get(r11)
                com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r1 = (com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem) r1
                r16 = r3
                java.lang.String r3 = r1.sectionName
                com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo r1 = r1.sectionInfo
                if (r1 == r14) goto Lc1
                r10 = r24
                goto L18c
            Lc1:
                int r1 = r13.sectionAppIndex
                if (r5 <= r1) goto Ldb
                boolean r1 = r3.equals(r15)
                if (r1 == 0) goto Ldb
                r20 = r4
                r17 = r6
                r18 = r7
                r22 = r10
                r19 = r12
                r21 = r13
                r10 = r24
                goto L174
            Ldb:
                android.graphics.PointF r1 = r0.getAndCacheSectionBounds(r3)
                float r15 = (float) r12
                r17 = r6
                float r6 = r1.y
                float r15 = r15 + r6
                int r6 = (int) r15
                com.android.launcher3.allapps.AllAppsGridAdapter r15 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                boolean r15 = r15.mIsRtl
                if (r15 == 0) goto Lff
                int r15 = r25.getWidth()
                r18 = r7
                com.android.launcher3.allapps.AllAppsGridAdapter r7 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                android.graphics.Rect r7 = r7.mBackgroundPadding
                int r7 = r7.left
                int r15 = r15 - r7
                com.android.launcher3.allapps.AllAppsGridAdapter r7 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r7 = r7.mSectionNamesMargin
                int r15 = r15 - r7
                goto L107
            Lff:
                r18 = r7
                com.android.launcher3.allapps.AllAppsGridAdapter r7 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                android.graphics.Rect r7 = r7.mBackgroundPadding
                int r15 = r7.left
            L107:
                com.android.launcher3.allapps.AllAppsGridAdapter r7 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r7 = r7.mSectionNamesMargin
                float r7 = (float) r7
                r19 = r12
                float r12 = r1.x
                float r7 = r7 - r12
                r12 = 1073741824(0x40000000, float:2.0)
                float r7 = r7 / r12
                int r7 = (int) r7
                int r15 = r15 + r7
                int r7 = r10.getTop()
                int r7 = r7 + r6
                java.lang.Object r12 = r2.get(r11)
                com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r12 = (com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem) r12
                int r12 = r12.sectionAppIndex
                int r20 = r2.size()
                r22 = r10
                r21 = 1
                int r10 = r20 + (-1)
                r20 = r4
                com.android.launcher3.allapps.AllAppsGridAdapter r4 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r4 = r4.mAppsPerRow
                int r4 = r4 + r11
                r21 = r13
                com.android.launcher3.allapps.AllAppsGridAdapter r13 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r13 = r13.mAppsPerRow
                int r12 = r12 % r13
                int r4 = r4 - r12
                int r4 = java.lang.Math.min(r10, r4)
                java.lang.Object r4 = r2.get(r4)
                com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = (com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem) r4
                java.lang.String r4 = r4.sectionName
                boolean r4 = r3.equals(r4)
                r10 = 1
                r4 = r4 ^ r10
                if (r4 != 0) goto L154
                int r7 = java.lang.Math.max(r6, r7)
            L154:
                if (r8 <= 0) goto L15d
                int r4 = r9 + r8
                if (r7 > r4) goto L15d
                int r9 = r9 - r7
                int r9 = r9 + r8
                int r7 = r7 + r9
            L15d:
                float r4 = (float) r15
                float r6 = (float) r7
                com.android.launcher3.allapps.AllAppsGridAdapter r8 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                android.graphics.Paint r8 = r8.mSectionTextPaint
                r10 = r24
                r10.drawText(r3, r4, r6, r8)
                float r1 = r1.y
                com.android.launcher3.allapps.AllAppsGridAdapter r4 = com.android.launcher3.allapps.AllAppsGridAdapter.this
                int r4 = r4.mSectionHeaderOffset
                float r4 = (float) r4
                float r1 = r1 + r4
                int r1 = (int) r1
                r8 = r1
                r15 = r3
                r9 = r7
            L174:
                int r5 = r5 + 1
                int r11 = r11 + 1
                r3 = r16
                r6 = r17
                r7 = r18
                r12 = r19
                r4 = r20
                r13 = r21
                r10 = r22
                goto Lab
            L188:
                r10 = r24
                r16 = r3
            L18c:
                r20 = r4
                r17 = r6
                r18 = r7
                r21 = r13
                int r1 = r14.numApps
                r13 = r21
                int r3 = r13.sectionAppIndex
                int r1 = r1 - r3
                int r4 = r20 + r1
                r7 = r18
                goto L1a4
            L1a0:
                r7 = r18
                r4 = r20
            L1a4:
                r1 = 1
            L1a5:
                int r4 = r4 + r1
                r1 = r25
                r3 = r16
                r6 = r17
                goto L2f
            L1ae:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AllAppsGridAdapter.GridItemDecoration.onDraw(android.graphics.Canvas, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$State):void");
        }

        private PointF getAndCacheSectionBounds(String sectionName) {
            PointF pointF = this.mCachedSectionBounds.get(sectionName);
            if (pointF != null) {
                return pointF;
            }
            AllAppsGridAdapter.this.mSectionTextPaint.getTextBounds(sectionName, 0, sectionName.length(), this.mTmpBounds);
            PointF pointF2 = new PointF(AllAppsGridAdapter.this.mSectionTextPaint.measureText(sectionName), this.mTmpBounds.height());
            this.mCachedSectionBounds.put(sectionName, pointF2);
            return pointF2;
        }

        private boolean isValidHolderAndChild(ViewHolder holder, View child, List<AlphabeticalAppsList.AdapterItem> items) {
            int position;
            return !((GridLayoutManager.LayoutParams) child.getLayoutParams()).isItemRemoved() && holder != null && (position = holder.getPosition()) >= 0 && position < items.size();
        }

        private boolean shouldDrawItemDivider(ViewHolder holder, List<AlphabeticalAppsList.AdapterItem> items) {
            return items.get(holder.getPosition()).viewType == 2;
        }

        private boolean shouldDrawItemSection(ViewHolder holder, int childIndex, List<AlphabeticalAppsList.AdapterItem> items) {
            int position = holder.getPosition();
            if (items.get(position).viewType != 1) {
                return false;
            }
            return childIndex == 0 || items.get(position - 1).viewType == 0;
        }
    }

    public AllAppsGridAdapter(Context context, AlphabeticalAppsList apps, View.OnTouchListener touchListener, View.OnClickListener iconClickListener, View.OnLongClickListener iconLongClickListener) {
        Resources resources = context.getResources();
        this.mApps = apps;
        this.mGridSizer = new GridSpanSizer();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1, 1, false);
        this.mGridLayoutMgr = gridLayoutManager;
        gridLayoutManager.setSpanSizeLookup(this.mGridSizer);
        this.mItemDecoration = new GridItemDecoration();
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mTouchListener = touchListener;
        this.mIconClickListener = iconClickListener;
        this.mIconLongClickListener = iconLongClickListener;
        this.mSectionNamesMargin = resources.getDimensionPixelSize(R.dimen.all_apps_grid_view_start_margin);
        this.mSectionHeaderOffset = resources.getDimensionPixelSize(R.dimen.all_apps_grid_section_y_offset);
        Paint paint = new Paint();
        this.mSectionTextPaint = paint;
        paint.setTextSize(resources.getDimensionPixelSize(R.dimen.all_apps_grid_section_text_size));
        this.mSectionTextPaint.setColor(resources.getColor(R.color.all_apps_grid_section_text_color));
        this.mSectionTextPaint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mPredictedAppsDividerPaint = paint2;
        paint2.setStrokeWidth(Utilities.pxFromDp(1.0f, resources.getDisplayMetrics()));
        this.mPredictedAppsDividerPaint.setColor(503316480);
        this.mPredictedAppsDividerPaint.setAntiAlias(true);
        this.mPredictionBarDividerOffset = ((-resources.getDimensionPixelSize(R.dimen.all_apps_prediction_icon_bottom_padding)) + resources.getDimensionPixelSize(R.dimen.all_apps_icon_top_bottom_padding)) / 2;
    }

    public void setNumAppsPerRow(int appsPerRow) {
        this.mAppsPerRow = appsPerRow;
        this.mGridLayoutMgr.setSpanCount(appsPerRow);
    }

    public void setRtl(boolean rtl) {
        this.mIsRtl = rtl;
    }

    public void setEmptySearchText(String query) {
        this.mEmptySearchText = query;
    }

    public void updateBackgroundPadding(Rect padding) {
        this.mBackgroundPadding.set(padding);
    }

    public GridLayoutManager getLayoutManager() {
        return this.mGridLayoutMgr;
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return this.mItemDecoration;
    }

    /* JADX DEBUG: Method merged with bridge method: onCreateViewHolder(Landroid/view/ViewGroup;I)Landroidx/recyclerview/widget/RecyclerView$ViewHolder; */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ViewHolder(new View(parent.getContext()));
        }
        if (viewType == 1) {
            BubbleTextView bubbleTextView = (BubbleTextView) this.mLayoutInflater.inflate(R.layout.all_apps_icon, parent, false);
            bubbleTextView.setOnTouchListener(this.mTouchListener);
            bubbleTextView.setOnClickListener(this.mIconClickListener);
            bubbleTextView.setOnLongClickListener(this.mIconLongClickListener);
            ViewConfiguration.get(parent.getContext());
            bubbleTextView.setLongPressTimeout(ViewConfiguration.getLongPressTimeout());
            bubbleTextView.setFocusable(true);
            return new ViewHolder(bubbleTextView);
        }
        if (viewType != 2) {
            if (viewType == 3) {
                return new ViewHolder(this.mLayoutInflater.inflate(R.layout.all_apps_empty_search, parent, false));
            }
            throw new RuntimeException("Unexpected view type");
        }
        BubbleTextView bubbleTextView2 = (BubbleTextView) this.mLayoutInflater.inflate(R.layout.all_apps_prediction_bar_icon, parent, false);
        bubbleTextView2.setOnTouchListener(this.mTouchListener);
        bubbleTextView2.setOnClickListener(this.mIconClickListener);
        bubbleTextView2.setOnLongClickListener(this.mIconLongClickListener);
        ViewConfiguration.get(parent.getContext());
        bubbleTextView2.setLongPressTimeout(ViewConfiguration.getLongPressTimeout());
        bubbleTextView2.setFocusable(true);
        return new ViewHolder(bubbleTextView2);
    }

    /* JADX DEBUG: Method merged with bridge method: onBindViewHolder(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;I)V */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 1) {
            ((BubbleTextView) holder.mContent).applyFromApplicationInfo(this.mApps.getAdapterItems().get(position).appInfo);
        } else if (itemViewType == 2) {
            ((BubbleTextView) holder.mContent).applyFromApplicationInfo(this.mApps.getAdapterItems().get(position).appInfo);
        } else {
            if (itemViewType != 3) {
                return;
            }
            ((TextView) holder.mContent.findViewById(R.id.empty_text)).setText(this.mEmptySearchText);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.mApps.hasNoFilteredResults()) {
            return 1;
        }
        return this.mApps.getAdapterItems().size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        if (this.mApps.hasNoFilteredResults()) {
            return 3;
        }
        return this.mApps.getAdapterItems().get(position).viewType;
    }
}
