package com.google.android.material.picker;

import android.os.Bundle;
import androidx.core.util.Pair;
import com.google.android.material.R;
import com.google.android.material.picker.selector.DateRangeGridSelector;
import com.google.android.material.picker.selector.GridSelector;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class MaterialDateRangePickerDialogFragment extends MaterialPickerDialogFragment<Pair<Calendar, Calendar>> {
    public static MaterialDateRangePickerDialogFragment newInstance() {
        return newInstance(0);
    }

    public static MaterialDateRangePickerDialogFragment newInstance(int i) {
        return newInstance(i, MaterialPickerDialogFragment.DEFAULT_BOUNDS);
    }

    public static MaterialDateRangePickerDialogFragment newInstance(CalendarBounds calendarBounds) {
        return newInstance(0, calendarBounds);
    }

    public static MaterialDateRangePickerDialogFragment newInstance(int i, CalendarBounds calendarBounds) {
        MaterialDateRangePickerDialogFragment materialDateRangePickerDialogFragment = new MaterialDateRangePickerDialogFragment();
        Bundle bundle = new Bundle();
        addArgsToBundle(bundle, i, calendarBounds);
        materialDateRangePickerDialogFragment.setArguments(bundle);
        return materialDateRangePickerDialogFragment;
    }

    @Override // com.google.android.material.picker.MaterialPickerDialogFragment
    protected int getDefaultThemeAttr() {
        return R.attr.materialDateRangePickerDialogTheme;
    }

    @Override // com.google.android.material.picker.MaterialPickerDialogFragment
    /* JADX INFO: renamed from: createGridSelector */
    protected GridSelector<Pair<Calendar, Calendar>> createGridSelector2() {
        return new DateRangeGridSelector();
    }

    /* JADX DEBUG: Method merged with bridge method: getHeaderText(Ljava/lang/Object;)Ljava/lang/String; */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.material.picker.MaterialPickerDialogFragment
    public String getHeaderText(Pair<Calendar, Calendar> pair) {
        if (pair == null) {
            return getContext().getResources().getString(R.string.mtrl_picker_range_header_prompt);
        }
        return getContext().getResources().getString(R.string.mtrl_picker_range_header_selected, getSimpleDateFormat().format(pair.first.getTime()), getSimpleDateFormat().format(pair.second.getTime()));
    }

    public Calendar getStart() {
        if (getSelection() == null) {
            return null;
        }
        return getSelection().first;
    }

    public Calendar getEnd() {
        if (getSelection() == null) {
            return null;
        }
        return getSelection().second;
    }
}
