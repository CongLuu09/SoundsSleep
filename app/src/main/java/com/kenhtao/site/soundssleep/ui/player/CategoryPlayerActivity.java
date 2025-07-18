package com.kenhtao.site.soundssleep.ui.player;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kenhtao.site.soundssleep.R;
import com.kenhtao.site.soundssleep.Timer.TimerCallBack;
import com.kenhtao.site.soundssleep.Timer.TimerDialog;
import com.kenhtao.site.soundssleep.Timer.TimerViewModel;
import com.kenhtao.site.soundssleep.adapter.PlayLayerAdapter;
import com.kenhtao.site.soundssleep.models.LayerSound;
import com.kenhtao.site.soundssleep.models.SoundDto;
import com.kenhtao.site.soundssleep.service.ApiResponse;
import com.kenhtao.site.soundssleep.service.PagedResponse;
import com.kenhtao.site.soundssleep.service.RetrofitClient;
import com.kenhtao.site.soundssleep.ui.custom.CustomSoundPickerActivity;
import com.kenhtao.site.soundssleep.utils.MixLocalManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryPlayerActivity extends AppCompatActivity {
    private ImageView btnBack, btnPlayPause, btnAddLayer, imgBackground;
    private TextView tvTitle, tvTimer;
    private RecyclerView recyclerViewLayers;
    private boolean isPlaying = false;
    private MediaPlayer mainPlayer;
    private final List<LayerSound> layers = new ArrayList<>();
    private PlayLayerAdapter layerAdapter;
    private TimerViewModel timerViewModel;
    private long categoryId;
    private String categoryTitle;
    private String categoryImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_player);

        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        initViews();
        getIntentData();
        setupBackgroundImage();

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        setupTimerObserver();

        setupListeners();
        setupLayerSounds();
        loadMixWithFallback();

    }
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnAddLayer = findViewById(R.id.btnAddLayer);
        tvTitle = findViewById(R.id.tvTitle);
        tvTimer = findViewById(R.id.tvTimer);
        recyclerViewLayers = findViewById(R.id.recyclerViewLayers);
        imgBackground = findViewById(R.id.imgBackground);
    }

    private void getIntentData() {
        categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
        categoryTitle = getIntent().getStringExtra("CATEGORY_TITLE");
        categoryImageUrl = getIntent().getStringExtra("avatar");
        tvTitle.setText(categoryTitle);
    }

    private void setupBackgroundImage() {
        if (categoryImageUrl != null && !categoryImageUrl.isEmpty()) {
            if (!categoryImageUrl.startsWith("http")) {
                categoryImageUrl = "https://sleepchills.kenhtao.site/storage/" + categoryImageUrl.replaceFirst("^/+", "");
            }
            Glide.with(this).load(categoryImageUrl)
                    .placeholder(R.drawable.backocean)
                    .error(R.drawable.backocean)
                    .into(imgBackground);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) pauseMainSound(); else playMainSound();
        });
        btnAddLayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomSoundPickerActivity.class);
            customSoundLauncher.launch(intent);
        });
        tvTimer.setOnClickListener(v -> {
            TimerDialog dialog = TimerDialog.newInstance();
            dialog.setCallback(new TimerCallBack() {
                public void onTimerSet(long durationMillis) { timerViewModel.startTimer(durationMillis); }
                public void onTimerCancelled() { timerViewModel.cancelTimer(); }
                public void onTimerFinished() {
                    pauseMainSound();
                    if (layerAdapter != null) layerAdapter.releaseAllPlayers();
                }
            });
            dialog.show(getSupportFragmentManager(), "TimerDialog");
        });
    }

    private void setupTimerObserver() {
        timerViewModel.getTimeLeftMillis().observe(this, timeLeft -> {
            if (timeLeft > 0) {
                long minutes = (timeLeft / 1000) / 60;
                long seconds = (timeLeft / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            } else {
                tvTimer.setText("Timer");

                // ✅ Khi hết giờ, dừng tất cả nhạc
                pauseMainSound(); // Dừng mainPlayer và releaseAllPlayers
//                Toast.makeText(this, "🕒 Timer đã kết thúc - Đã dừng tất cả âm thanh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLayerSounds() {
        layerAdapter = new PlayLayerAdapter(this, layers);
        recyclerViewLayers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLayers.setAdapter(layerAdapter);

        layerAdapter.setOnSoundDeletedListener(() -> {
            saveMixToLocal();
            Toast.makeText(this, "✅ Đã lưu mix sau khi xoá", Toast.LENGTH_SHORT).show();
        });

    }

    private void saveMixToLocal() {
        MixLocalManager.saveMixFull(this, categoryId, layers);
        Log.d("SAVE_MIX", "✅ Auto saved mix to local");
    }

    private void loadMixWithFallback() {
        List<LayerSound> savedMix = MixLocalManager.loadMixFull(this, categoryId);
        if (savedMix != null && !savedMix.isEmpty()) {
            Log.d("LOAD_MIX", "✅ Load mix từ local");
            layers.clear();
            layers.addAll(savedMix);
            layerAdapter.notifyDataSetChanged();
        } else {
            Log.d("LOAD_MIX", "📡 Không có mix local, gọi API random");
            loadAndRandomMixFromApi();
        }
    }

    private void loadAndRandomMixFromApi() {
        RetrofitClient.getApiService()
                .getAllMusics()
                .enqueue(new Callback<ApiResponse<PagedResponse<SoundDto>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PagedResponse<SoundDto>>> call,
                                           Response<ApiResponse<PagedResponse<SoundDto>>> response) {
                        ApiResponse<PagedResponse<SoundDto>> apiResponse = response.body();

                        if (response.isSuccessful() && apiResponse != null && apiResponse.isStatus()) {
                            List<SoundDto> soundList = apiResponse.getData().getData();

                            if (soundList == null || soundList.isEmpty()) {
                                Toast.makeText(CategoryPlayerActivity.this, "Danh sách âm thanh rỗng", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 🔀 Trộn ngẫu nhiên danh sách
                            Collections.shuffle(soundList);

                            // 🧹 Xoá dữ liệu cũ và thêm sound mới
                            layers.clear();
                            for (int i = 0; i < Math.min(4, soundList.size()); i++) {
                                SoundDto sound = soundList.get(i);
                                float volume = parseVolume(sound.getVolume());

                                layers.add(new LayerSound(
                                        sound.getId(),
                                        sound.getTitle(),
                                        sound.getLink_Music(),
                                        sound.getAvatar(),
                                        volume
                                ));
                            }

                            // 💾 Lưu lại mix sound cho category hiện tại
                            MixLocalManager.saveMixFull(CategoryPlayerActivity.this, categoryId, layers);

                            // 🔄 Cập nhật giao diện
                            layerAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(CategoryPlayerActivity.this, "❌ Không có dữ liệu từ API", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PagedResponse<SoundDto>>> call, Throwable t) {
                        Toast.makeText(CategoryPlayerActivity.this, "❌ Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private float parseVolume(String volumeStr) {
        try {
            return Float.parseFloat(volumeStr);
        } catch (Exception e) {
            return 0.5f;
        }
    }

    private void playMainSound() {
        if (mainPlayer == null && !layers.isEmpty()) {
            String url = layers.get(0).getFileUrl();
            mainPlayer = new MediaPlayer();
            try {
                mainPlayer.setDataSource(url);
                mainPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mainPlayer.setLooping(true);
                mainPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    isPlaying = true;
                    btnPlayPause.setImageResource(R.drawable.stop);
                });
                mainPlayer.setOnErrorListener((mp, what, extra) -> {
                    Toast.makeText(this, "❌ Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
                    return true;
                });
                mainPlayer.prepareAsync();
            } catch (IOException e) {
                Log.e("CategoryPlayer", "❌ MediaPlayer error", e);
            }
        } else if (mainPlayer != null) {
            mainPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.stop);
        }
    }

    private void pauseMainSound() {
        // Dừng mainPlayer
        if (mainPlayer != null) {
            if (mainPlayer.isPlaying()) mainPlayer.pause();
            mainPlayer.stop();
            mainPlayer.release();
            mainPlayer = null;
        }

        // Dừng toàn bộ layer player (MediaPlayer trong adapter)
        if (layerAdapter != null) {
            layerAdapter.releaseAllPlayers(); // 👈 đảm bảo tất cả player trong layer được dừng
        }

        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.play); // đổi icon lại
    }


    private final ActivityResultLauncher<Intent> customSoundLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    long soundId = data.getLongExtra("soundId", -1);
                    String name = data.getStringExtra("name");
                    String fileUrl = data.getStringExtra("fileUrl");
                    String imageUrl = data.getStringExtra("imageUrl");
                    if (soundId != -1 && fileUrl != null) {
                        LayerSound newLayer = new LayerSound(soundId, name, fileUrl, imageUrl, 0.5f);
                        layers.add(newLayer);
                        layerAdapter.notifyItemInserted(layers.size() - 1);


                        saveMixToLocal();
                    } else {
                        Toast.makeText(this, "❌ Âm thanh không hợp lệ.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainPlayer != null) {
            mainPlayer.stop();
            mainPlayer.release();
        }
        if (layerAdapter != null) layerAdapter.releaseAllPlayers();
    }
}