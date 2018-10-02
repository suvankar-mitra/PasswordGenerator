package com.suvankarmitra.passwordgenerator;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.suvankarmitra.passwordgenerator.util.Utils;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private TextView mOutput;
    private TextView mStrength;
    private SeekBar mSeekBar;

    private ClipboardManager clipboard;

    private int strength = 8;
    private static final int BASE_MULTIPLIER = 2;
    private static final int BASE_STRENGTH = 8;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup clipboard manager
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Button mGenerate = findViewById(R.id.generate);
        mOutput = findViewById(R.id.output);
        mStrength = findViewById(R.id.strength);
        mSeekBar = findViewById(R.id.seekBar);
        ImageButton mCopy = findViewById(R.id.copy);

        mGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String generatedString = Utils.generateRandomPassword(strength,true, true, true);
                mOutput.setText(generatedString);
            }
        });

        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("password",mOutput.getText().toString());
                clipboard.setPrimaryClip(clipData);
                mOutput.setSelected(true);
                Toast.makeText(MainActivity.this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String generatedString = Utils.generateRandomPassword(strength,true, true, true);
        mOutput.setText(generatedString);

        String sb = getString(R.string.password_strength) + ": " + strength;
        mStrength.setText(sb);

        mSeekBar.setProgress(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                showAboutDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment dialogFragment = new AboutDialogFragment();
        dialogFragment.show(ft, "dialog");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            StringBuilder sb = new StringBuilder(getString(R.string.password_strength));
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
}
