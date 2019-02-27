package com.example.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, passwrod, email;
    Button btn_register;
    private ProgressDialog dialog;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Register");

        dialog = new ProgressDialog(this);

        username = findViewById(R.id.username);
        passwrod = findViewById(R.id.password);
        email = findViewById(R.id.Email);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_userName = username.getText().toString();
                String txt_password = passwrod.getText().toString();
                String txt_email = email.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_userName)) {


                } else {

                    dialog.setMessage("please wait.");
                    dialog.show();
                    register(txt_userName, txt_password, txt_email);
                }

            }
        });


    }

    private void register(final String username, final String password, final String email) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {

                    String userid = auth.getCurrentUser().getUid();


                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);


                    User user = new User(username,userid,"default");

                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, Main2Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(RegisterActivity.this, "you cn't register", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }

                        }
                    });
                }

            }
        });


    }
}
