package com.example.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import com.example.mychat.adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView userName,Message;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;
    List<Messages> mChat;
    RecyclerView recyclerView;


    ImageButton btn_send;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar_message = findViewById(R.id.toolbar_message);

//        setSupportActionBar(toolbar_message);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar_message.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) );
            }
        });


        recyclerView = findViewById(R.id.recycleview_message_bottem);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        profile_image = findViewById(R.id.profile_image_message);
        userName = findViewById(R.id.user_name_Message);
        btn_send = findViewById(R.id.btn_send_message);
        Message = findViewById(R.id.text_send_message);




         intent = getIntent();
         final String userId = intent.getStringExtra("userId");

         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 User user = dataSnapshot.getValue(User.class);
                 userName.setText(user.getName());

                 if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);

                 }else {
                     try {
                         Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                     }catch (Exception e){

                     }
                 }

                 readMessage(firebaseUser.getUid(),userId,user.getImageURL());
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = Message.getText().toString();

                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,msg);
                }else {

                }
                Message.setText("");
            }
        });

    }

    private void sendMessage(String sender,String reciever,String Message){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Messages messages= new Messages(sender,reciever,Message);

        databaseReference.child("Chats").push().setValue(messages);
    }

    private void readMessage(final String myId, final String userId, final String imageURL){

        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Messages chat = snapshot.getValue(Messages.class);

                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {

                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat,imageURL);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    private void status(String status){

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }
}
