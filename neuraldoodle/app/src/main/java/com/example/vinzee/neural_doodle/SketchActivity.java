package com.example.vinzee.neural_doodle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;


public class SketchActivity extends AppCompatActivity {
    private RequestQueue queue;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private String imageURL, style, projectId, projectName, projectPath;
    private int handlerCount = 0;
    private static final int handlerCountThreshold = 8;
    private TextView artistNameText;
    private ImageButton saveSketchButton, contactArtistButton;
    private FirebaseAuth auth;
    private StorageReference mStorageRef;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private NetworkImageView backImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_sketch);

        Bundle b = getIntent().getExtras();
        imageURL = b.getString("imageURL");
        style = b.getString("style");
        projectId = b.getString("projectId");
        projectName = b.getString("projectName");
        projectPath = "images/"+projectId+"_sketch.png";

        artistNameText = findViewById(R.id.artistName);
        artistNameText.setText(projectName + " - " + style);

        queue = Volley.newRequestQueue(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("projects");

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

        contactArtistButton = findViewById(R.id.contactArtist);
        saveSketchButton = findViewById(R.id.saveSketch);
        saveSketchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String imgURL = MediaStore.Images.Media.insertImage( getContentResolver(), networkImageView.getDrawingCache(), projectName + "-" + style + UUID.randomUUID().toString() + ".png", "sketch");

                saveToFireBase();

                if (imgURL != null) {
                    Toast.makeText(getApplicationContext(), "Drawing saved to Gallery.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
                }

                networkImageView.destroyDrawingCache();
            }
        });
    }

    public void saveToFireBase() {
        new FetchDoodle().execute();
    }

    class FetchDoodle extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... uris) {
            String urlString = imageURL + "/?time=" + System.currentTimeMillis();
            URL url = null;
            Bitmap bitmap = null;
            try {
                url = new URL(urlString);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // drawView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG,80,baos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data= baos.toByteArray();

            StorageReference storageReference = mStorageRef.child(projectPath);
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
                    Log.d("Save Status", "Successful");
                    String userId = auth.getCurrentUser().getUid();
                    mFirebaseDatabase.child(userId).child(projectId).child("sketchExists").setValue(true);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handlerCount < handlerCountThreshold) {
            handler.postDelayed(runnable, 1000 * 1);
            handler2.postDelayed(runnable2, 1000 * 5);
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

            Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Voila, your Sketch is Ready !", Snackbar.LENGTH_SHORT).show();

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
            saveToFireBase();

            if (handlerCount++ < handlerCountThreshold) {
                handler2.postDelayed(this, 1000*10);
            }
        }
    };
}
