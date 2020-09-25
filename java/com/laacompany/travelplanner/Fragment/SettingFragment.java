package com.laacompany.travelplanner.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.LoginActivity;
import com.laacompany.travelplanner.R;

public class SettingFragment extends Fragment implements View.OnClickListener{

    private TextView mTVEmail, mTVPhone;
    private Button mBTNLogout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container,false);

        mTVEmail = v.findViewById(R.id.id_fragment_setting_tv_email);
        mTVPhone = v.findViewById(R.id.id_fragment_setting_tv_phone);



        mBTNLogout = v.findViewById(R.id.id_fragment_setting_btn_logout);
        mBTNLogout.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        mTVEmail.setText(Handle.sCurrentUser.getEmail());
        mTVPhone.setText(Handle.sCurrentUser.getPhone());
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_fragment_setting_btn_logout){
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Sign Out");
            alertDialog.setMessage("Are you sure you want to sign out?");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sign Out",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Handle.mAuth.signOut();
                            startActivity(LoginActivity.newIntent(getActivity()));
                        }
                    });
            alertDialog.show();

        }
    }
}
