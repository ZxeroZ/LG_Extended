package com.zxerox.lg_extended.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zxerox.lg_extended.R;
import com.zxerox.lg_extended.root.RootUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnBatteryStyle).setOnClickListener(v ->
                startActivity(new Intent(this, BatteryStyleActivity.class)));

        findViewById(R.id.btnIosStyle).setOnClickListener(v ->
                startActivity(new Intent(this, IosStyleActivity.class)));

        findViewById(R.id.btnDpi).setOnClickListener(v ->
                startActivity(new Intent(this, DpiActivity.class)));

        findViewById(R.id.btnBypass).setOnClickListener(v ->
                startActivity(new Intent(this, BypassActivity.class)));

        findViewById(R.id.btnRestart).setOnClickListener(v ->
                RootUtils.reiniciarSystemUI(() -> runOnUiThread(() ->
                        Toast.makeText(this, "SystemUI reiniciado", Toast.LENGTH_SHORT).show())));

        verificarEstadoSistema();
    }

    private void verificarEstadoSistema() {
        TextView lsposedDetail = findViewById(R.id.lsposedDetail);
        TextView rootDetail = findViewById(R.id.rootDetail);

        lsposedDetail.setText("LSPosed Active");
        lsposedDetail.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Pinta el texto de verde

        new Thread(() -> {
            boolean root = RootUtils.tieneRoot();
            runOnUiThread(() -> {
                if (root) {
                    rootDetail.setText("Root access granted");
                    rootDetail.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Verde
                } else {
                    rootDetail.setText("System service not running");
                    rootDetail.setTextColor(android.graphics.Color.parseColor("#FF5252")); // Rojo
                }
            });
        }).start();
    }
}