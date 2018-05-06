package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateUserProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{

    private static final String TAG = CreateUserProfileActivity.class.getSimpleName();
    private Spinner profileSpinner;
    private EditText edtName, edtAddress, edtPhone, edtEmail;
    private Button btnCreateProfile;
    User user = new User();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId, txtDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);

        initUI();
    }

    private void initUI()   {
        profileSpinner = (Spinner) findViewById(R.id.profile_type_spinner);
        profileSpinner.setOnItemSelectedListener(this);
        profileSpinner.setSelection(0);


        Intent intentExtras = getIntent();
        String email= intentExtras.getStringExtra("email");



        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.profile_type_array,android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileSpinner.setAdapter(typeAdapter);
        btnCreateProfile = (Button) findViewById(R.id.create_profile);
        btnCreateProfile.setOnClickListener(this);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Imagination Station");
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

        edtName = (EditText)findViewById(R.id.name);
        edtAddress = (EditText)findViewById(R.id.address);
        edtPhone = (EditText) findViewById(R.id.phone);
        edtEmail = (EditText) findViewById(R.id.email);
        edtEmail.setText(email);
        edtEmail.setEnabled(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        user.userType = (parent.getItemAtPosition(position).toString());

        if(user.userType.equals("Patreon"))   {

            //show elements of patreon
        }
        else if(user.userType.equals("Artist"))  {
            // show elements of artist

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())  {
            case R.id.create_profile:createProfile();
                break;
        }
    }

    private void createProfile()    {

        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();
        String userType = profileSpinner.getSelectedItem().toString();
        createUser(name,email,phone,address,userType);



        //direct user to home screen
        startActivity(new Intent(CreateUserProfileActivity.this, MainActivity.class));
        finish();
    }

    private void createUser(String name, String email, String phone, String address, String usertype) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        //if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        //}

        User user = new User(name, email, phone, address, usertype);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email + ", "+ user.phone + ", "+ user.address+", "+user.userType);


                // Display newly updated name and email
                //txtDetails.setText(user.name + ", " + user.email);

                // clear edit text
                edtEmail.setText("");
                edtName.setText("");
                edtPhone.setText("");
                edtAddress.setText("");

                //toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
}
