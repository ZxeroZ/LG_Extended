package androidx.viewpager2.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes.dex */
public abstract class FragmentStateAdapter extends RecyclerView.Adapter<FragmentViewHolder> implements StatefulAdapter {
    private static final String KEY_PREFIX_FRAGMENT = "f#";
    private static final String KEY_PREFIX_STATE = "s#";
    private final FragmentManager mFragmentManager;
    private final LongSparseArray<Fragment> mFragments = new LongSparseArray<>();
    private final LongSparseArray<Fragment.SavedState> mSavedStates = new LongSparseArray<>();

    public abstract Fragment getItem(int i);

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i;
    }

    public FragmentStateAdapter(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
        super.setHasStableIds(true);
    }

    /* JADX DEBUG: Method merged with bridge method: onCreateViewHolder(Landroid/view/ViewGroup;I)Landroidx/recyclerview/widget/RecyclerView$ViewHolder; */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final FragmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return FragmentViewHolder.create(viewGroup);
    }

    /* JADX DEBUG: Method merged with bridge method: onBindViewHolder(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;I)V */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final void onBindViewHolder(final FragmentViewHolder fragmentViewHolder, int i) {
        Fragment fragment = getFragment(i);
        if (fragmentViewHolder.mFragment != fragment) {
            removeFragment(fragmentViewHolder);
        }
        fragmentViewHolder.mFragment = fragment;
        final FrameLayout container = fragmentViewHolder.getContainer();
        if (ViewCompat.isAttachedToWindow(container)) {
            if (container.getParent() != null) {
                throw new IllegalStateException("Design assumption violated.");
            }
            container.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: androidx.viewpager2.adapter.FragmentStateAdapter.1
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                    if (container.getParent() != null) {
                        container.removeOnLayoutChangeListener(this);
                        FragmentStateAdapter.this.onViewAttachedToWindow(fragmentViewHolder);
                    }
                }
            });
        }
    }

    private Fragment getFragment(int i) {
        long itemId = getItemId(i);
        Fragment fragment = this.mFragments.get(itemId);
        if (fragment != null) {
            return fragment;
        }
        Fragment item = getItem(i);
        item.setInitialSavedState(this.mSavedStates.get(itemId));
        this.mFragments.put(itemId, item);
        return item;
    }

    /* JADX DEBUG: Method merged with bridge method: onViewAttachedToWindow(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;)V */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final void onViewAttachedToWindow(FragmentViewHolder fragmentViewHolder) {
        Fragment fragment = fragmentViewHolder.mFragment;
        FrameLayout container = fragmentViewHolder.getContainer();
        View view = fragment.getView();
        if (!fragment.isAdded() && view != null) {
            throw new IllegalStateException("Design assumption violated.");
        }
        if (fragment.isAdded() && view == null) {
            scheduleViewAttach(fragment, container);
            return;
        }
        if (fragment.isAdded() && view.getParent() != null) {
            if (view.getParent() != container) {
                addViewToContainer(view, container);
            }
        } else if (fragment.isAdded()) {
            addViewToContainer(view, container);
        } else {
            scheduleViewAttach(fragment, container);
            this.mFragmentManager.beginTransaction().add(fragment, "f" + fragmentViewHolder.getItemId()).commitNow();
        }
    }

    private void scheduleViewAttach(final Fragment fragment, final FrameLayout frameLayout) {
        this.mFragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() { // from class: androidx.viewpager2.adapter.FragmentStateAdapter.2
            @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
            public void onFragmentViewCreated(FragmentManager fragmentManager, Fragment fragment2, View view, Bundle bundle) {
                if (fragment2 == fragment) {
                    fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                    FragmentStateAdapter.this.addViewToContainer(view, frameLayout);
                }
            }
        }, false);
    }

    void addViewToContainer(View view, FrameLayout frameLayout) {
        if (frameLayout.getChildCount() > 1) {
            throw new IllegalStateException("Design assumption violated.");
        }
        if (view.getParent() == frameLayout) {
            return;
        }
        if (frameLayout.getChildCount() > 0) {
            frameLayout.removeAllViews();
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        frameLayout.addView(view);
    }

    /* JADX DEBUG: Method merged with bridge method: onViewRecycled(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;)V */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final void onViewRecycled(FragmentViewHolder fragmentViewHolder) {
        removeFragment(fragmentViewHolder);
    }

    /* JADX DEBUG: Method merged with bridge method: onFailedToRecycleView(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;)Z */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final boolean onFailedToRecycleView(FragmentViewHolder fragmentViewHolder) {
        removeFragment(fragmentViewHolder);
        return false;
    }

    private void removeFragment(FragmentViewHolder fragmentViewHolder) {
        removeFragment(fragmentViewHolder.mFragment, fragmentViewHolder.getItemId());
        fragmentViewHolder.getContainer().removeAllViews();
        fragmentViewHolder.mFragment = null;
    }

    private void removeFragment(Fragment fragment, long j) {
        FragmentTransaction fragmentTransactionBeginTransaction = this.mFragmentManager.beginTransaction();
        removeFragment(fragment, j, fragmentTransactionBeginTransaction);
        fragmentTransactionBeginTransaction.commitNow();
    }

    private void removeFragment(Fragment fragment, long j, FragmentTransaction fragmentTransaction) {
        if (fragment == null) {
            return;
        }
        if (fragment.isAdded() && containsItem(j)) {
            this.mSavedStates.put(j, this.mFragmentManager.saveFragmentInstanceState(fragment));
        }
        this.mFragments.remove(j);
        fragmentTransaction.remove(fragment);
    }

    public boolean containsItem(long j) {
        return j >= 0 && j < ((long) getItemCount());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public final void setHasStableIds(boolean z) {
        throw new UnsupportedOperationException("Stable Ids are required for the adapter to function properly, and the adapter takes care of setting the flag.");
    }

    @Override // androidx.viewpager2.adapter.StatefulAdapter
    public Parcelable saveState() {
        Bundle bundle = new Bundle(this.mFragments.size() + this.mSavedStates.size());
        for (int i = 0; i < this.mFragments.size(); i++) {
            long jKeyAt = this.mFragments.keyAt(i);
            Fragment fragment = this.mFragments.get(jKeyAt);
            if (fragment != null && fragment.isAdded()) {
                this.mFragmentManager.putFragment(bundle, createKey(KEY_PREFIX_FRAGMENT, jKeyAt), fragment);
            }
        }
        for (int i2 = 0; i2 < this.mSavedStates.size(); i2++) {
            long jKeyAt2 = this.mSavedStates.keyAt(i2);
            if (containsItem(jKeyAt2)) {
                bundle.putParcelable(createKey(KEY_PREFIX_STATE, jKeyAt2), this.mSavedStates.get(jKeyAt2));
            }
        }
        return bundle;
    }

    @Override // androidx.viewpager2.adapter.StatefulAdapter
    public void restoreState(Parcelable parcelable) {
        if (!this.mSavedStates.isEmpty() || !this.mFragments.isEmpty()) {
            throw new IllegalStateException("Expected the adapter to be 'fresh' while restoring state.");
        }
        Bundle bundle = (Bundle) parcelable;
        for (String str : bundle.keySet()) {
            if (isValidKey(str, KEY_PREFIX_FRAGMENT)) {
                this.mFragments.put(parseIdFromKey(str, KEY_PREFIX_FRAGMENT), this.mFragmentManager.getFragment(bundle, str));
            } else if (isValidKey(str, KEY_PREFIX_STATE)) {
                this.mSavedStates.put(parseIdFromKey(str, KEY_PREFIX_STATE), (Fragment.SavedState) bundle.getParcelable(str));
            } else {
                throw new IllegalArgumentException("Unexpected key in savedState: " + str);
            }
        }
    }

    private static String createKey(String str, long j) {
        return str + j;
    }

    private static boolean isValidKey(String str, String str2) {
        return str.startsWith(str2) && str.length() > str2.length();
    }

    private static long parseIdFromKey(String str, String str2) {
        return Long.parseLong(str.substring(str2.length()));
    }
}
