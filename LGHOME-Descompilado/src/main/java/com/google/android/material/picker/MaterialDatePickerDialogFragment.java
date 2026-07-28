package com.google.android.material.picker;

import android.os.Bundle;
import com.google.android.material.R;
import com.google.android.material.picker.selector.DateGridSelector;
import com.google.android.material.picker.selector.GridSelector;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class MaterialDatePickerDialogFragment extends MaterialPickerDialogFragment<Calendar> {
    public static MaterialDatePickerDialogFragment newInstance() {
        return newInstance(0);
    }

    public static MaterialDatePickerDialogFragment newInstance(int i) {
        return newInstance(i, MaterialPickerDialogFragment.DEFAULT_BOUNDS);
    }

    public static MaterialDatePickerDialogFragment newInstance(CalendarBounds calendarBounds) {
        return newInstance(0, calendarBounds);
    }

    public static MaterialDatePickerDialogFragment newInstance(int i, CalendarBounds calendarBounds) {
        MaterialDatePickerDialogFragment materialDatePickerDialogFragment = new MaterialDatePickerDialogFragment();
        Bundle bundle = new Bundle();
        addArgsToBundle(bundle, i, calendarBounds);
        materialDatePickerDialogFragment.setArguments(bundle);
        return materialDatePickerDialogFragment;
    }

    @Override // com.google.android.material.picker.MaterialPickerDialogFragment
    protected int getDefaultThemeAttr() {
        return R.attr.materialDatePickerDialogTheme;
    }

    /* JADX DEBUG: Method merged with bridge method: createGridSelector()Lcom/google/android/material/picker/selector/GridSelector; */
    /* JADX DEBUG: Return type fixed from 'com.google.android.material.picker.selector.DateGridSelector' to match base method */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.material.picker.MaterialPickerDialogFragment
    /* JADX INFO: renamed from: createGridSelector, reason: merged with bridge method [inline-methods] */
    public GridSelector<Calendar> createGridSelector2() {
        return new DateGridSelector();
    }

    /* JADX DEBUG: Method merged with bridge method: getHeaderText(Ljava/lang/Object;)Ljava/lang/String; */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.material.picker.MaterialPickerDialogFragment
    public String getHeaderText(Calendar calendar) {
        if (calendar == null) {
            return getContext().getResources().getString(R.string.mtrl_picker_header_prompt);
        }
        return getContext().getResources().getString(R.string.mtrl_picker_header_selected, getSimpleDateFormat().format(calendar.getTime()));
    }
}
