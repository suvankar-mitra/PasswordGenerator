package com.app.blooddonation.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.blooddonation.R;
import com.app.blooddonation.adapters.RequestHistoryAdapter;
import com.app.blooddonation.adapters.SearchAdapter;
import com.app.blooddonation.interfaces.GreetingRestApi;
import com.app.blooddonation.interfaces.RestApi;
import com.app.blooddonation.models.Constants;
import com.app.blooddonation.models.DonationRequest;
import com.app.blooddonation.models.User;
import com.app.blooddonation.models.UserName;
import com.app.blooddonation.util.SharedPrefService;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestHistoryFragment extends Fragment {


    public RequestHistoryFragment() {
        // Required empty public constructor
    }

    private final String TAG = "RequestHistoryFragment";
    private List<DonationRequest> donationRequestList = new ArrayList<>();
    private RequestHistoryAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_request_history, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.req_hist_recycler);
        mAdapter = new RequestHistoryAdapter(donationRequestList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ImageButton back = root.findViewById(R.id.req_hist_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });
        populateHistory();

        return root;
    }

    private void removeSelf() {
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                .remove(this).commit();
        getChildFragmentManager().popBackStack();
        FragmentManager fm = getChildFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void populateHistory() {
        ProgressDialog mDialog;
        mDialog = new ProgressDialog(getContext());
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading. Please wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        User user = (User) SharedPrefService.getSharedPref(Constants.USER, User.class, getContext());
        int userId = user.getUserId();
        Log.d(TAG, "prepareAllUserData: User id = "+userId);
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl(GreetingRestApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestApi restApi = retrofit.create(RestApi.class);
        final Call<List<DonationRequest>> call = restApi.getDonationRequestByMeAll(userId);
        call.enqueue(new Callback<List<DonationRequest>>() {
            @Override
            public void onResponse(Call<List<DonationRequest>> call, Response<List<DonationRequest>> response) {
                Log.d(TAG, "onResponse: " + response);
                int code = response.code();
                if(code == HTTP_OK) {
                    List<DonationRequest> donationRequests = response.body();
                    if(donationRequests != null) {
                        Log.d(TAG, "onResponse: donations i made " + donationRequests);
                        donationRequestList.addAll(donationRequests);
                        Log.d(TAG, "onResponse: donations2 i made " + donationRequestList);
                        populateDonorName();
                        Collections.sort(donationRequestList, new Comparator<DonationRequest>() {
                            @Override
                            public int compare(DonationRequest o1, DonationRequest o2) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss");

                                String d1 = o1.getCreateDateTime();
                                String d2 = o2.getCreateDateTime();
                                try {
                                    Date date1 = sdf.parse(d1);
                                    Date date2 = sdf.parse(d2);
                                    if (date1 != null && date1.after(date2)) return -1;
                                    if (date1 != null && date1.before(date2)) return 1;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        mDialog.dismiss();
                    }
                } else {
                    Log.d(TAG, "onResponse: Something wrong");
                    mDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<DonationRequest>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mDialog.dismiss();
            }
        });
    }

    private void populateDonorName() {
        List<User> users = SharedPrefService.getSharedPrefUserList(getContext());
        if(users!=null) {
            for(DonationRequest dr : donationRequestList) {
                for(User u : users) {
                    if(u.getUserId() == dr.getAcceptedBy()) {
                        dr.setAcceptedByName(u.getFirstName() + " " + u.getLastName());
                        break;
                    }
                }
            }
        }
    }

}
