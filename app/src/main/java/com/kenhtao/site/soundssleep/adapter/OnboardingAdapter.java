package com.kenhtao.site.soundssleep.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kenhtao.site.soundssleep.OnboardingFragment1;
import com.kenhtao.site.soundssleep.OnboardingFragment2;
import com.kenhtao.site.soundssleep.OnboardingFragment3;
import com.kenhtao.site.soundssleep.OnboardingFragment4;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OnboardingFragment1();
            case 1:
                return new OnboardingFragment2();
            case 2:
                return new OnboardingFragment3();
            case 3:
                return new OnboardingFragment4();
            default:
                return new OnboardingFragment1();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}