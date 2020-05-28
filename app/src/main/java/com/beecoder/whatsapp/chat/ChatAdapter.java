package com.beecoder.whatsapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beecoder.whatsapp.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<Chat> chats;

    public ChatAdapter(ArrayList<Chat> chats) {
        this.chats = chats;
    }
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(int i);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView name,lastMsg,date;
        ImageView chatIcon;
        ChatViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_name);
            lastMsg= itemView.findViewById(R.id.chat_lastMsg);
            date= itemView.findViewById(R.id.chat_lastDate);
            chatIcon = itemView.findViewById(R.id.chat_icon);
            itemView.setOnClickListener(v->onItemClickListener.onClick(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_view,parent,false);
        return new ChatViewHolder(itemView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.name.setText(chats.get(position).getChatName());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
