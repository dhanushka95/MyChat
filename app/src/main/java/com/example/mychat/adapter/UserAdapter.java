package com.example.mychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mychat.MessageActivity;
import com.example.mychat.R;
import com.example.mychat.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;
    private boolean isChat;

    public UserAdapter(Context mContext, List<User> mUser,boolean isChat) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.isChat=isChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUser.get(position);
        holder.user_Names.setText(user.getName());
        if(user.getImageURL().equals("default")){
               holder.profile_images.setImageResource(R.mipmap.ic_launcher);
        }else {

              Glide.with(mContext).load(user.getImageURL()).into((holder.profile_images));
        }


        if(isChat){
            if(user.getStatus().equals("online")){
                 holder.img_on.setVisibility(View.VISIBLE);
                 holder.img_off.setVisibility(View.GONE);


            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userId",user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView user_Names;
        public ImageView profile_images;
        public ImageView img_on;
        public ImageView img_off;

        public ViewHolder(View itemView) {
            super(itemView);

            user_Names = itemView.findViewById(R.id.usernameList);
            profile_images = itemView.findViewById(R.id.profile_images_list);
            img_off = itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);



        }
    }
}
