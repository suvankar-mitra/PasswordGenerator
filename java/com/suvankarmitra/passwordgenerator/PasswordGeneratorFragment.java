package com.suvankarmitra.passwordgenerator;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.suvankarmitra.passwordgenerator.util.PasswordUtil;
import com.suvankarmitra.passwordgenerator.util.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordGeneratorFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private TextView mOutput;
    private TextView mStrength;
    private SeekBar mSeekBar;
    private ImageButton mSave;
    private Context mContext;
    private ClipboardManager clipboard;

    private static final int BASE_MULTIPLIER = 2;
    private static final int BASE_STRENGTH = 6;
    private int strength = BASE_STRENGTH;
    private boolean useLetters = true;
    private boolean useNumbers = true;
    private boolean useSpecialChars = true;

    private static final String TAG = "PasswordGenerator";

    public PasswordGeneratorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // setup clipboard manager
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_generator, container, false);

        setupViews(view);

        String generatedString = PasswordUtil.getSaltString(strength, useLetters, useNumbers, useSpecialChars); //Utils.generateRandomPassword(strength, useLetters, useNumbers, useSpecialChars);
        mOutput.setText(generatedString);
        mSeekBar.setProgress(0);

        /*AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        return view;
    }

    public void setupViews(View view) {
        final Button mGenerate = view.findViewById(R.id.generate);
        mOutput = view.findViewById(R.id.output);
        mStrength = view.findViewById(R.id.strength);
        mSeekBar = view.findViewById(R.id.seekBar);
        ImageButton mCopy = view.findViewById(R.id.copy);
        Switch mLetter = view.findViewById(R.id.alphabet);
        Switch mNumber = view.findViewById(R.id.numeric);
        Switch mSpecChar = view.findViewById(R.id.spec_chars);
        mSave = view.findViewById(R.id.save);

        useLetters = mLetter.isChecked();
        useNumbers = mNumber.isChecked();
        useSpecialChars = mSpecChar.isChecked();

        String sb = getString(R.string.password_length) + ": " + BASE_STRENGTH;
        mStrength.setText(sb);
        mLetter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useLetters = isChecked;
                Log.d(TAG, "onCheckedChanged: "+useLetters+","+useNumbers+","+useSpecialChars);
                if(!useNumbers && !useLetters)
                    mGenerate.setEnabled(false);
                else
                    mGenerate.setEnabled(true);
            }
        });

        mNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useNumbers = isChecked;
                Log.d(TAG, "onCheckedChanged: "+useLetters+","+useNumbers+","+useSpecialChars);
                if(!useNumbers && !useLetters)
                    mGenerate.setEnabled(false);
                else
                    mGenerate.setEnabled(true);
            }
        });

        mSpecChar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useSpecialChars = isChecked;
                Log.d(TAG, "onCheckedChanged: "+useLetters+","+useNumbers+","+useSpecialChars);
                if(!useNumbers && !useLetters)
                    mGenerate.setEnabled(false);
                else
                    mGenerate.setEnabled(true);
            }
        });

        mGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String generatedString = PasswordUtil.getSaltString(strength, useLetters, useNumbers, useSpecialChars);
                mOutput.setText(generatedString);
            }
        });

        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("password",mOutput.getText().toString());
                clipboard.setPrimaryClip(clipData);
                mOutput.setSelected(true);
                Toast.makeText(mContext, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetMasterPasswordDialog();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            StringBuilder sb = new StringBuilder(getString(R.string.password_length));
            sb.append(": ");
            strength = progress * BASE_MULTIPLIER + BASE_STRENGTH;
            sb.append(strength);
            mStrength.setText(sb.toString());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void showSetMasterPasswordDialog() {

    }

}
