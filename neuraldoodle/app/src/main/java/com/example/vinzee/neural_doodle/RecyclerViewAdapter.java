package com.example.vinzee.neural_doodle;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    public ArrayList<Project> projectList;
    private StorageReference storageRef;

    public RecyclerViewAdapter (ArrayList<Project> myValues){
        this.projectList = myValues;
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Project project = projectList.get(position);
        holder.name.setText(project.name);
        holder.projectID = project.id;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onBindViewHolder", "onClick：");
            }
        });

        String projectPath = "images/" + project.id + "_project.png";
        Log.d("projectPath", projectPath);

        storageRef.child(projectPath).getDownloadUrl()
        .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
            DefaultSliderView s1 = new DefaultSliderView(holder.slider.getContext());
            s1.image(uri.toString());
            holder.slider.addSlider(s1);
        }
        })
        .addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
            Log.d("onFailure", "!!!!! image loading failed !!!!!");
            }
        });

        String sketchPath = "images/" + project.id + "_sketch.png";
        Log.d("sketchPath", sketchPath);

        storageRef.child(sketchPath).getDownloadUrl()
        .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DefaultSliderView s1 = new DefaultSliderView(holder.slider.getContext());
                s1.image(uri.toString());
                holder.slider.addSlider(s1);
            }
        })
        .addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure", "!!!!! image loading failed !!!!!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private String projectID;
        private TextView name;
        private SliderLayout slider;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.project_name);
            slider = itemView.findViewById(R.id.project_slider);
        }

        public String getIntent () {
            return "hello";
        }
    }

}