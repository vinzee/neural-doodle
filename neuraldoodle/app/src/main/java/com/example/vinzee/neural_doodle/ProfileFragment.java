package com.example.vinzee.neural_doodle;


import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    public ProfileFragment() {
        // Required empty public constructor
    }

    private static final String TAG = SignupActivity.class.getSimpleName();
    private EditText edtName,edtAddress,edtPhone,edtBio;
    private Button btnUpload, btnSelectInterest;
    private TextView tvInterests;
    private String uid;
    private FirebaseAuth auth;
    private StorageReference mStorageRef;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2, PICK_ARTIST_IMAGE=3;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private Bitmap profileBitmap, artBitmap;
    private ImageView profileImg;
    private View profileView;
    private Activity linkedActivity;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = new User();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();
        uid = auth.getCurrentUser().getUid();
        linkedActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profileView =  inflater.inflate(R.layout.fragment_profile, container, false);

        profileImg = (ImageView) profileView.findViewById(R.id.profile_image);
        profileImg.setOnClickListener(this);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(linkedActivity,
                R.array.profile_type_array,android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtName = (EditText) profileView.findViewById(R.id.name);
        edtAddress = (EditText) profileView.findViewById(R.id.address);
        edtPhone = (EditText) profileView.findViewById(R.id.phone);
        edtBio = (EditText) profileView.findViewById(R.id.bio);
        tvInterests = (TextView) profileView.findViewById(R.id.userInterest);

        btnSelectInterest=(Button) profileView.findViewById(R.id.selectInterest);
        btnSelectInterest.setOnClickListener(this);
        btnUpload = (Button) profileView.findViewById(R.id.update_button);
        btnUpload.setOnClickListener(this);

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

        getProfileDetails();

        return profileView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())  {
            case R.id.profile_image:
                loadImageFromStorage();
                break;
            case R.id.update_button:
                updateUser();
                break;
            case R.id.selectInterest:
                getInterests();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(linkedActivity);
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

    public void loadImageFromStorage()  {

        if (ActivityCompat.checkSelfPermission(linkedActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(linkedActivity, new String[]{android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        if (ActivityCompat.checkSelfPermission(linkedActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(linkedActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }
        try {
            PackageManager pm = linkedActivity.getPackageManager();
            int hasPerm = pm.checkPermission(android.Manifest.permission.CAMERA, linkedActivity.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(linkedActivity);
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
                Toast.makeText(linkedActivity, "Camera Permission error", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(linkedActivity, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case PICK_IMAGE_CAMERA:
                if(resultCode == linkedActivity.RESULT_OK){
                    try {
                        final Bitmap selectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        profileBitmap = selectedImage;
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                        profileImg.setImageBitmap(selectedImage);
                        uploadImage(profileBitmap);
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_IMAGE_GALLERY:
                if(resultCode == linkedActivity.RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = linkedActivity.getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profileBitmap = selectedImage;
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                        profileImg.setImageBitmap(selectedImage);
                        uploadImage(profileBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    private void uploadImage(Bitmap profileBitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileBitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] data = baos.toByteArray();

        StorageReference storageReference = mStorageRef.child("images/"+uid+"_"+"profile.jpg");

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(linkedActivity.getApplicationContext(),"image upload failed ",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        Glide.with(linkedActivity)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(profileImg);
    }

    private void getProfileDetails() {
        final String tempUserId = uid;

        mFirebaseDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            if (user.name != null && !user.name.isEmpty() && user.name.length() > 0) {
                                edtName.setText(user.name);
                            } else{
                                edtName.setText("");
                            }
                            if (user.address != null && !user.address.isEmpty() && user.address.length() > 0) {
                                edtAddress.setText(user.address);
                            } else{
                                edtAddress.setText("");
                            }
                            if (user.phone != null && !user.phone.isEmpty() && user.phone.length() > 0) {
                                edtPhone.setText(user.phone);
                            } else{
                                edtPhone.setText("");
                            }
                            if (user.userBio != null && !user.userBio.isEmpty() && user.userBio.length() > 0) {
                                edtBio.setText(user.userBio);
                            } else{
                                edtBio.setText("");
                            }
                            if (user.interests != null && !user.interests.isEmpty() && user.interests.length() > 0) {
                                tvInterests.setText(user.interests);
                            } else{
                                tvInterests.setText("");
                            }

                            mStorageRef = FirebaseStorage.getInstance().getReference();

                            StorageReference storageReference = mStorageRef.child("images/"+tempUserId+"_profile.jpg");

                            Glide.with(linkedActivity)
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .into(profileImg);
                        } else{
                            edtName.setText("");
                            edtAddress.setText("");
                            edtPhone.setText("");
                            edtBio.setText("");
                            tvInterests.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateUser() {
        User new_user = new User();

        new_user.name = edtName.getText().toString();
        new_user.phone = edtPhone.getText().toString();
        new_user.address = edtAddress.getText().toString();
        new_user.userBio = edtBio.getText().toString();
        new_user.interests = tvInterests.getText().toString();

        DatabaseReference userProfileFirebaseDatabaseReference = mFirebaseDatabase.child("users").child(uid);
        userProfileFirebaseDatabaseReference.setValue(new_user);

        Toast.makeText(linkedActivity.getApplicationContext(),"Profile updated successfully", Toast.LENGTH_LONG).show();
    }


}
