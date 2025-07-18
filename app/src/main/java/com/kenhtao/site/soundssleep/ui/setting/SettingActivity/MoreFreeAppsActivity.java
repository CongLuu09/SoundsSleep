package com.kenhtao.site.soundssleep.ui.setting.SettingActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.kenhtao.site.soundssleep.R;
import com.kenhtao.site.soundssleep.utils.LocaleHelper;

public class MoreFreeAppsActivity extends AppCompatActivity {

    private static final String DEVELOPER_URL = "https://play.google.com/store/apps/dev?id=8569549389947187841";

    @Override
    protected void attachBaseContext(Context newBase) {

        String lang = newBase.getSharedPreferences("lang_pref", MODE_PRIVATE)
                .getString("app_lang", "en");
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_free_apps);

        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );


        int[] clickableIds = new int[]{
                R.id.imgBanner,
                R.id.imgLogo,
                R.id.logoGooglePlay,
                R.id.tvCompanyName,
                R.id.tvDescription,
                R.id.tvAppDesc,
                R.id.btnViewAllApps
        };

        View.OnClickListener openDevPageListener = view -> openDeveloperPage();
        for (int id : clickableIds) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnClickListener(openDevPageListener);
            }
        }


        MaterialButton btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        btnChangeLanguage.setOnClickListener(v -> toggleLanguage(btnChangeLanguage));

    }

    private void openDeveloperPage() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DEVELOPER_URL));
            intent.setPackage("com.android.vending");
            startActivity(intent);
        } catch (Exception e) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DEVELOPER_URL)));
        }
    }

    private void toggleLanguage(MaterialButton btnChangeLanguage) {

        String currentLang = getSharedPreferences("lang_pref", MODE_PRIVATE)
                .getString("app_lang", "en");
        String newLang = currentLang.equals("vi") ? "en" : "vi";


        getSharedPreferences("lang_pref", MODE_PRIVATE)
                .edit()
                .putString("app_lang", newLang)
                .apply();


        btnChangeLanguage.setEnabled(false);
        btnChangeLanguage.setText("Switching...");


        new Handler().postDelayed(this::recreate, 100);
    }

}
