package com.example.vinzee.neural_doodle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.UserViewHolder> {


    private ArrayList<User> UserArrayList = new ArrayList<>();
    private Context mcontext;

    public ChatFragmentAdapter(ArrayList<User> UserArrayList, Context context) {
        this.UserArrayList = UserArrayList;
        this.mcontext = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rows_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {

        final User result = UserArrayList.get(position);

        if (result != null) {
            final String usrName = result.name;
            holder.textATMTitle.setText(result.name);
//            Picasso.get()
//                    .load(result.icon)
//                    .into(holder.imageATM);
            holder.textATMAddress.setText(result.userType);

            holder.textATMTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mcontext, Messaging.class);

                    i.putExtra("chatwith", result.name);
                    mcontext.startActivity(i);
                }
            });
        }
        //Log.e("Called","Bind");

    }

    @Override
    public int getItemCount() {
        return UserArrayList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {


        private ImageView imageATM;
        private TextView textATMTitle;
        private TextView textATMAddress;
        private TextView textATMRating;

        public UserViewHolder(View itemView) {
            super(itemView);
            imageATM = itemView.findViewById(R.id.imageATM);
            textATMTitle = itemView.findViewById(R.id.textATMTitle);
            textATMAddress = itemView.findViewById(R.id.textATMAddress);
            textATMRating = itemView.findViewById(R.id.textATMRating);
        }
    }


}
