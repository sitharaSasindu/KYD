package com.fyp.kyd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.fyp.auth.LoginActivity;
import com.fyp.qr.DetailsView;
import com.fyp.qr.DetailsViewActivity;
import com.fyp.qr.MyApplication;

public class VerificationActivity extends AppCompatActivity {

    private VerificationView verificationView;
    private ProgressBar progressBar;
    private TextView hashtxt;
    private ImageView iconsuccess;
    public static boolean reqresponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verificationView = findViewById(R.id.layout_verify);
        progressBar = findViewById(R.id.progressBar2);
        hashtxt = findViewById(R.id.hash);
        iconsuccess = findViewById(R.id.icona);


        verificationView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
         String hash = intent.getStringExtra("stellerash");
        hashtxt.setText(hash);

        verifyDetails(hash);


    }


    public void verifyDetails(final String hash) {

        final ProgressDialog progressDialog = new ProgressDialog(VerificationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying Details...");
        progressDialog.show();


        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                checkHash(hash);
                progressDialog.dismiss();
            }
        }, 3000);
    }


    public void checkHash(String hash) {
        reqresponse = false;
// Tag used to cancel the request
        String tag_string_req = "string_req";

        String url = "https://horizon-testnet.stellar.org/transactions/" + hash + "/operations";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println(response);
                iconsuccess.setBackgroundResource(R.drawable.i6);
                reqresponse = true;
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                reqresponse = false;
                iconsuccess.setBackgroundResource(R.drawable.i8);
            }
        });

// Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
