package com.fyp.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.kyd.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.fyp.kyd.SplashActivity;
import com.fyp.rsa.RSA;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Encryption encryptor;
    private Decryption decryptor;
    private static final String KYD_ALIAS = "kydalias";

    private static String publicKey = "";
    private static String privateKey = "";

    private static final String TAG = "SignupActivity";
    private DatabaseReference mFirebaseDatabase=null;
    private FirebaseDatabase mFirebaseInstance=null;
    public static String encrypted = null;
    private String userId;
    private static String uuidd;
    public static String role = null;
public String encryptedPrivateKey;
public static String newuserStatus="false";
    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private static final String STORE_KEY_1 = "STORE_KEY_1";

    private static FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private EditText userText;
    private FirebaseAuth auth = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        AndroidKeystore.init(getApplicationContext());
        auth = FirebaseAuth.getInstance();

        encryptor = new Encryption();

        try {
            decryptor = new Decryption();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner_array);
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Role");
        categories.add("Manufacturer");
        categories.add("Distributor");
        categories.add("Retailer");
        categories.add("Customer");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    private void createUser(final String name, final String email, final String mobile, final String role, final String publicKey, final String encryptedPrivateKey, final String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

//                        System.out.println(auth.getCurrentUser().getUid());
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            UserDetails user = new UserDetails(name, email, mobile, role, publicKey, encryptedPrivateKey, newuserStatus);

                            mFirebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    public void signup() {
        if (isNetworkConnected()) {
            Log.d(TAG, "Signup");

            if (!validate()) {
                onSignupFailed();
                return;
            }

            _signupButton.setEnabled(false);

            final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            String name = _nameText.getText().toString();
            String email = _emailText.getText().toString();
            String mobile = _mobileText.getText().toString();
            String password = _passwordText.getText().toString();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setKeys(password);
            }

            if (TextUtils.isEmpty(userId)) {
                createUser(name, email, mobile, role, publicKey, encrypted, password);
            }

            new Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            onSignupSuccess();
                            // onSignupFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }else{
            Toast.makeText(SignUpActivity.this, "No Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);finish();
    }

    public void onSignupFailed() {
//        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                UserDetails user = dataSnapshot.getValue(UserDetails.class);
//
//                Log.d(TAG, "User name: " + user.getUserStatus() + ", email " + user.getEncryptedPvtKey());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
        Toast.makeText(getBaseContext(), "SignUp failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String roleCheck = role;


            if(roleCheck.equals("Select Role")){
                Toast.makeText(getApplicationContext(), "Select a Role", Toast.LENGTH_LONG).show();
                valid = false;
            }


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setKeys(String signupPassword){

        try {
            String KEY_NAME = "KYD";
            AndroidKeystore c = new AndroidKeystore(KEY_NAME);

            Map<String, Object> keyMap = RSA.initKey();
            publicKey = RSA.getPublicKey(keyMap);
            privateKey = RSA.getPrivateKey(keyMap);
            System.out.println(privateKey);
            c.save("PvtKey", privateKey);
            c.save("PubKey", publicKey);

            String secretKey = signupPassword; //getting signup password of the user
            String privateKeyString = privateKey; //get user rsa private key genarated
             encrypted = PassEncryption.encrypt(privateKeyString, secretKey) ;//encrypt the private key using user password


//            System.out.println(encryptedPrivateKeyString);
//            encrypted = c.encrypt(encryptedPrivateKeyString); // returns base 64 data: 'BASE64_DATA,BASE64_IV'
//            c.save(KEY_NAME, encrypted);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String item = parent.getItemAtPosition(pos).toString();
        // Showing selected spinner item
        if(item.equals("Select Role")){
//            Toast.makeText(parent.getContext(), "Select Your Role ", Toast.LENGTH_LONG).show();
        }else {
//            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

        role = item;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
