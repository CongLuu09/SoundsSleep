package com.kenhtao.site.soundssleep.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kenhtao.site.soundssleep.R;
import com.kenhtao.site.soundssleep.data.AppDatabase;
import com.kenhtao.site.soundssleep.models.LayerSound;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayLayerAdapter extends RecyclerView.Adapter<PlayLayerAdapter.ViewHolder> {
    private final List<LayerSound> layers;
    private final Context context;
    private final Map<Long, MediaPlayer> playerMap = new HashMap<>();

    public PlayLayerAdapter(Context context, List<LayerSound> layers) {
        this.context = context;
        this.layers = layers;
    }

    public void playAllPlayers() {
        for (LayerSound sound : layers) {
            long id = sound.getId();
            MediaPlayer player = playerMap.get(id);
            if (player == null) {
                try {
                    player = new MediaPlayer();
                    player.setDataSource(sound.getFileUrl());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setLooping(true);
                    player.setVolume(sound.getVolume(), sound.getVolume());
                    player.prepare();
                    player.start();
                    playerMap.put(id, player);
                    sound.setMediaPlayer(player);
                } catch (IOException e) {
                    Log.e("PlayLayerAdapter", "❌ playAllPlayers error", e);
                }
            } else {
                try {
                    if (!player.isPlaying()) player.start();
                } catch (IllegalStateException e) {
                    Log.e("PlayLayerAdapter", "❌ player start failed", e);
                }
            }
        }
    }

    public void pauseAllPlayers() {
        for (LayerSound sound : layers) {
            MediaPlayer player = sound.getMediaPlayer();
            if (player != null && player.isPlaying()) {
                try {
                    player.pause();
                } catch (IllegalStateException e) {
                    Log.e("PlayLayerAdapter", "❌ pause failed", e);
                }
            }
        }
    }

    public interface OnSoundDeletedListener {
        void onSoundDeleted();
    }

    private OnSoundDeletedListener onSoundDeletedListener;

    public void setOnSoundDeletedListener(OnSoundDeletedListener listener) {
        this.onSoundDeletedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layer_sound, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LayerSound layer = layers.get(position);

        holder.tvLayerName.setText(layer.getName());

        String imageUrl = layer.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = "https://sleepchills.kenhtao.site/storage/" + imageUrl.replaceFirst("^/+", "");
            }
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.airplane_flying)
                    .error(R.drawable.bird_chirping)
                    .into(holder.imgLayerIcon);
        } else {
            if (layer.getIconResId() != 0) {
                holder.imgLayerIcon.setImageResource(layer.getIconResId());
            } else {
                holder.imgLayerIcon.setImageDrawable(null);
            }
        }

        holder.seekBarVolume.setProgress((int) (layer.getVolume() * 100));
        holder.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                layer.setVolume(volume);
                MediaPlayer current = layer.getMediaPlayer();
                if (current != null) current.setVolume(volume, volume);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        MediaPlayer player = layer.getMediaPlayer();
        if (player == null) {
            try {
                if (layer.getFileUrl() != null && !layer.getFileUrl().isEmpty()) {
                    player = new MediaPlayer();
                    player.setDataSource(layer.getFileUrl());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setLooping(true);
                    player.setOnPreparedListener(mp -> {
                        mp.setVolume(layer.getVolume(), layer.getVolume());
                        mp.start();
                    });
                    player.prepareAsync();
                    playerMap.put(layer.getId(), player);
                } else if (layer.getSoundResId() != 0) {
                    player = MediaPlayer.create(context, layer.getSoundResId());
                    player.setLooping(true);
                    player.setVolume(layer.getVolume(), layer.getVolume());
                    player.start();
                    playerMap.put(layer.getId(), player);
                }
                layer.setMediaPlayer(player);
            } catch (IOException e) {
                Log.e("PlayLayerAdapter", "❌ onBindViewHolder MediaPlayer error", e);
            }
        }

        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Đổi tên âm thanh");

            final EditText input = new EditText(context);
            input.setText(layer.getName());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Lưu", (dialog, which) -> {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    AppDatabase db = new AppDatabase(context);
                    db.updateSoundName(layer.getName(), newName, "");
                    db.close();
                    layer.setName(newName);
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });

            builder.setNegativeButton("Huỷ", null);
            builder.show();
        });

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            new AlertDialog.Builder(context)
                    .setTitle("Xoá âm thanh")
                    .setMessage("Bạn có chắc muốn xoá \"" + layer.getName() + "\" không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        AppDatabase db = new AppDatabase(context);
                        db.deleteSoundByName(layer.getName(), "");
                        db.close();

                        releaseLayer(layer);
                        layers.remove(pos);
                        notifyItemRemoved(pos);

                        if (onSoundDeletedListener != null) {
                            onSoundDeletedListener.onSoundDeleted();
                        }

                        Toast.makeText(context, "🗑️ Đã xoá âm thanh", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return layers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLayerIcon, btnEdit, btnDelete;
        TextView tvLayerName;
        SeekBar seekBarVolume;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLayerIcon = itemView.findViewById(R.id.imgLayerIcon);
            tvLayerName = itemView.findViewById(R.id.tvLayerName);
            seekBarVolume = itemView.findViewById(R.id.seekBarVolume);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void addLayer(LayerSound layer, MediaPlayer player) {
        String newName = layer.getName().trim().toLowerCase();
        for (LayerSound l : layers) {
            if (l.getName().trim().toLowerCase().equals(newName)) {
                Toast.makeText(context, "Đã tồn tại âm thanh \"" + layer.getName() + "\"", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        layers.add(layer);
        notifyItemInserted(layers.size() - 1);

        if (player != null) {
            player.setLooping(true);
            player.setVolume(layer.getVolume(), layer.getVolume());
            player.start();
            layer.setMediaPlayer(player);
            playerMap.put(layer.getId(), player);
        }

        AppDatabase db = new AppDatabase(context);
        if (!db.isSoundExists(layer.getName(), "")) {
            db.insertSound(layer.getName(), layer.getIconResId(), layer.getSoundResId(), "");
        }
    }

    public void removeLayer(String soundName) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getName().equals(soundName)) {
                releaseLayer(layers.get(i));
                layers.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void removeLayer(LayerSound sound) {
        removeLayer(sound.getName());
    }

    public void releaseAllPlayers() {
        for (LayerSound layer : layers) {
            releaseLayer(layer);
        }
    }

    private void releaseLayer(LayerSound layer) {
        MediaPlayer player = layer.getMediaPlayer();
        if (player != null) {
            try {
                if (player.isPlaying()) player.stop();
            } catch (IllegalStateException e) {
                Log.e("PlayLayerAdapter", "❌ stop before release failed", e);
            }
            try {
                player.release();
            } catch (Exception e) {
                Log.e("PlayLayerAdapter", "❌ release failed", e);
            }
            playerMap.remove(layer.getId());
            layer.setMediaPlayer(null);
        }
    }
}
