package com.android.launcher3.util;

import android.content.Context;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.ResourceProvider;

/* JADX INFO: loaded from: classes.dex */
public class DynamicResource implements ResourceProvider, PluginListener<ResourceProvider> {
    private static final MainThreadInitializedObject<DynamicResource> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.util.-$$Lambda$DynamicResource$H76pgZzgL_y1hqAVfGzB3i_vAOw
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return DynamicResource.lambda$H76pgZzgL_y1hqAVfGzB3i_vAOw(context);
        }
    });
    private final Context mContext;
    private ResourceProvider mPlugin;

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.launcher3.util.DynamicResource.<init>(android.content.Context):void type: CONSTRUCTOR */
    public static /* synthetic */ DynamicResource lambda$H76pgZzgL_y1hqAVfGzB3i_vAOw(Context context) {
        return new DynamicResource(context);
    }

    private DynamicResource(Context context) {
        this.mContext = context;
        PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).addPluginListener(this, ResourceProvider.class, false);
    }

    @Override // com.android.systemui.plugins.ResourceProvider
    public int getInt(int resId) {
        return this.mContext.getResources().getInteger(resId);
    }

    @Override // com.android.systemui.plugins.ResourceProvider
    public float getFraction(int resId) {
        return this.mContext.getResources().getFraction(resId, 1, 1);
    }

    @Override // com.android.systemui.plugins.ResourceProvider
    public float getDimension(int resId) {
        return this.mContext.getResources().getDimension(resId);
    }

    @Override // com.android.systemui.plugins.ResourceProvider
    public int getColor(int resId) {
        return this.mContext.getResources().getColor(resId, null);
    }

    @Override // com.android.systemui.plugins.ResourceProvider
    public float getFloat(int resId) {
        return this.mContext.getResources().getFloat(resId);
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginConnected(Lcom/android/systemui/plugins/Plugin;Landroid/content/Context;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(ResourceProvider plugin, Context context) {
        this.mPlugin = plugin;
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginDisconnected(Lcom/android/systemui/plugins/Plugin;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(ResourceProvider plugin) {
        this.mPlugin = null;
    }

    public static ResourceProvider provider(Context context) {
        DynamicResource dynamicResource = INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        ResourceProvider resourceProvider = dynamicResource.mPlugin;
        return resourceProvider == null ? dynamicResource : resourceProvider;
    }
}
