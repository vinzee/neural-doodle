package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    pushFragment(new ProjectFragment());
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


        setContentView(R.layout.activity_home);
        pushFragment(new ProjectFragment());
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
            case R.id.settings:Intent intent = new Intent(HomeActivity.this, UpdateUserProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:auth.signOut();
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

    private void pushFragment(ProjectFragment projectFragment){
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
        if(profileFragment==null)    {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager!=null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.rootLayout,profileFragment);
            transaction.commit();
        }
    }
}