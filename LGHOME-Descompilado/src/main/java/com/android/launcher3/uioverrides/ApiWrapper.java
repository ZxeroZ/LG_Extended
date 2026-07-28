package com.android.launcher3.uioverrides;

import android.app.Person;
import android.content.pm.ShortcutInfo;
import android.view.Display;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class ApiWrapper {
    public static final boolean TASKBAR_DRAWN_IN_PROCESS = true;

    public static Person[] getPersons(ShortcutInfo si) {
        Person[] persons = si.getPersons();
        return persons == null ? Utilities.EMPTY_PERSON_ARRAY : persons;
    }

    public static boolean isInternalDisplay(Display display) {
        return display.getType() == 1;
    }
}
