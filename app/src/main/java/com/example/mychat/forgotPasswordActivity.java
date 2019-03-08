package com.example.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button send;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Reset Password");

        email = findViewById(R.id.send_email);
        send = findViewById(R.id.reset);

        auth = FirebaseAuth.getInstance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String Email = email.getText().toString();

            if(Email.equals("")){

                Toast.makeText(getApplicationContext(),"fill Email",Toast.LENGTH_SHORT).show();

            }else {

                auth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                        startActivity(new Intent(forgotPasswordActivity.this,LoginActivity.class));

                        }else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }

            }
        });

    }
}
