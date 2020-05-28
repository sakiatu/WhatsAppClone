package com.beecoder.whatsapp.chatMessage;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beecoder.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(int i);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        LinearLayout textLayout;
        MessageViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            text = itemView.findViewById(R.id.textMessage);
            textLayout= itemView.findViewById(R.id.textLayout);

            itemView.setOnClickListener(v->onItemClickListener.onClick(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_view,parent,false);
        return new MessageViewHolder(itemView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.text.setText(messages.get(position).getMessage());
        if(messages.get(position).getSender().equals(FirebaseAuth.getInstance().getUid())){
            holder.textLayout.setGravity(Gravity.RIGHT);
            holder.text.setBackgroundColor(Color.CYAN);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
