package com.android.launcher3.logging;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.function.IntFunction;

/* JADX INFO: loaded from: classes.dex */
public class EventLogArray {
    private static final int TYPE_BOOL_FALSE = 4;
    private static final int TYPE_BOOL_TRUE = 3;
    private static final int TYPE_FLOAT = 1;
    private static final int TYPE_INTEGER = 2;
    private static final int TYPE_ONE_OFF = 0;
    private final EventEntry[] logs;
    private int mLogId;
    private final String name;
    private int nextIndex = 0;

    static /* synthetic */ EventEntry lambda$clear$0(int i) {
        return null;
    }

    public EventLogArray(String name, int size) {
        this.name = name;
        this.logs = new EventEntry[size];
    }

    public void addLog(String event) {
        addLog(0, event, 0.0f);
    }

    public void addLog(String event, int extras) {
        addLog(2, event, extras);
    }

    public void addLog(String event, float extras) {
        addLog(1, event, extras);
    }

    public void addLog(String event, boolean extras) {
        addLog(extras ? 3 : 4, event, 0.0f);
    }

    private void addLog(int type, String event, float extras) {
        int i = this.nextIndex;
        EventEntry[] eventEntryArr = this.logs;
        int length = ((eventEntryArr.length + i) - 1) % eventEntryArr.length;
        int length2 = ((i + eventEntryArr.length) - 2) % eventEntryArr.length;
        if (isEntrySame(eventEntryArr[length], type, event) && isEntrySame(this.logs[length2], type, event)) {
            this.logs[length].update(type, event, extras);
            this.logs[length2].duplicateCount++;
        } else {
            EventEntry[] eventEntryArr2 = this.logs;
            int i2 = this.nextIndex;
            if (eventEntryArr2[i2] == null) {
                eventEntryArr2[i2] = new EventEntry();
            }
            this.logs[this.nextIndex].update(type, event, extras);
            this.nextIndex = (this.nextIndex + 1) % this.logs.length;
        }
    }

    public void clear() {
        Arrays.setAll(this.logs, new IntFunction() { // from class: com.android.launcher3.logging.-$$Lambda$EventLogArray$rbMJNvq-9ZP8CRcMigRn-snisDA
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return EventLogArray.lambda$clear$0(i);
            }
        });
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "EventLog (" + this.name + ") history:");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("  HH:mm:ss.SSSZ  ", Locale.US);
        Date date = new Date();
        int i = 0;
        while (true) {
            EventEntry[] eventEntryArr = this.logs;
            if (i >= eventEntryArr.length) {
                return;
            }
            EventEntry eventEntry = eventEntryArr[(((this.nextIndex + eventEntryArr.length) - i) - 1) % eventEntryArr.length];
            if (eventEntry != null) {
                date.setTime(eventEntry.time);
                StringBuilder sbAppend = new StringBuilder(prefix).append(simpleDateFormat.format(date)).append(eventEntry.event);
                int i2 = eventEntry.type;
                if (i2 == 1) {
                    sbAppend.append(": ").append(eventEntry.extras);
                } else if (i2 == 2) {
                    sbAppend.append(": ").append((int) eventEntry.extras);
                } else if (i2 == 3) {
                    sbAppend.append(": true");
                } else if (i2 == 4) {
                    sbAppend.append(": false");
                }
                if (eventEntry.duplicateCount > 0) {
                    sbAppend.append(" & ").append(eventEntry.duplicateCount).append(" similar events");
                }
                writer.println(sbAppend);
            }
            i++;
        }
    }

    public int generateAndSetLogId() {
        int iNextInt = new Random().nextInt(900) + 100;
        this.mLogId = iNextInt;
        return iNextInt;
    }

    private boolean isEntrySame(EventEntry entry, int type, String event) {
        return entry != null && entry.type == type && entry.event.equals(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class EventEntry {
        private int duplicateCount;
        private String event;
        private float extras;
        private long time;
        private int type;

        private EventEntry() {
        }

        public void update(int type, String event, float extras) {
            this.type = type;
            this.event = event;
            this.extras = extras;
            this.time = System.currentTimeMillis();
            this.duplicateCount = 0;
        }
    }
}
