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
    User user=new User();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private EditText projectNameText;
    private Button beginButton;
    private RadioGroup styleRadioGroup;
    private RadioButton renoir;
    private RadioButton monet;
    private RadioButton gogh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        projectNameText = findViewById(R.id.project_name);
        beginButton = findViewById(R.id.create_project_button);
        beginButton.setOnClickListener(this);
        styleRadioGroup = findViewById(R.id.styleRadioGroup);
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
        String userId = auth.getCurrentUser().getUid();
        String projectKey = mFirebaseDatabase.child(userId).push().getKey();

        String style = ((RadioButton)findViewById(styleRadioGroup.getCheckedRadioButtonId())).getText().toString();

        mFirebaseDatabase.child(userId).child(projectKey).child("project-name").setValue(projectNameText.getText().toString());
        mFirebaseDatabase.child(userId).child(projectKey).child("style").setValue(style);

        Intent intent = new Intent(NewProjectActivity.this, CanvasActivity.class);
        intent.putExtra("projectId", projectKey);
        intent.putExtra("projectName", projectNameText.getText().toString());
        intent.putExtra("userId", userId);
        intent.putExtra("style", style);
        startActivity(intent);
        finish();
    }
}
