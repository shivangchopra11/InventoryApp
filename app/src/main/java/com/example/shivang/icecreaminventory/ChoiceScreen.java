package com.example.shivang.icecreaminventory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChoiceScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_screen);
        Button btnBoss = findViewById(R.id.btn_boss);
        btnBoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChoiceScreen.this,MainActivity.class);
                i.putExtra("code",0);
                startActivity(i);
            }
        });
        Button btnStaff = findViewById(R.id.btn_staff);
        btnStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChoiceScreen.this,MainActivity.class);
                i.putExtra("code",1);
                startActivity(i);
            }
        });
    }
}
