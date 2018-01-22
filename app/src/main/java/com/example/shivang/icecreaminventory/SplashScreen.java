package com.example.shivang.icecreaminventory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.shimmer.ShimmerFrameLayout;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        ShimmerFrameLayout container = findViewById(R.id.shimmer_view_container);
        container.startShimmerAnimation();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {


                Intent intent = new Intent(SplashScreen.this, ChoiceScreen.class);
                startActivity(intent);
                finish();
            }

        }, 3000L);
    }
}
