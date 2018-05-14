package com.example.vinzee.neural_doodle;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * https://www.androidhive.info/2017/02/android-creating-gmail-like-inbox-using-recyclerview/
 */
public class ChatFragment extends Fragment {

    Button btn;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View chatview = inflater.inflate(R.layout.fragment_chat, container, false);
        btn = chatview.findViewById(R.id.btnMsg);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListUser.class);
                startActivity(i);
                }

        });
        return chatview;
    }

}
