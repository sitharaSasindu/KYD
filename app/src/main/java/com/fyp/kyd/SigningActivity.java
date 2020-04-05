package com.fyp.kyd;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.fyp.auth.LoginActivity;
import com.fyp.auth.SignUpActivity;

public class SigningActivity extends AppCompatActivity {

    public static boolean verified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    Intent i = new Intent(SigningActivity.this, QRActivity.class);
                    startActivity(i);
                    Toast.makeText(getApplicationContext(), "Transaction Signed Successfully.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }, 3000);

    }
}
