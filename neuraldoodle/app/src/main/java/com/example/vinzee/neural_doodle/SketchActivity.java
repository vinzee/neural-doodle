package com.example.vinzee.neural_doodle;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class SketchActivity extends AppCompatActivity {
    private RequestQueue queue;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch_view);

        Bundle b = getIntent().getExtras();
        String imageURL = b.getString("imageURL");


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
        networkImageView.setImageUrl(imageURL, imageLoader);
        // "http://43a87bf7.ngrok.io/static/results/out.png"
    }
}