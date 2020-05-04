package com.al375502.covid_19data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.al375502.covid_19data.database.Country;

public class InfoDialog extends AppCompatDialogFragment {
    public Country countrySelected;
    public MainActivity view;
    public TextView capital, population, govern, life;

    public InfoDialog(Country countrySelected, MainActivity view) {
        this.countrySelected = countrySelected;
        this.view = view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewInflate = inflater.inflate(R.layout.info_dialog, null);
        builder.setView(viewInflate).setTitle(countrySelected.name);

        builder.setNeutralButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        life = viewInflate.findViewById(R.id.govern);
        capital = viewInflate.findViewById(R.id.capital);
        govern = viewInflate.findViewById(R.id.life);
        population = viewInflate.findViewById(R.id.population);


        life.setText(countrySelected.life  + "");
        capital.setText(countrySelected.capital+ "");
        population.setText(countrySelected.population + "");
        govern.setText(countrySelected.govern + "");

        return builder.create();
    }
}

