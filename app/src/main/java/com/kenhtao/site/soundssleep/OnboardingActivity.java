package com.kenhtao.site.soundssleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.kenhtao.site.soundssleep.adapter.OnboardingAdapter;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnGrantAccess;
    private TextView btnNotNow;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnGrantAccess = findViewById(R.id.btnGrantAccess);
        btnNotNow = findViewById(R.id.btnNotNow);

        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == adapter.getItemCount() - 1) {
                    btnGrantAccess.setVisibility(View.GONE);
                    btnNotNow.setVisibility(View.GONE);
                } else {
                    btnGrantAccess.setVisibility(View.VISIBLE);
                    btnNotNow.setVisibility(View.VISIBLE);
                }
            }
        });

        btnGrantAccess.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        btnNotNow.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        SharedPreferences prefs = getSharedPreferences("onboarding", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstTime", false).apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
