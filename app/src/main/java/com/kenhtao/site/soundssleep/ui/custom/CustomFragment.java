package com.kenhtao.site.soundssleep.ui.custom;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kenhtao.site.soundssleep.R;
import com.kenhtao.site.soundssleep.adapter.CustomAdapter;
import com.kenhtao.site.soundssleep.models.CustomSound;
import com.kenhtao.site.soundssleep.models.CustomSoundGroup;
import com.kenhtao.site.soundssleep.models.SoundDto;
import com.kenhtao.site.soundssleep.service.ApiService;
import com.kenhtao.site.soundssleep.service.RetrofitClient;
import com.kenhtao.site.soundssleep.service.SoundResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomFragment extends Fragment {

    private static final String TAG = "CustomFragment";
    private static final String IMAGE_HOST = "https://sleepchills.kenhtao.site/storage/";

    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;

    private final Map<Long, MediaPlayer> playingSounds = new HashMap<>();
    private final Set<Long> activeSoundIds = new HashSet<>();
    private final Map<Long, Float> volumeLevels = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewCustom);
        setupRecyclerView();
        fetchAllSounds();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        for (MediaPlayer player : playingSounds.values()) {
            if (player != null && player.isPlaying()) player.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (MediaPlayer player : playingSounds.values()) {
            try {
                if (player.isPlaying()) player.stop();
            } catch (IllegalStateException ignored) {}
            player.release();
        }
        playingSounds.clear();
        activeSoundIds.clear();
        volumeLevels.clear();
    }


    private void setupRecyclerView() {
        customAdapter = new CustomAdapter(new ArrayList<>(), getContext(), new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomSound sound) {
                handleSoundClick(sound);
            }

            @Override
            public void onVolumeChange(CustomSound sound, float volume) {
                updateSoundVolume(sound, volume);
            }

            @Override
            public float getSoundVolume(CustomSound sound) {
                return volumeLevels.getOrDefault(sound.getId(), 1.0f);
            }

            @Override
            public boolean isSoundActive(CustomSound sound) {
                return activeSoundIds.contains(sound.getId());
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return customAdapter.getItemViewType(position) == CustomAdapter.TYPE_GROUP ? 3 : 1;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(customAdapter);
    }


    private void fetchAllSounds() {
        ApiService api = RetrofitClient.getApiService();
        api.getAllSounds().enqueue(new Callback<SoundResponse>() {
            @Override
            public void onResponse(Call<SoundResponse> call, Response<SoundResponse> response) {
                if (response.isSuccessful()) {
                    SoundResponse soundResponse = response.body();

                    if (soundResponse != null && soundResponse.getData() != null) {
                        List<SoundDto> sounds = soundResponse.getData().getSounds();

                        Log.d(TAG, "✅ Số lượng âm thanh nhận được: " + sounds.size());
                        for (SoundDto sound : sounds) {
                            Log.d(TAG, "🎵 Sound: ID=" + sound.getId() +
                                    ", Title=" + sound.getTitle() +
                                    ", FileURL=" + sound.getLink_Music());
                        }

                        groupAndDisplaySounds(sounds);
                    } else {
                        Log.e(TAG, "⚠️ Dữ liệu trả về rỗng hoặc không đúng định dạng");
                        Log.d(TAG, "➡️ Body: " + new Gson().toJson(response.body()));
                    }
                } else {
                    Log.e(TAG, "❌ Response không thành công: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "❌ ErrorBody: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Lỗi khi đọc errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<SoundResponse> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi khi gọi API /music: " + t.getMessage(), t);
            }
        });
    }


    // ==================== UI Update ====================
    private void groupAndDisplaySounds(List<SoundDto> sounds) {
        Map<String, List<CustomSound>> groupedMap = new TreeMap<>(); // ✅ Sắp xếp theo A-Z

        for (SoundDto dto : sounds) {
            String soundUrl = dto.getLink_Music();
            String imageUrl = dto.getAvatar();

            if (imageUrl != null && !imageUrl.startsWith("http")) {
                imageUrl = IMAGE_HOST + imageUrl.replaceFirst("^/", "");
            }

            CustomSound sound = new CustomSound(dto.getId(), dto.getTitle(), soundUrl, imageUrl);

            // ✅ Lấy category name, nếu null thì cho vào "Others"
            String group = dto.getCategory() != null ? dto.getCategory().trim() : "Others";

            // ✅ Thêm vào nhóm
            groupedMap.computeIfAbsent(group, k -> new ArrayList<>()).add(sound);
        }

        List<CustomSoundGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<CustomSound>> entry : groupedMap.entrySet()) {
            groups.add(new CustomSoundGroup(entry.getKey(), entry.getValue()));
        }

        requireActivity().runOnUiThread(() -> customAdapter.setData(groups));
    }


    // ==================== MediaPlayer Handling ====================
    private void handleSoundClick(CustomSound sound) {
        long id = sound.getId();
        String url = sound.getFileUrl();
        if (url == null) return;

        if (playingSounds.containsKey(id)) {
            MediaPlayer player = playingSounds.get(id);
            if (player != null) {
                try {
                    if (player.isPlaying()) player.stop();
                } catch (IllegalStateException ignored) {}
                player.release();
            }
            playingSounds.remove(id);
            activeSoundIds.remove(id);
        } else {
            try {
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(url);
                player.setLooping(true);
                float volume = volumeLevels.getOrDefault(id, 1.0f);
                player.setVolume(volume, volume);
                player.prepare();
                player.start();
                playingSounds.put(id, player);
                activeSoundIds.add(id);
            } catch (Exception e) {
                Log.e(TAG, "❌ Lỗi phát âm thanh: " + url, e);
            }
        }

        customAdapter.notifyDataSetChanged();
    }

    private void updateSoundVolume(CustomSound sound, float volume) {
        long id = sound.getId();
        volumeLevels.put(id, volume);
        MediaPlayer player = playingSounds.get(id);
        if (player != null) {
            player.setVolume(volume, volume);
        }
    }

    // ==================== Public Utilities ====================
    public void playSoundByName(String name) {
        // Optional: dùng nếu muốn phát 1 âm thanh cụ thể theo tên
    }
}

