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
import com.google.firebase.database.core.utilities.Utilities;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    private MaterialEditText  passwrod, email;
    private ProgressDialog dialog;
    Button btn_login;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Login");

        passwrod = findViewById(R.id.password);
        email = findViewById(R.id.Email);
        btn_login = findViewById(R.id.btn_Login);
        auth = FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_password = passwrod.getText().toString();
                String txt_email = email.getText().toString();

                dialog.setMessage("please wait.");
                dialog.show();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){

                }else {

                    auth.signInWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(LoginActivity.this, "you cn't Login", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }

                        }
                    });

                }

            }
        });

   }


}
