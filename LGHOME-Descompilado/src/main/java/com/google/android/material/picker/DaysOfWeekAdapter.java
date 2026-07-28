package com.google.android.material.picker;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
class DaysOfWeekAdapter extends BaseAdapter {
    private static final int CALENDAR_DAY_STYLE;
    private static final int NARROW_FORMAT = 4;
    private final Calendar calendar;
    private final int daysInWeek;
    private final int firstDayOfWeek;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    static {
        CALENDAR_DAY_STYLE = Build.VERSION.SDK_INT >= 26 ? 4 : 1;
    }

    public DaysOfWeekAdapter() {
        Calendar calendar = Calendar.getInstance();
        this.calendar = calendar;
        calendar.clear();
        this.daysInWeek = calendar.getMaximum(7);
        this.firstDayOfWeek = calendar.getFirstDayOfWeek();
    }

    /* JADX DEBUG: Method merged with bridge method: getItem(I)Ljava/lang/Object; */
    @Override // android.widget.Adapter
    public Integer getItem(int i) {
        if (i >= this.daysInWeek) {
            return null;
        }
        return Integer.valueOf(positionToDayOfWeek(i));
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.daysInWeek;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = (TextView) view;
        if (view == null) {
            textView = new TextView(viewGroup.getContext());
        }
        this.calendar.set(7, positionToDayOfWeek(i));
        textView.setText(this.calendar.getDisplayName(7, CALENDAR_DAY_STYLE, Locale.getDefault()));
        return textView;
    }

    private int positionToDayOfWeek(int i) {
        int i2 = i + this.firstDayOfWeek;
        int i3 = this.daysInWeek;
        return i2 > i3 ? i2 - i3 : i2;
    }
}
