package com.example.vinzee.neural_doodle;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * https://www.androidhive.info/2017/02/android-creating-gmail-like-inbox-using-recyclerview/
 */
public class ChatFragment extends Fragment {

    private static final String TAG = ListUser.class.getSimpleName();
    ListView usersList;
    TextView noUsersText;
    ArrayList<User> ul = new ArrayList<>();


    Button btn;

    private RecyclerView chatRecyclerView;


    private ChatFragmentAdapter atmRecyclerViewAdapter;
    private ArrayList<User> userArraylist = new ArrayList<>();
    private LinearLayoutManager layoutManager;


    ArrayList<String> userName = new ArrayList<>();
    int totalUsers = 0;
    //ProcessDialog pd;

    //Firebase references
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    View view;

    public ChatFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


//        View chatview = inflater.inflate(R.layout.fragment_chat, container, false);
//        btn = chatview.findViewById(R.id.btnMsg);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");
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

        usersList = (ListView)view.findViewById(R.id.usersList);
        noUsersText = (TextView)view.findViewById(R.id.noUsersText);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User chatWith = ul.get(position);
                Intent i = new Intent(view.getContext(),Messaging.class);
                i.putExtra("chatwith", chatWith.name);
                startActivity(i);
            }
        });




        return view;


//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), ListUser.class);
//                startActivity(i);
//                }
//
//        });
//        return chatview;
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
                //usersList.setAdapter(new ArrayAdapter<String>(this, v, userName));
                usersList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, userName));
            }
        }
        catch (Exception e){

        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
//        //layoutManager = new LinearLayoutManager(getContext());
//        //atmRecyclerView.setLayoutManager(layoutManager);
//        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(chatRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
//        chatRecyclerView.addItemDecoration(dividerItemDecoration);
//
//
//        atmRecyclerViewAdapter = new ChatFragmentAdapter(userArraylist, getActivity());
//
//        chatRecyclerView.setAdapter(atmRecyclerViewAdapter);
//        //atmRecyclerView.addOnScrollListener(onScrollListener);
//        addOnscrollListener(chatRecyclerView);
//        getUserList();

    }

    public void getUserList(){

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e(TAG, "App title updated");
                for (DataSnapshot uniqueUserSnapshot : dataSnapshot.getChildren()) {
                    User currentUser = uniqueUserSnapshot.getValue(User.class);
                    userArraylist.add(currentUser);
                    userName.add(currentUser.name);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
    }


    private void addOnscrollListener(RecyclerView recyclerView) {
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount;
                int requestCount = 0;
                int totalPages;
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
//                    if (!loadingData) {
//                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
//                            loadingData = true;
//
//                            if (nextPageToken != null) {
//                                //requestCount += 1;
//                                getATMS(nextPageToken);
//                                nextPageToken = "";
//
//                            }
//                        }
//                    }

                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }

}
