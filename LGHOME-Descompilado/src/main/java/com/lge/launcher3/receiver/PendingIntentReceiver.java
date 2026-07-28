package com.lge.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.SystemClock;
import com.android.launcher3.LauncherAppState;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class PendingIntentReceiver extends BroadcastReceiver {
    private static final String ADDITIONAL_SCREEN_VALUE_CHANGE = "additional_screen_value_change";
    private static final boolean DBG = false;
    static final String INTENT_KEY_RECEIVED_TIME = "com.lge.launcher3:received_time";
    private static final String PENDING_ADDITIONAL_SCREEN_INTENT_LIST = "pending_additional_screen_intent_list";
    private static final String PENDING_INTENT_LIST = "pending_intent_list";
    private static final String TAG = "PendingIntentReceiver";
    private static boolean sActivated = false;
    private static PendingIntentReceiver sPendingIntentReceiver = null;
    private static boolean sUseQueue = true;
    private static final Object sLock = new Object();
    private static HashMap<String, IntentHandler> sIntents = new HashMap<>();

    private static void clearInstance() {
        sPendingIntentReceiver = null;
    }

    private PendingIntentReceiver() {
        sIntents.clear();
        for (Class<?> cls : PendingIntentObjectList.class.getDeclaredClasses()) {
            try {
                Object objNewInstance = cls.newInstance();
                if (objNewInstance instanceof IntentHandler) {
                    sIntents.put(((IntentHandler) objNewInstance).getNameOfIntent(), (IntentHandler) objNewInstance);
                }
            } catch (IllegalAccessException | InstantiationException unused) {
                LGLog.w("CheckClass", "not found", new int[0]);
            }
        }
    }

    private static BroadcastReceiver getInstance() {
        if (sPendingIntentReceiver == null) {
            sPendingIntentReceiver = new PendingIntentReceiver();
        }
        return sPendingIntentReceiver;
    }

    static void flushQueue(Context context, boolean addScreenValueChange) {
        ArrayList<Intent> andClearIntentQueue = getAndClearIntentQueue(context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0), context, addScreenValueChange);
        if (andClearIntentQueue.isEmpty()) {
            return;
        }
        for (Intent intent : andClearIntentQueue) {
            if (sIntents.containsKey(intent.getAction())) {
                sIntents.get(intent.getAction()).onHandle(context, intent);
            }
        }
    }

    private static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        Iterator<String> it = sIntents.keySet().iterator();
        while (it.hasNext()) {
            intentFilter.addAction(it.next());
        }
        return intentFilter;
    }

    public static void registerReceiver(Context context) {
        context.registerReceiver(getInstance(), getIntentFilter());
        sActivated = true;
        Intent intent = new Intent("com.lge.gdec.intent.action.WORKSPACE_RELOADER_ACTIVATED");
        if (LGHomeFeature.isEnableDefaultHome()) {
            intent.putExtra("currentHome", 0);
        } else if (LGHomeFeature.isDisableEasyHome()) {
            intent.putExtra("currentHome", 1);
        } else {
            intent.putExtra("currentHome", 2);
        }
        intent.addFlags(16777216);
        context.sendBroadcast(intent);
    }

    public static void unregisterReceiver(Context context) {
        try {
            context.unregisterReceiver(getInstance());
        } catch (IllegalArgumentException e) {
            LGLog.e(TAG, "error on unregisterReceiver : " + e.getMessage());
        }
        clearInstance();
        sActivated = false;
    }

    static boolean isActivated() {
        return sActivated;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LGLog.i(TAG, "onReceive intent: " + intent);
        queuePendingIntent(context, intent);
    }

    static boolean isUseQueue() {
        return sUseQueue;
    }

    public static void enableQueue() {
        sUseQueue = true;
    }

    public static void disableAndFlushQueue(Context context, boolean addScreenValueChange) {
        sUseQueue = false;
        flushQueue(context, addScreenValueChange);
    }

    static void queuePendingIntent(Context context, Intent intent) {
        intent.putExtra(INTENT_KEY_RECEIVED_TIME, SystemClock.elapsedRealtime());
        boolean booleanExtra = intent.getBooleanExtra(ADDITIONAL_SCREEN_VALUE_CHANGE, false);
        addToQueue(context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0), intent);
        if (sUseQueue) {
            return;
        }
        flushQueue(context, booleanExtra);
    }

    private static void addToQueue(SharedPreferences sharedPrefs, Intent intent) {
        Set<String> stringSet;
        HashSet hashSet;
        synchronized (sLock) {
            String uri = intent.toUri(0);
            boolean booleanExtra = intent.getBooleanExtra(ADDITIONAL_SCREEN_VALUE_CHANGE, false);
            LGLog.d(TAG, "addToQue addScreenValueChange : " + booleanExtra);
            if (uri != null) {
                if (booleanExtra) {
                    stringSet = sharedPrefs.getStringSet(PENDING_ADDITIONAL_SCREEN_INTENT_LIST, null);
                } else {
                    stringSet = sharedPrefs.getStringSet(PENDING_INTENT_LIST, null);
                }
                if (stringSet == null) {
                    hashSet = new HashSet(1);
                } else {
                    hashSet = new HashSet(stringSet);
                }
                hashSet.add(uri);
                if (booleanExtra) {
                    sharedPrefs.edit().putStringSet(PENDING_ADDITIONAL_SCREEN_INTENT_LIST, hashSet).commit();
                } else {
                    sharedPrefs.edit().putStringSet(PENDING_INTENT_LIST, hashSet).commit();
                }
            }
        }
    }

    private static ArrayList<Intent> getAndClearIntentQueue(SharedPreferences sharedPrefs, Context context, boolean addScreenValueChange) {
        Set<String> stringSet;
        Intent uri;
        synchronized (sLock) {
            if (addScreenValueChange) {
                stringSet = sharedPrefs.getStringSet(PENDING_ADDITIONAL_SCREEN_INTENT_LIST, null);
            } else {
                stringSet = sharedPrefs.getStringSet(PENDING_INTENT_LIST, null);
            }
            LGLog.d(TAG, "Getting and clearing PENDING_INTENT_LIST:  " + stringSet);
            if (stringSet == null) {
                return new ArrayList<>();
            }
            ArrayList<Intent> arrayList = new ArrayList<>();
            Iterator<String> it = stringSet.iterator();
            while (it.hasNext()) {
                try {
                    uri = Intent.parseUri(it.next(), 0);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    uri = null;
                }
                if (uri != null) {
                    arrayList.add(uri);
                }
            }
            if (addScreenValueChange) {
                sharedPrefs.edit().putStringSet(PENDING_ADDITIONAL_SCREEN_INTENT_LIST, new HashSet()).commit();
            } else {
                sharedPrefs.edit().putStringSet(PENDING_INTENT_LIST, new HashSet()).commit();
            }
            return arrayList;
        }
    }
}
