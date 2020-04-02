package com.fyp.kyd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.fyp.auth.LoginActivity;

import com.fyp.auth.LoginActivity;
import com.fyp.rsa.Main2Activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.internal.Utils;

import static com.fyp.Utils.getSha256Hash;

public class MainActivity extends AppCompatActivity {

    Button scanQR, firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(i);
                finish();
            }
        });

        Toast.makeText(this, getSha256Hash("123456_MY_PASSWORD"), Toast.LENGTH_SHORT).show();

    }

    private void init() {
        scanQR = (Button) findViewById(R.id.qr);
        firebase = (Button) findViewById(R.id.firebase);
    }


}
