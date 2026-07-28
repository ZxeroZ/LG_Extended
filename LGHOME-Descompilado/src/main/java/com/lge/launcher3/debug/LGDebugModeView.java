package com.lge.launcher3.debug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class LGDebugModeView extends TextView {
    private final String TAG;

    public LGDebugModeView(Context context) {
        this(context, null);
    }

    public LGDebugModeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void addText(String addText) {
        StringBuilder sb = new StringBuilder(getText());
        if (getLineCount() > 2) {
            sb.append(" ");
        } else {
            sb.append("\n");
        }
        sb.append(addText);
        setText(sb);
    }

    public LGDebugModeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.TAG = "LGDebugModeView";
        Resources resources = getResources();
        String str = Build.TYPE;
        if (resources != null) {
            setSingleLine(false);
            if (EventLogger.sPackageVersion != null) {
                str = str + "\n" + EventLogger.sPackageVersion;
            } else {
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    str = str + "\n" + packageInfo.versionName + "(" + packageInfo.versionCode + ")";
                } catch (PackageManager.NameNotFoundException unused) {
                    LGLog.w("LGDebugModeView", "NameNotFoundException : packageInfo", new int[0]);
                }
            }
            if (EventLogger.sElapsedForLauncherCreation != null) {
                str = str + "\n" + EventLogger.sElapsedForLauncherCreation;
            }
            setText(str);
            return;
        }
        setText("");
        setVisibility(8);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
