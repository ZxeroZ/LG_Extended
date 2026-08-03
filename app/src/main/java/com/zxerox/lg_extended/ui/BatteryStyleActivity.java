package com.zxerox.lg_extended.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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
            int bordeNormal = leerColorGuardado("battery_color_borde", Color.WHITE);

            view.setColoresNormal(fondoNormal, textoNormal);
            view.setColoresBordeNormal(bordeNormal);

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

    private void configurarBotonColor(BottomSheetDialog dialog, View btn, View preview, String prefKey, String titulo, int fallbackColor) {
        if (btn == null) return;
        
        int currentColor = leerColorGuardado(prefKey, fallbackColor);
        if (preview != null && preview.getBackground() != null) {
            preview.getBackground().mutate().setTint(currentColor);
        }

        btn.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarSelectorDeColor(prefKey, titulo);
        });
    }

    private void mostrarMenuColores() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setContentView(R.layout.bottom_sheet_colors);

        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorFondoNormal), bottomSheet.findViewById(R.id.previewFondoNormal), "battery_color_fondo", "Fondo Normal", Color.parseColor("#1C1C1E"));
        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorTextoNormal), bottomSheet.findViewById(R.id.previewTextoNormal), "battery_color_texto", "Texto Normal", Color.WHITE);
        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorBordeNormal), bottomSheet.findViewById(R.id.previewBordeNormal), "battery_color_borde", "Borde Normal", Color.WHITE);

        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorFondoCargando), bottomSheet.findViewById(R.id.previewFondoCargando), "battery_color_fondo_cargando", "Fondo Cargando", Color.parseColor("#34C759"));
        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorTextoCargando), bottomSheet.findViewById(R.id.previewTextoCargando), "battery_color_texto_cargando", "Texto Cargando", Color.WHITE);
        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorBordeCargando), bottomSheet.findViewById(R.id.previewBordeCargando), "battery_color_borde_cargando", "Borde Cargando", Color.WHITE);

        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorFondoBateriaBaja), bottomSheet.findViewById(R.id.previewFondoBateriaBaja), "battery_color_fondo_bajo", "Fondo Batería Baja", Color.parseColor("#FF3B30"));
        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorTextoBateriaBaja), bottomSheet.findViewById(R.id.previewTextoBateriaBaja), "battery_color_texto_bajo", "Texto Batería Baja", Color.WHITE);
        configurarBotonColor(bottomSheet, bottomSheet.findViewById(R.id.btnColorBordeBateriaBaja), bottomSheet.findViewById(R.id.previewBordeBateriaBaja), "battery_color_borde_bajo", "Borde Batería Baja", Color.WHITE);

        bottomSheet.show();
    }
    private void mostrarSelectorDeColor(String prefKey, String titulo) {
        int currentColor = leerColorGuardado(prefKey, Color.WHITE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom_color_picker, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView titleView = dialogView.findViewById(R.id.dialogTitle);
        titleView.setText(titulo);

        com.skydoves.colorpickerview.ColorPickerView colorPickerView = dialogView.findViewById(R.id.colorPickerView);
        com.skydoves.colorpickerview.sliders.AlphaSlideBar alphaSlideBar = dialogView.findViewById(R.id.alphaSlideBar);
        com.skydoves.colorpickerview.sliders.BrightnessSlideBar brightnessSlideBar = dialogView.findViewById(R.id.brightnessSlideBar);
        View hexPreview = dialogView.findViewById(R.id.hexPreview);
        EditText hexInput = dialogView.findViewById(R.id.hexInput);

        colorPickerView.attachAlphaSlider(alphaSlideBar);
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);
        colorPickerView.setInitialColor(currentColor);

        final boolean[] isUpdatingHex = {false};
        
        colorPickerView.setColorListener(new com.skydoves.colorpickerview.listeners.ColorEnvelopeListener() {
            @Override
            public void onColorSelected(com.skydoves.colorpickerview.ColorEnvelope envelope, boolean fromUser) {
                int selectedColor = envelope.getColor();
                if (hexPreview != null && hexPreview.getBackground() != null) {
                    hexPreview.getBackground().mutate().setTint(selectedColor);
                }
                if (fromUser || !isUpdatingHex[0]) {
                    isUpdatingHex[0] = true;
                    hexInput.setText(String.format("#%08X", (0xFFFFFFFF & selectedColor)));
                    isUpdatingHex[0] = false;
                }
            }
        });

        hexInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (isUpdatingHex[0]) return;
                String hex = s.toString().trim();
                if (!hex.startsWith("#")) {
                    hex = "#" + hex;
                }
                try {
                    int color = Color.parseColor(hex);
                    isUpdatingHex[0] = true;
                    colorPickerView.setInitialColor(color);
                    if (hexPreview != null && hexPreview.getBackground() != null) {
                        hexPreview.getBackground().mutate().setTint(color);
                    }
                    isUpdatingHex[0] = false;
                } catch (Exception ignored) {}
            }
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            int finalColor = colorPickerView.getColor();
            ContentValues values = new ContentValues();
            values.put("key", prefKey);
            values.put("type", "int");
            values.put("value", finalColor);
            getContentResolver().insert(PREFS_URI, values);
            actualizarVistasPrevias();
            dialog.dismiss();
        });

        dialog.show();
    }
}