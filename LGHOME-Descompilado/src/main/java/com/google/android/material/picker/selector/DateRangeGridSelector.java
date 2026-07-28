package com.google.android.material.picker.selector;

import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import androidx.core.internal.view.SupportMenu;
import androidx.core.util.Pair;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class DateRangeGridSelector implements GridSelector<Pair<Calendar, Calendar>> {
    static final ColorDrawable emptyColor = new ColorDrawable(0);
    static final ColorDrawable startColor = new ColorDrawable(SupportMenu.CATEGORY_MASK);
    static final ColorDrawable endColor = new ColorDrawable(-16711936);
    static final ColorDrawable rangeColor = new ColorDrawable(InputDeviceCompat.SOURCE_ANY);
    public static final Parcelable.Creator<DateRangeGridSelector> CREATOR = new Parcelable.Creator<DateRangeGridSelector>() { // from class: com.google.android.material.picker.selector.DateRangeGridSelector.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DateRangeGridSelector createFromParcel(Parcel parcel) {
            DateRangeGridSelector dateRangeGridSelector = new DateRangeGridSelector();
            dateRangeGridSelector.selectedStartItem = (Calendar) parcel.readSerializable();
            dateRangeGridSelector.selectedEndItem = (Calendar) parcel.readSerializable();
            return dateRangeGridSelector;
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DateRangeGridSelector[] newArray(int i) {
            return new DateRangeGridSelector[i];
        }
    };
    private Calendar selectedStartItem = null;
    private Calendar selectedEndItem = null;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.material.picker.selector.GridSelector
    public void select(Calendar calendar) {
        Calendar calendar2 = this.selectedStartItem;
        if (calendar2 == null) {
            this.selectedStartItem = calendar;
        } else if (this.selectedEndItem == null && calendar.after(calendar2)) {
            this.selectedEndItem = calendar;
        } else {
            this.selectedEndItem = null;
            this.selectedStartItem = calendar;
        }
    }

    @Override // com.google.android.material.picker.selector.GridSelector
    public void drawCell(View view, Calendar calendar) {
        ColorDrawable colorDrawable = emptyColor;
        if (calendar.equals(this.selectedStartItem)) {
            colorDrawable = startColor;
        } else if (calendar.equals(this.selectedEndItem)) {
            colorDrawable = endColor;
        } else if (calendar.after(this.selectedStartItem) && calendar.before(this.selectedEndItem)) {
            colorDrawable = rangeColor;
        }
        ViewCompat.setBackground(view, colorDrawable);
    }

    /* JADX DEBUG: Method merged with bridge method: getSelection()Ljava/lang/Object; */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.material.picker.selector.GridSelector
    public Pair<Calendar, Calendar> getSelection() {
        Calendar start = getStart();
        Calendar end = getEnd();
        if (start == null || end == null) {
            return null;
        }
        return new Pair<>(getStart(), getEnd());
    }

    public Calendar getStart() {
        return this.selectedStartItem;
    }

    public Calendar getEnd() {
        return this.selectedEndItem;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this.selectedStartItem);
        parcel.writeSerializable(this.selectedEndItem);
    }
}
