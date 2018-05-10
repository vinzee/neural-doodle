package com.example.vinzee.neural_doodle;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class CanvasActivity extends AppCompatActivity implements View.OnClickListener {
    //custom drawing view
    private DrawingView drawView;
    //buttons
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, opacityBtn, magicBtn;
    //sizes
    private float smallBrush, mediumBrush, largeBrush;
    private RequestQueue queue;
    private final String BASE_URL = "http://43a87bf7.ngrok.io";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        queue = Volley.newRequestQueue(this);

        //get drawing view
        drawView = findViewById(R.id.drawing);

        //get the palette and first color button
        LinearLayout paintLayout = findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        //sizes from dimensions
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //draw button
        drawBtn = findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        //set initial size
        drawView.setBrushSize(mediumBrush);
        drawView.setDrawingCacheEnabled(true);
        drawView.setDrawingCacheBackgroundColor(0xfffafafa);
        drawView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        //erase button
        eraseBtn = findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        //new button
        newBtn = findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        //save button
        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        //opacity
        opacityBtn = findViewById(R.id.opacity_btn);
        opacityBtn.setOnClickListener(this);

        //opacity
        magicBtn = findViewById(R.id.magic_btn);
        magicBtn.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "already Not granted");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET}, 0);

                Log.d("Permission", "Requesting !");
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "already Not granted");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                Log.d("Permission", "Requesting !");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //R.id.action_settings
        switch (item.getItemId()) {
            case R.id.settings:Intent intent = new Intent(this, UpdateUserProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                auth.signOut();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
        }
        return false;

    }

    //user clicked paint
    public void paintClicked(View view){
        //use chosen color

        //set erase false
        drawView.setErase(false);
        drawView.setPaintAlpha(100);
        drawView.setBrushSize(drawView.getLastBrushSize());

        if(view != currPaint){
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            // update ui
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

    @Override
    public void onClick(View view){
        final Dialog brushDialog;
        ImageButton smallBtn, mediumBtn, largeBtn;

        switch(view.getId()) {
            case R.id.draw_btn:
                //draw button clicked
                brushDialog = new Dialog(this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                //listen for clicks on size buttons
                smallBtn = brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(false);
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                mediumBtn = brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(false);
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                largeBtn = brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(false);
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                //show and wait for user interaction
                brushDialog.show();
                break;

            case R.id.erase_btn:
                //switch to erase - choose size
                brushDialog = new Dialog(this);
                brushDialog.setTitle("Eraser size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                //size buttons
                smallBtn = brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                mediumBtn = brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                largeBtn = brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
                break;

            case R.id.new_btn:
                //new button
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;

            case R.id.opacity_btn:
                //launch opacity chooser
                final Dialog seekDialog = new Dialog(this);
                seekDialog.setTitle("Opacity level:");
                seekDialog.setContentView(R.layout.opacity_chooser);
                //get ui elements
                final TextView seekTxt = seekDialog.findViewById(R.id.opq_txt);
                final SeekBar seekOpq = seekDialog.findViewById(R.id.opacity_seek);
                //set max
                seekOpq.setMax(100);
                //show current level
                int currLevel = drawView.getPaintAlpha();
                seekTxt.setText(currLevel + "%");
                seekOpq.setProgress(currLevel);
                //update as user interacts
                seekOpq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekTxt.setText(Integer.toString(progress) + "%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });
                //listen for clicks on ok
                Button opqBtn = seekDialog.findViewById(R.id.opq_ok);
                opqBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setPaintAlpha(seekOpq.getProgress());
                        seekDialog.dismiss();
                    }
                });
                //show dialog
                seekDialog.show();
                break;

            case R.id.save_btn:
                //save drawing
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Save drawing");
                saveDialog.setMessage("Save drawing to device Gallery?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //save drawing
//                        drawView.setDrawingCacheEnabled(true);
//                        drawView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                        //attempt to save
                        String imgURL = MediaStore.Images.Media.insertImage(
                                getContentResolver(), drawView.getDrawingCache(),
                                UUID.randomUUID().toString() + ".png", "drawing");
                        // feedback
                        if (imgURL != null) {
                            Toast.makeText(getApplicationContext(),
                                    "Drawing saved to Gallery: " + imgURL, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
                        }
                        drawView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                break;

            case R.id.magic_btn:
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, BASE_URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        String imageURL = BASE_URL + "/" + resultResponse;

                        Log.i("resultResponse: ", imageURL);

                        Intent myIntent = new Intent(CanvasActivity.this, SketchActivity.class);
                        myIntent.putExtra("imageURL", imageURL); //Optional parameters
                        CanvasActivity.this.startActivity(myIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String status = response.getString("status");
                                String message = response.getString("message");

                                Log.e("Error Status", status);
                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message+" Please login again";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message+ " Check your inputs";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message+" Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                    }
                }) {
//                    @Override
//                    protected Map<String, String> getParams() {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("contact", mContactInput.getText().toString());
//                        return params;
//                    }

                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        // file name could found file base or direct access from real path
                        // for now just get bitmap data from ImageView

                        params.put("file", new DataPart("test.png", getFileDataFromDrawable(drawView.getDrawingCache()), "image/png"));

                        return params;
                    }
                };

                multipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                multipartRequest.setShouldCache(false);

                queue.add(multipartRequest);

                break;
        }
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Permission", "Granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Permission", "Not granted");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}