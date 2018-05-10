package com.example.vinzee.neural_doodle;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class SketchActivity extends AppCompatActivity {
    private RequestQueue queue;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private String imageURL;
    private int handlerCount = 0;
    private static final int handlerCountThreshold = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch_view);

        Bundle b = getIntent().getExtras();
        imageURL = b.getString("imageURL");

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000*5);
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
}
