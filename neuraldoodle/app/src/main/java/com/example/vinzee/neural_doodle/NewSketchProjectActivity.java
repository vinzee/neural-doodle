package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewSketchProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private StorageReference mStorageRef;
    User user=new User();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private EditText projectNameText;
    private Button beginButton;
    private RadioButton renoir;
    private RadioButton monet;
    private RadioButton gogh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sketch_project);

        projectNameText = findViewById(R.id.project_name);
        beginButton = findViewById(R.id.create_project_button);
        beginButton.setOnClickListener(this);
        renoir = findViewById(R.id.renoir);
        monet = findViewById(R.id.monet);
        gogh = findViewById(R.id.gogh);

        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("projects");
    }

    @Override
    public void onClick(View v) {
        Log.d("New Project", "Clicked!!");
        String userId = auth.getCurrentUser().getUid();
        String projectKey = mFirebaseDatabase.child(userId).push().getKey();
        mFirebaseDatabase.child(userId).child(projectKey).
                child("project-name").setValue(projectNameText.getText().toString());
        String style = "renoir";
        if(renoir.isChecked()){
            style = "renoir";
        } else if (monet.isChecked()){
            style = "monet";
        } else if (gogh.isChecked()){
            style = "gogh";
        }

        mFirebaseDatabase.child(userId).child(projectKey).child("style").setValue(style);
        Intent intent = new Intent(NewSketchProjectActivity.this, CanvasActivity.class);
        intent.putExtra("projectId", projectKey);
        intent.putExtra("projectName", projectNameText.getText().toString());
        intent.putExtra("userId", userId);
        intent.putExtra("style", style);
        startActivity(intent);
        finish();
    }
}
