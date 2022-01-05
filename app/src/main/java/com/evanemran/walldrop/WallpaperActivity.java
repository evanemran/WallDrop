package com.evanemran.walldrop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.evanemran.walldrop.Models.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;

public class WallpaperActivity extends AppCompatActivity {

    Photo photo;
    ImageView imageView_wallpaper;
    Button button_download, button_wallpaper;
    RelativeLayout relative_wallpaper;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Setting as Wallpaper...");

        photo = (Photo) getIntent().getSerializableExtra("photo");

        imageView_wallpaper = findViewById(R.id.imageView_wallpaper);
        button_download = findViewById(R.id.button_download);
        button_wallpaper = findViewById(R.id.button_wallpaper);
        relative_wallpaper = findViewById(R.id.relative_wallpaper);

        relative_wallpaper.setBackgroundColor(Color.parseColor(photo.avg_color));

        Picasso.get().load(photo.getSrc().getOriginal()).into(imageView_wallpaper);

        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager downloadManager = null;
                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                Uri uri = Uri.parse(photo.getSrc().getLarge());

                DownloadManager.Request request = new DownloadManager.Request(uri);

                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("test_download")
                        .setMimeType("image/jpeg")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+"test_doggo"+".jpg");

                downloadManager.enqueue(request);

                Toast.makeText(WallpaperActivity.this, "Download Complete!", Toast.LENGTH_SHORT).show();
            }
        });

        button_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView_wallpaper.getDrawable() == null){
                    Toast.makeText(WallpaperActivity.this, "Please wait! Image is loading.", Toast.LENGTH_SHORT).show();
                    return;
                }
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperActivity.this);
                Bitmap bitmap = ((BitmapDrawable)imageView_wallpaper.getDrawable()).getBitmap();
                try {
                    dialog.show();
                    wallpaperManager.setBitmap(bitmap);
                    dialog.dismiss();
                    Toast.makeText(WallpaperActivity.this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(WallpaperActivity.this, "Couldn't set wallpaper!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}