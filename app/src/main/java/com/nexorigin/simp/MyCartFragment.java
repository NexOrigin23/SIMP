package com.nexorigin.simp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyCartFragment extends Fragment {

    public static CartAdapter cartAdapter;
    private RecyclerView cartItemsRecyclerView;
    private Button continueBtn;
    private Dialog loadingDialog;
    private TextView totalCartAmount;

    public MyCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.progress_dailog_layout);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerView);
        continueBtn = view.findViewById(R.id.cart_continue_btn);

        totalCartAmount = view.findViewById(R.id.total_cart_amount);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(DBquries.cartItemModelList, totalCartAmount, true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;
                for (int x = 0; x < DBquries.cartItemModelList.size(); x++) {
                    CartItemModel cartItemModel = DBquries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()) {
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loadingDialog.show();
                if (DBquries.addressesModelList.size() == 0) {
                    DBquries.loadAddresses(getContext(), loadingDialog);
                } else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        if (DBquries.cartItemModelList.size() == 0) {
            DBquries.cartList.clear();
            DBquries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalCartAmount);
        } else {
            if (DBquries.cartItemModelList.size() > 0) {
                if (DBquries.cartItemModelList.get(DBquries.cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_AMOUNT) {
                    if (totalCartAmount != null) {
                        LinearLayout parent = (LinearLayout) totalCartAmount.getParent();
                        if (parent != null) {
                            parent.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();
    }
}