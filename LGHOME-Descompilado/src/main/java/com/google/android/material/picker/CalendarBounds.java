package com.google.android.material.picker;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class CalendarBounds implements Parcelable {
    public static final Parcelable.Creator<CalendarBounds> CREATOR = new Parcelable.Creator<CalendarBounds>() { // from class: com.google.android.material.picker.CalendarBounds.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CalendarBounds createFromParcel(Parcel parcel) {
            return CalendarBounds.create((Month) parcel.readParcelable(Month.class.getClassLoader()), (Month) parcel.readParcelable(Month.class.getClassLoader()), (Month) parcel.readParcelable(Month.class.getClassLoader()));
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CalendarBounds[] newArray(int i) {
            return new CalendarBounds[i];
        }
    };
    private final Month current;
    private final Month end;
    private final Month start;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private CalendarBounds(Month month, Month month2, Month month3) {
        this.start = month;
        this.end = month2;
        this.current = month3;
        if (month.compareTo(month3) > 0) {
            throw new IllegalArgumentException("start Month cannot be after current Month");
        }
        if (month3.compareTo(month2) > 0) {
            throw new IllegalArgumentException("current Month cannot be after end Month");
        }
    }

    public static CalendarBounds create(Month month, Month month2, Month month3) {
        return new CalendarBounds(month, month2, month3);
    }

    public static CalendarBounds create(Month month, Month month2) {
        Month month3 = Month.today();
        if (month2.compareTo(month3) >= 0 && month3.compareTo(month) >= 0) {
            return new CalendarBounds(month, month2, Month.today());
        }
        return new CalendarBounds(month, month2, month);
    }

    public Month getStart() {
        return this.start;
    }

    public Month getEnd() {
        return this.end;
    }

    public Month getCurrent() {
        return this.current;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CalendarBounds)) {
            return false;
        }
        CalendarBounds calendarBounds = (CalendarBounds) obj;
        return this.start.equals(calendarBounds.start) && this.end.equals(calendarBounds.end) && this.current.equals(calendarBounds.current);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.start, this.end, this.current});
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.start, 0);
        parcel.writeParcelable(this.end, 0);
        parcel.writeParcelable(this.current, 0);
    }
}
