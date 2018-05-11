package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectsFragment extends Fragment {
    private static final String TAG = SignupActivity.class.getSimpleName();
    private EditText edtName,edtAddress,edtPhone,edtBio;
    private Button btnUpload, btnSelectInterest , btnSelectArt;
    private TextView tvInterests,tvArtistArt;
    private LinearLayout artistExt;
    private String uid;

    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private StorageReference mStorageRef;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private View view;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        Log.d("uid: ", uid);
        mFirebaseDatabase = mFirebaseInstance.getReference("projects").child(uid);

        mFirebaseDatabase.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                mStorageRef = FirebaseStorage.getInstance().getReference();
//                StorageReference storageReference = mStorageRef.child("images/"+tempUserId+"_profile.jpg");
//                Glide.with(linkedActivity)
//                        .using(new FirebaseImageLoader())
//                        .load(storageReference)
//                        .into(profileImg);

                Log.d("dataSnapshot: ", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_projects, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewProjectActivity.class);
                startActivity(intent);
            }
        });

        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }

}
