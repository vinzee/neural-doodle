package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private StorageReference mStorageRef;
        private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private EditText projectNameText;
    private Button beginButton;
    private RadioGroup styleRadioGroup;
    private RadioButton renoir;
    private RadioButton monet;
    private RadioButton gogh;
    private RadioButton picasso;
    private RadioButton artist1;
    private RadioButton artist2;
    private RadioButton artist3;
    private RadioButton artist4;
    private RadioButton artist5;
    private RadioButton artist6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_icon);

        projectNameText = findViewById(R.id.project_name);
        beginButton = findViewById(R.id.create_project_button);
        beginButton.setOnClickListener(this);
        styleRadioGroup = findViewById(R.id.styleRadioGroup);
        renoir = findViewById(R.id.renoir);
        monet = findViewById(R.id.monet);
        gogh = findViewById(R.id.gogh);
        picasso = findViewById(R.id.picasso);
        artist1 = findViewById(R.id.artist1);
        artist2 = findViewById(R.id.artist2);
        artist3 = findViewById(R.id.artist3);
        artist4 = findViewById(R.id.artist4);
        artist5 = findViewById(R.id.artist5);
        artist6 = findViewById(R.id.artist6);


        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("projects");

    }

    @Override
    public void onClick(View v) {
        String projectName = projectNameText.getText().toString();

        if (projectName == null || projectName.equals("") ) {
            projectNameText.setError( "Project name is required!" );
            return;
        }

        projectName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);

        String userId = auth.getCurrentUser().getUid();
        String projectKey = mFirebaseDatabase.child(userId).push().getKey();
        String style = ((RadioButton)findViewById(styleRadioGroup.getCheckedRadioButtonId())).getText().toString();

        mFirebaseDatabase.child(userId).child(projectKey).child("name").setValue(projectName);
        mFirebaseDatabase.child(userId).child(projectKey).child("style").setValue(style);
        mFirebaseDatabase.child(userId).child(projectKey).child("sketchExists").setValue(false);

        Intent intent = new Intent(NewProjectActivity.this, CanvasActivity.class);
        intent.putExtra("projectId", projectKey);
        intent.putExtra("name", projectName);
        intent.putExtra("userId", userId);
        intent.putExtra("style", style);
        startActivity(intent);
        finish();
    }
}
