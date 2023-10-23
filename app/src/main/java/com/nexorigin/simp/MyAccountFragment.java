package com.nexorigin.simp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MyAccountFragment extends Fragment {

    private Button viewAllAddressBtn;

    public static final int MANAGE_ADDRESS = 1;

    private Context context;


    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        viewAllAddressBtn = view.findViewById(R.id.view_all_address_btn);

        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtain the context from the Fragment using getContext()
                Context context = getContext();

                if (context != null) {
                    Intent myAddressesIntent = new Intent(context, MyAddressesActivity.class);
                    myAddressesIntent.putExtra("MODE", MANAGE_ADDRESS);
                    startActivity(myAddressesIntent);
                }
            }
        });


        return view;
    }
}