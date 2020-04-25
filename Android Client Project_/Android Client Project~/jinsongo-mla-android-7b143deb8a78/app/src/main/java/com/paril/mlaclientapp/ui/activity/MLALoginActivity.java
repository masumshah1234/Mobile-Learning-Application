package com.paril.mlaclientapp.ui.activity;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.security.InvalidAlgorithmParameterException;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.util.CommonUtils;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.security.auth.x500.X500Principal;

import retrofit2.Call;
import retrofit2.Response;


public class MLALoginActivity extends BaseActivity {

    EditText txtUserName;
    EditText txtPassword;
    Button btnLogin;
    MLARegisterUsers register;
    private ProgressDialog progressDialog;
    private PrefsManager prefsManager;
    PublicKey publicKey;
    PrivateKey privateKey;
    KeyStore keyStore;
    MLAAdminDetails adminDetails = new MLAAdminDetails();
    MLAInstructorDetails instructorDetails = new MLAInstructorDetails();
    MLAStudentDetails studentDetails = new MLAStudentDetails();
    String user;
    String emailId;



    public void showProgressDialog(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(this, getString(R.string.app_name), message, true, false);

        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
    }

    void loadingUserInformation() {
        register.userId = prefsManager.getStringData("userId");
        register.userName = prefsManager.getStringData("userName");
        register.userType = prefsManager.getStringData("userType");
    }

    public void showSnackBar(String message, View view) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    void savingUserInformation() {
        prefsManager.saveData("userId", register.userId);
        prefsManager.saveData("userName", register.userName);
        prefsManager.saveData("userType", register.userType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Sign In");
        prefsManager=new PrefsManager(this);
        register = new MLARegisterUsers();

        loadingUserInformation();
        if (register.userType != "" && register.userType != null) {
            Intent mlaActivity = new Intent();
            mlaActivity.setClass(MLALoginActivity.this, MLAHomeActivity.class);
            mlaActivity.putExtra("userId", register.userId);
            mlaActivity.putExtra("userName", register.userName);
            mlaActivity.putExtra("userType", register.userType);
            mlaActivity.putExtra("PrivateKey:",privateKey);
            mlaActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mlaActivity);
            finish();
        }

        btnLogin = (Button) findViewById(R.id.mla_login_btnLogin);
        txtUserName = (EditText) findViewById(R.id.mla_login_txtUserName);
        txtPassword = (EditText) findViewById(R.id.mla_login_txtPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (TextUtils.isEmpty(txtPassword.getText().toString()) || TextUtils.isEmpty(txtUserName.getText().toString())) {

                    showSnackBar(getString(R.string.enter_all_fields), findViewById(R.id.activity_main_coordinatorLayout));

                } else {
                    if (CommonUtils.checkInternetConnection(MLALoginActivity.this)) {
                        MLALoginAPI authentication = new MLALoginAPI(getApplicationContext());
                        authentication.execute(txtUserName.getText().toString(), txtPassword.getText().toString());
                    } else {
                        showSnackBar(getString(R.string.check_connection), findViewById(R.id.activity_main_coordinatorLayout));
                    }
                }
            }


        });

    }


    class MLALoginAPI extends AsyncTask<String, Void, MLARegisterUsers> {
        Context appContext;

        public MLALoginAPI (Context context) {
            appContext = context;
        }
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void generatekey() {
            try {
                if (!keyStore.containsAlias((register.userName))) {
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 30);
                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(appContext)
                            .setAlias(register.userName)
                            .setSubject(new X500Principal("CN=" + register.userName))
                            .setSerialNumber(BigInteger.TEN)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                    kpg.initialize(spec);
                    KeyPair keyPair = kpg.generateKeyPair();
                    publicKey = keyPair.getPublic();
                    privateKey = keyPair.getPrivate();
                }
            }catch (KeyStoreException e){
                e.printStackTrace();
            }catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }catch (NoSuchProviderException e){
                e.printStackTrace();
            }catch (InvalidAlgorithmParameterException e){
                e.printStackTrace();
            }

        }


        @Override
        protected void onPreExecute() {
            showProgressDialog("Verifying Credentials...");
        }

        @Override
        protected void onPostExecute(MLARegisterUsers registerArg) {
            hideProgressDialog();
            register = registerArg;
            if (register.userType != null) {
                Intent mlaActivity = new Intent();
                mlaActivity.setClass(MLALoginActivity.this, MLAHomeActivity.class);
                mlaActivity.putExtra("userId", register.userId);
                mlaActivity.putExtra("userName", register.userName);
                mlaActivity.putExtra("userType", register.userType);
                mlaActivity.putExtra("privatekey",privateKey);
                savingUserInformation();
                mlaActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try{
                    keyStore=KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(null);

                }catch (KeyStoreException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }catch (CertificateException e){
                    e.printStackTrace();
                }
                try{
                    if(keyStore.containsAlias(register.userName)){
                        KeyStore.Entry entry = keyStore.getEntry(register.userName,null);
                        privateKey=((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
                        publicKey=keyStore.getCertificate(register.userName).getPublicKey();
                    }
                    else{
                        generatekey();
                    }
                }catch (KeyStoreException e){
                    e.printStackTrace();
                }catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }catch (UnrecoverableEntryException e) {
                    e.printStackTrace();
                }
                MLAPublicKeyAPI mlaPublicKeyAPI = new MLAPublicKeyAPI(getApplicationContext());
                mlaPublicKeyAPI.execute();


                startActivity(mlaActivity);
                finish();
            } else {
                showSnackBar("User Name/Password is incorrect. Please enter correct credentials.", findViewById(R.id.activity_main_coordinatorLayout));
            }
        }

        @Override
        protected MLARegisterUsers doInBackground(String... params) {
            MLARegisterUsers register = new MLARegisterUsers();
            Call<List<MLARegisterUsers>> callAuth = Api.getClient().authenticate(params[0], params[1]);
            try {
                Response<List<MLARegisterUsers>> respAuth = callAuth.execute();
                if (respAuth != null && respAuth.isSuccessful() & respAuth.body() != null && respAuth.body().size() > 0) {
                    register = respAuth.body().get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return register;

        }
    }
        private class MLAPublicKeyAPI extends AsyncTask<String, Void, MLARegisterUsers> {
            Context ctx;
            public MLAPublicKeyAPI(Context applicationContext) {
                ctx = applicationContext;
            }

            @Override
            protected void onPreExecute() {
                //showProgressDialog("Verifying Credentials...");
            }

            @Override
            protected void onPostExecute(MLARegisterUsers registerArg) {

            }


            @Override
            protected MLARegisterUsers doInBackground(String... strings) {
                //public key in RDS
                int id = Integer.parseInt(register.userId);
                if(register.userType != null  && register.userType.equals("admin") ) {

                    Call<MLAAdminDetails> postAdminKey = Api.getClient().addAdminPublicKey(publicKey.toString(),register.userName);
                    try {
                        postAdminKey.execute();
                    } catch (IOException e) {
                        e.printStackTrace();


                    }
                }else if(register.userType != null  && register.userType.equals("student") ) {

                    Call<MLAStudentDetails> postStudentKey = Api.getClient().addStudentPublicKey(publicKey.toString(),register.userName);
                    try {
                        postStudentKey.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Toast.makeText(MLALoginActivity.this, "error here", Toast.LENGTH_LONG).show();

                    }
                }else if(register.userType != null  && register.userType.equals("instructor") ) {

                    Call<MLAInstructorDetails> postInstructorKey = Api.getClient().addInstructorPublicKey(publicKey.toString(),register.userName);
                    try {
                        postInstructorKey.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Toast.makeText(MLALoginActivity.this, "error here", Toast.LENGTH_LONG).show();

                    }
                }

                return register;
            }
        }
    }


