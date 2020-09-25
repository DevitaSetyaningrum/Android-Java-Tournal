package com.laacompany.travelplanner.PickerDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.laacompany.travelplanner.R;

public class SortDialog extends AppCompatDialogFragment {
    private SortDialog.SortDialogListener listener;

    private Spinner mSpinnerCategory;
    private ToggleButton mToggleSortBy;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sort, null);
        TextView textView = new TextView(getContext());
        textView.setText("Select an option");
        textView.setTextSize(25);
        textView.setBackgroundColor(Color.parseColor("#6ec6ff"));
        textView.setTextColor(Color.WHITE);

        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = (String) mSpinnerCategory.getSelectedItem();
                        String toggleButton = mToggleSortBy.isChecked()?"Descending":"Ascending";
                        listener.applySort(category,toggleButton);
                    }
                });

        mSpinnerCategory = view.findViewById(R.id.id_dialog_sort_spinner_category);
        mToggleSortBy = view.findViewById(R.id.id_dialog_sort_toggle_sort);
        String[] arrayOfCategorySpinner = new String[] {
                "ByName", "ByRating", "ByVisitor"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, arrayOfCategorySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCategory.setAdapter(adapter);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
      //  getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        try {
            listener = (SortDialog.SortDialogListener) context;
        } catch (Exception e){
            throw new ClassCastException(context.toString() + "must implement DurationDialogListener");
        }


    }

    public interface SortDialogListener{
        void applySort(String category, String sortBy);
    }

}
