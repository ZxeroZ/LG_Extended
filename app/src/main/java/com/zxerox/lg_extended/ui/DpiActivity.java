package com.zxerox.lg_extended.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxerox.lg_extended.R;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DpiActivity extends AppCompatActivity {

    private List<ApplicationInfo> appList;
    private PackageManager packageManager;
    private AppAdapter adapter;
    private static final Uri PREFS_URI = Uri.parse("content://com.zxerox.lg_extended.prefs/prefs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dpi);

        packageManager = getPackageManager();
        appList = new ArrayList<>();

        obtenerAplicaciones();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewApps);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("lg_extended_prefs", Context.MODE_PRIVATE);

        adapter = new AppAdapter(appList, packageManager, prefs, appInfo -> mostrarDialogoDpi(appInfo, prefs));
        recyclerView.setAdapter(adapter);
    }

    private void obtenerAplicaciones() {
        List<ApplicationInfo> todasLasApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : todasLasApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.add(appInfo);
            }
        }
    }

    private void mostrarDialogoDpi(ApplicationInfo appInfo, SharedPreferences pref) {
        String nombreApp = packageManager.getApplicationLabel(appInfo).toString();

        View view = getLayoutInflater().inflate(R.layout.dialog_dpi_edit, null);

        TextView dialogSubtitle = view.findViewById(R.id.dialogSubtitle);
        dialogSubtitle.setText("Enter the new DPI for " + nombreApp + " (leave empty to restore default)");

        EditText input = view.findViewById(R.id.inputDpi);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        int dpiActual = pref.getInt(appInfo.packageName, 0);
        if (dpiActual > 0) {
            input.setText(String.valueOf(dpiActual));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        view.findViewById(R.id.btnCancelar).setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String dpiStr = input.getText().toString();
            int dpiValue = dpiStr.isEmpty() ? 0 : Integer.parseInt(dpiStr);

            guardarDpiPorApp(appInfo.packageName, dpiValue);
            pref.edit().putInt(appInfo.packageName, dpiValue).apply();
            reiniciarAppConRoot(appInfo.packageName);
            adapter.notifyDataSetChanged();

            Toast.makeText(this,
                    (dpiValue == 0 ? "DPI restored" : "DPI changed to " + dpiValue) + ". App restarted.",
                    Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void guardarDpiPorApp(String packageName, int dpiValue) {
        ContentValues values = new ContentValues();
        values.put("key", packageName);
        values.put("type", "int");
        values.put("value", dpiValue);
        getContentResolver().insert(PREFS_URI, values);
    }

    private void reiniciarAppConRoot(String packageName) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            os.writeBytes("am force-stop " + packageName + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}