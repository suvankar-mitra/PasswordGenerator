package com.suvankarmitra.passwordgenerator;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.suvankarmitra.passwordgenerator.util.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity implements OnDataPass{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragmentAt(0, new PasswordGeneratorFragment(), "Generator");
        //viewPagerAdapter.addFragmentAt(1,new PasswordManagerFragment(), "Manager");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            case R.id.share:
                showShareDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showShareDialog() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Password Generator");
        String sAux = "\nLet me recommend you this application\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=com.suvankarmitra.passwordgenerator \n\n";
        i.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(i, "Choose one"));
    }

    private void showAboutDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("about-dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment dialogFragment = new AboutDialogFragment();
        dialogFragment.show(ft, "about-dialog");
    }

    @Override
    public void onDataPass(String data, String intent) {
        Toast.makeText(this, "Got data="+data + "    with intent="+intent, Toast.LENGTH_LONG).show();
        /*if(intent.equals("PASSWORD")) {
            try {
                HashMap<String, byte[]> encMap = encryptBytes(data.getBytes(), data);
                saveMapToDisk(encMap,"user_password.map");
            } catch (NoSuchPaddingException | InvalidAlgorithmParameterException |
                    NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException |
                    InvalidKeyException | InvalidKeySpecException | IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
