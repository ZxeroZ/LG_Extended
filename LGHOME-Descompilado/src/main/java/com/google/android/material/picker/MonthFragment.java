package com.google.android.material.picker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.fragment.app.Fragment;
import com.google.android.material.R;
import com.google.android.material.picker.MaterialCalendar;
import com.google.android.material.picker.selector.GridSelector;

/* JADX INFO: loaded from: classes.dex */
public class MonthFragment extends Fragment {
    private static final String GRID_SELECTOR_KEY = "GRID_SELECTOR_KEY";
    private static final String MONTH_KEY = "MONTH_KEY";
    private Month month;
    private MonthAdapter monthAdapter;
    private MaterialCalendar.OnDayClickListener onDayClickListener;

    public void setOnDayClickListener(MaterialCalendar.OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }

    public static MonthFragment newInstance(Month month, GridSelector<?> gridSelector) {
        MonthFragment monthFragment = new MonthFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MONTH_KEY, month);
        bundle.putParcelable(GRID_SELECTOR_KEY, gridSelector);
        monthFragment.setArguments(bundle);
        return monthFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.month = (Month) getArguments().getParcelable(MONTH_KEY);
        this.monthAdapter = new MonthAdapter(getContext(), this.month, (GridSelector) getArguments().getParcelable(GRID_SELECTOR_KEY));
    }

    /* JADX DEBUG: Method merged with bridge method: onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View; */
    @Override // androidx.fragment.app.Fragment
    public GridView onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        GridView gridView = (GridView) layoutInflater.inflate(R.layout.mtrl_month_grid, viewGroup, false).findViewById(R.id.month_grid);
        gridView.setNumColumns(this.month.daysInWeek);
        gridView.setAdapter((ListAdapter) this.monthAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.google.android.material.picker.MonthFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (MonthFragment.this.onDayClickListener == null || !MonthFragment.this.monthAdapter.withinMonth(i)) {
                    return;
                }
                MonthFragment.this.onDayClickListener.onDayClick(MonthFragment.this.monthAdapter.getItem(i));
            }
        });
        return gridView;
    }

    void notifyDataSetChanged() {
        this.monthAdapter.notifyDataSetChanged();
    }
}
