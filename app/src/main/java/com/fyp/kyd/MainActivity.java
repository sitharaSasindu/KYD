package com.fyp.kyd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fyp.auth.LoginActivity;

//import com.fyp.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    Button scanQR;

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
    }

    private void init() {
        scanQR = (Button) findViewById(R.id.qr);
    }
}
