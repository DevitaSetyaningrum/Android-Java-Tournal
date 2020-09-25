package com.laacompany.travelplanner.PickerDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.laacompany.travelplanner.R;

public class FilterDialog extends AppCompatDialogFragment {
    private FilterDialog.FilterDialogListener listener;

    private EditText mEDTRating, mEDTVisitor;
    private Spinner mSpinnerCountry;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    //    LayoutInflater inflater = getActivity().getLayoutInflater();
   //     View view = inflater.inflate(R.layout.dialog_filter, null);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter, null);
        TextView textView = new TextView(getContext());
        textView.setText("Filter");
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(Color.parseColor("#6ec6ff"));
        textView.setTextColor(Color.WHITE);

        builder.setView(view)
                .setCustomTitle(textView)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String country = (String) mSpinnerCountry.getSelectedItem();
                        double rating;
                        int visitor;
                        if ( mEDTRating.getText().toString().isEmpty()) {
                            rating = 0;
                        } else {
                            rating  = Double.parseDouble(mEDTRating.getText().toString());
                        }

                        if ( mEDTVisitor.getText().toString().isEmpty()) {
                            visitor = 0;
                        } else {
                            visitor  = Integer.parseInt(mEDTVisitor.getText().toString());
                        }
                         listener.applyFilter(country,rating,visitor);
                    }
                });

      mEDTRating = view.findViewById(R.id.id_dialog_filter_edt_rating);
      mEDTVisitor = view.findViewById(R.id.id_dialog_filter_edt_visitor);
      mSpinnerCountry = view.findViewById(R.id.id_dialog_filter_spinner_country);
      String[] arrayOfCountrySpinner = new String[] {
                "None","Indonesia", "Malaysia", "Singapore", "Australia", "Brunei", "Thailand", "Laos"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arrayOfCountrySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCountry.setAdapter(adapter);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (FilterDialog.FilterDialogListener) context;
        } catch (Exception e){
            throw new ClassCastException(context.toString() + "must implement DurationDialogListener");
        }


    }

    public interface FilterDialogListener{
        void applyFilter(String country, double rating, int visitor);
    }

}
