package com.google.android.material.picker;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public final class Month implements Comparable<Month>, Parcelable {
    private final Calendar calendar;
    final int daysInMonth;
    final int daysInWeek;
    private final String longName;
    final int month;
    final int year;
    private static final SimpleDateFormat longNameFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
    public static final Parcelable.Creator<Month> CREATOR = new Parcelable.Creator<Month>() { // from class: com.google.android.material.picker.Month.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Month createFromParcel(Parcel parcel) {
            return Month.create(parcel.readInt(), parcel.readInt());
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Month[] newArray(int i) {
            return new Month[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private Month(Calendar calendar) {
        this.calendar = calendar;
        calendar.set(5, 1);
        this.month = calendar.get(2);
        this.year = calendar.get(1);
        this.daysInWeek = calendar.getMaximum(7);
        this.daysInMonth = calendar.getActualMaximum(5);
        this.longName = longNameFormat.format(calendar.getTime());
    }

    public static Month create(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1, i);
        calendar.set(2, i2);
        return new Month(calendar);
    }

    public static Month today() {
        Calendar calendar = Calendar.getInstance();
        return create(calendar.get(1), calendar.get(2));
    }

    int daysFromStartOfWeekToFirstOfMonth() {
        int firstDayOfWeek = this.calendar.get(7) - this.calendar.getFirstDayOfWeek();
        return firstDayOfWeek < 0 ? firstDayOfWeek + this.daysInWeek : firstDayOfWeek;
    }

    Calendar getDay(int i) {
        Calendar calendar = (Calendar) this.calendar.clone();
        calendar.set(5, i);
        return calendar;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Month)) {
            return false;
        }
        Month month = (Month) obj;
        return this.month == month.month && this.year == month.year;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.month), Integer.valueOf(this.year)});
    }

    /* JADX DEBUG: Method merged with bridge method: compareTo(Ljava/lang/Object;)I */
    @Override // java.lang.Comparable
    public int compareTo(Month month) {
        return this.calendar.compareTo(month.calendar);
    }

    int monthsUntil(Month month) {
        if (this.calendar instanceof GregorianCalendar) {
            return ((month.year - this.year) * 12) + (month.month - this.month);
        }
        throw new IllegalArgumentException("Only Gregorian calendars are supported.");
    }

    Month monthsLater(int i) {
        Calendar calendar = (Calendar) this.calendar.clone();
        calendar.add(2, i);
        return new Month(calendar);
    }

    String getLongName() {
        return this.longName;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.year);
        parcel.writeInt(this.month);
    }
}
