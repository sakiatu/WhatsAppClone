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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messages;
    private Context context;

    public MessageAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int i);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;
        LinearLayout messageLayout;

        MessageViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            text = itemView.findViewById(R.id.textMessage);
            image = itemView.findViewById(R.id.image_message);
            messageLayout = itemView.findViewById(R.id.textLayout);

            itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_view, parent, false);
        return new MessageViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String textMsg = messages.get(position).getText();
        if (textMsg.length() > 0) {
            holder.text.setText(textMsg);
        } else {
            holder.text.setVisibility(View.GONE);
        }
        if (messages.get(position).getImageUrl() != null) {
            Glide
                    .with(context)
                    .load(messages.get(position).getImageUrl())
                    .placeholder(R.drawable.icon_photo)
                    .centerCrop().into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }
        if (messages.get(position).getCreator().equals(FirebaseAuth.getInstance().getUid())) {
            holder.messageLayout.setGravity(Gravity.RIGHT);
            holder.text.setBackgroundColor(Color.CYAN);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
