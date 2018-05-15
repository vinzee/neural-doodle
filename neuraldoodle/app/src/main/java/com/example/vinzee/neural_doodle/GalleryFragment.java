package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private String uid;
    private FirebaseAuth auth;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private View view;
    private User user;
    private FloatingActionButton fab;
    private ProgressBar progressBar;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("projects");

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Project> projectList = new ArrayList<>();

                for (DataSnapshot userProjects: dataSnapshot.getChildren()) {
                    for (DataSnapshot projectSnapshot: userProjects.getChildren()) {
                        Project project = projectSnapshot.getValue(Project.class);
                        Object projectName = projectSnapshot.child("name").getValue();
                        project.name = (projectName == null) ? "Untitled" : projectName.toString();
                        project.name = project.name.substring(0, 1).toUpperCase() + project.name.substring(1);
                        project.id = projectSnapshot.getKey();
                        projectList.add(project);
                    }
                }

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(projectList);
                RecyclerView myView =  view.findViewById(R.id.recyclerview);
                myView.setHasFixedSize(true);
                myView.setAdapter(adapter);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                myView.setLayoutManager(llm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        isArtist();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_projects, container, false);

        fab = view.findViewById(R.id.fab);
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


    private void isArtist(){
        mFirebaseDatabase.child("users").child(uid)
            .addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("isArtist", uid + " : " + dataSnapshot.toString());
                    user = dataSnapshot.getValue(User.class);

                    if(user != null && user.userType.equals("Artist")) {
                        fab.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}
