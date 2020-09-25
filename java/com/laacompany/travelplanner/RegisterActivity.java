package com.laacompany.travelplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.Handle.VolleyHandle;
import com.laacompany.travelplanner.ModelClass.User;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements VolleyHandle.VolleyResponseListener {

    public TextInputLayout mTILEmail, mTILPassword, mTILConfirmPassword, mTILPhone, mTILDate, mTILTerms;
    public Button mBTNRegister, mBTNDate;
    private EditText mEDTEmail,mEDTPassword,mEDTConfirmPassword, mEDTPhone;

    public RadioButton mRBMale;
    public CheckBox mCBCheckBox;
    public DatePickerDialog dpdialog;
    public SimpleDateFormat dateFormatter;
    private ProgressBar mPBLoading;

    public Date pickedDate = new Date();
    private boolean hasShown;

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext,RegisterActivity.class);
    }

    void initView(){
        mTILEmail = findViewById(R.id.id_activity_register_iptlayout_username);
        mTILPassword = findViewById(R.id.id_activity_register_iptlayout_password);
        mTILConfirmPassword = findViewById(R.id.id_activity_register_iptlayout_confirm_password);
        mTILPhone = findViewById(R.id.id_activity_register_iptlayout_phone);
        mTILDate = findViewById(R.id.id_activity_register_til_date);
        mTILTerms = findViewById(R.id.id_activity_register_til_terms);

        mEDTEmail = findViewById(R.id.id_activity_register_edt_username);
        mEDTPassword = findViewById(R.id.id_activity_register_edt_password);
        mEDTConfirmPassword = findViewById(R.id.id_activity_register_edt_confirmation_password);
        mEDTPhone = findViewById(R.id.id_activity_register_edt_phone);
        mBTNDate = findViewById(R.id.id_activity_register_btn_date);
        mBTNRegister = findViewById(R.id.id_activity_register_btn_register);
        mRBMale = findViewById(R.id.id_activity_register_rb_Male);
        mCBCheckBox = findViewById(R.id.id_activity_register_cb_cbox);

        mPBLoading = findViewById(R.id.id_activity_register_pb_loading);
        mRBMale.toggle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VolleyHandle.listener = this;
    }

    private void checkRegister() {
        boolean registerValid = true;

        String email = mEDTEmail.getText().toString().trim();
        String phone = mEDTPhone.getText().toString().trim();
        String password = mEDTPassword.getText().toString().trim();
        String cpassword = mEDTConfirmPassword.getText().toString().trim();
        String gender = (mRBMale.isChecked())? "Male" : "Female";

        //EMAIL CHECK
        if (email.isEmpty()){
            mTILEmail.setError("Email must not be empty");
            registerValid = false;
        } else {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (email.matches(emailPattern)){
                mTILEmail.setError(null);
            } else {
                mTILEmail.setError("Email format not supported");
                registerValid = false;
            }
        }


        //PASSWORD CHECK
        if (password.length() < 6){
            mTILPassword.setError("Password must contain at least 6 character");
            registerValid = false;
        } else {
            mTILPassword.setError(null);
            if (cpassword.equals(password)){
                mTILConfirmPassword.setError(null);
            } else {
                mTILConfirmPassword.setError("Password must contain at least 6 character");
                registerValid = false;
            }
        }

        //PHONE NUMBER CHECK
        boolean flag = true;
        int len = phone.length();
        if (len>6){
            for (int i = 0; i < len; i++){
                if (!(mEDTPhone.getText().toString().charAt(i)>='0' &&
                        mEDTPhone.getText().toString().charAt(i)<='9'))
                    flag = false;
            }
            if (flag) mTILPhone.setError(null);
            else{
                mTILPhone.setError("Phone number must contains digit only");
                registerValid = false;
            }
        }
        else {
            mTILPhone.setError("Phone number must between 10 and 12 character");
            registerValid = false;
        }

//        DOB CHECK
        if (mBTNDate.getText().equals("Select Date")){
            mTILDate.setError("Please select date");
            mTILDate.setVisibility(View.VISIBLE);
            registerValid = false;
        } else {
            mTILDate.setVisibility(View.GONE);
            mTILDate.setError(null);
        }

        //CHECKBOX CHECK

        if(!mCBCheckBox.isChecked()){
            mTILTerms.setError("Terms and Condition must be checked");
            registerValid = false;
        } else {
            mTILTerms.setError(null);
        }

        if (!registerValid) return;;

        mPBLoading.setVisibility(View.VISIBLE);
        mBTNRegister.setEnabled(false);
        Handle.mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful()) {
                    final String userId;
                    userId = Handle.mAuth.getCurrentUser().getUid();
//                    Handle.mAuth.getCurrentUser().updatePhoneNumber(phone);

                    VolleyHandle.addNewUser(userId, gender, Handle.convDateToLong(pickedDate), phone);
                    Handle.sCurrentUser = new User(userId, email,phone,gender,pickedDate);
                } else {
                    //Toast.makeText(SignupActivity.this, "User Failed to Register", Toast.LENGTH_SHORT).show();
                    mPBLoading.setVisibility(View.GONE);
                    mBTNRegister.setEnabled(true);
                    if ( task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(),"You are already registered",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    public void clickDate(View v) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        dateFormatter = new SimpleDateFormat("EEEE, dd MMM YYYY", Locale.US);

        dpdialog = new DatePickerDialog(RegisterActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year,monthOfYear,dayOfMonth);
            pickedDate = newDate.getTime();
            mBTNDate.setText(dateFormatter.format(newDate.getTime()));
        }, mYear, mMonth, mDay);

        dpdialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpdialog.show();
    }

    public void clickRegister(View view) {
        hasShown = false;
        checkRegister();
    }

    @Override
    public void onResponse(String functionResp) {
        if (functionResp.equals("addNewUser")){
            Toast.makeText(this, "User Register Success", Toast.LENGTH_SHORT).show();
            Handle.sPlanMasters.clear();
            finish();
        }
    }

    @Override
    public void onError(String functionResp) {
        if (!hasShown){
            mPBLoading.setVisibility(View.GONE);
            mBTNRegister.setEnabled(true);
            Toast.makeText(this, R.string.internet_error, Toast.LENGTH_SHORT).show();
            if (Handle.mAuth.getCurrentUser() != null){
                Handle.mAuth.getCurrentUser().delete();
            }
            hasShown = true;

        }
    }
}
