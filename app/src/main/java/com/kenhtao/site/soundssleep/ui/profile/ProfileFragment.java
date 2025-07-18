package com.kenhtao.site.soundssleep.ui.profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kenhtao.site.soundssleep.R;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatar, imgAppPanda, imgAppBang;
    private Button btnCreateAccount;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setListeners();
        setupSettings(view);

        return view;
    }

    private void initViews(View view) {
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnCreateAccount = view.findViewById(R.id.btnCreateAccount);
        imgAppPanda = view.findViewById(R.id.imgAppPanda);
        imgAppBang = view.findViewById(R.id.imgAppBang);
    }

    private void setListeners() {
        btnCreateAccount.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sign-up feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        imgAppPanda.setOnClickListener(v -> openAppOnPlayStore("com.pandamess.app"));
        imgAppBang.setOnClickListener(v -> openAppOnPlayStore("com.vietuy.slime.warrior.army.idle.strategy"));
    }

    private void openAppOnPlayStore(String packageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Unable to open Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSettings(View rootView) {
        List<SettingItem> trackerSettings = Arrays.asList(
                new SettingItem("Wake-up Alarm", "Off"),
                new SettingItem("Placement Reminders", "On"),
                new SettingItem("Gear-Up Reminder", "Off"),
                new SettingItem("Battery Warning", "On"),
                new SettingItem("Sleep Note", "Off"),
                new SettingItem("Wake-up Mood", "On")
        );

        List<SettingItem> familyPlan = Arrays.asList(
                new SettingItem("Activate Code", "On")
        );

        List<SettingItem> settings = Arrays.asList(
                new SettingItem("Sleep Reminder", "23:00"),
                new SettingItem("Apple Health", ""),
                new SettingItem("Siri Shortcuts", ""),
                new SettingItem("Live Activity", "On")
        );

        List<SettingItem> other = Arrays.asList(
                new SettingItem("FAQ", "faq"),
                new SettingItem("Privacy Policy", "privacy"),
                new SettingItem("Terms of Service", "terms"),
                new SettingItem("Feedback", "feedback")
        );


        LinearLayout container = rootView.findViewById(R.id.profile_container);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        addSettingSection(inflater, container, "Tracker", trackerSettings);
        addSettingSection(inflater, container, "Family Plan", familyPlan);
        addSettingSection(inflater, container, "Settings", settings);
        addSettingSection(inflater, container, "Other", other);
    }

    private void addSettingSection(LayoutInflater inflater, LinearLayout parent, String title, List<SettingItem> settings) {

        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setTextSize(16);
        titleView.setPadding(0, 24, 0, 8);
        parent.addView(titleView);

        for (SettingItem item : settings) {
            View settingView = inflater.inflate(R.layout.item_profile_setting, parent, false);
            TextView name = settingView.findViewById(R.id.tvSettingName);
            TextView value = settingView.findViewById(R.id.tvSettingValue);

            name.setText(item.name);
            value.setText(item.value);


            if (title.equals("Other")) {
                settingView.setOnClickListener(v -> {
                    switch (item.value) {
                        case "faq":
                            startActivity(new Intent(requireContext(), FaqActivity.class));
                            break;
                        case "privacy":

                            Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://kenhtao.site/sleep-chills-privacy-policy"));
                            startActivity(privacyIntent);
                            break;
                        case "terms":
                            Intent termsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://kenhtao.site/sleep-chills-terms"));
                            startActivity(termsIntent);
                            break;
                        case "feedback":
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto: dev@kenhtao.site"));
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Sleep Sound Feedback");
                            startActivity(Intent.createChooser(intent, "Send Feedback"));
                            break;
                        default:
                            Toast.makeText(requireContext(), "Không tìm thấy mục này", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });

            }

            parent.addView(settingView);
        }
    }


    public static class SettingItem {
        public String name;
        public String value;

        public SettingItem(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
