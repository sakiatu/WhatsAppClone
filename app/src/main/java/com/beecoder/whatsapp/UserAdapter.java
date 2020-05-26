package com.beecoder.whatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<Contact> contacts;

    public UserAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name,number;
        ImageView userImage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            number= itemView.findViewById(R.id.contact_number);
            userImage = itemView.findViewById(R.id.contact_image);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_view,parent,false);
        return new UserViewHolder(itemView);
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
