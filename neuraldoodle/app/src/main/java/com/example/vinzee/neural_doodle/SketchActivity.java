package com.example.vinzee.neural_doodle;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

public class SketchActivity extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue queue;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private String imageURL, style, projectId;
    private int handlerCount = 0;
    private static final int handlerCountThreshold = 15;
    private TextView artistNameText;
    private Button saveSketchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch_view);

        Bundle b = getIntent().getExtras();
        imageURL = b.getString("imageURL");
        style = b.getString("style");
        projectId = b.getString("projectId");

        artistNameText = findViewById(R.id.artistName);
        artistNameText.setText(style);

        queue = Volley.newRequestQueue(this);

        imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        networkImageView = findViewById(R.id.networkImageView);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        saveSketchButton = findViewById(R.id.saveSketch);
        saveSketchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String imgURL = MediaStore.Images.Media.insertImage( getContentResolver(), networkImageView.getDrawingCache(), UUID.randomUUID().toString() + ".png", "drawing");

                if (imgURL != null) {
                    Toast.makeText(getApplicationContext(), "Drawing saved to Gallery: " + imgURL, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000*20);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            networkImageView.setImageUrl(imageURL + "/?time=" + System.currentTimeMillis(), imageLoader);
            progressBar.setVisibility(View.GONE);

            if (handlerCount++ < handlerCountThreshold) {
                handler.postDelayed(this, 1000*5);
            }
        }
    };

    @Override
    public void onClick(View v) {

    }
}
