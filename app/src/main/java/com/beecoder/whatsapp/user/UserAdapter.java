package com.beecoder.whatsapp.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beecoder.whatsapp.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<Contact> contacts;

    UserAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(int i);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name,number;
        ImageView userImage;
        UserViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            number= itemView.findViewById(R.id.contact_number);
            userImage = itemView.findViewById(R.id.contact_image);
            itemView.setOnClickListener(v->onItemClickListener.onClick(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_view,parent,false);
        return new UserViewHolder(itemView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.name.setText(contacts.get(position).getName());
        holder.number.setText(contacts.get(position).getNumber());
        int contactImage= contacts.get(position).getImage();
        if(contactImage!=0){
            //set image resource
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
