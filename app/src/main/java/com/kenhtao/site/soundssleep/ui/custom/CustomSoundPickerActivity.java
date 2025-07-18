package com.kenhtao.site.soundssleep.ui.custom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenhtao.site.soundssleep.R;
import com.kenhtao.site.soundssleep.adapter.CustomSoundAdapter;
import com.kenhtao.site.soundssleep.models.SoundDto;
import com.kenhtao.site.soundssleep.models.SoundItem;
import com.kenhtao.site.soundssleep.service.ApiService;
import com.kenhtao.site.soundssleep.service.RetrofitClient;
import com.kenhtao.site.soundssleep.service.SoundResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomSoundPickerActivity extends AppCompatActivity implements CustomSoundAdapter.OnSoundClickListener {

    private RecyclerView recyclerView;
    private CustomSoundAdapter adapter;
    private ImageView btnClose;
    private TextView tvTitle;
    private final List<Object> allItems = new ArrayList<>();

    private static final String FILE_HOST = "https://sleepchills.kenhtao.site/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_sound_picker);

        recyclerView = findViewById(R.id.recyclerViewCustomSounds);
        btnClose = findViewById(R.id.btnClose);
        tvTitle = findViewById(R.id.tvTitle);

        setupRecyclerView();
        setupListeners();
        fetchOnlineSounds();
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new CustomSoundAdapter(this, allItems, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == CustomSoundAdapter.VIEW_TYPE_HEADER ? 3 : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void fetchOnlineSounds() {
        ApiService api = RetrofitClient.getApiService();
        api.getAllSounds().enqueue(new Callback<SoundResponse>() {
            @Override
            public void onResponse(Call<SoundResponse> call, Response<SoundResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SoundDto> sounds = response.body().getData().getSounds();
                    updateSoundListSorted(sounds);
                } else {
                    Log.e("API", "❌ Failed to fetch sounds: null body or error");
                }
            }

            @Override
            public void onFailure(Call<SoundResponse> call, Throwable t) {
                Log.e("API", "❌ Failed to fetch sounds", t);
            }
        });
    }

    private void updateSoundListSorted(List<SoundDto> soundDtos) {
        allItems.clear();

        // ✅ Dùng TreeMap để tự động sắp xếp theo tên category (A → Z)
        Map<String, List<SoundItem>> grouped = new TreeMap<>();

        for (SoundDto dto : soundDtos) {
            // --- Chuẩn hóa URL ---
            String soundUrl = dto.getLink_Music();
            String imageUrl = dto.getAvatar();

            if (soundUrl != null && !soundUrl.startsWith("http")) {
                soundUrl = FILE_HOST + soundUrl;
            }
            if (imageUrl != null && !imageUrl.startsWith("http")) {
                imageUrl = FILE_HOST + "storage/" + imageUrl;
            }

            SoundItem item = new SoundItem(
                    dto.getId(),
                    dto.getTitle(),
                    soundUrl,
                    imageUrl,
                    dto.getCategory()
            );

            String category = dto.getCategory() != null ? dto.getCategory().trim() : "Others";
            grouped.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
        }

        // ✅ Add vào allItems: header + sounds
        for (Map.Entry<String, List<SoundItem>> entry : grouped.entrySet()) {
            allItems.add(entry.getKey());           // Header category name
            allItems.addAll(entry.getValue());      // Danh sách sound
        }

        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    private void returnResult(SoundItem item) {
        Intent intent = new Intent();
        intent.putExtra("soundId", item.getId());
        intent.putExtra("name", item.getName());
        intent.putExtra("fileUrl", item.getFileUrl());
        intent.putExtra("imageUrl", item.getImageUrl());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSoundClick(SoundItem item) {
        returnResult(item);
    }
}
