package com.example.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mychat.fragments.ChatsFragment;
import com.example.mychat.fragments.ProfileFragment;
import com.example.mychat.fragments.UserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main2Activity extends AppCompatActivity {


    CircleImageView profile_image;
    TextView userName;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

   // TabLayout tableLayout;
   // ViewPager viewPager;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Main2Activity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                return  true;
        }
        return  false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("");

        profile_image = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name_main);

        final TabLayout tableLayout = findViewById(R.id.tab_layput);
        final ViewPager viewPager = findViewById(R.id.view_pager);


        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());
                int UnreadMessage = 0;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Messages chat = snapshot.getValue(Messages.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isSeen()){
                        UnreadMessage++;
                    }
                }

                if(UnreadMessage == 0){
                    viewpagerAdapter.addFragment(new ChatsFragment(),"chat");
                }else {
                    viewpagerAdapter.addFragment(new ChatsFragment(),"("+UnreadMessage+")chat");
                }
                viewpagerAdapter.addFragment(new UserFragment(),"user");
                viewpagerAdapter.addFragment(new ProfileFragment(),"Profile");
                viewPager.setAdapter(viewpagerAdapter);

                tableLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user=dataSnapshot.getValue(User.class);

                try {
                    userName.setText(user.getName());

                    if (user.getImageURL().equals("default")) {
                        profile_image.setImageResource(R.drawable.user);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    }
                }catch(Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    class ViewpagerAdapter extends FragmentPagerAdapter{


        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewpagerAdapter(FragmentManager fm){
            super(fm);

            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
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
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
