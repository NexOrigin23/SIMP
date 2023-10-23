package com.nexorigin.simp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {
    public static final int SELECT_ADDRESS = 0;
    public static List<CartItemModel> cartItemModelList;
    public static boolean fromCart;
    private boolean successResponse = false;
    private Toolbar toolbar;
    private RecyclerView deliveryRecyclerView;
    private Button change_btn;
    private TextView totalCartAmount;
    private TextView fullname;
    private String name, mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    private Button continueBtn;
    private Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageButton cod;
    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private String orderIdNo;
    public static boolean codOrderConfirmed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delivery");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.progress_dailog_layout);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        paymentMethodDialog = new Dialog(this);
        paymentMethodDialog.setContentView(R.layout.payment_method_layout);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.setCanceledOnTouchOutside(false);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        change_btn = findViewById(R.id.change_or_add_address_button);
        totalCartAmount = findViewById(R.id.total_cart_amount);
        fullname = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);
        orderIdNo = UUID.randomUUID().toString().substring(0, 28);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        CartAdapter cartAdapter = new CartAdapter(cartItemModelList, totalCartAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        change_btn.setVisibility(View.VISIBLE);
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethodDialog.show();
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationLayout();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (DBquries.selectedAddresses >= 0 && DBquries.selectedAddresses < DBquries.addressesModelList.size()) {
            AddressesModel selectedAddress = DBquries.addressesModelList.get(DBquries.selectedAddresses);

            if (selectedAddress != null) {
                name = selectedAddress.getFullname();
                mobileNo = selectedAddress.getMobileNo();
                fullname.setText(name + " - " + mobileNo);
                fullAddress.setText(selectedAddress.getAddress());
                pincode.setText(selectedAddress.getPincode());
            }
        }

        if (codOrderConfirmed){
            showConfirmationLayout();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DBquries.selectedAddresses >= 0 && DBquries.selectedAddresses < DBquries.addressesModelList.size()) {
            AddressesModel selectedAddress = DBquries.addressesModelList.get(DBquries.selectedAddresses);

            if (selectedAddress != null) {
                name = selectedAddress.getFullname();
                mobileNo = selectedAddress.getMobileNo();
                fullname.setText(name + " - " + mobileNo);
                fullAddress.setText(selectedAddress.getAddress());
                pincode.setText(selectedAddress.getPincode());
            }
        }

        if (codOrderConfirmed){
            showConfirmationLayout();
        }

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

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout() {
        paymentMethodDialog.dismiss();
        loadingDialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                successResponse = true;
                codOrderConfirmed=false;

                if (MainActivity.mainActivity != null) {
                    MainActivity.mainActivity.finish();
                    MainActivity.mainActivity = null;
                    MainActivity.showCart = false;
                }

                if (ProductDetailsActivity.productDetailsActivity != null) {
                    ProductDetailsActivity.productDetailsActivity.finish();
                    ProductDetailsActivity.productDetailsActivity = null;
                }

                if (fromCart) {
                    loadingDialog.show();
                    Map<String, Object> updateCartList = new HashMap<>();
                    long cartListSize = 0;
                    List<Integer> indexList = new ArrayList<>();
                    for (int x = 0; x < DBquries.cartList.size(); x++) {
                        if (!cartItemModelList.get(x).isInStock()) {
                            updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                            cartListSize++;
                        } else {
                            indexList.add(x);
                        }
                    }
                    updateCartList.put("list_size", cartListSize);
                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART").set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    DBquries.cartList.remove(indexList.get(x).intValue());
                                    DBquries.cartItemModelList.remove(indexList.get(x).intValue());
                                    DBquries.cartItemModelList.remove(DBquries.cartItemModelList.size() - 1);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });

                }

                continueBtn.setEnabled(false);
                change_btn.setEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                orderId.setText("Order ID " + orderIdNo);
                orderConfirmationLayout.setVisibility(View.VISIBLE);
                continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DeliveryActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        }, 5000);
    }
}
