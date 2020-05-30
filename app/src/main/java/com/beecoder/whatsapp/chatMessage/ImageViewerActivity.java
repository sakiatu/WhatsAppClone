package com.beecoder.whatsapp.chatMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.beecoder.whatsapp.R;
import com.bumptech.glide.Glide;

public class ImageViewerActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        imageView = findViewById(R.id.item_image);

        String url = getIntent().getStringExtra("url");
        Glide.with(this).load(url).placeholder(R.drawable.icon_photo).into(imageView);
    }
}
