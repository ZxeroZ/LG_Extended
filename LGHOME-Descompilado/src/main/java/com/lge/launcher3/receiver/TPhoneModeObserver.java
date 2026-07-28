package com.lge.launcher3.receiver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class TPhoneModeObserver extends ContentObserver {
    public static final String TAG = "TPhoneModeObserver";
    private Context mContext;
    private String mPms;

    public TPhoneModeObserver(Context context, Handler handler) {
        super(handler);
        this.mPms = null;
        this.mContext = context;
        if (context != null) {
            this.mPms = context.getResources().getString(R.string.operator_phone_mode_uri);
        }
    }

    public void registerObserver(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String str = this.mPms;
        if (str != null) {
            Uri uriFor = Settings.System.getUriFor(str);
            contentResolver.registerContentObserver(uriFor, true, this);
            LGLog.d(TAG, "register TPhoneModeObserver(" + uriFor.toString() + ")");
            UpdateTPhoneMode();
        }
    }

    public void unregisterObserver(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
        LGLog.d(TAG, "unregister TPhoneModeObserver");
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        UpdateTPhoneMode();
    }

    public void UpdateTPhoneMode() {
        Intent intent = new Intent(IntentConst.Action.ACTION_RELOAD_TPHONEMODE.getValue(this.mContext));
        int i = Settings.System.getInt(this.mContext.getContentResolver(), this.mPms, 2);
        intent.putExtra("modeAfter", i);
        this.mContext.sendBroadcast(intent);
        LGLog.i(TAG, "onChange TPhoneModeObserver, modeAfter = " + i);
    }
}
