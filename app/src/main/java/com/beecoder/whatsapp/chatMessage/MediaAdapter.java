package com.beecoder.whatsapp.chatMessage;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beecoder.whatsapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private ArrayList<Media> medias;
    private Context context;

    public MediaAdapter(Context context, ArrayList<Media> medias) {
        this.medias = medias;
        this.context = context;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int i);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView media;

        MediaViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            media = itemView.findViewById(R.id.media);

            itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item_view, parent, false);
        return new MediaViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Glide.with(context).load(medias.get(position).getUri()).centerCrop().into(holder.media);
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }
}
