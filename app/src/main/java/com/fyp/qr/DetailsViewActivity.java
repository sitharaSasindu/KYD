package com.fyp.qr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fyp.kyd.MainActivity;
import com.fyp.kyd.QRActivity;
import com.fyp.kyd.R;
import com.fyp.kyd.SigningActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import static java.sql.DriverManager.println;


public class DetailsViewActivity extends AppCompatActivity {
    private static final String TAG = DetailsViewActivity.class.getSimpleName();

    // url to search barcode
    private static final String URL = "http://104.197.159.148:8080/api/query/history/";

    private TextView txtPackage, txtProduct, txtOwner, txtManufacturer, txtQty, txtManudate,txtExpire, txtError;
    private ImageView imgPoster;
    private Button btnAccept;
    private ProgressBar progressBar;
    private DetailsView detailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtPackage = findViewById(R.id.name);
        txtOwner = findViewById(R.id.owner);
        txtProduct = findViewById(R.id.product);
        txtManudate = findViewById(R.id.manu_date);
        txtQty = findViewById(R.id.qty);
        txtExpire = findViewById(R.id.expiration);
        imgPoster = findViewById(R.id.poster);
        imgPoster = findViewById(R.id.poster);

        txtManufacturer = findViewById(R.id.manufacturer);
        btnAccept = findViewById(R.id.btn_buy);
        txtError = findViewById(R.id.txt_error);
        detailsView = findViewById(R.id.layout_ticket);
        progressBar = findViewById(R.id.progressBar);

        String barcode = getIntent().getStringExtra("code");

        // close the activity in case of empty barcode
        if (TextUtils.isEmpty(barcode)) {
            Toast.makeText(getApplicationContext(), "QR is empty!", Toast.LENGTH_LONG).show();
            finish();
        }

        findViewById(R.id.btn_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAccept.setEnabled(true);
                startActivity(new Intent(DetailsViewActivity.this, SigningActivity.class));
            }
        });
        // search the barcode
        searchBarcode(barcode);


    }

    private void searchBarcode(String barcode) {
        // making volley's json request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL + barcode, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Ticket response: " + response.toString());

                        // check for success status
                        if (!response.has("error")) {
                            // received drugPackage response
                            renderPackage(response);
                        } else {
                            // no drugPackage found
                            showNoPackage();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                showNoPackage();
            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showNoPackage() {
        txtError.setVisibility(View.VISIBLE);
        detailsView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Rendering drugPackage details on the ticket
     */
    private void renderPackage(JSONObject response) {
        try {
            String value = (String) response.get("response");
            String replaced = value.replaceAll("\\[", "").replaceAll("\\]", "");

            System.out.println(replaced);


            // converting json to drugPackage object
            Package drugPackage = new Gson().fromJson(replaced, Package.class);
            if (drugPackage != null) {
                txtPackage.setText(drugPackage.getPackageID());
                txtOwner.setText(drugPackage.getOwner());
                txtProduct.setText(drugPackage.getProductName());
                txtManufacturer.setText(drugPackage.getManufacturer());
                txtQty.setText(drugPackage.getQuantity());
                txtExpire.setText(drugPackage.getExpireDate());
                txtManudate.setText(drugPackage.getManufacturedDate());

                if (drugPackage.getStatus().equals("0")) {
                    imgPoster.setBackgroundResource(R.drawable.a1);
                } else if (drugPackage.getStatus().equals("1")) {
                    imgPoster.setBackgroundResource(R.drawable.a2);
                } else if (drugPackage.getStatus().equals("2")) {
                    imgPoster.setBackgroundResource(R.drawable.a4);
                } else if (drugPackage.getStatus().equals("2")) {
                    imgPoster.setBackgroundResource(R.drawable.a8);
                } else if (drugPackage.getStatus().equals("1")) {
                    imgPoster.setBackgroundResource(R.drawable.a7);
                }

                btnAccept.setText("Accept");
                btnAccept.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

                detailsView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                // drugPackage not found
                btnAccept.setEnabled(false);
                btnAccept.setText("Illegal Drug Found");
                btnAccept.setTextColor(ContextCompat.getColor(this, R.color.red));
                showNoPackage();
            }
        } catch (JsonSyntaxException e) {
            btnAccept.setEnabled(false);
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            btnAccept.setText("Illegal Drug Found");
            btnAccept.setTextColor(ContextCompat.getColor(this, R.color.red));
            showNoPackage();
            Toast.makeText(getApplicationContext(), "Error occurred.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // exception
            btnAccept.setEnabled(false);
            btnAccept.setText("Illegal Drug Found");
            btnAccept.setTextColor(ContextCompat.getColor(this, R.color.red));
            showNoPackage();
            Toast.makeText(getApplicationContext(), "Error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class Package {
        String PackageID;
        String ProductID;
        String ProductName;
        String ManufacturedDate;
        String ExpireDate;
        String Manufacturer;
        String Temperature;
        String Quantity;
        String Owner;
        String Status;
        String StellarHash;

        @SerializedName("released")
        boolean isReleased;

        public String getPackageID() {
            return PackageID;
        }

        public String getProductID() {
            return ProductID;
        }

        public String getProductName() {
            return ProductName;
        }

        public String getManufacturedDate() {

            String result = ManufacturedDate.split("T")[0];

            return result;
        }

        public String getQuantity() {
            return Quantity;
        }

        public String getOwner() {
            return "Current Owner: " + Owner;
        }

        public String getStatus() {
            return Status;
        }

        public String getStellarHash() {
            return StellarHash;
        }

        public String getExpireDate() {
            return ExpireDate;
        }

        public String getManufacturer() {
            return Manufacturer;
        }

        public String getTemperature() {
            return Temperature;
        }

        public boolean isReleased() {
            return isReleased;
        }
    }
}
