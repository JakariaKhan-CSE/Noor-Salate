package com.noor.prayer.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.noor.prayer.R;

public class SettingsFragment extends Fragment {

    private Spinner calcMethodSpinner;
    private Spinner madhabSpinner;
    private SharedPreferences prefs;

    // Methods from Aladhan: 
    // 0: Shia Ithna-Ashari, 1: Univ of Islamic Sciences, Karachi, 2: ISNA, 3: Muslim World League, 4: Umm Al-Qura, Mecca, 5: Egyptian General Authority
    private final String[] methodNames = {"Shia Ithna-Ashari", "Karachi", "ISNA (North America)", "Muslim World League", "Umm Al-Qura (Mecca)", "Egyptian General Authority"};
    private final int[] methodValues = {0, 1, 2, 3, 4, 5};

    private final String[] madhabNames = {"Shafi (Standard)", "Hanafi"};
    private final int[] madhabValues = {0, 1}; // 0: Shafi/Maliki/Hanbali, 1: Hanafi

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calcMethodSpinner = view.findViewById(R.id.spinner_calc_method);
        madhabSpinner = view.findViewById(R.id.spinner_madhab);
        prefs = requireActivity().getSharedPreferences("NoorSalatPrefs", Context.MODE_PRIVATE);

        setupSpinners();
    }

    private void setupSpinners() {
        // Calculation Method
        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, methodNames);
        methodAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        calcMethodSpinner.setAdapter(methodAdapter);
        
        int savedMethod = prefs.getInt("calc_method", 2); // Default ISNA
        calcMethodSpinner.setSelection(savedMethod); // Values map 1:1 to index for now
        
        calcMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("calc_method", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Madhab
        ArrayAdapter<String> madhabAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, madhabNames);
        madhabAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        madhabSpinner.setAdapter(madhabAdapter);

        int savedMadhab = prefs.getInt("madhab", 0);
        madhabSpinner.setSelection(savedMadhab);

        madhabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("madhab", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
