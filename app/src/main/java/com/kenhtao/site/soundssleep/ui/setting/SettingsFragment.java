package com.kenhtao.site.soundssleep.ui.setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenhtao.site.soundssleep.R;
import com.kenhtao.site.soundssleep.adapter.SettingsAdapter;
import com.kenhtao.site.soundssleep.data.AppDatabase;
import com.kenhtao.site.soundssleep.models.SettingItem;
import com.kenhtao.site.soundssleep.ui.setting.SettingActivity.CreditsActivity;
import com.kenhtao.site.soundssleep.ui.setting.SettingActivity.FaqActivity;
import com.kenhtao.site.soundssleep.ui.setting.SettingActivity.GoPremiumActivity;
import com.kenhtao.site.soundssleep.ui.setting.SettingActivity.MoreFreeAppsActivity;
import com.kenhtao.site.soundssleep.ui.setting.SettingActivity.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SettingsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewSettings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SettingsAdapter(getSettingItems(), getContext(), this::handleSettingClick);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<SettingItem> getSettingItems() {
        List<SettingItem> items = new ArrayList<>();
        items.add(new SettingItem("Go Premium"));
        items.add(new SettingItem("Playback Stops Unexpectedly?"));
        items.add(new SettingItem("Rate Us"));
        items.add(new SettingItem("Share"));
        items.add(new SettingItem("App Version"));
        items.add(new SettingItem("FAQ"));
        items.add(new SettingItem("Feedback"));
        items.add(new SettingItem("Credit"));
        items.add(new SettingItem("Manage Subscriptions"));
        items.add(new SettingItem("Privacy Policy"));
        items.add(new SettingItem("More Free Apps"));
        items.add(new SettingItem("Reset App")); // ✅ Add Reset at the end
        return items;
    }

    private void handleSettingClick(SettingItem item) {
        switch (item.getTitle()) {
            case "Rate Us":
                openPlayStore();
                break;
            case "Playback Stops Unexpectedly?":
                showPlaybackHelpDialog();
                break;
            case "Share":
                shareApp();
                break;
            case "Go Premium":
                openPremiumScreen();
                break;
            case "App Version":
                showAppVersion();
                break;
            case "FAQ":
                openWeb("https://yourapp.com/faq");
                break;
            case "Feedback":
                sendFeedbackEmail();
                break;
            case "Credit":
                startActivity(new Intent(getContext(), CreditsActivity.class));
                break;
            case "Manage Subscriptions":
                openSubscriptionPage();
                break;
            case "Privacy Policy":
                openPrivacyPolicy();
                break;
            case "More Free Apps":
                startActivity(new Intent(requireContext(), MoreFreeAppsActivity.class));
                break;
            case "Reset App":
                showResetDialog();
                break;
            default:
                showToast(item.getTitle());
                break;
        }
    }

    private void openPlayStore() {
        String marketLink = getString(R.string.google_play_dev_market);
        String fallbackHttpLink = getString(R.string.google_play_dev_http);

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketLink)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackHttpLink)));
        }
    }

    private void showPlaybackHelpDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Troubleshooting Playback")
                .setMessage("If the sound stops unexpectedly, try the following:\n\n" +
                        "• Disable battery optimization\n" +
                        "• Allow background activity\n" +
                        "• Lock the app in recent apps\n" +
                        "• Avoid Battery Saver mode")
                .setPositiveButton("Got it", null)
                .show();
    }

    private void shareApp() {
        String shareText = "Try SleepChills – the app that helps you relax and sleep better:\n\n" +
                getString(R.string.app_share_link);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "SleepChills App");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void openPremiumScreen() {
        startActivity(new Intent(requireContext(), GoPremiumActivity.class));
    }

    private void showAppVersion() {
        try {
            String versionName = requireContext()
                    .getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0)
                    .versionName;

            new AlertDialog.Builder(requireContext())
                    .setTitle("App Version")
                    .setMessage("Current version: " + versionName)
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            showToast("Unable to get version info");
        }
    }

    private void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: dev@kenhtao.site"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sleep Sound Feedback");
        startActivity(Intent.createChooser(intent, "Send Feedback"));
    }

    private void openWeb(String url) {
        Intent intent = new Intent(requireContext(), FaqActivity.class);
        // or use WebViewActivity to pass the URL
        startActivity(intent);
    }

    private void openPrivacyPolicy() {
        Intent intent = new Intent(requireContext(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, "https://kenhtao.site/sleep-chills-privacy-policy");
        intent.putExtra(WebViewActivity.EXTRA_TITLE, "Privacy Policy");
        startActivity(intent);
    }

    private void openSubscriptionPage() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/account/subscriptions?sku=your-sub-product-id&package=your-app-package")));
        } catch (Exception e) {
            showToast("Unable to open subscriptions.");
        }
    }

    private void showResetDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reset App")
                .setMessage("This will delete all local mix data and reset the app. Proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    resetAppData();
                    showToast("App has been reset.");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetAppData() {
        new Thread(() -> {
            AppDatabase db = new AppDatabase(requireContext());
            db.clearAllLocalMixData();
        }).start();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
