package com.suvankarmitra.passwordgenerator;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.suvankarmitra.passwordgenerator.util.PasswordUtil;
import com.suvankarmitra.passwordgenerator.util.Utils;

public class SetMasterPasswordDialogFragment extends DialogFragment {

    private OnDataPass dataPasser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_master_password_dialog, container, false);

        Button mCancel = view.findViewById(R.id.cancel);
        Button mSave = view.findViewById(R.id.save);
        final TextView mPass1 = view.findViewById(R.id.password);
        final TextView mPass2 = view.findViewById(R.id.password2);
        final TextView mWarn = view.findViewById(R.id.warning);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetMasterPasswordDialogFragment.this.dismiss();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = mPass1.getText().toString();
                String pass2 = mPass2.getText().toString();

                if(!pass1.equals(pass2)) {
                    mWarn.setTextColor(Color.RED);
                    mWarn.setText(R.string.password_dont_match);
                    mPass1.setText("");
                    mPass2.setText("");
                } else if(!PasswordUtil.isStrong(pass1)) { //
                    mWarn.setTextColor(Color.RED);
                    mWarn.setText(R.string.password_must_be);
                    mPass1.setText("");
                    mPass2.setText("");
                } else {
                    mWarn.setTextColor(Color.DKGRAY);
                    mWarn.setText(getString(R.string.set_master_pass));
                    dataPasser.onDataPass(pass1, "PASSWORD");
                    SetMasterPasswordDialogFragment.this.dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }
}
