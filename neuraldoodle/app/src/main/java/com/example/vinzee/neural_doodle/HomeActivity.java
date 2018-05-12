package com.example.vinzee.neural_doodle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    SharedPreferences pref;
    private StorageReference mStorageRef;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    pushFragment(new ProjectsFragment());
                    return true;
                case R.id.navigation_messages:
                    pushFragment(new ChatFragment());
                    return true;
                case R.id.navigation_profile:
                    pushFragment(new ProfileFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Imagination Station");
        this.getUser();

        setContentView(R.layout.activity_home);
        pushFragment(new ProjectsFragment());

        auth = FirebaseAuth.getInstance();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:Intent intent = new Intent(HomeActivity.this, UpdateUserProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                auth.signOut();
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
        }

        return false;
    }

    private void pushFragment(ChatFragment chatFragment){
        if(chatFragment==null)    {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager!=null){

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.rootLayout,chatFragment);
            transaction.commit();
        }
    }

    private void pushFragment(ProjectsFragment projectFragment){
        if(projectFragment ==null)    {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager!=null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.rootLayout, projectFragment);
            transaction.commit();
        }
    }

    private void pushFragment(ProfileFragment profileFragment){
        if(profileFragment == null)    {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager!=null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.rootLayout,profileFragment);
            transaction.commit();
        }
    }
    public void getUser() {
        final SharedPreferences pref;
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        FirebaseAuth auth;
        DatabaseReference mFirebaseDatabase;
        FirebaseDatabase mFirebaseInstance;
        auth = FirebaseAuth.getInstance();
        final String uID = auth.getCurrentUser().getUid();


        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users/" + uID);


        mFirebaseDatabase.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User userObj = dataSnapshot.getValue(User.class);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("uID",uID);
                        editor.putString("name",userObj.name);
                        editor.putString("email",userObj.email);
                        editor.commit();


                        //user.email now has your email value
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
