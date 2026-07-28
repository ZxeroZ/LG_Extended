package com.google.android.material.picker;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.picker.selector.GridSelector;
import com.google.android.material.resources.MaterialAttributes;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;

/* JADX INFO: loaded from: classes.dex */
public final class MaterialCalendar<S> extends Fragment {
    private static final String CALENDAR_BOUNDS_KEY = "CALENDAR_BOUNDS_KEY";
    private static final String GRID_SELECTOR_KEY = "GRID_SELECTOR_KEY";
    private static final String THEME_RES_ID_KEY = "THEME_RES_ID_KEY";
    public static final Object VIEW_PAGER_TAG = "VIEW_PAGER_TAG";
    private CalendarBounds calendarBounds;
    private GridSelector<S> gridSelector;
    private MonthsPagerAdapter monthsPagerAdapter;
    private final LinkedHashSet<OnSelectionChangedListener<S>> onSelectionChangedListeners = new LinkedHashSet<>();
    private int themeResId;

    interface OnDayClickListener {
        void onDayClick(Calendar calendar);
    }

    interface OnSelectionChangedListener<S> {
        void onSelectionChanged(S s);
    }

    public static <T> MaterialCalendar<T> newInstance(GridSelector<T> gridSelector, int i, CalendarBounds calendarBounds) {
        MaterialCalendar<T> materialCalendar = new MaterialCalendar<>();
        Bundle bundle = new Bundle();
        bundle.putInt(THEME_RES_ID_KEY, i);
        bundle.putParcelable(GRID_SELECTOR_KEY, gridSelector);
        bundle.putParcelable(CALENDAR_BOUNDS_KEY, calendarBounds);
        materialCalendar.setArguments(bundle);
        return materialCalendar;
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(THEME_RES_ID_KEY, this.themeResId);
        bundle.putParcelable(GRID_SELECTOR_KEY, this.gridSelector);
        bundle.putParcelable(CALENDAR_BOUNDS_KEY, this.calendarBounds);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            bundle = getArguments();
        }
        this.themeResId = bundle.getInt(THEME_RES_ID_KEY);
        this.gridSelector = (GridSelector) bundle.getParcelable(GRID_SELECTOR_KEY);
        this.calendarBounds = (CalendarBounds) bundle.getParcelable(CALENDAR_BOUNDS_KEY);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LayoutInflater layoutInflaterCloneInContext = layoutInflater.cloneInContext(new ContextThemeWrapper(getContext(), this.themeResId));
        Month start = this.calendarBounds.getStart();
        Month end = this.calendarBounds.getEnd();
        Month current = this.calendarBounds.getCurrent();
        View viewInflate = layoutInflaterCloneInContext.inflate(R.layout.mtrl_calendar, viewGroup, false);
        GridView gridView = (GridView) viewInflate.findViewById(R.id.calendar_days_header);
        gridView.setAdapter((ListAdapter) new DaysOfWeekAdapter());
        gridView.setNumColumns(start.daysInWeek);
        ViewPager viewPager = (ViewPager) viewInflate.findViewById(R.id.month_pager);
        viewPager.setTag(VIEW_PAGER_TAG);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(-1, MonthAdapter.MAXIMUM_WEEKS * getDayHeight(getContext())));
        MonthsPagerAdapter monthsPagerAdapter = new MonthsPagerAdapter(getChildFragmentManager(), this.gridSelector, start, end, current, new OnDayClickListener() { // from class: com.google.android.material.picker.MaterialCalendar.1
            /* JADX DEBUG: Multi-variable search result rejected for r0v4, resolved type: com.google.android.material.picker.MaterialCalendar$OnSelectionChangedListener */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.google.android.material.picker.MaterialCalendar.OnDayClickListener
            public void onDayClick(Calendar calendar) {
                MaterialCalendar.this.gridSelector.select(calendar);
                MaterialCalendar.this.monthsPagerAdapter.notifyDataSetChanged();
                Iterator it = MaterialCalendar.this.onSelectionChangedListeners.iterator();
                while (it.hasNext()) {
                    ((OnSelectionChangedListener) it.next()).onSelectionChanged(MaterialCalendar.this.gridSelector.getSelection());
                }
            }
        });
        this.monthsPagerAdapter = monthsPagerAdapter;
        viewPager.setAdapter(monthsPagerAdapter);
        viewPager.setCurrentItem(this.monthsPagerAdapter.getStartPosition());
        addMonthChangeListeners(viewInflate);
        return viewInflate;
    }

    public final S getSelection() {
        return this.gridSelector.getSelection();
    }

    boolean addOnSelectionChangedListener(OnSelectionChangedListener<S> onSelectionChangedListener) {
        return this.onSelectionChangedListeners.add(onSelectionChangedListener);
    }

    boolean removeOnSelectionChangedListener(OnSelectionChangedListener<S> onSelectionChangedListener) {
        return this.onSelectionChangedListeners.remove(onSelectionChangedListener);
    }

    void clearOnSelectionChangedListeners() {
        this.onSelectionChangedListeners.clear();
    }

    static int getDayHeight(Context context) {
        return MaterialAttributes.resolveMinimumAccessibleTouchTarget(context);
    }

    private void addMonthChangeListeners(View view) {
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.month_pager);
        final MaterialButton materialButton = (MaterialButton) view.findViewById(R.id.month_drop_select);
        materialButton.setText(viewPager.getAdapter().getPageTitle(viewPager.getCurrentItem()));
        MaterialButton materialButton2 = (MaterialButton) view.findViewById(R.id.month_previous);
        MaterialButton materialButton3 = (MaterialButton) view.findViewById(R.id.month_next);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() { // from class: com.google.android.material.picker.MaterialCalendar.2
            @Override // androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                MaterialCalendar materialCalendar = MaterialCalendar.this;
                materialCalendar.calendarBounds = CalendarBounds.create(materialCalendar.calendarBounds.getStart(), MaterialCalendar.this.calendarBounds.getEnd(), MaterialCalendar.this.monthsPagerAdapter.getPageMonth(i));
                materialButton.setText(MaterialCalendar.this.monthsPagerAdapter.getPageTitle(i));
            }
        });
        materialButton3.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.picker.MaterialCalendar.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (viewPager.getCurrentItem() + 1 < viewPager.getAdapter().getCount()) {
                    ViewPager viewPager2 = viewPager;
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                }
            }
        });
        materialButton2.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.picker.MaterialCalendar.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (viewPager.getCurrentItem() - 1 >= 0) {
                    viewPager.setCurrentItem(r2.getCurrentItem() - 1);
                }
            }
        });
    }
}
