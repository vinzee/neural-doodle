package com.example.vinzee.neural_doodle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messaging extends AppCompatActivity {


    private static final String TAG = Messaging.class.getSimpleName();

    User usr = new User();
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    TextView userName;
    EditText messageArea;
    ScrollView scrollView;
    String chatwith;
    List<Message> messageList = new ArrayList<>();

    private Button mSendButton;
    //Firebase reference1, reference2;

    //Firebase references
    private DatabaseReference mFirebaseDatabaseUsr, mFirebaseDatabaseCht;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;

    private BroadcastReceiver usrReceiver;
    private IntentFilter usrIntentFilter;
    private SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);


        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        usr.uID = prf.getString("uID",null);
        usr.name = prf.getString("name",null);
        Intent intent = getIntent();
        chatwith = intent.getExtras().getString("chatwith");

        userName = findViewById(R.id.userName);
        userName.setText(chatwith);

//        usrReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                usr = (User) intent.getSerializableExtra("usrDetails");
//            }
//        };
//        usrIntentFilter = new IntentFilter("sendinguser");
//        registerReceiver(usrReceiver,usrIntentFilter);


        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
//        mSendButton = (Button)findViewById(R.id.sendButton) ;

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabaseUsr = mFirebaseInstance.getReference("messages/"+ usr.name + "_" + chatwith);
        mFirebaseDatabaseCht = mFirebaseInstance.getReference("messages/"+ chatwith + "_" + usr.name);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){


                    Message friendl = new Message(messageText,usr.name,usr.uID + "Profile.jpg",null,Helper.GetDateTime());

                    mFirebaseDatabaseUsr.push().setValue(friendl);
                    mFirebaseDatabaseCht.push().setValue(friendl);

//                    mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
//
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("message", messageText);
//                    map.put("user", usr.name);
//                    mFirebaseDatabaseUsr.push().setValue(map);
//                    mFirebaseDatabaseCht.push().setValue(map);
                    messageArea.setText("");
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
                    String name = friendlyMessage.getName() == null ? "Untitled" : friendlyMessage.getName();
                    if (name.equals(usr.name)) {
                        addMessageBox(friendlyMessage.getText(), 1);
                    } else {
                        addMessageBox(friendlyMessage.getText(), 2);
                    }
                    //messageList.add(friendlyMessage);
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


    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Messaging.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 2) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }


}
