package com.fyp.kyd;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fyp.auth.LoginActivity;
import com.fyp.auth.SignUpActivity;

public class SigningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SigningActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);

    }
}
