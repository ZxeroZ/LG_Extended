package com.zxerox.lg_extended.hooks;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.database.Cursor;
import android.net.Uri;

import java.util.Map;
import java.util.WeakHashMap;

import com.zxerox.lg_extended.views.BatteryIconView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class BatteryHook {

    private final Map<View, BatteryIconView> baterias = new WeakHashMap<>();

    public void hook(LoadPackageParam lpparam) {

        XposedHelpers.findAndHookMethod(
                "com.lge.systemui.widget.LGBatteryMeterView",
                lpparam.classLoader,
                "onAttachedToWindow",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View original = (View) param.thisObject;
                        Context context = original.getContext();

                        ViewGroup padre = (ViewGroup) original.getParent();
                        if (padre == null) {
                            XposedBridge.log("LG_Extended/Battery: padre nulo, aún no está en el árbol de vistas.");
                            return;
                        }

                        original.setVisibility(View.GONE);

                        if (!baterias.containsKey(original)) {
                            BatteryIconView nueva = new BatteryIconView(context);

                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            padre.addView(nueva, padre.indexOfChild(original), params);

                            baterias.put(original, nueva);
                            XposedBridge.log("LG_Extended/Battery: ícono inyectado en " + padre.getClass().getSimpleName());

                            aplicarColoresGuardados(context, nueva);

                            context.getContentResolver().registerContentObserver(
                                    Uri.parse("content://com.zxerox.lg_extended.prefs/prefs"),
                                    true,
                                    new android.database.ContentObserver(new android.os.Handler(android.os.Looper.getMainLooper())) {
                                        @Override
                                        public void onChange(boolean selfChange) {
                                            BatteryIconView.Estilo nuevoEstilo = leerEstiloGuardado(context);
                                            for (BatteryIconView v : baterias.values()) {
                                                v.setEstilo(nuevoEstilo);
                                                aplicarColoresGuardados(context, v);
                                            }
                                        }
                                    }
                            );
                        } else {
                            baterias.get(original).setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                "com.lge.systemui.widget.LGBatteryMeterView",
                lpparam.classLoader,
                "onBatteryLevelChanged",
                int.class,
                boolean.class,
                boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View original = (View) param.thisObject;
                        original.setVisibility(View.GONE);

                        ocultarTextoPorcentaje(param);

                        BatteryIconView bateria = baterias.get(original);
                        if (bateria != null) {
                            int nivel = (int) param.args[0];
                            boolean cargando = (boolean) param.args[2];
                            bateria.actualizarEstado(nivel, cargando);
                        }
                    }
                }
        );
    }

    private void ocultarTextoPorcentaje(XC_MethodHook.MethodHookParam param) {
        try {
            TextView textoPorcentaje = (TextView) XposedHelpers.getObjectField(param.thisObject, "mBatteryLevel");
            if (textoPorcentaje != null) {
                textoPorcentaje.setVisibility(View.GONE);
                
                android.view.ViewGroup.LayoutParams lp = textoPorcentaje.getLayoutParams();
                if (lp != null) {
                    lp.width = 0;
                    lp.height = 0;
                    textoPorcentaje.setLayoutParams(lp);
                }
                textoPorcentaje.setPadding(0, 0, 0, 0);
                textoPorcentaje.setTextSize(0);
                textoPorcentaje.setText("");
            }
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended/Battery: error ocultando texto de porcentaje: " + t.getMessage());
        }
    }

    private BatteryIconView.Estilo leerEstiloGuardado(Context context) {
        try {
            Cursor c = context.getContentResolver().query(
                    Uri.parse("content://com.zxerox.lg_extended.prefs/prefs"),
                    new String[]{"battery_style"},
                    "string", null, null
            );
            if (c != null && c.moveToFirst()) {
                String valor = c.getString(0);
                c.close();
                if (valor != null && !valor.isEmpty()) {
                    return BatteryIconView.Estilo.valueOf(valor);
                }
            }
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended/Battery: error leyendo estilo: " + t.getMessage());
        }
        return BatteryIconView.Estilo.ONEUI_8;
    }

    private void aplicarColoresGuardados(Context context, BatteryIconView view) {
        int fondoNormal = leerColorGuardado(context, "battery_color_fondo", Color.parseColor("#1C1C1E"));
        int textoNormal = leerColorGuardado(context, "battery_color_texto", Color.WHITE);
        int bordeNormal = leerColorGuardado(context, "battery_color_borde", Color.WHITE);
        view.setColoresNormal(fondoNormal, textoNormal);
        view.setColoresBordeNormal(bordeNormal);

        int fondoCargando = leerColorGuardado(context, "battery_color_fondo_cargando", Color.parseColor("#34C759"));
        int textoCargando = leerColorGuardado(context, "battery_color_texto_cargando", Color.WHITE);
        int bordeCargando = leerColorGuardado(context, "battery_color_borde_cargando", Color.WHITE);
        view.setColoresCargando(fondoCargando, textoCargando);
        view.setColoresBordeCargando(bordeCargando);

        int fondoBajo = leerColorGuardado(context, "battery_color_fondo_bajo", Color.parseColor("#FF3B30"));
        int textoBajo = leerColorGuardado(context, "battery_color_texto_bajo", Color.WHITE);
        int bordeBajo = leerColorGuardado(context, "battery_color_borde_bajo", Color.WHITE);
        view.setColoresBateriaBaja(fondoBajo, textoBajo);
        view.setColoresBordeBaja(bordeBajo);
    }

    private int leerColorGuardado(Context context, String key, int fallback) {
        try {
            Cursor c = context.getContentResolver().query(
                    Uri.parse("content://com.zxerox.lg_extended.prefs/prefs"),
                    new String[]{key},
                    "int", new String[]{String.valueOf(fallback)}, null
            );
            if (c != null && c.moveToFirst()) {
                String valor = c.getString(0);
                c.close();
                if (valor != null && !valor.isEmpty()) {
                    return Integer.parseInt(valor);
                }
            }
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended/Battery: error leyendo color " + key + ": " + t.getMessage());
        }
        return fallback;
    }
}