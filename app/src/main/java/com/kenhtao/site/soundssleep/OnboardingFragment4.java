package com.kenhtao.site.soundssleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class OnboardingFragment4 extends Fragment {
    private Button btn_start;


    public OnboardingFragment4() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding4, container, false);


        Button btnStart = view.findViewById(R.id.btn_start);




        btnStart.setOnClickListener(v -> {

            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        });

        return view;
    }
}