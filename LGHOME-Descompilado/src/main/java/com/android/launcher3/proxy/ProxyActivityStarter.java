package com.android.launcher3.proxy;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ProxyActivityStarter extends Activity {
    public static final String EXTRA_PARAMS = "start-activity-params";
    private static final String TAG = "ProxyActivityStarter";
    private StartActivityParams mParams;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVisible(false);
        StartActivityParams startActivityParams = (StartActivityParams) getIntent().getParcelableExtra(EXTRA_PARAMS);
        this.mParams = startActivityParams;
        if (startActivityParams == null) {
            Log.d(TAG, "Proxy activity started without params");
            finishAndRemoveTask();
            return;
        }
        if (savedInstanceState != null) {
            return;
        }
        if (startActivityParams.intent != null) {
            try {
                startActivityForResult(this.mParams.intent, this.mParams.requestCode, this.mParams.options);
                return;
            } catch (ActivityNotFoundException unused) {
                Toast.makeText(this, R.string.activity_not_found, 0).show();
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.activity_not_found, 0).show();
                Log.e(TAG, "Launcher does not have the permission to launch " + this.mParams.intent + ". Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity.", e);
            }
        } else if (this.mParams.intentSender != null) {
            try {
                startIntentSenderForResult(this.mParams.intentSender, this.mParams.requestCode, this.mParams.fillInIntent, this.mParams.flagsMask, this.mParams.flagsValues, this.mParams.extraFlags, this.mParams.options);
                return;
            } catch (IntentSender.SendIntentException unused2) {
                this.mParams.deliverResult(this, 0, null);
            }
        }
        finishAndRemoveTask();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.mParams.requestCode) {
            this.mParams.deliverResult(this, resultCode, data);
        }
        finishAndRemoveTask();
    }

    public static Intent getLaunchIntent(Context context, StartActivityParams params) {
        return new Intent(context, (Class<?>) ProxyActivityStarter.class).putExtra(EXTRA_PARAMS, params).addFlags(270565376);
    }
}
