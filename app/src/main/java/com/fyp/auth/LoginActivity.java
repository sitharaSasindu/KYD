package com.fyp.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.kyd.QRActivity;
import com.fyp.kyd.R;
import com.fyp.qr.DetailsViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static shadow.com.google.common.base.Strings.isNullOrEmpty;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private Encryption encryptor;
    private Decryption decryptor;
    private static final String KYD_ALIAS = "kydalias";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;
    private java.security.KeyStore keyStore;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    private static final String STORE_KEY_1 = "STORE_KEY_1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        AndroidKeystore.init(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Users");

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    try {
                        try {
                            login();
                        } catch (NoSuchProviderException e) {
                            e.printStackTrace();
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        }
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void login() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Log.d(TAG, "Login");

//        if (!validate()) {
//            onLoginFailed();
//            return;
//        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");

                            mFirebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get user value
                                            UserDetails user = dataSnapshot.getValue(UserDetails.class);
                                            String encryptedPrivateKey = user.getEncryptedPvtKey();
                                            String encryptedPrivateKeyString = PassEncryption.decrypt(encryptedPrivateKey, password);
                                            Boolean statusUser = Boolean.parseBoolean(user.getUserStatus());
                                            Toast.makeText(LoginActivity.this, user.getUserStatus(),
                                                    Toast.LENGTH_SHORT).show();
                                            if (isNullOrEmpty(encryptedPrivateKeyString)) {
                                                new android.os.Handler().postDelayed(
                                                        new Runnable() {
                                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                                onLoginFailed();

                                                                _loginButton.setEnabled(true);
                                                            }
                                                        }, 3000);

                                            } else {
                                                if (statusUser) {
                                                    new android.os.Handler().postDelayed(
                                                            new Runnable() {
                                                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                                public void run() {
                                                                    onLoginSuccess();
                                                                    progressDialog.dismiss();
                                                                    _loginButton.setEnabled(true);
                                                                }
                                                            }, 3000);
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "User Profile is not activated",
                                                            Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                    _loginButton.setEnabled(true);
                                                }
                                            }


                                            System.out.println(user.getEncryptedPvtKey());

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                             progressDialog.dismiss();
                            onLoginFailed();
                        }

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onLoginSuccess() {
//        Toast.makeText(getBaseContext(), "Login Success", Toast.LENGTH_LONG).show();
        Intent i = new Intent(LoginActivity.this, QRActivity.class);
        startActivity(i);
        finish();
        _loginButton.setEnabled(true);
//        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
