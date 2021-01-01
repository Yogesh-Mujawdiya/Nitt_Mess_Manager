package com.my.nitt_mess_manager;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_manager.ui.HomeFragment;

import org.jetbrains.annotations.NotNull;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 100;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContentView = findViewById(R.id.fullscreen_content);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int TIME_OUT = 1000;
        if (user != null) {
            new Handler().postDelayed(() -> {
                final FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                final String UserId = User.getEmail().split("@")[0];
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Manager").child(UserId);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (ContextCompat.checkSelfPermission(
                                    SplashScreenActivity.this, Manifest.permission.CAMERA) ==
                                    PackageManager.PERMISSION_DENIED) {
                                ActivityCompat.requestPermissions(SplashScreenActivity.this, new String[]{Manifest.permission.CAMERA}, 1234);
                            }
                            else {
                                Intent intent
                                        = new Intent(SplashScreenActivity.this,
                                        HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent
                                    = new Intent(SplashScreenActivity.this,
                                    InvalidUserActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                });
            }, TIME_OUT);
        } else {
            new Handler().postDelayed(() -> {
                if (ContextCompat.checkSelfPermission(
                        SplashScreenActivity.this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(SplashScreenActivity.this, new String[]{Manifest.permission.CAMERA}, 123);
                }
                else {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, TIME_OUT);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        Intent i;
        if(requestCode == 123)
            i = new Intent(SplashScreenActivity.this, LoginActivity.class);
        else
            i = new Intent(SplashScreenActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

}