package com.android.launcher3.uioverrides.plugins;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginActionManager;
import com.android.systemui.shared.plugins.PluginInstance;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.plugins.PluginManagerImpl;
import com.android.systemui.shared.plugins.PluginPrefs;
import com.android.systemui.shared.system.UncaughtExceptionPreHandlerManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class PluginManagerWrapper {
    public static final String PLUGIN_CHANGED = "com.android.systemui.action.PLUGIN_CHANGED";
    private final Context mContext;
    private final PluginEnablerImpl mPluginEnabler;
    private final PluginManager mPluginManager;
    public static final MainThreadInitializedObject<PluginManagerWrapper> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.uioverrides.plugins.-$$Lambda$PluginManagerWrapper$wcrwVKrOKSKOcLjAzNnTL7QDkFk
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return PluginManagerWrapper.lambda$wcrwVKrOKSKOcLjAzNnTL7QDkFk(context);
        }
    });
    private static final UncaughtExceptionPreHandlerManager UNCAUGHT_EXCEPTION_PRE_HANDLER_MANAGER = new UncaughtExceptionPreHandlerManager();

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.launcher3.uioverrides.plugins.PluginManagerWrapper.<init>(android.content.Context):void type: CONSTRUCTOR */
    public static /* synthetic */ PluginManagerWrapper lambda$wcrwVKrOKSKOcLjAzNnTL7QDkFk(Context context) {
        return new PluginManagerWrapper(context);
    }

    private PluginManagerWrapper(Context c) {
        this.mContext = c;
        PluginEnablerImpl pluginEnablerImpl = new PluginEnablerImpl(c);
        this.mPluginEnabler = pluginEnablerImpl;
        List listEmptyList = Collections.emptyList();
        this.mPluginManager = new PluginManagerImpl(c, new PluginActionManager.Factory(c, c.getPackageManager(), c.getMainExecutor(), Executors.MODEL_EXECUTOR, (NotificationManager) c.getSystemService(NotificationManager.class), pluginEnablerImpl, listEmptyList, new PluginInstance.Factory(getClass().getClassLoader(), new PluginInstance.InstanceFactory(), new PluginInstance.VersionChecker(), listEmptyList, Utilities.IS_DEBUG_DEVICE)), Utilities.IS_DEBUG_DEVICE, UNCAUGHT_EXCEPTION_PRE_HANDLER_MANAGER, pluginEnablerImpl, new PluginPrefs(c), listEmptyList);
    }

    public PluginEnablerImpl getPluginEnabler() {
        return this.mPluginEnabler;
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> listener, Class<T> pluginClass) {
        addPluginListener(listener, pluginClass, false);
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> listener, Class<T> pluginClass, boolean allowMultiple) {
        this.mPluginManager.addPluginListener(listener, pluginClass, allowMultiple);
    }

    public void removePluginListener(PluginListener<? extends Plugin> listener) {
        this.mPluginManager.removePluginListener(listener);
    }

    public Set<String> getPluginActions() {
        return new PluginPrefs(this.mContext).getPluginList();
    }

    public static String pluginEnabledKey(ComponentName cn) {
        return PluginEnablerImpl.pluginEnabledKey(cn);
    }

    public static boolean hasPlugins(Context context) {
        return PluginPrefs.hasPlugins(context);
    }

    public void dump(PrintWriter pw) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Iterator<String> it = getPluginActions().iterator();
        while (it.hasNext()) {
            for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryIntentServices(new Intent(it.next()), 512)) {
                ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                if (this.mPluginEnabler.isEnabled(componentName)) {
                    arrayList.add(componentName);
                } else {
                    arrayList2.add(componentName);
                }
            }
        }
        pw.println("PluginManager:");
        pw.println("  numEnabledPlugins=" + arrayList.size());
        pw.println("  numDisabledPlugins=" + arrayList2.size());
        pw.println("  enabledPlugins=" + arrayList);
        pw.println("  disabledPlugins=" + arrayList2);
    }
}
