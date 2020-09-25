package com.laacompany.travelplanner.PickerDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.laacompany.travelplanner.R;

public class DialogDuration extends AppCompatDialogFragment {

    private TimePicker mTimePicker;
    private DurationDialogListener listener;
    private int minutes;

    public DialogDuration(int minutes){
        this.minutes = minutes;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_duration, null);

        builder.setView(view)
                .setTitle("Pick event duration")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int minutes = mTimePicker.getHour()*60+mTimePicker.getMinute();
                        listener.applyTime(minutes);
                    }
                });

        mTimePicker = view.findViewById(R.id.id_dialog_duration_time_picker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setHour(minutes/60);
        mTimePicker.setMinute(minutes%60);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DurationDialogListener) context;
        } catch (Exception e){
            throw new ClassCastException(context.toString() + "must implement DurationDialogListener");
        }


    }

    public interface DurationDialogListener{
        void applyTime(int minutes);
    }
}
