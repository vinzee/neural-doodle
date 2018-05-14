package com.example.vinzee.neural_doodle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        pushFragment(new GalleryFragment());
        this.getUser();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_icon);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    pushFragment(new GalleryFragment());
                    return true;
                case R.id.navigation_projects:
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

    private void pushFragment(Fragment fragment){
        if(fragment == null)    {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager != null){

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.rootLayout, fragment);
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
