package com.example.vinzee.neural_doodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class NewProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private StorageReference mStorageRef;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private EditText projectNameText;
    private FloatingActionButton beginButton;

    private static final @DrawableRes int[] ARTIST_IMAGES = {
        R.drawable.renoir, R.drawable.monet, R.drawable.gogh, R.drawable.picasso, R.drawable.artist1, R.drawable.artist2, R.drawable.artist3, R.drawable.artist4, R.drawable.artist5, R.drawable.artist6
    };

    private static final String[] ARTIST_LABELS = {
        "Renoir", "Monet", "Gogh", "Picasso", "Artist1", "Artist2", "Artist3", "Artist4", "Artist5", "Artist6"
    };

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_icon);

        projectNameText = findViewById(R.id.project_name);
        beginButton = findViewById(R.id.create_project_button);
        beginButton.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("projects");

        mGridView = findViewById(R.id.gridview);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        mGridView.setAdapter(new BaseAdapter() {
            @Override public int getCount() {
                return ARTIST_IMAGES.length;
            }
            @Override public Object getItem(int position) {
                return ARTIST_IMAGES[position];
            }
            @Override public long getItemId(int position) {
                return ARTIST_IMAGES[position];
            }
            @Override public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null || !(convertView instanceof ImageView)) {
                    CheckedRelativeLayout relativeLayout = (CheckedRelativeLayout) getLayoutInflater().inflate(R.layout.grid_item_view, parent, false);

                    final ImageView imageView = relativeLayout.findViewById(R.id.grid_image_view);
                    imageView.setImageResource(ARTIST_IMAGES[position]);

                    TextView textView = relativeLayout.findViewById(R.id.grid_text_view);
                    textView.setText(ARTIST_LABELS[position]);

                    convertView = relativeLayout;
                }

                return convertView;
            }
        });

        mGridView.setItemChecked(0, true);
    }

    @Override
    public void onClick(View v) {
        String projectName = projectNameText.getText().toString();

        if (mGridView.getCheckedItemPosition() == -1) {
            Toast.makeText(getApplicationContext(), "Please Select Artist", Toast.LENGTH_SHORT).show();
            return;
        }

        if (projectName == null || projectName.equals("") ) {
            projectNameText.setError( "Project name is required!" );
            return;
        }

        projectName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);

        String userId = auth.getCurrentUser().getUid();
        String projectKey = mFirebaseDatabase.child(userId).push().getKey();
        String style = ARTIST_LABELS[mGridView.getCheckedItemPosition()];

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
