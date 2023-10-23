package com.nexorigin.simp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText city;
    private EditText locality;
    private EditText flatNo;
    private EditText pincode;
    private EditText landmark;
    private EditText name;
    private EditText mobileNo;

    private EditText alternateMobileNo;
    private Spinner stateSpinner;
    private Button saveBtn;
    private String[] stateList;
    private String selectedState;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.progress_dailog_layout);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add a new address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        flatNo = findViewById(R.id.flat_no);
        pincode = findViewById(R.id.pincod);
        landmark = findViewById(R.id.landmark);
        name = findViewById(R.id.nam);
        mobileNo = findViewById(R.id.mobile_no);
        alternateMobileNo = findViewById(R.id.alternate_mobile_no);
        stateSpinner = findViewById(R.id.state_spinner);
        saveBtn = findViewById(R.id.saveBtn);
        stateList = getResources().getStringArray(R.array.india_states);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSpinner.setAdapter(spinnerAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.show();

                String fullAddress = flatNo.getText().toString() + " " + locality.getText().toString() + " " + landmark.getText().toString() + " " + city.getText().toString() + " " + selectedState;

                Map<String, Object> addAddress = new HashMap();
                addAddress.put("list_size", (long) DBquries.addressesModelList.size() + 1);
                if (TextUtils.isEmpty(alternateMobileNo.getText())) {
                    addAddress.put("mobile_no_" + ((long) DBquries.addressesModelList.size() + 1), mobileNo.getText().toString());
                } else {
                    addAddress.put("mobile_no_" + ((long) DBquries.addressesModelList.size() + 1), mobileNo.getText().toString() + " or " + alternateMobileNo.getText().toString());
                }

                addAddress.put("fullname_" + ((long) DBquries.addressesModelList.size() + 1), name.getText().toString());
                addAddress.put("address_" + ((long) DBquries.addressesModelList.size() + 1), fullAddress);
                addAddress.put("pincode_" + ((long) DBquries.addressesModelList.size() + 1), pincode.getText().toString());
                addAddress.put("selected_" + ((long) DBquries.addressesModelList.size() + 1), true);

                if (DBquries.addressesModelList.size() > 0) {
                    addAddress.put("selected_" + (DBquries.selectedAddresses + 1), false);
                }

                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES").update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (DBquries.addressesModelList.size() > 0) {
                                DBquries.addressesModelList.get(DBquries.selectedAddresses).setSelected(false);
                            }

                            if (TextUtils.isEmpty(alternateMobileNo.getText())) {
                                DBquries.addressesModelList.add(new AddressesModel(name.getText().toString(), fullAddress, pincode.getText().toString(), true, mobileNo.getText().toString()));
                            } else {
                                DBquries.addressesModelList.add(new AddressesModel(name.getText().toString(), fullAddress, pincode.getText().toString(), true, mobileNo.getText().toString() + "or" + alternateMobileNo.getText().toString()));
                            }
                            if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                startActivity(deliveryIntent);
                            } else {
                                MyAddressesActivity.refreshItem(DBquries.selectedAddresses, DBquries.addressesModelList.size() - 1);
                            }
                            DBquries.selectedAddresses = DBquries.addressesModelList.size() - 1;
                            finish();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}