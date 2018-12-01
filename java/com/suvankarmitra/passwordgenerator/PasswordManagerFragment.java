package com.suvankarmitra.passwordgenerator;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suvankarmitra.passwordgenerator.util.PasswordUtil;
import com.suvankarmitra.passwordgenerator.util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordManagerFragment extends Fragment {

    private LinearLayout mSetMasterPassLinearLayout;
    private EditText mSetMasterPass1EditText;
    private EditText mSetMasterPass2EditText;
    private Button mSetMasterPassSaveButton;
    private TextView mSetMasterPassMsgTextView;

    private LinearLayout mLoginLinearLayout;
    private EditText mLoginEditText;
    private Button mLoginButton;
    private TextView mLoginMsg;

    private Context mContext;
    private HashMap<String, byte[]> userPassMap;
    private PasswordUtil passwordUtil;
    private boolean isMasterPasswordSet = false;

    private static final String CONSTANT_STRING = "thIs iS ju$t A c^$Tom $tr!n&";
    private static final String USER_PASSWORD_FILE = "user_password.map";

    public PasswordManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        passwordUtil = new PasswordUtil(mContext);

        //retrieve user password from disk
        try {
            userPassMap = passwordUtil.getMapFromDisk(USER_PASSWORD_FILE);
            if(userPassMap!=null && !userPassMap.isEmpty()) {
                //userPassword = decryptData(userPassMap, userEnteredPassword);
                Toast.makeText(mContext, "Password is saved.", Toast.LENGTH_SHORT).show();
                isMasterPasswordSet = true;
            }
        } catch (FileNotFoundException e) {
            isMasterPasswordSet = false;
            Toast.makeText(mContext, "You have not setup master password.", Toast.LENGTH_SHORT).show();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_manager, container, false);

        setViews(view);

        if(isMasterPasswordSet) {
            mSetMasterPassLinearLayout.setVisibility(View.GONE);
            mLoginLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mSetMasterPassLinearLayout.setVisibility(View.VISIBLE);
            mLoginLinearLayout.setVisibility(View.GONE);
        }

        return view;
    }

    private void setViews(View v) {
        mSetMasterPassLinearLayout = v.findViewById(R.id.pass_mgr_set_pass_layout);
        mSetMasterPass1EditText = v.findViewById(R.id.pass_mgr_set_pass_pass1);
        mSetMasterPass2EditText = v.findViewById(R.id.pass_mgr_set_pass_pass2);
        mSetMasterPassSaveButton = v.findViewById(R.id.pass_mgr_set_pass_save);
        mSetMasterPassMsgTextView = v.findViewById(R.id.pass_mgr_set_pass_tv);

        mLoginLinearLayout = v.findViewById(R.id.pass_mgr_login_layout);
        mLoginEditText = v.findViewById(R.id.pass_mgr_login_pass);
        mLoginButton = v.findViewById(R.id.pass_mgr_login_button);
        mLoginMsg = v.findViewById(R.id.pass_mgr_login_msg);

        mSetMasterPassSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = mSetMasterPass1EditText.getText().toString();
                String pass2 = mSetMasterPass2EditText.getText().toString();

                if(!pass1.equals(pass2)) {
                    mSetMasterPassMsgTextView.setTextColor(Color.RED);
                    mSetMasterPassMsgTextView.setText(getString(R.string.password_dont_match));
                    mSetMasterPass1EditText.setText("");
                    mSetMasterPass2EditText.setText("");
                } else if (!PasswordUtil.isStrong(pass1)) {
                    mSetMasterPassMsgTextView.setTextColor(Color.RED);
                    mSetMasterPassMsgTextView.setText(getString(R.string.password_must_be));
                    mSetMasterPass1EditText.setText("");
                    mSetMasterPass2EditText.setText("");
                } else {
                    try {
                        HashMap<String, byte[]> map = passwordUtil.encryptBytes(CONSTANT_STRING.getBytes(),pass1);
                        passwordUtil.saveMapToDisk(map, USER_PASSWORD_FILE);
                        Toast.makeText(mContext, "Your password is all set", Toast.LENGTH_LONG).show();
                        mSetMasterPassLinearLayout.setVisibility(View.GONE);
                        mLoginLinearLayout.setVisibility(View.VISIBLE);
                    } catch (NoSuchPaddingException | InvalidAlgorithmParameterException
                            | NoSuchAlgorithmException | IllegalBlockSizeException
                            | BadPaddingException | InvalidKeyException | InvalidKeySpecException
                            | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Something went wrong while saving your password!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mLoginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLoginMsg.setVisibility(View.GONE);
                if(s.length()<8) {
                    mLoginButton.setEnabled(false);
                } else {
                    mLoginButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = mLoginEditText.getText().toString();
                mLoginMsg.setVisibility(View.VISIBLE);
                try {
                    HashMap<String, byte[]> map = passwordUtil.getMapFromDisk(USER_PASSWORD_FILE);
                    byte[] bytes = passwordUtil.decryptData(map, pass);
                    String retrieved = new String (bytes, "UTF-8");
                    if(retrieved.equals(CONSTANT_STRING)) {
                        //Toast.makeText(mContext, "Password matched!!", Toast.LENGTH_LONG).show();
                        mLoginMsg.setTextColor(Color.GREEN);
                        mLoginMsg.setText(R.string.password_matched);
                    } else {
                        //Toast.makeText(mContext, "Password didn't match!!", Toast.LENGTH_LONG).show();
                        mLoginMsg.setTextColor(Color.RED);
                        mLoginMsg.setText(R.string.password_didnt_match);
                    }
                } catch (IOException | ClassNotFoundException |NoSuchPaddingException | InvalidAlgorithmParameterException
                        | NoSuchAlgorithmException | IllegalBlockSizeException
                        | BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {
                    e.printStackTrace();
                    mLoginMsg.setTextColor(Color.RED);
                    mLoginMsg.setText(R.string.password_didnt_match);
                    //Toast.makeText(mContext, "Something went wrong while retrieving your password!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



}
