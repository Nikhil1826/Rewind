package com.fasttech.rewind;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class SplashScreen extends AppCompatActivity {

    AnimationDrawable animationDrawable;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        frameLayout = (FrameLayout)findViewById(R.id.myFrameLay);
        animationDrawable =(AnimationDrawable)frameLayout.getBackground();

        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        animationDrawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, 3000);

    }
}
