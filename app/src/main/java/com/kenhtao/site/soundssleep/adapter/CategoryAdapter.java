package com.kenhtao.site.soundssleep.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kenhtao.site.soundssleep.R;
import com.kenhtao.site.soundssleep.models.Category;
import com.kenhtao.site.soundssleep.ui.player.CategoryPlayerActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvSoundTitle.setText(category.getTitle());

        String rawUrl = category.getAvatar();
        Log.d("CategoryAdapter", "Raw avatar: " + rawUrl);


        String imageUrl;

        if (rawUrl != null && rawUrl.startsWith("http")) {
            imageUrl = rawUrl;
        } else {

            if (rawUrl != null && !rawUrl.startsWith("storage/") && !rawUrl.startsWith("/storage/")) {
                rawUrl = "storage/" + rawUrl.replaceFirst("^/+", "");
            }
            imageUrl = "https://sleepchills.kenhtao.site/" + rawUrl;
        }

        Log.d("CategoryAdapter", "Loading image from: " + imageUrl);

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.sound_airplane)
                .error(R.drawable.birds_singing)
                .into(holder.imgSound);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            } else {
                Intent intent = new Intent(context, CategoryPlayerActivity.class);
                intent.putExtra("categoryId", category.getId());
                intent.putExtra("categoryTitle", category.getTitle());
                intent.putExtra("avatar", category.getAvatar());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSound;
        TextView tvSoundTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSound = itemView.findViewById(R.id.imgSound);
            tvSoundTitle = itemView.findViewById(R.id.tvSoundTitle);
        }
    }


    private String buildFullImageUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isEmpty()) return null;

        if (rawUrl.startsWith("http")) return rawUrl;

        rawUrl = rawUrl.replaceFirst("^/+", "");
        if (!rawUrl.startsWith("storage/")) {
            rawUrl = "storage/" + rawUrl;
        }


        return "https://sleepchills.kenhtao.site/" + rawUrl;
    }

}

