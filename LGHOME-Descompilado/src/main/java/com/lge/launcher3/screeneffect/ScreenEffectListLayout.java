package com.lge.launcher3.screeneffect;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/* JADX INFO: compiled from: ScreenEffectSelectionDialog.java */
/* JADX INFO: loaded from: classes.dex */
class ScreenEffectListLayout extends FrameLayout implements AdapterView.OnItemClickListener {
    public static final String TAG = "ScreenEffectListLayout";
    private ListView mListView;
    private ScreenEffectPreviewManager mScreenEffectPreviewManager;

    public ScreenEffectListLayout(Context context) {
        super(context);
        this.mListView = null;
        this.mScreenEffectPreviewManager = null;
        setupListView(context);
        ScreenEffectPreviewTargetManager.getInstance(this.mContext).setParent(this);
        this.mScreenEffectPreviewManager = new ScreenEffectPreviewManager(context);
    }

    private void setupListView(Context context) {
        ListView listView = new ListView(new ContextThemeWrapper(context, 34210229));
        this.mListView = listView;
        listView.setAdapter((ListAdapter) getAdapter(context));
        this.mListView.setChoiceMode(1);
        this.mListView.setClipChildren(false);
        this.mListView.setClipToPadding(false);
        this.mListView.setOnItemClickListener(this);
        addView(this.mListView);
    }

    private ArrayAdapter<String> getAdapter(Context context) {
        return new ArrayAdapter<>(context, 33751059, R.id.text1, ScreenEffectUtils.getScreenEffectList(context));
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view != null) {
            this.mListView.setSelectionFromTop(position, view.getTop());
        } else {
            this.mListView.setSelection(position);
        }
        this.mScreenEffectPreviewManager.startPreviewAnimation(position);
    }

    public void setItemCheckedAndSelection(int position) {
        this.mListView.setItemChecked(position, true);
        this.mListView.setSelection(position);
    }

    public int getCheckedItemPosition() {
        return this.mListView.getCheckedItemPosition();
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean zDrawChild = this.mScreenEffectPreviewManager.drawChild(canvas, child);
        return !zDrawChild ? super.drawChild(canvas, child, drawingTime) : zDrawChild;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mScreenEffectPreviewManager.isPreviewAnimationStarted()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void reset() {
        this.mScreenEffectPreviewManager.cancelPreviewAnimation();
    }

    public void destroy() {
        ScreenEffectPreviewManager screenEffectPreviewManager = this.mScreenEffectPreviewManager;
        if (screenEffectPreviewManager != null) {
            screenEffectPreviewManager.destroy();
            this.mScreenEffectPreviewManager = null;
        }
        ListView listView = this.mListView;
        if (listView != null) {
            listView.setLayerType(0, null);
            this.mListView = null;
        }
        removeAllViews();
    }
}
