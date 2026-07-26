package com.zxerox.lg_extended.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.zxerox.lg_extended.R;
import com.zxerox.lg_extended.views.BatteryIconView;

public class BatteryStyleActivity extends AppCompatActivity {

    private static final Uri PREFS_URI = Uri.parse("content://com.zxerox.lg_extended.prefs/prefs");

    private LinearLayout rowIos26, rowIos17, rowOneUi9, rowOneUi8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_style);

        rowIos26 = findViewById(R.id.rowIos26);
        rowIos17 = findViewById(R.id.rowIos17);
        rowOneUi9 = findViewById(R.id.rowOneUi9);
        rowOneUi8 = findViewById(R.id.rowOneUi8);

        actualizarVistasPrevias();

        rowIos26.setOnClickListener(v -> seleccionar(BatteryIconView.Estilo.IOS_26));
        rowIos17.setOnClickListener(v -> seleccionar(BatteryIconView.Estilo.IOS_17));
        rowOneUi9.setOnClickListener(v -> seleccionar(BatteryIconView.Estilo.ONEUI_9));
        rowOneUi8.setOnClickListener(v -> seleccionar(BatteryIconView.Estilo.ONEUI_8));

        findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());
        findViewById(R.id.btnPersonalizarColores).setOnClickListener(v -> mostrarMenuColores());

        marcarSeleccionInicial();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarVistasPrevias();
    }

    private void actualizarVistasPrevias() {
        setPreview(rowIos26, BatteryIconView.Estilo.IOS_26);
        setPreview(rowIos17, BatteryIconView.Estilo.IOS_17);
        setPreview(rowOneUi9, BatteryIconView.Estilo.ONEUI_9);
        setPreview(rowOneUi8, BatteryIconView.Estilo.ONEUI_8);
    }

    private void setPreview(LinearLayout row, BatteryIconView.Estilo estilo) {
        View child = row.getChildAt(0);
        if (child instanceof BatteryIconView) {
            BatteryIconView view = (BatteryIconView) child;
            view.setEstilo(estilo);

            int fondoNormal = leerColorGuardado("battery_color_fondo", Color.parseColor("#1C1C1E"));
            int textoNormal = leerColorGuardado("battery_color_texto", Color.WHITE);

            view.setColoresNormal(fondoNormal, textoNormal);

            view.actualizarEstado(75, false);
        }
    }

    private int leerColorGuardado(String key, int fallback) {
        try {
            Cursor c = getContentResolver().query(
                    PREFS_URI,
                    new String[]{key},
                    "int",
                    new String[]{String.valueOf(fallback)},
                    null
            );
            if (c != null && c.moveToFirst()) {
                String valor = c.getString(0);
                c.close();
                if (valor != null && !valor.isEmpty()) {
                    return Integer.parseInt(valor);
                }
            }
        } catch (Throwable ignored) {}
        return fallback;
    }

    private void seleccionar(BatteryIconView.Estilo estilo) {
        rowIos26.setBackgroundResource(estilo == BatteryIconView.Estilo.IOS_26 ? R.drawable.row_selected : R.drawable.row_default);
        rowIos17.setBackgroundResource(estilo == BatteryIconView.Estilo.IOS_17 ? R.drawable.row_selected : R.drawable.row_default);
        rowOneUi9.setBackgroundResource(estilo == BatteryIconView.Estilo.ONEUI_9 ? R.drawable.row_selected : R.drawable.row_default);
        rowOneUi8.setBackgroundResource(estilo == BatteryIconView.Estilo.ONEUI_8 ? R.drawable.row_selected : R.drawable.row_default);

        ContentValues values = new ContentValues();
        values.put("key", "battery_style");
        values.put("type", "string");
        values.put("value", estilo.name());
        getContentResolver().insert(PREFS_URI, values);
    }

    private void marcarSeleccionInicial() {
        BatteryIconView.Estilo actual = BatteryIconView.Estilo.ONEUI_9;
        try {
            Cursor c = getContentResolver().query(PREFS_URI, new String[]{"battery_style"}, "string", null, null);
            if (c != null && c.moveToFirst()) {
                String valor = c.getString(0);
                c.close();
                if (valor != null && !valor.isEmpty()) {
                    actual = BatteryIconView.Estilo.valueOf(valor);
                }
            }
        } catch (Throwable ignored) {}

        seleccionar(actual);
    }

    private void mostrarMenuColores() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setContentView(R.layout.bottom_sheet_colors);

        View btnColorFondoNormal = bottomSheet.findViewById(R.id.btnColorFondoNormal);
        View btnColorTextoNormal = bottomSheet.findViewById(R.id.btnColorTextoNormal);

        View btnColorFondoCargando = bottomSheet.findViewById(R.id.btnColorFondoCargando);
        View btnColorTextoCargando = bottomSheet.findViewById(R.id.btnColorTextoCargando);
        View btnColorFondoBajo = bottomSheet.findViewById(R.id.btnColorFondoBateriaBaja);
        View btnColorTextoBajo = bottomSheet.findViewById(R.id.btnColorTextoBateriaBaja);

        if (btnColorFondoNormal != null) {
            btnColorFondoNormal.setOnClickListener(v -> {
                bottomSheet.dismiss();
                mostrarSelectorDeColor("battery_color_fondo", "Fondo Normal");
            });
        }
        if (btnColorTextoNormal != null) {
            btnColorTextoNormal.setOnClickListener(v -> {
                bottomSheet.dismiss();
                mostrarSelectorDeColor("battery_color_texto", "Texto Normal");
            });
        }

        if (btnColorFondoCargando != null) {
            btnColorFondoCargando.setOnClickListener(v -> {
                bottomSheet.dismiss();
                mostrarSelectorDeColor("battery_color_fondo_cargando", "Fondo Cargando");
            });
        }
        if (btnColorTextoCargando != null) {
            btnColorTextoCargando.setOnClickListener(v -> {
                bottomSheet.dismiss();
                mostrarSelectorDeColor("battery_color_texto_cargando", "Texto Cargando");
            });
        }

        if (btnColorFondoBajo != null) {
            btnColorFondoBajo.setOnClickListener(v -> {
                bottomSheet.dismiss();
                mostrarSelectorDeColor("battery_color_fondo_bajo", "Fondo Batería Baja");
            });
        }
        if (btnColorTextoBajo != null) {
            btnColorTextoBajo.setOnClickListener(v -> {
                bottomSheet.dismiss();
                mostrarSelectorDeColor("battery_color_texto_bajo", "Texto Batería Baja");
            });
        }

        bottomSheet.show();
    }
    private void mostrarSelectorDeColor(String prefKey, String titulo) {
        new ColorPickerDialog.Builder(this)
                .setTitle(titulo)
                .setPreferenceName(prefKey)
                .setPositiveButton("Guardar", (ColorEnvelopeListener) (envelope, fromUser) -> {
                    int colorSeleccionado = envelope.getColor();

                    ContentValues values = new ContentValues();
                    values.put("key", prefKey);
                    values.put("type", "int");
                    values.put("value", colorSeleccionado);
                    getContentResolver().insert(PREFS_URI, values);

                    actualizarVistasPrevias();
                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }
}