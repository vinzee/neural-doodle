package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectsFragment extends Fragment {
    private String uid;
    private FirebaseAuth auth;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Project> projectList;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("projects").child(uid);

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                projectList = new ArrayList<>();

                for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                    Project project = projectSnapshot.getValue(Project.class);
                    Object projectName = projectSnapshot.child("name").getValue();
                    project.name = (projectName == null) ? "Untitled" : projectName.toString();
                    project.name = project.name.substring(0, 1).toUpperCase() + project.name.substring(1);
                    project.id = projectSnapshot.getKey();
                    projectList.add(project);
                }

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(projectList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
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

        recyclerView =  view.findViewById(R.id.recyclerview);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        openCanvasActivity(position);
                    }

                    @Override public void onLongItemClick(View view, int position) {
//                        openCanvasActivity(position);
                    }
                })
        );

        return view;
    }

    private void openCanvasActivity (int position) {
        Project project = projectList.get(position);

        Intent intent = new Intent(getActivity(), CanvasActivity.class);
        intent.putExtra("projectId", project.id);
        intent.putExtra("name", project.name);
        intent.putExtra("userId", uid);
        intent.putExtra("style", project.style);

        startActivity(intent);
    }
}
