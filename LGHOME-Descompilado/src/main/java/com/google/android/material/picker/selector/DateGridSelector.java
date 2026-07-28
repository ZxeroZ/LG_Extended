package com.google.android.material.picker.selector;

import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class DateGridSelector implements GridSelector<Calendar> {
    private Calendar selectedItem;
    static final ColorDrawable emptyColor = new ColorDrawable(0);
    static final ColorDrawable selectedColor = new ColorDrawable(SupportMenu.CATEGORY_MASK);
    public static final Parcelable.Creator<DateGridSelector> CREATOR = new Parcelable.Creator<DateGridSelector>() { // from class: com.google.android.material.picker.selector.DateGridSelector.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DateGridSelector createFromParcel(Parcel parcel) {
            DateGridSelector dateGridSelector = new DateGridSelector();
            dateGridSelector.selectedItem = (Calendar) parcel.readSerializable();
            return dateGridSelector;
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DateGridSelector[] newArray(int i) {
            return new DateGridSelector[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.material.picker.selector.GridSelector
    public void select(Calendar calendar) {
        this.selectedItem = calendar;
    }

    @Override // com.google.android.material.picker.selector.GridSelector
    public void drawCell(View view, Calendar calendar) {
        ViewCompat.setBackground(view, calendar.equals(this.selectedItem) ? selectedColor : emptyColor);
    }

    /* JADX DEBUG: Method merged with bridge method: getSelection()Ljava/lang/Object; */
    @Override // com.google.android.material.picker.selector.GridSelector
    public Calendar getSelection() {
        return this.selectedItem;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this.selectedItem);
    }
}
