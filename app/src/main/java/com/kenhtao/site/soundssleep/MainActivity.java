package com.kenhtao.site.soundssleep;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kenhtao.site.soundssleep.ui.custom.CustomFragment;
import com.kenhtao.site.soundssleep.ui.home.HomeFragment;
import com.kenhtao.site.soundssleep.ui.profile.ProfileFragment;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_sound);
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.nav_sound) {
                        selectedFragment = new HomeFragment();
                    } else if (item.getItemId() == R.id.nav_custom) {
                        selectedFragment = new CustomFragment();
                    } else if (item.getItemId() == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    return loadFragment(selectedFragment);
                }
            };

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}