package com.example.mychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import com.example.mychat.adapter.MessageAdapter;
import com.example.mychat.fragments.APIService;
import com.example.mychat.notification.Client;
import com.example.mychat.notification.Data;
import com.example.mychat.notification.MyResponse;
import com.example.mychat.notification.Sender;
import com.example.mychat.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ValueEventListener seenListener;
    String userId;

    APIService apiService;
    boolean notify =false;

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

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


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
         userId = intent.getStringExtra("userId");

         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 User user = dataSnapshot.getValue(User.class);
                 userName.setText(user.getName());

                 if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.user);

                 }else {

                         Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                 }

                 readMessage(firebaseUser.getUid(),userId,user.getImageURL());
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        seenMessage(userId);



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String msg = Message.getText().toString();

                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,msg);
                }else {

                }
                Message.setText("");
            }
        });

    }

    private void seenMessage(final String uId){

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                Messages chat = snapshot.getValue(Messages.class);
                if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(uId)){

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("seen",true);
                    snapshot.getRef().updateChildren(hashMap);

                }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage(String sender, final String reciever, String Message){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Messages messages= new Messages(sender,reciever,Message,false);

        databaseReference.child("Chats").push().setValue(messages);


        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userId);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        final String msg = Message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify) {
                    sendNotification(reciever, user.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        addChatListResever(userId);
    }
    private boolean addChatListResever(final String uid){

//add new
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(uid)
                .child(firebaseUser.getUid());

        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    chatRef1.child("id").setValue(firebaseUser.getUid());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


return true;
    }

    private void sendNotification(String reciever, final String name, final String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String token = snapshot.getValue(String.class);
                    Data data = new Data(firebaseUser.getUid(),name+": "+message,"New Message",userId,R.mipmap.ic_launcher);

                    Sender sender = new Sender(data,token);

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if(response.body().succes!=1){
                                   // Toast.makeText(MessageActivity.this,"faild send notification!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private boolean currentUser(String UserId){


        SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("Currentuser",UserId);
        editor.apply();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
        currentUser(userId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("Offline");
        currentUser("none");
    }
}
