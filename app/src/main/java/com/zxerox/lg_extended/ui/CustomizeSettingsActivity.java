package com.zxerox.lg_extended.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.zxerox.lg_extended.R;
import com.zxerox.lg_extended.prefs.ModPrefs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CustomizeSettingsActivity extends AppCompatActivity {

    private ImageView previewAvatar, editAvatar;
    private TextView previewName, previewPhrase;
    private EditText editName, editPhrase;
    private ActivityResultLauncher<Intent> avatarLauncher;
    private String currentBase64Avatar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_settings);

        editName = findViewById(R.id.editName);
        editPhrase = findViewById(R.id.editPhrase);
        previewAvatar = findViewById(R.id.previewAvatar);
        editAvatar = findViewById(R.id.editAvatar);
        previewName = findViewById(R.id.previewName);
        previewPhrase = findViewById(R.id.previewPhrase);

        avatarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                InputStream in = getContentResolver().openInputStream(uri);
                                Bitmap rawBmp = BitmapFactory.decodeStream(in);
                                in.close();

                                int maxSize = 256;
                                int width = rawBmp.getWidth();
                                int height = rawBmp.getHeight();
                                float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
                                int newWidth = Math.max(1, Math.round(ratio * width));
                                int newHeight = Math.max(1, Math.round(ratio * height));
                                Bitmap scaledBmp = Bitmap.createScaledBitmap(rawBmp, newWidth, newHeight, true);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                scaledBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                currentBase64Avatar = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                                if (previewAvatar != null) previewAvatar.setImageBitmap(scaledBmp);
                                if (editAvatar != null) editAvatar.setImageBitmap(scaledBmp);
                            } catch (Exception e) {
                                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        loadSavedData();
        setupListeners();
    }

    private void loadSavedData() {
        String savedName = "";
        String savedPhrase = "";
        
        try {
            Cursor cName = getContentResolver().query(ModPrefs.CONTENT_URI,
                    new String[]{"profile_name"}, null, new String[]{""}, null);
            if (cName != null && cName.moveToFirst()) {
                savedName = cName.getString(0);
                cName.close();
            }
            Cursor cPhrase = getContentResolver().query(ModPrefs.CONTENT_URI,
                    new String[]{"profile_phrase"}, null, new String[]{""}, null);
            if (cPhrase != null && cPhrase.moveToFirst()) {
                savedPhrase = cPhrase.getString(0);
                cPhrase.close();
            }
            Cursor cAvatar = getContentResolver().query(ModPrefs.CONTENT_URI,
                    new String[]{"profile_avatar_base64"}, null, new String[]{""}, null);
            if (cAvatar != null && cAvatar.moveToFirst()) {
                currentBase64Avatar = cAvatar.getString(0);
                cAvatar.close();
            }
        } catch (Throwable ignored) {}

        if (!savedName.isEmpty()) {
            editName.setText(savedName);
            previewName.setText(savedName);
        }
        if (!savedPhrase.isEmpty()) {
            editPhrase.setText(savedPhrase);
            previewPhrase.setText(savedPhrase);
        }
        if (currentBase64Avatar != null && !currentBase64Avatar.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(currentBase64Avatar, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                if (bmp != null) {
                    if (previewAvatar != null) previewAvatar.setImageBitmap(bmp);
                    if (editAvatar != null) editAvatar.setImageBitmap(bmp);
                }
            } catch (Throwable ignored) {}
        }
    }

    private void setupListeners() {
        editName.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewName.setText(s.length() > 0 ? s.toString() : "LG V60 User");
            }
            public void afterTextChanged(android.text.Editable s) {}
        });
        editPhrase.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewPhrase.setText(s.length() > 0 ? s.toString() : "Stock is a suggestion");
            }
            public void afterTextChanged(android.text.Editable s) {}
        });

        findViewById(R.id.avatarSelector).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            avatarLauncher.launch(intent);
        });

        findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phrase = editPhrase.getText().toString().trim();

            SharedPreferences prefs = getSharedPreferences("lg_extended_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            if (!name.isEmpty()) editor.putString("profile_name", name);
            if (!phrase.isEmpty()) editor.putString("profile_phrase", phrase);
            if (currentBase64Avatar != null && !currentBase64Avatar.isEmpty()) {
                editor.putString("profile_avatar_base64", currentBase64Avatar);
            }
            
            editor.apply();
            
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "am force-stop com.android.settings"});
                Toast.makeText(this, "Perfil actualizado. Ajustes reiniciado.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Perfil actualizado. Por favor, cierra Ajustes manualmente.", Toast.LENGTH_LONG).show();
            }
            
            finish();
        });
    }
}
