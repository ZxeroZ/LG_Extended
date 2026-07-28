package com.google.android.material.picker;

import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.google.android.material.picker.MaterialCalendar;
import com.google.android.material.picker.selector.GridSelector;

/* JADX INFO: loaded from: classes.dex */
class MonthsPagerAdapter extends FragmentStatePagerAdapter {
    private final Month firstPage;
    private final GridSelector<?> gridSelector;
    private final Month lastPage;
    private final SparseArray<DataSetObserver> observingFragments;
    private final MaterialCalendar.OnDayClickListener onDayClickListener;
    private final int startIndex;

    MonthsPagerAdapter(FragmentManager fragmentManager, GridSelector<?> gridSelector, Month month, Month month2, Month month3, MaterialCalendar.OnDayClickListener onDayClickListener) {
        super(fragmentManager);
        this.observingFragments = new SparseArray<>();
        if (month.compareTo(month3) > 0) {
            throw new IllegalArgumentException("firstPage cannot be after startPage");
        }
        if (month3.compareTo(month2) > 0) {
            throw new IllegalArgumentException("startPage cannot be after lastPage");
        }
        this.firstPage = month;
        this.lastPage = month2;
        this.startIndex = month.monthsUntil(month3);
        this.gridSelector = gridSelector;
        this.onDayClickListener = onDayClickListener;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.firstPage.monthsUntil(this.lastPage) + 1;
    }

    /* JADX DEBUG: Method merged with bridge method: instantiateItem(Landroid/view/ViewGroup;I)Ljava/lang/Object; */
    @Override // androidx.fragment.app.FragmentStatePagerAdapter, androidx.viewpager.widget.PagerAdapter
    public Fragment instantiateItem(ViewGroup viewGroup, int i) {
        MonthFragment monthFragment = (MonthFragment) super.instantiateItem(viewGroup, i);
        monthFragment.setOnDayClickListener(this.onDayClickListener);
        return monthFragment;
    }

    /* JADX DEBUG: Method merged with bridge method: getItem(I)Landroidx/fragment/app/Fragment; */
    @Override // androidx.fragment.app.FragmentStatePagerAdapter
    public MonthFragment getItem(int i) {
        final MonthFragment monthFragmentNewInstance = MonthFragment.newInstance(this.firstPage.monthsLater(i), this.gridSelector);
        DataSetObserver dataSetObserver = new DataSetObserver() { // from class: com.google.android.material.picker.MonthsPagerAdapter.1
            @Override // android.database.DataSetObserver
            public void onChanged() {
                monthFragmentNewInstance.notifyDataSetChanged();
            }
        };
        registerDataSetObserver(dataSetObserver);
        this.observingFragments.put(i, dataSetObserver);
        return monthFragmentNewInstance;
    }

    @Override // androidx.fragment.app.FragmentStatePagerAdapter, androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        DataSetObserver dataSetObserver = this.observingFragments.get(i);
        if (dataSetObserver != null) {
            this.observingFragments.remove(i);
            unregisterDataSetObserver(dataSetObserver);
        }
        super.destroyItem(viewGroup, i, obj);
    }

    int getStartPosition() {
        return this.startIndex;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public CharSequence getPageTitle(int i) {
        return getPageMonth(i).getLongName();
    }

    Month getPageMonth(int i) {
        return this.firstPage.monthsLater(i);
    }
}
