package com.android.launcher3.allapps;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsSearchEditView;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.lge.launcher3.R;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
final class DefaultAppSearchController extends AllAppsSearchBarController implements TextWatcher, TextView.OnEditorActionListener, View.OnClickListener {
    private static final boolean ALLOW_SINGLE_APP_LAUNCH = true;
    private static final int FADE_IN_DURATION = 175;
    private static final int FADE_OUT_DURATION = 100;
    private static final int SEARCH_TRANSLATION_X_DP = 18;
    AllAppsRecyclerView mAppsRecyclerView;
    private ViewGroup mContainerView;
    private final Context mContext;
    private View mDismissSearchButtonView;
    Runnable mFocusRecyclerViewRunnable = new Runnable() { // from class: com.android.launcher3.allapps.DefaultAppSearchController.1
        @Override // java.lang.Runnable
        public void run() {
            DefaultAppSearchController.this.mAppsRecyclerView.requestFocus();
        }
    };
    final InputMethodManager mInputMethodManager;
    View mSearchBarContainerView;
    AllAppsSearchEditView mSearchBarEditView;
    private View mSearchButtonView;
    private DefaultAppSearchAlgorithm mSearchManager;
    private View mSearchView;

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController
    public boolean shouldShowPredictionBar() {
        return false;
    }

    public DefaultAppSearchController(Context context, ViewGroup containerView, AllAppsRecyclerView appsRecyclerView) {
        this.mContext = context;
        this.mInputMethodManager = (InputMethodManager) context.getSystemService("input_method");
        this.mContainerView = containerView;
        this.mAppsRecyclerView = appsRecyclerView;
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController
    public View getView(ViewGroup parent) {
        View viewInflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_apps_search_bar, parent, false);
        this.mSearchView = viewInflate;
        viewInflate.setOnClickListener(this);
        this.mSearchButtonView = this.mSearchView.findViewById(R.id.search_button);
        View viewFindViewById = this.mSearchView.findViewById(R.id.search_container);
        this.mSearchBarContainerView = viewFindViewById;
        View viewFindViewById2 = viewFindViewById.findViewById(R.id.dismiss_search_button);
        this.mDismissSearchButtonView = viewFindViewById2;
        viewFindViewById2.setOnClickListener(this);
        AllAppsSearchEditView allAppsSearchEditView = (AllAppsSearchEditView) this.mSearchBarContainerView.findViewById(R.id.search_box_input);
        this.mSearchBarEditView = allAppsSearchEditView;
        allAppsSearchEditView.addTextChangedListener(this);
        this.mSearchBarEditView.setOnEditorActionListener(this);
        this.mSearchBarEditView.setOnBackKeyListener(new AllAppsSearchEditView.OnBackKeyListener() { // from class: com.android.launcher3.allapps.DefaultAppSearchController.2
            @Override // com.android.launcher3.allapps.AllAppsSearchEditView.OnBackKeyListener
            public void onBackKey() {
                if (Utilities.trim(DefaultAppSearchController.this.mSearchBarEditView.getEditableText().toString()).isEmpty() || DefaultAppSearchController.this.mApps.hasNoFilteredResults()) {
                    DefaultAppSearchController defaultAppSearchController = DefaultAppSearchController.this;
                    defaultAppSearchController.hideSearchField(true, defaultAppSearchController.mFocusRecyclerViewRunnable);
                }
            }
        });
        return this.mSearchView;
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController
    public void focusSearchField() {
        this.mSearchBarEditView.requestFocus();
        showSearchField();
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController
    public boolean isSearchFieldFocused() {
        return this.mSearchBarEditView.isFocused();
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController
    protected void onInitialize() {
        this.mSearchManager = new DefaultAppSearchAlgorithm(this.mApps.getApps());
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController
    public void reset() {
        hideSearchField(false, null);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mSearchView) {
            showSearchField();
        } else if (v == this.mDismissSearchButtonView) {
            hideSearchField(true, this.mFocusRecyclerViewRunnable);
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(final Editable s) {
        String string = s.toString();
        if (string.isEmpty()) {
            this.mSearchManager.cancel(true);
            this.mCb.clearSearchResult();
        } else {
            this.mSearchManager.cancel(false);
            this.mSearchManager.doSearch(string, this.mCb);
        }
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != 6 || this.mApps.getSize() != 1) {
            return false;
        }
        List<AlphabeticalAppsList.AdapterItem> adapterItems = this.mApps.getAdapterItems();
        for (int i = 0; i < adapterItems.size(); i++) {
            if (adapterItems.get(i).viewType == 1) {
                this.mAppsRecyclerView.getChildAt(i).performClick();
                this.mInputMethodManager.hideSoftInputFromWindow(this.mContainerView.getWindowToken(), 0);
                return true;
            }
        }
        return false;
    }

    private void showSearchField() {
        int iPxFromDp = Utilities.pxFromDp(18.0f, this.mContext.getResources().getDisplayMetrics());
        this.mSearchBarContainerView.setVisibility(0);
        this.mSearchBarContainerView.setAlpha(0.0f);
        this.mSearchBarContainerView.setTranslationX(iPxFromDp);
        this.mSearchBarContainerView.animate().alpha(1.0f).translationX(0.0f).setDuration(175L).withLayer().withEndAction(new Runnable() { // from class: com.android.launcher3.allapps.DefaultAppSearchController.3
            @Override // java.lang.Runnable
            public void run() {
                DefaultAppSearchController.this.mSearchBarEditView.requestFocus();
                DefaultAppSearchController.this.mInputMethodManager.showSoftInput(DefaultAppSearchController.this.mSearchBarEditView, 1);
            }
        });
        this.mSearchButtonView.animate().alpha(0.0f).translationX(-iPxFromDp).setDuration(100L).withLayer();
    }

    void hideSearchField(boolean animated, final Runnable postAnimationRunnable) {
        this.mSearchManager.cancel(true);
        final boolean z = this.mSearchBarEditView.getText().toString().length() > 0;
        int iPxFromDp = Utilities.pxFromDp(18.0f, this.mContext.getResources().getDisplayMetrics());
        if (animated) {
            this.mSearchBarContainerView.animate().alpha(0.0f).translationX(0.0f).setDuration(175L).withLayer().withEndAction(new Runnable() { // from class: com.android.launcher3.allapps.DefaultAppSearchController.4
                @Override // java.lang.Runnable
                public void run() {
                    DefaultAppSearchController.this.mSearchBarContainerView.setVisibility(4);
                    if (z) {
                        DefaultAppSearchController.this.mSearchBarEditView.setText("");
                    }
                    DefaultAppSearchController.this.mCb.clearSearchResult();
                    Runnable runnable = postAnimationRunnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            this.mSearchButtonView.setTranslationX(-iPxFromDp);
            this.mSearchButtonView.animate().alpha(1.0f).translationX(0.0f).setDuration(100L).withLayer();
        } else {
            this.mSearchBarContainerView.setVisibility(4);
            if (z) {
                this.mSearchBarEditView.setText("");
            }
            this.mCb.clearSearchResult();
            this.mSearchButtonView.setAlpha(1.0f);
            this.mSearchButtonView.setTranslationX(0.0f);
            if (postAnimationRunnable != null) {
                postAnimationRunnable.run();
            }
        }
        this.mInputMethodManager.hideSoftInputFromWindow(this.mContainerView.getWindowToken(), 0);
    }
}
