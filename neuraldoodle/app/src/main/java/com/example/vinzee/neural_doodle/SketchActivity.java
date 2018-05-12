package com.example.vinzee.neural_doodle;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class SketchActivity extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue queue;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private String imageURL, style, projectId;
    private int handlerCount = 0;
    private static final int handlerCountThreshold = 6;
    private TextView artistNameText;
    private FloatingActionButton saveSketchButton;
    private StorageReference mStorageRef;
    private NetworkImageView backImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sketch_view);

        Bundle b = getIntent().getExtras();
        imageURL = b.getString("imageURL");
        style = b.getString("style");
        projectId = b.getString("projectId");

        artistNameText = findViewById(R.id.artistName);
        artistNameText.setText("- " + style);

        queue = Volley.newRequestQueue(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        backImgView = findViewById(R.id.backImageView);
        networkImageView = findViewById(R.id.networkImageView);
        networkImageView.setDrawingCacheEnabled(true);
        networkImageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        saveSketchButton = findViewById(R.id.saveSketch);
        saveSketchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String imgURL = MediaStore.Images.Media.insertImage( getContentResolver(), networkImageView.getDrawingCache(), UUID.randomUUID().toString() + ".png", "sketch");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // drawView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG,80,baos);
                networkImageView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 0 ,baos);
                byte[] data= baos.toByteArray();
                StorageReference storageReference = mStorageRef.child("images/"+projectId+"_sketch.png");
                Log.d("Upload path", "images/"+projectId+"_sketch.png");
                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(),"image upload failed ",Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Sketch Upload", "Success!!");
                    }
                });
                if (imgURL != null) {
                    Toast.makeText(getApplicationContext(), "Drawing saved to Gallery.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
                }

                networkImageView.destroyDrawingCache();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handlerCount++ < handlerCountThreshold) {
            handler.postDelayed(runnable, 1000 * 1);
            handler2.postDelayed(runnable2, 1000 * 3);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        handler2.removeCallbacks(runnable2);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            backImgView.setVisibility(View.VISIBLE);
            networkImageView.setVisibility(View.GONE);
            networkImageView.setImageUrl(imageURL + "/?time=" + System.currentTimeMillis(), imageLoader);
            progressBar.setVisibility(View.GONE);

            if (handlerCount++ < handlerCountThreshold) {
                handler.postDelayed(this, 1000*10);
            }
        }
    };

    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            networkImageView.setVisibility(View.VISIBLE);
            backImgView.setVisibility(View.GONE);
            backImgView.setImageUrl(imageURL + "/?time=" + System.currentTimeMillis(), imageLoader);

            if (handlerCount++ < handlerCountThreshold) {
                handler2.postDelayed(this, 1000*10);
            }
        }
    };

    @Override
    public void onClick(View v) {

    }
}
