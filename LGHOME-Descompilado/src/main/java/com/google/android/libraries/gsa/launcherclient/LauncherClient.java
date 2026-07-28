package com.google.android.libraries.gsa.launcherclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.LauncherConst;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class LauncherClient {
    private static int b = -1;
    protected com.google.android.libraries.a.c a;
    private final Activity c;
    private final LauncherClientCallbacks d;
    private final Handler e;
    private final e f;
    private final e g;
    private final l h;
    private final c i;
    private final BroadcastReceiver j;
    private int k;
    private boolean l;
    private int m;
    private ClientOptions n;
    private WindowManager.LayoutParams o;
    private i p;

    public static class ClientOptions {
        private final int a;
        private String b;
        private final Bitmap c;
        private final boolean d;
        private final String e;

        private ClientOptions(String str, Bitmap bitmap, boolean z) {
            this.b = LauncherConst.GOOGLE_SEARCH_WIDGET_PACKAGENAME;
            this.a = 7;
            this.c = bitmap;
            this.d = z;
            this.e = str;
        }

        public ClientOptions(boolean z, boolean z2, boolean z3) {
            this.b = LauncherConst.GOOGLE_SEARCH_WIDGET_PACKAGENAME;
            this.a = (z ? 1 : 0) | (true != z2 ? 0 : 2) | (true != z3 ? 0 : 4);
            this.c = null;
            this.e = "";
            this.d = false;
        }

        /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
        public static ClientOptions createForSharedOverlay(String str, Bitmap bitmap, boolean z) {
            return new ClientOptions(str, bitmap, z);
        }
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    public LauncherClient(Activity activity) {
        this(activity, new LauncherClientCallbacksAdapter());
    }

    static /* synthetic */ void a(LauncherClient launcherClient, Bundle bundle) {
        int i = bundle.getInt("service_status", -1);
        if (i != -1) {
            launcherClient.b(i);
        }
        LauncherClientCallbacks launcherClientCallbacks = launcherClient.d;
        if (launcherClientCallbacks instanceof j) {
            ((j) launcherClientCallbacks).a();
        }
        LauncherClientCallbacks launcherClientCallbacks2 = launcherClient.d;
        if (launcherClientCallbacks2 instanceof SharedOverlayCallbacks) {
            SharedOverlayCallbacks sharedOverlayCallbacks = (SharedOverlayCallbacks) launcherClientCallbacks2;
            if (bundle.getBoolean("overlay_animation_complete")) {
                sharedOverlayCallbacks.onGoogleOverlayTransitionComplete();
            }
            Bitmap bitmap = (Bitmap) bundle.getParcelable("google_overlay_icon");
            if (bitmap != null) {
                sharedOverlayCallbacks.onGoogleOverlayIconChanged(bitmap);
            }
            if (bundle.getBoolean("initiate_overlay_switch")) {
                sharedOverlayCallbacks.onSharedOverlaySwitchInitiated();
            }
        }
    }

    private final boolean b() {
        return this.a != null;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    public LauncherClient(Activity activity, LauncherClientCallbacks launcherClientCallbacks) {
        this(activity, launcherClientCallbacks, new ClientOptions(true, true, true));
    }

    public LauncherClient(Activity activity, LauncherClientCallbacks launcherClientCallbacks, ClientOptions clientOptions) {
        this(activity, launcherClientCallbacks, clientOptions, Looper.getMainLooper());
    }

    public LauncherClient(Activity activity, LauncherClientCallbacks launcherClientCallbacks, ClientOptions clientOptions, Looper looper) {
        this.f = new e("Client", 20);
        this.g = new e("Service", 10);
        f fVar = new f(this);
        this.j = fVar;
        this.k = 0;
        this.l = false;
        this.m = 0;
        this.c = activity;
        this.d = launcherClientCallbacks;
        this.n = clientOptions;
        Handler handler = new Handler(looper);
        this.e = handler;
        this.h = new l(activity, 65, handler, this.n.b);
        c cVarA = c.a(activity, handler, this.n.b);
        this.i = cVarA;
        this.a = cVarA.a(this);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        if (Build.VERSION.SDK_INT >= 19) {
            intentFilter.addDataSchemeSpecificPart(this.n.b, 0);
        }
        activity.registerReceiver(fVar, intentFilter);
        if (b <= 0) {
            a(activity);
        }
        reconnect();
        if (Build.VERSION.SDK_INT < 19 || activity.getWindow() == null || activity.getWindow().peekDecorView() == null || !activity.getWindow().peekDecorView().isAttachedToWindow()) {
            return;
        }
        onAttachedToWindow();
    }

    private final void a() {
        if (this.a != null) {
            try {
                if (this.p == null) {
                    this.p = new i();
                }
                this.p.a(this);
                if (b < 3) {
                    this.a.a(this.o, this.p, this.n.a);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("layout_params", this.o);
                    bundle.putParcelable("configuration", this.c.getResources().getConfiguration());
                    bundle.putInt("client_options", this.n.a);
                    if (this.n.c != null) {
                        bundle.putParcelable("partner_overlay_icon", this.n.c);
                        bundle.putBoolean("google_overlay_is_default", this.n.d);
                        bundle.putString("partner_overlay_product_name", this.n.e);
                    }
                    this.a.a(bundle, this.p);
                }
                if (b >= 4) {
                    this.a.b(this.k);
                } else if ((this.k & 2) != 0) {
                    this.a.e();
                } else {
                    this.a.d();
                }
            } catch (RemoteException unused) {
            }
        }
    }

    public void disconnect() {
        a(true);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(String.valueOf(str).concat("LauncherClient"));
        String strConcat = String.valueOf(str).concat("  ");
        boolean zB = b();
        StringBuilder sb = new StringBuilder(String.valueOf(strConcat).length() + 18);
        sb.append(strConcat);
        sb.append("isConnected: ");
        sb.append(zB);
        printWriter.println(sb.toString());
        boolean zC = this.h.c();
        StringBuilder sb2 = new StringBuilder(String.valueOf(strConcat).length() + 18);
        sb2.append(strConcat);
        sb2.append("act.isBound: ");
        sb2.append(zC);
        printWriter.println(sb2.toString());
        boolean zC2 = this.i.c();
        StringBuilder sb3 = new StringBuilder(String.valueOf(strConcat).length() + 18);
        sb3.append(strConcat);
        sb3.append("app.isBound: ");
        sb3.append(zC2);
        printWriter.println(sb3.toString());
        int i = b;
        StringBuilder sb4 = new StringBuilder(String.valueOf(strConcat).length() + 27);
        sb4.append(strConcat);
        sb4.append("serviceVersion: ");
        sb4.append(i);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder(String.valueOf(strConcat).length() + 17);
        sb5.append(strConcat);
        sb5.append("clientVersion: 18");
        printWriter.println(sb5.toString());
        boolean z = this.n.d;
        StringBuilder sb6 = new StringBuilder(String.valueOf(strConcat).length() + 29);
        sb6.append(strConcat);
        sb6.append("isGoogleOverlayDefault: ");
        sb6.append(z);
        printWriter.println(sb6.toString());
        String str2 = this.n.e;
        StringBuilder sb7 = new StringBuilder(String.valueOf(strConcat).length() + 27 + String.valueOf(str2).length());
        sb7.append(strConcat);
        sb7.append("partnerOverlayProductName: ");
        sb7.append(str2);
        printWriter.println(sb7.toString());
        boolean z2 = this.n.c != null;
        StringBuilder sb8 = new StringBuilder(String.valueOf(strConcat).length() + 34);
        sb8.append(strConcat);
        sb8.append("isPartnerOverlayIconPresent: ");
        sb8.append(z2);
        printWriter.println(sb8.toString());
        int i2 = this.k;
        StringBuilder sb9 = new StringBuilder(String.valueOf(strConcat).length() + 27);
        sb9.append(strConcat);
        sb9.append("mActivityState: ");
        sb9.append(i2);
        printWriter.println(sb9.toString());
        int i3 = this.m;
        StringBuilder sb10 = new StringBuilder(String.valueOf(strConcat).length() + 27);
        sb10.append(strConcat);
        sb10.append("mServiceStatus: ");
        sb10.append(i3);
        printWriter.println(sb10.toString());
        int i4 = this.n.a;
        StringBuilder sb11 = new StringBuilder(String.valueOf(strConcat).length() + 45);
        sb11.append(strConcat);
        sb11.append("mCurrentServiceConnectionOptions: ");
        sb11.append(i4);
        printWriter.println(sb11.toString());
        this.f.a(strConcat, printWriter);
        this.g.a(strConcat, printWriter);
    }

    public void endMove() {
        this.f.a("endMove");
        if (b()) {
            try {
                this.a.c();
            } catch (RemoteException unused) {
            }
        }
    }

    static Intent a(Context context, String str) {
        String packageName = context.getPackageName();
        int iMyUid = Process.myUid();
        StringBuilder sb = new StringBuilder(String.valueOf(packageName).length() + 18);
        sb.append("app://");
        sb.append(packageName);
        sb.append(":");
        sb.append(iMyUid);
        return new Intent("com.android.launcher3.WINDOW_OVERLAY").setPackage(str).setData(Uri.parse(sb.toString()).buildUpon().appendQueryParameter("v", Integer.toString(10)).appendQueryParameter("cv", Integer.toString(18)).build());
    }

    public void hideOverlay(int i) {
        int iA = a(i);
        this.f.a("hideOverlay", i);
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null) {
            try {
                cVar.a(iA);
            } catch (RemoteException unused) {
            }
        }
    }

    public void hideOverlay(AnimationType animationType, int i) {
        if (b < 10) {
            hideOverlay(i);
        }
        e eVar = this.f;
        String strValueOf = String.valueOf(animationType);
        StringBuilder sb = new StringBuilder(String.valueOf(strValueOf).length() + 15);
        sb.append("hideOverlay: ");
        sb.append(strValueOf);
        sb.append(", ");
        eVar.a(sb.toString(), i);
        if (this.a != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("overlay_animation_type", animationType.a());
            bundle.putInt("overlay_animation_duration", i);
            try {
                this.a.a(bundle);
            } catch (RemoteException e) {
                Log.d("DrawerOverlayClient", "Unable to close overlay", e);
            }
        }
    }

    public void hideOverlay(boolean z) {
        this.f.a("hideOverlay", z);
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null) {
            try {
                cVar.a(z ? 1 : 0);
            } catch (RemoteException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void b(int i) {
        if (this.m != i) {
            this.m = i;
            int i2 = i & 1;
            this.d.onServiceStateChanged(1 == i2, (i & 2) != 0);
        }
    }

    public final void onAttachedToWindow() {
        if (this.l) {
            return;
        }
        this.f.a("attachedToWindow");
        a(this.c.getWindow().getAttributes());
    }

    public void onDestroy() {
        a(!this.c.isChangingConfigurations());
    }

    public final void onDetachedFromWindow() {
        if (this.l) {
            return;
        }
        this.f.a("detachedFromWindow");
        a((WindowManager.LayoutParams) null);
    }

    public void onPause() {
        if (this.l) {
            return;
        }
        int i = this.k & (-3);
        this.k = i;
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null && this.o != null) {
            try {
                if (b < 4) {
                    cVar.d();
                } else {
                    cVar.b(i);
                }
            } catch (RemoteException unused) {
            }
        }
        this.f.a("stateChanged ", this.k);
    }

    public void onResume() {
        if (this.l) {
            return;
        }
        int i = this.k | 2;
        this.k = i;
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null && this.o != null) {
            try {
                if (b < 4) {
                    cVar.e();
                } else {
                    cVar.b(i);
                }
            } catch (RemoteException unused) {
            }
        }
        this.f.a("stateChanged ", this.k);
    }

    public void onStart() {
        if (this.l) {
            return;
        }
        this.i.a(false);
        reconnect();
        int i = this.k | 1;
        this.k = i;
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null && this.o != null) {
            try {
                cVar.b(i);
            } catch (RemoteException unused) {
            }
        }
        this.f.a("stateChanged ", this.k);
    }

    public void onStop() {
        if (this.l) {
            return;
        }
        this.i.a(true);
        this.h.a();
        int i = this.k & (-2);
        this.k = i;
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null && this.o != null) {
            try {
                cVar.b(i);
            } catch (RemoteException unused) {
            }
        }
        this.f.a("stateChanged ", this.k);
    }

    final void a(com.google.android.libraries.a.c cVar) {
        this.g.a("Connected", cVar != null);
        this.a = cVar;
        if (cVar == null) {
            b(0);
        } else if (this.o != null) {
            a();
        }
    }

    public void reattachOverlay() {
        this.f.a("reattachOverlay");
        if (this.o == null || b < 7) {
            return;
        }
        a();
    }

    public void reconnect() {
        if (this.l) {
            return;
        }
        l.a(this.e, new h(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void a(Context context) {
        ResolveInfo resolveInfoResolveService = context.getPackageManager().resolveService(a(context, this.n.b), 128);
        if (resolveInfoResolveService == null || resolveInfoResolveService.serviceInfo.metaData == null) {
            b = 1;
        } else {
            b = resolveInfoResolveService.serviceInfo.metaData.getInt("service.api.version", 1);
        }
    }

    private final void a(boolean z) {
        if (!this.l) {
            this.c.unregisterReceiver(this.j);
        }
        this.l = true;
        this.h.a();
        i iVar = this.p;
        if (iVar != null) {
            iVar.a();
            this.p = null;
        }
        this.i.a(this, z);
    }

    public void requestHotwordDetection(boolean z) {
        this.f.a("requestHotwordDetection", z);
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null) {
            try {
                cVar.b(z);
            } catch (RemoteException unused) {
            }
        }
    }

    public void setClientOptions(ClientOptions clientOptions) {
        if (this.n.a != clientOptions.a) {
            this.n = clientOptions;
            if (this.o != null) {
                a();
            }
            e eVar = this.f;
            int i = this.n.a;
            StringBuilder sb = new StringBuilder(28);
            sb.append("setClientOptions ");
            sb.append(i);
            eVar.a(sb.toString());
            return;
        }
        if (b < 10 || this.n.c == null || clientOptions.c == null) {
            return;
        }
        try {
            this.n = clientOptions;
            if (this.a != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("partner_overlay_icon", this.n.c);
                bundle.putString("partner_overlay_product_name", this.n.e);
                bundle.putBoolean("google_overlay_is_default", this.n.d);
                this.a.c(bundle);
                this.f.a("updateClientOptions");
                return;
            }
            Log.d("DrawerOverlayClient", "updateClientOptions not called since the overlay has not yet attached");
        } catch (RemoteException e) {
            Log.e("DrawerOverlayClient", "updateClientOptions() to set partner overlay icon failed", e);
        }
    }

    private final void a(WindowManager.LayoutParams layoutParams) {
        if (this.o == layoutParams) {
            return;
        }
        this.o = layoutParams;
        if (layoutParams != null) {
            a();
            return;
        }
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null) {
            try {
                cVar.a(this.c.isChangingConfigurations());
            } catch (RemoteException unused) {
            }
            this.a = null;
        }
    }

    public void showOverlay(int i) {
        int iA = a(i);
        this.f.a("showOverlay", i);
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null) {
            try {
                cVar.c(iA);
            } catch (RemoteException unused) {
            }
        }
    }

    public void showOverlay(AnimationType animationType, int i) {
        if (b < 10) {
            showOverlay(i);
        }
        e eVar = this.f;
        String strValueOf = String.valueOf(animationType);
        StringBuilder sb = new StringBuilder(String.valueOf(strValueOf).length() + 15);
        sb.append("showOverlay: ");
        sb.append(strValueOf);
        sb.append(", ");
        eVar.a(sb.toString(), i);
        if (this.a != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("overlay_animation_type", animationType.a());
            bundle.putInt("overlay_animation_duration", i);
            try {
                this.a.b(bundle);
            } catch (RemoteException e) {
                Log.d("DrawerOverlayClient", "Unable to show overlay", e);
            }
        }
    }

    public void showOverlay(boolean z) {
        this.f.a("showOverlay", z);
        com.google.android.libraries.a.c cVar = this.a;
        if (cVar != null) {
            try {
                cVar.c(z ? 1 : 0);
            } catch (RemoteException unused) {
            }
        }
    }

    public void startMove() {
        this.f.a("startMove");
        if (b()) {
            try {
                this.a.b();
            } catch (RemoteException unused) {
            }
        }
    }

    public void updateMove(float f) {
        this.f.a("updateMove", f);
        if (b()) {
            try {
                this.a.a(f);
            } catch (RemoteException unused) {
            }
        }
    }

    public void updatePartnerOverlayIcon(Bitmap bitmap) {
        setClientOptions(ClientOptions.createForSharedOverlay(this.n.e, bitmap, this.n.d));
    }

    private static int a(int i) {
        if (i <= 0 || i > 2047) {
            throw new IllegalArgumentException("Invalid duration");
        }
        return (i << 2) | 1;
    }
}
