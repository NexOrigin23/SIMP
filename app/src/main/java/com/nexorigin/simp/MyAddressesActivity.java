package com.nexorigin.simp;

import static com.nexorigin.simp.DeliveryActivity.SELECT_ADDRESS;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyAddressesActivity extends AppCompatActivity {

    public static AddressesAdapter addressesAdapter;
    private int previouslyAdded;
    private RecyclerView myAddressRecyclerView;
    private Button deliverHereBtn;
    private LinearLayout addNewAddressBtn;
    private TextView addressesSaved;
    private Toolbar toolbar;
    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.progress_dailog_layout);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        previouslyAdded = DBquries.selectedAddresses;

        myAddressRecyclerView = findViewById(R.id.addresses_recyclerview);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        addNewAddressBtn = findViewById(R.id.add_new_address_btn);
        addressesSaved = findViewById(R.id.address_saved);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myAddressRecyclerView.setLayoutManager(layoutManager);

        int mode = getIntent().getIntExtra("MODE", -1);

        if (mode == SELECT_ADDRESS) {
            deliverHereBtn.setVisibility(View.VISIBLE);
        } else {
            deliverHereBtn.setVisibility(View.GONE);
        }

        deliverHereBtn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                if (DBquries.selectedAddresses != previouslyAdded) {

                    final int previousAddressIndex = previouslyAdded;
                    loadingDialog.show();

                    Map<String, Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_" + String.valueOf(previouslyAdded +1), false);
                    updateSelection.put("selected_" + String.valueOf(DBquries.selectedAddresses+1), true);

                    previouslyAdded = DBquries.selectedAddresses;

                    FirebaseFirestore.getInstance()
                            .collection("USERS")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA")
                            .document("MY_ADDRESSES")
                            .update(updateSelection)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                    } else {
                                        previouslyAdded = previousAddressIndex;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(MyAddressesActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                } else {
                    finish();
                }
            }
        });

        addressesAdapter = new AddressesAdapter(DBquries.addressesModelList, mode);
        myAddressRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator) myAddressRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent = new Intent(MyAddressesActivity.this, AddAddressActivity.class);
                addAddressIntent.putExtra("INTENT", "null");
                startActivity(addAddressIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBquries.addressesModelList.size() + " saved addresses"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (DBquries.selectedAddresses != previouslyAdded) {
                DBquries.addressesModelList.get(DBquries.selectedAddresses).setSelected(false);
                DBquries.addressesModelList.get(previouslyAdded).setSelected(true);
                DBquries.selectedAddresses = previouslyAdded;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void refreshItem(int deselect, int select) {

        addressesAdapter.notifyItemChanged(deselect);
        addressesAdapter.notifyItemChanged(select);
    }


    public void onBackPressed() {
        if (DBquries.selectedAddresses != previouslyAdded) {
            DBquries.addressesModelList.get(DBquries.selectedAddresses).setSelected(false);
            DBquries.addressesModelList.get(previouslyAdded).setSelected(true);
            DBquries.selectedAddresses = previouslyAdded;
        }
        super.onBackPressed();
    }
}