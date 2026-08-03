package com.zxerox.lg_extended.hooks;

import android.content.res.XModuleResources;

import com.zxerox.lg_extended.MainHook;
import com.zxerox.lg_extended.R;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class SystemUIHook implements IXposedHookInitPackageResources {

    public void hook(InitPackageResourcesParam resparam) {
        if (!resparam.packageName.equals("com.android.systemui") && !resparam.packageName.equals("com.lge.systemui")) {
            return;
        }

        XSharedPreferences prefs = new XSharedPreferences("com.zxerox.lg_extended", "lg_extended_prefs");
        prefs.makeWorldReadable();
        boolean enabled = prefs.getBoolean("pref_enable_miui_qs", false);
        
        if (!enabled) {
            try {
                java.io.File f = new java.io.File("/data/local/tmp/lg_ext_miui_qs");
                if (f.exists()) {
                    java.util.Scanner s = new java.util.Scanner(f);
                    enabled = s.nextBoolean();
                    s.close();
                }
            } catch (Exception ignored) {}
        }
        
        if (!enabled) {
            return;
        }

        try {
            XModuleResources modRes = XModuleResources.createInstance(MainHook.MODULE_PATH, resparam.res);
            
            resparam.res.setReplacement(resparam.packageName, "drawable", "ic_indi_noti_button_on", modRes.fwd(R.drawable.qs_tile_background));
            resparam.res.setReplacement(resparam.packageName, "drawable", "ic_indi_noti_button_off", modRes.fwd(R.drawable.qs_tile_background));
            
            resparam.res.setReplacement(resparam.packageName, "color", "notification_material_background_color", modRes.fwd(R.color.notification_material_background_color_miui));
            resparam.res.setReplacement(resparam.packageName, "color", "notification_material_background_dimmed_color", modRes.fwd(R.color.notification_material_background_dimmed_color_miui));
            resparam.res.setReplacement(resparam.packageName, "color", "notification_material_background_dimmed_color_transparent", modRes.fwd(R.color.notification_material_background_color_miui));
            
            boolean separateCards = prefs.getBoolean("pref_separate_notification_cards", false);
            if (!separateCards) {
                try {
                    java.io.File f = new java.io.File("/data/local/tmp/lg_ext_separate_cards");
                    if (f.exists()) {
                        java.util.Scanner s = new java.util.Scanner(f);
                        separateCards = s.nextBoolean();
                        s.close();
                    }
                } catch (Exception ignored) {}
            }
            
            if (separateCards) {
                resparam.res.setReplacement(resparam.packageName, "dimen", "notification_corner_radius", modRes.fwd(R.dimen.notification_corner_radius_miui));
                resparam.res.setReplacement(resparam.packageName, "dimen", "notification_corner_radius_small", modRes.fwd(R.dimen.notification_corner_radius_miui));
                resparam.res.setReplacement(resparam.packageName, "dimen", "notification_divider_height", modRes.fwd(R.dimen.notification_divider_height_miui));
            }
            
            resparam.res.setReplacement(resparam.packageName, "dimen", "qs_icon_size", modRes.fwd(R.dimen.qs_icon_size_miui));
            
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended: Error in SystemUIHook initResources - " + t.getMessage());
        }
    }

    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        hook(resparam);
    }
}
