package com.laacompany.travelplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.Handle.VolleyHandle;

public class LoginActivity extends AppCompatActivity implements VolleyHandle.VolleyResponseListener {


    private TextInputEditText mEDTUsername, mEDTPassword;
    private TextInputLayout mLAYUsername, mLAYPassword;
    private ProgressBar mPBLoading;
    private Button mBTNLogin, mBTNRegister;
    private boolean isUserLoad, isPlanMastersLoad, hasShown;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext,LoginActivity.class);
    }

    void initView(){
        mLAYUsername =findViewById(R.id.id_activity_login_iptlayout_username);
        mLAYPassword =findViewById(R.id.id_activity_login_iptlayout_password);
        mEDTUsername = findViewById(R.id.id_activity_login_edt_username);
        mEDTPassword = findViewById(R.id.id_activity_login_edt_password);
        mPBLoading = findViewById(R.id.id_activity_login_PB_loading);
        mBTNLogin = findViewById(R.id.id_activity_login_btn_login);
        mBTNRegister = findViewById(R.id.id_activity_login_btn_register);
        signInButton = findViewById(R.id.sign_in_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

                signInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                    }
                });

        isUserLoad = isPlanMastersLoad = false;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            finish();
        }
        super.onStart();
        if (Handle.mAuth.getCurrentUser() != null){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VolleyHandle.listener = this;
    }

    private void validateLogin() {

        String email = mEDTUsername.getText().toString().trim();
        String password = mEDTPassword.getText().toString().trim();

        boolean login = true;

        if (email.isEmpty()){
            mLAYUsername.setError("Username must be filled");
            login = false;
        } else {
            mLAYUsername.setError(null);
        }

        if (password.isEmpty()){
            mLAYPassword.setError("Password must be filled");
            login = false;
        } else {
            mLAYPassword.setError(null);
        }
        if (!login) return;

        mPBLoading.setVisibility(View.VISIBLE);
        mBTNLogin.setEnabled(false);
        mBTNRegister.setEnabled(false);
        Handle.mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    VolleyHandle.getCurrentUser(Handle.mAuth.getCurrentUser().getUid(), true);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    mPBLoading.setVisibility(View.GONE);
                    mBTNLogin.setEnabled(true);
                    mBTNRegister.setEnabled(true);
                }
            }
        });

    }

    public void clickLogin(View view) {
        hasShown = false;
        validateLogin();
    }

    public void clickRegister(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
    }

    @Override
    public void onBackPressed() {
        Handle.isExit = true;
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Exit Application");
        alertDialog.setMessage("Are you sure you want to quit?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Quit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onResponse(String functionResp) {
        if (functionResp.equals("getCurrentUser")){
            isUserLoad = true;
        } else if (functionResp.equals("getAllPlanMasters")){
            isPlanMastersLoad = true;
        }
        if (isUserLoad && isPlanMastersLoad) {
            finish();
        }
    }

    @Override
    public void onError(String functionResp) {
        if (!hasShown){
            if (functionResp.equals("getCurrentUserNotFound")){
                if (Handle.mAuth.getCurrentUser() != null){
                    Handle.mAuth.getCurrentUser().delete();
                }
                Toast.makeText(this, "User not found, Please register", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, R.string.internet_error, Toast.LENGTH_SHORT).show();
            }
            mPBLoading.setVisibility(View.GONE);
            mBTNLogin.setEnabled(true);
            mBTNRegister.setEnabled(true);
            hasShown = true;
        }

    }
}




