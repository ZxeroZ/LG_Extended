package androidx.viewpager2.adapter;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes.dex */
public final class FragmentViewHolder extends RecyclerView.ViewHolder {
    Fragment mFragment;

    private FragmentViewHolder(FrameLayout frameLayout) {
        super(frameLayout);
    }

    static FragmentViewHolder create(ViewGroup viewGroup) {
        FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        frameLayout.setId(ViewCompat.generateViewId());
        frameLayout.setSaveEnabled(false);
        return new FragmentViewHolder(frameLayout);
    }

    FrameLayout getContainer() {
        return (FrameLayout) this.itemView;
    }
}
