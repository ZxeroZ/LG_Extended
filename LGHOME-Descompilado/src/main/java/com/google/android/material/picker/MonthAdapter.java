package com.google.android.material.picker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.android.material.picker.selector.GridSelector;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class MonthAdapter extends BaseAdapter {
    static final int MAXIMUM_WEEKS = Calendar.getInstance().getMaximum(4);
    private final GridSelector<?> gridSelector;
    private final Month month;
    private final int textViewSize;

    public MonthAdapter(Context context, Month month, GridSelector<?> gridSelector) {
        this.month = month;
        this.textViewSize = MaterialCalendar.getDayHeight(context);
        this.gridSelector = gridSelector;
    }

    /* JADX DEBUG: Method merged with bridge method: getItem(I)Ljava/lang/Object; */
    @Override // android.widget.Adapter
    public Calendar getItem(int i) {
        if (i < this.month.daysFromStartOfWeekToFirstOfMonth() || i > lastPositionInMonth()) {
            return null;
        }
        return this.month.getDay(positionToDay(i));
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i / this.month.daysInWeek;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.month.daysInWeek * MAXIMUM_WEEKS;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = (TextView) view;
        if (view == null) {
            textView = new TextView(viewGroup.getContext());
            textView.setHeight(this.textViewSize);
        }
        int iFirstPositionInMonth = i - firstPositionInMonth();
        if (iFirstPositionInMonth < 0 || iFirstPositionInMonth >= this.month.daysInMonth) {
            textView.setVisibility(4);
        } else {
            textView.setText(String.valueOf(iFirstPositionInMonth + 1));
            textView.setTag(this.month);
            textView.setVisibility(0);
        }
        Calendar item = getItem(i);
        if (item != null) {
            this.gridSelector.drawCell(textView, item);
        }
        return textView;
    }

    public int firstPositionInMonth() {
        return this.month.daysFromStartOfWeekToFirstOfMonth();
    }

    public int lastPositionInMonth() {
        return (this.month.daysFromStartOfWeekToFirstOfMonth() + this.month.daysInMonth) - 1;
    }

    public int positionToDay(int i) {
        return (i - this.month.daysFromStartOfWeekToFirstOfMonth()) + 1;
    }

    public boolean withinMonth(int i) {
        return i >= firstPositionInMonth() && i <= lastPositionInMonth();
    }
}
