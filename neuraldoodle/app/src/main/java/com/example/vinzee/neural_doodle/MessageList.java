package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageList extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private SharedPreferences prf;
    User usr = new User();
    List<Message> messageList = new ArrayList<>();

    private DatabaseReference mFirebaseDatabaseUsr, mFirebaseDatabaseCht;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;
    Button mMessageSendButton;
    private EditText mMessageEditText;

    String chatwith;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageEditText = (EditText) findViewById(R.id.edittext_chatbox);

        mMessageSendButton = (Button) findViewById(R.id.button_chatbox_send);

        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        usr.uID = prf.getString("uID",null);
        usr.name = prf.getString("name",null);
        Intent intent = getIntent();
        chatwith = intent.getExtras().getString("chatwith");


        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabaseUsr = mFirebaseInstance.getReference("messages/"+ usr.name + "_" + chatwith);
        mFirebaseDatabaseCht = mFirebaseInstance.getReference("messages/"+ chatwith + "_" + usr.name);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);

        Message message = new Message("Hello",null,null,null,null);
        messageList.add(message);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = mMessageEditText.getText().toString();

                if(!messageText.equals("")){

                    Message friendlyMessage = new Message(messageText, usr.name,
                            usr.uID + "_profile.jpg", null,Helper.GetDateTime());
                    mFirebaseDatabaseUsr.push().setValue(friendlyMessage);
                    mFirebaseDatabaseCht.push().setValue(friendlyMessage);

//                    mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
//
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("message", messageText);
//                    map.put("user", usr.name);
//                    mFirebaseDatabaseUsr.push().setValue(map);
//                    mFirebaseDatabaseCht.push().setValue(map);
                    mMessageEditText.setText("");
                }
            }
        });


        mFirebaseDatabaseUsr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Message friendlyMessage = dataSnapshot.getValue(Message.class);
                    if (friendlyMessage != null) {
                        friendlyMessage.setId(dataSnapshot.getKey());
                    }
                    if (friendlyMessage.getName().equals(usr.name)) {
                        //addMessageBox(friendlyMessage.getText(), 1);
                    } else {
                        // addMessageBox(friendlyMessage.getText(), 2);
                    }
                    messageList.add(friendlyMessage);
                    mMessageAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}
