package com.fyp.qr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.auth.AndroidKeystore;
import com.fyp.auth.LoginActivity;
import com.fyp.auth.PassEncryption;
import com.fyp.auth.UserDetails;
import com.fyp.kyd.HistoryActivity;
import com.fyp.kyd.R;
import com.fyp.kyd.SigningActivity;
import com.fyp.kyd.SplashActivity;
import com.fyp.kyd.VerificationActivity;
import com.fyp.rsa.RSA;
import com.fyp.stellar.Stellar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.acl.Owner;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static shadow.com.google.common.base.Strings.isNullOrEmpty;


public class DetailsViewActivity extends AppCompatActivity {
    private static final String TAG = DetailsViewActivity.class.getSimpleName();

    //    Button verify =  findViewById(R.id.verify);
    // url to search barcode
    private static final String URL = "http://34.69.253.149:8080/api/query/";

   static String signedPackageID, signedProductID, signedProductName, signedMnufacturer, signedManufactureDate, signedExpireDate, signedQuantity, signedOwner, timestamp, signedStellarHash, signedStatus, signedTemparature, signedOwnerid, signedPackagePosition;
    private TextView txtPackage, txtProduct, txtOwner, txtManufacturer, txtQty, txtManudate, txtExpire, txtError;
    private ImageView imgPoster;
    private Button btnAccept;
    private ProgressBar progressBar;

    public String getSignedOwner() {
        return signedOwner;
    }

    public String getSignedOwnerid() {
        return signedOwnerid;
    }

    private DetailsView detailsView;

    public void setSignedOwner(String signedOwner) {
        this.signedOwner = signedOwner;
    }

    public void setSignedOwnerid(String signedOwnerid) {
        this.signedOwnerid = signedOwnerid;
    }

    Package drugPackage = null;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;

    public static String PvtKey = null;
    public static String PubKey = null;

    String myLog = "myLog";

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    Stellar stellar = new Stellar();
    FrameLayout progressBarHolder;
    RequestQueue queue;

    static String ipackageID, iproductId, iproducName, itemparature, iquantity, iowner, imanufacturer, iexpireDate, imanufactureDate, istatus, istellarHash, ipackagePosition, iownerid, itimestamp = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        queue = Volley.newRequestQueue(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Users");

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

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
//            finish();
        }

        findViewById(R.id.btn_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAccept.setEnabled(true);
                popUpEditText();
            }
        });
        // search the barcode
        if(isNetworkConnected()){
            searchBarcode(barcode);
        } else {
            Toast.makeText(DetailsViewActivity.this, "No Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void searchBarcode(String barcode) {

        System.out.println("======================");
        System.out.println(PassEncryption.encrypt("PKG2", "kydqr"));
        System.out.println(PassEncryption.encrypt("PKG1", "kydqr"));
        System.out.println("======================");
        String barcodeDecoded = PassEncryption.decrypt(barcode, "kydqr");
        System.out.println(barcodeDecoded);


        // making volley's json request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL + barcodeDecoded, null,
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.button, menu);
        return true;
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
            drugPackage = new Gson().fromJson(replaced, Package.class);
            if (drugPackage != null) {
                ipackageID = drugPackage.getPackageID();
                iproducName = drugPackage.getProductName();
                iproductId = drugPackage.getProductID();
                iowner = drugPackage.getOwner2();
                iexpireDate = drugPackage.getExpireDate();
                imanufactureDate = drugPackage.getManufacturedDate();
                imanufacturer = drugPackage.getManufacturer();
                itemparature = drugPackage.getTemperature();
                iquantity = drugPackage.getQuantity();
                istatus = drugPackage.getStatus();
                istellarHash = drugPackage.getStellarHash();
                iownerid = drugPackage.getOwnerId();
                ipackagePosition = drugPackage.getPackagePosition();
                itimestamp = drugPackage.getTimestamp();

                txtPackage.setText(drugPackage.getPackageID());
                txtOwner.setText(drugPackage.getOwner());
                txtProduct.setText(drugPackage.getProductName());
                txtManufacturer.setText(drugPackage.getManufacturer());
                txtQty.setText(drugPackage.getQuantity());
                txtExpire.setText(drugPackage.getExpireDate());
                txtManudate.setText(drugPackage.getManufacturedDate());

                if (drugPackage.getPackagePosition().equals("0")) {
                    imgPoster.setBackgroundResource(R.drawable.a2);
                } else if (drugPackage.getPackagePosition().equals("1")) {
                    imgPoster.setBackgroundResource(R.drawable.a5);
                } else if (drugPackage.getPackagePosition().equals("2")) {
                    imgPoster.setBackgroundResource(R.drawable.a8);
                } else if (drugPackage.getPackagePosition().equals("3")) {
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
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            verify();
        }

        if (id == R.id.history) {
            showHistory();
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
        String OwnerID;
        String Timestamp;
        String PackagePosition;

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

        public String getOwner2() {
            return Owner;
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

        public String getOwnerId() {
            return OwnerID;
        }

        public String getPackagePosition() {
            return PackagePosition;
        }

        public String getTimestamp() {
            return Timestamp;
        }
    }


    private void popUpEditText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new MyTask().execute();
                final String password = input.getText().toString();

                FirebaseAuth.AuthStateListener authListener
                        = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        //If user is not signed in getCurrentUser method returns null
                        if (user != null) {
                            mFirebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get user value
                                            UserDetails user = dataSnapshot.getValue(UserDetails.class);
                                            String encryptedPrivateKey = user.getEncryptedPvtKey();

                                            setSignedOwner(user.getName());
                                            setSignedOwnerid(user.getPublicKey());
                                            System.out.println(user.getName());
                                            System.out.println(user.getPublicKey());

                                            String encryptedPrivateKeyString = PassEncryption.decrypt(encryptedPrivateKey, password);
//                                Toast.makeText(LoginActivity.this, user.getUserStatus(),
//                                        Toast.LENGTH_SHORT).show();
                                            if (isNullOrEmpty(encryptedPrivateKeyString)) {
                                                new android.os.Handler().postDelayed(
                                                        new Runnable() {
                                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Incorrect Password.", Toast.LENGTH_LONG).show();
                                                            }
                                                        }, 2000);

                                            } else {
                                                new android.os.Handler().postDelayed(
                                                        new Runnable() {
                                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                            public void run() {
                                                                DetailsViewActivity d = new DetailsViewActivity();
                                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                                                    try {
                                                                        d.HashData();
                                                                    } catch (CertificateException e) {
                                                                        e.printStackTrace();
                                                                    } catch (NoSuchAlgorithmException e) {
                                                                        e.printStackTrace();
                                                                    } catch (KeyStoreException e) {
                                                                        e.printStackTrace();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    } catch (NoSuchProviderException e) {
                                                                        e.printStackTrace();
                                                                    } catch (InvalidAlgorithmParameterException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                                startActivity(new Intent(DetailsViewActivity.this, SigningActivity.class));
                                                                finish();
                                                            }
                                                        }, 3000);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                        else {
//                            Toast.makeText(getApplicationContext(), "User not sined in.", Toast.LENGTH_LONG).show();
                        }

                    }
                };
                auth.addAuthStateListener(authListener);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void HashData() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException {
//
//        FirebaseAuth.AuthStateListener authListener2
//                = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                //If user is not signed in getCurrentUser method returns null
//                if (user != null) {
//                    mFirebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
//                            new ValueEventListener() {
//                                @RequiresApi(api = Build.VERSION_CODES.O)
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    // Get user value
//                                    UserDetails user = dataSnapshot.getValue(UserDetails.class);
//
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                }
//                else {
////                            Toast.makeText(getApplicationContext(), "User not sined in.", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        };
//        auth.addAuthStateListener(authListener2);
        System.out.println("ggggggggggggg");
        System.out.println(signedOwnerid);
        System.out.println("ggggggggggggg");
        System.out.println(getSignedOwnerid());

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String date = df.format(Calendar.getInstance().getTime());
        System.out.println("---------------------------------------------");
        signedExpireDate = iexpireDate;
        signedManufactureDate = imanufactureDate;
        signedMnufacturer = imanufacturer;
        signedStellarHash = istellarHash;
        signedStatus = istatus;
        signedQuantity = iquantity;
        signedProductName = iproducName;
        signedPackageID = ipackageID;
        signedProductID = iproductId;
        signedPackagePosition = ipackagePosition;
        timestamp = date;

        String jsonString = null;
        try {
            jsonString = new JSONObject()
                    .put("PackageID", signedPackageID)
                    .put("ProductID", signedProductID)
                    .put("ProductName", signedProductName)
                    .put("Owner", getSignedOwner())
                    .put("Manufacturer", signedMnufacturer)
                    .put("ManufactureDate", signedManufactureDate)
                    .put("Status", signedStatus)
                    .put("PackagePosition", signedStatus)
                    .put("OwnerId", getSignedOwnerid())
                    .put("Status", signedStatus)
                    .put("Temperature", signedTemparature)
                    .put("Timestamp", timestamp)
                    .put("Quantity", signedQuantity).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonString);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonString.getBytes(StandardCharsets.UTF_8));
        String hex = bytesToHex(hash);
        String txnHash = null;

        try {
            System.out.println("-------------------------------------------------------------------------------------------");
//            stellar.CheckBalance();
            stellar.doManageData("kyd", hex);
            txnHash = stellar.getTransactionhashStellar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        changeOwner(signedPackageID, signedOwner, signedOwnerid, signedPackagePosition, timestamp, txnHash);
    }


    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < 5; i++) {
                    Log.d(myLog, "Emulating some task.. Step " + i);
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }


    public void changeOwner(String PackageID, final String NewOwner, final String NewOwnerId, final String PackagePosition, final String Timestamp, final String StellarHash) {
        int myNum = 0;

        try {
            myNum = Integer.parseInt(PackagePosition);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        int position = myNum + 1;
        String tag = "json_obj_req";
        final String NewPackagePosition = String.valueOf(position);

        String requestUrl = "http://34.69.253.149:8080/api/changeowner/" + PackageID;

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                requestUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            public String getBodyContentType() {
//                Map<String, String> pars = new HashMap<String, String>();
//                pars.put("Content-Type", "application/x-www-form-urlencoded");
                //return pars;
                return "application/x-www-form-urlencoded";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner", NewOwner);
                params.put("ownerid", NewOwnerId);
                params.put("packgepositin", NewPackagePosition);
                params.put("timestamp", Timestamp);
                params.put("stellrhash", StellarHash);

                return params;
            }
        };
// Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag);
    }

    public void verify() {
        Intent i = new Intent(DetailsViewActivity.this, VerificationActivity.class);
        i.putExtra("stellerash", istellarHash);
        startActivity(i);
    }


    public void showHistory() {
        Intent i = new Intent(DetailsViewActivity.this, HistoryActivity.class);
        i.putExtra("pkgid", ipackageID);
        startActivity(i);
    }
}
