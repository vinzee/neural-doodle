package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ListUser extends AppCompatActivity {
    private static final String TAG = ListUser.class.getSimpleName();
    ListView usersList;
    TextView noUsersText;
    ArrayList<User> ul = new ArrayList<>();
    ArrayList<String> userName = new ArrayList<>();
    int totalUsers = 0;
    //ProcessDialog pd;

    //Firebase references
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueUserSnapshot : dataSnapshot.getChildren()) {
                    User currentUser = uniqueUserSnapshot.getValue(User.class);
                    ul.add(currentUser);
                    if (currentUser.name != null) {
                        userName.add(currentUser.name);
                    } else {
                        userName.add("Untitled");
                    }
                }
                updateUserList();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User chatWith = ul.get(position);
                Intent i = new Intent(ListUser.this, Messaging.class);
                i.putExtra("chatwith", chatWith.name);
                startActivity(i);
            }
        });
    }

    public void updateUserList(){
        try{
            if(ul.isEmpty()){
                noUsersText.setVisibility(View.VISIBLE);
                usersList.setVisibility(View.GONE);
            }
            else{
                noUsersText.setVisibility(View.GONE);
                usersList.setVisibility(View.VISIBLE);
                usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userName));
            }
        }
        catch (Exception e){

        }

    }

}
