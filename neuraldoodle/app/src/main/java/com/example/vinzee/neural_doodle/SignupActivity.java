package com.example.vinzee.neural_doodle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private static final String TAG = SignupActivity.class.getSimpleName();
    private EditText inputEmail, inputPassword, inputCnfmPassword,edtName,edtAddress,edtPhone,edtEmail,edtBio;
    private Button btnSignIn, btnSignUp, btnResetPassword, btnSelectInterest , btnSelectArt;
    private TextView tvInterests,tvArtistArt;
    private LinearLayout artistExt;
    private String uid;

    private ProgressBar progressBar;
    private Spinner profileSpinner;
    private FirebaseAuth auth;
    private StorageReference mStorageRef;

    User user=new User();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2, PICK_ARTIST_IMAGE=3;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private Bitmap profileBitmap,artBitmap;
    private ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        artistExt=(LinearLayout) findViewById(R.id.artistExtras);
        profileImg = (ImageView) findViewById(R.id.profile_image);
        profileImg.setOnClickListener(this);
        artistExt.setVisibility(View.GONE);

        profileSpinner = (Spinner) findViewById(R.id.profile_type_spinner);
        profileSpinner.setOnItemSelectedListener(this);
        profileSpinner.setSelection(0);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.profile_type_array,android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileSpinner.setAdapter(typeAdapter);
        edtName = (EditText)findViewById(R.id.name);
        edtAddress = (EditText)findViewById(R.id.address);
        edtPhone = (EditText) findViewById(R.id.phone);
        edtBio = (EditText) findViewById(R.id.bio);
        tvInterests = (TextView) findViewById(R.id.userInterest);
        tvArtistArt = (TextView) findViewById(R.id.artistArt);

        btnSelectArt = (Button) findViewById(R.id.btn_select_art);
        btnSelectArt.setOnClickListener(this);
        btnSelectInterest=(Button)findViewById(R.id.selectInterest);
        btnSelectInterest.setOnClickListener(this);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(this);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnSignUp.setOnClickListener(this);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputCnfmPassword = (EditText) findViewById(R.id.confirmpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        btnResetPassword.setOnClickListener(this);

        edtPhone.addTextChangedListener(new TextWatcher() {

            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length_before < s.length()) {
                    if (s.length() == 3 || s.length() == 7)
                        s.append("-");
                    if (s.length() > 3) {
                        if (Character.isDigit(s.charAt(3)))
                            s.insert(3, "-");
                    }
                    if (s.length() > 7) {
                        if (Character.isDigit(s.charAt(7)))
                            s.insert(7, "-");
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())  {
            case R.id.profile_image:
                loadImageFromStorage();
                break;
            case R.id.sign_up_button:
                if (inputPassword.getText().toString().equals(inputCnfmPassword.getText().toString())) {
                    attemptSignup();
                }
                else    {
                    Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sign_in_button:
                finish();
                break;
            case R.id.btn_reset_password:
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.selectInterest:getInterests();
                break;
            case R.id.btn_select_art:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_ARTIST_IMAGE);
                break;
        }
    }

    public void getInterests()    {

        final StringBuilder interests=new StringBuilder();
        final CharSequence[] dialogList = {"Abstract", "Cotemporary","Photorealism"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ArrayList<Integer> selectedItems = new ArrayList<Integer>();
        // set the dialog title
        boolean[] itemChecked = new boolean[selectedItems.size()];

        builder.setMultiChoiceItems(dialogList,null, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked) {
                    // if the user checked the item, add it to the selected items
                    selectedItems.add(which);
                }

                else if (selectedItems.contains(which)) {
                    // else if the item is already in the array, remove it
                    selectedItems.remove(Integer.valueOf(which));
                }

            }

        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String selectedIndex = "";
                        for(Integer i : selectedItems){
                            selectedIndex += i + ", ";
                            interests.append(dialogList[i]+"; ");
                            tvInterests.setText(interests.toString());
                        }
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                })
                .show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        user.userType = (parent.getItemAtPosition(position).toString());

        if(user.userType.equals("Patreon"))   {
            //show elements of patreon
            artistExt.setVisibility(View.GONE);
        }
        else if(user.userType.equals("Artist"))  {
            // show elements of artist
            artistExt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void loadImageFromStorage()  {

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case PICK_IMAGE_CAMERA:
                if(resultCode == RESULT_OK){
                    try {
                        //final Uri imageUri = imageReturnedIntent.getData();
                        final Bitmap selectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        profileBitmap=selectedImage;
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                        profileImg.setImageBitmap(selectedImage);
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_IMAGE_GALLERY:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profileBitmap=selectedImage;
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                        profileImg.setImageBitmap(selectedImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_ARTIST_IMAGE:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        artBitmap=selectedImage;
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                        //keep the image ready here to upload to db
                        tvArtistArt.setText(imageUri.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }



    private void uploadImage(Bitmap profileBitmap, final String uid, String type)   {
        progressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileBitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] data= baos.toByteArray();

        StorageReference storageReference = mStorageRef.child("images/"+uid+"_"+type+".jpg");

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

            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void attemptSignup()    {
        progressBar.setVisibility(View.VISIBLE);
        final String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "Sign up successful" , Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            createProfile();
                        }
                    }
                });
        progressBar.setVisibility(View.GONE);
    }

    private void createProfile()    {

        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();
        String userType = profileSpinner.getSelectedItem().toString();
        String userBio = edtBio.getText().toString();
        String userInterests = tvInterests.getText().toString();
        createUser(name,email,phone,address,userType,userBio,userInterests);

        //direct user to home screen
        startActivity(new Intent(SignupActivity.this, HomeActivity.class));
        finish();
    }

    private void createUser(String name, String email, String phone, String address, String usertype, String userBio, String userInterests) {

        uid=auth.getCurrentUser().getUid();

        User user = new User(name, email, phone, address, usertype, userBio, userInterests);

        mFirebaseDatabase.child(uid).setValue(user);

        if (profileBitmap != null) {
            uploadImage(profileBitmap, uid,"profile");
        }

        if(usertype.equals("Artist") && artBitmap!=null) {
            uploadImage(artBitmap, uid, "art");
        }
        addUserChangeListener();
    }


    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }
                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email + ", "+ user.phone + ", "+ user.address+", "+user.userType);

                // clear edit text
                inputEmail.setText("");
                edtAddress.setText("");
                edtName.setText("");
                edtPhone.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
}
