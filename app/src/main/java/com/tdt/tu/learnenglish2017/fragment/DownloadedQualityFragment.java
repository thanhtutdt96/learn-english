package com.tdt.tu.learnenglish2017.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 1stks on 01-Jan-18.
 */

public class DownloadedQualityFragment extends Fragment {
    @BindView(R.id.btnBackArrow)
    Button btnBackArrow;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.rbMedium)
    RadioButton rbMedium;
    @BindView(R.id.rbFullHd)
    RadioButton rbFullHd;
    @BindView(R.id.rbHd)
    RadioButton rbHd;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_quality, container, false);
        init();
        radioButtonHandler();
        restoreCheckedState();
        return view;
    }

    private void restoreCheckedState() {
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        rbMedium.setChecked(sharedPreferences.getBoolean("medium_quality", rbMedium.isChecked()));
        rbHd.setChecked(sharedPreferences.getBoolean("hd_quality", rbHd.isChecked()));
        rbFullHd.setChecked(sharedPreferences.getBoolean("fullhd_quality", rbFullHd.isChecked()));
    }

    private void init() {
        ButterKnife.bind(this, view);
        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCheckedState();
                getFragmentManager().popBackStack();
            }
        });
    }

    private void radioButtonHandler() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbMedium:
                        saveQualityValue("medium");
                        break;
                    case R.id.rbHd:
                        saveQualityValue("hd");
                        break;
                    case R.id.rbFullHd:
                        saveQualityValue("fullhd");
                        break;
                }
            }
        });

    }

    private void saveQualityValue(String quality) {
        SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
        switch (quality) {
            case "medium":
                editor.putInt("iTag", 18);
                editor.commit();
                break;
            case "hd":
                editor.putInt("iTag", 22);
                editor.commit();
                break;
            case "fullhd":
                editor.putInt("iTag", 137);
                editor.commit();
                break;
        }
    }

    private void saveCheckedState() {
        SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
        editor.putBoolean("medium_quality", rbMedium.isChecked());
        editor.putBoolean("hd_quality", rbHd.isChecked());
        editor.putBoolean("fullhd_quality", rbFullHd.isChecked());
        editor.commit();
    }
}
