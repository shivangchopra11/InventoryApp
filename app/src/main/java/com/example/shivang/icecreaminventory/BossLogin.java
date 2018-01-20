package com.example.shivang.icecreaminventory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shivang.icecreaminventory.Models.Credentials;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class BossLogin extends AppCompatActivity {
//    private DatabaseReference mDatabase;
    Button btnLogin;
    EditText etEmail,etPassword;
//    String[] boss = new String[2];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss_login);
        Log.v("TAG","Button clicked");
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
//        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().toString().trim().length() > 0) {
                    if(etPassword.getText().toString().trim().length() > 0) {
                        final ProgressDialog dialog = new ProgressDialog(BossLogin.this);
                        dialog.setMessage("Verifying");
                        dialog.show();

//                        Log.v("TAG","Button clicked");
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                            @Override
                            public void onSuccess(AuthResult authResult) {
                                dialog.dismiss();
                                Intent i = new Intent(BossLogin.this,MainActivity.class);
                                startActivity(i);
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Log.v("TAG","wrong credentials entered");
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(BossLogin.this);
                                builder1.setMessage("Wrong Credentials Entered");
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        });
                    }
                    else {
                        etPassword.setError("Enter Password");
                    }
                }
                else {
                    etEmail.setError("Enter Email");
                }

            }
        });

    }

}

