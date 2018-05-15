package com.example.vinzee.neural_doodle;

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
    private String uid;
    private FirebaseAuth auth;
    private User user;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        pushFragment(new GalleryFragment());
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        isArtist();
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

    private void isArtist(){
        mFirebaseDatabase.child("users").child(uid)
        .addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if(user!=null) {
                        if (user.userType.equals("Artist")) {
                            navigation.getMenu().removeItem(R.id.navigation_projects);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}
