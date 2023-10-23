package com.nexorigin.simp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    private final List<CartItemModel> cartItemModelList;
    private final TextView cartTotalAmount;
    private final boolean showDeleteBtn;
    private int lastPosition = -1;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                return CartItemModel.CART_ITEM;
            case CartItemModel.TOTAL_AMOUNT:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new CartTotalAmountViewHolder(cartTotalView); // Use CartTotalAmountViewHolder here
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {

            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                Long freeCoupons = cartItemModelList.get(position).getFreeCoupon();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                Boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity = cartItemModelList.get(position).getProductMaxQuantity();

                if (holder instanceof CartItemViewHolder) {
                    // Bind data for CartItemViewHolder
                    ((CartItemViewHolder) holder).setItemDetails(productID, resource, title, freeCoupons, productPrice, cuttedPrice, offersApplied, position, inStock, String.valueOf(productQuantity), maxQuantity);
                }
                break;

            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemsPrice = 0;
                String deliveryPrice = "100";
                int savedAmount = 0;
                int totalAmount;

                boolean hasInStockItem = false;

                for (int x = 0; x < cartItemModelList.size(); x++) {
                    CartItemModel cartItem = cartItemModelList.get(x);
                    if (cartItem.getType() == CartItemModel.CART_ITEM) {
                        totalItems++;

                        try {
                            totalItemsPrice += Integer.parseInt(cartItem.getProductPrice().replace(",", ""));
                        } catch (NumberFormatException e) {
                        }

                        if (cartItem.isInStock()) {
                            hasInStockItem = true;
                        }
                    }
                }

                if (hasInStockItem) {
                    if (totalItemsPrice > 500) {
                        deliveryPrice = "FREE";
                        totalAmount = totalItemsPrice;
                    } else {
                        totalAmount = totalItemsPrice + 100; // Fixed delivery cost to 100
                    }

                    if (holder instanceof CartTotalAmountViewHolder) {
                        ((CartTotalAmountViewHolder) holder).setTotalAmount(totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount);
                    }
                } else {
                    if (holder instanceof CartTotalAmountViewHolder) {
                        ((CartTotalAmountViewHolder) holder).setTotalAmount(0, 0, "100", 0, 0);
                    }
                }
                break;

        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView productImage;
        private final ImageView freeCouponIcon;
        private final TextView productTitle;
        private final TextView freeCoupons;
        private final TextView productPrice;
        private final TextView cuttedPrice;
        private final TextView offersApplied;
        private final TextView couponApplied;
        private final TextView productQuantity;
        private final LinearLayout couponRedemptionLayout;
        private final LinearLayout deleteBtn;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            freeCouponIcon = itemView.findViewById(R.id.free_coupon_icon);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupon);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            offersApplied = itemView.findViewById(R.id.offer_applied);
            couponApplied = itemView.findViewById(R.id.coupon_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
            couponRedemptionLayout = itemView.findViewById(R.id.coupen_redemption_layout);
        }

        private void setItemDetails(String productID, String resource, String title, Long freeCouponsNo, String productPriceText, String cuttedPriceText, Long offersAppliedNo, int Viewposition, boolean inStock, String quantity, Long maxQuantity) {


            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.category_placeholder)).into(productImage);
            productTitle.setText(title);

            if (inStock) {
                if (freeCouponsNo > 0) {
                    freeCouponIcon.setVisibility(View.VISIBLE);
                    freeCoupons.setVisibility(View.VISIBLE);
                    if (freeCouponsNo == 1) {
                        freeCoupons.setText("free " + freeCouponsNo + "Coupon");
                    } else {
                        freeCoupons.setText("free " + freeCouponsNo + "Coupons");
                    }
                } else {
                    freeCouponIcon.setVisibility(View.INVISIBLE);
                    freeCoupons.setVisibility(View.INVISIBLE);
                }
                productPrice.setText("Rs." + productPriceText + "/-");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                cuttedPrice.setText("Rs." + cuttedPriceText + "/-");
                couponRedemptionLayout.setVisibility(View.VISIBLE);

                productQuantity.setText("Qty: " + quantity);

                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        quantityDialog.setCancelable(false);
                        EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max " + String.valueOf(maxQuantity));

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0) {
                                        if (DeliveryActivity.fromCart) {
                                            DBquries.cartItemModelList.get(Viewposition).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        } else {
                                            if (DeliveryActivity.cartItemModelList != null) {
                                                try {
                                                    long newQuantity = Long.valueOf(quantityNo.getText().toString());
                                                    DeliveryActivity.cartItemModelList.get(Viewposition).setProductQuantity(newQuantity);
                                                } catch (NumberFormatException |
                                                         IndexOutOfBoundsException e) {
                                                }
                                            }
                                        }
                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                    } else {
                                        Toast.makeText(itemView.getContext(), "please select quantity between 1 to " + maxQuantity.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });

                if (offersAppliedNo > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    offersApplied.setText(offersAppliedNo + " Offers applied");
                } else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }
            } else {
                productPrice.setText("Out Of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.app_background));
                cuttedPrice.setText("");
                couponRedemptionLayout.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.INVISIBLE);
                couponApplied.setVisibility(View.INVISIBLE);
                offersApplied.setVisibility(View.INVISIBLE);
                freeCouponIcon.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
            }


            if (showDeleteBtn) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;
                        DBquries.removeFromCart(Viewposition, itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {
        public final TextView totalItems;
        public final TextView totalItemPrice;
        public final TextView deliveryPrice;
        public final TextView totalAmount;
        public final TextView savedAmount;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_charge_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        public void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Price (" + totalItemText + " items)");
            totalItemPrice.setText("Rs." + totalItemPriceText + "/-");
            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            } else {
                deliveryPrice.setText("Rs." + deliveryPriceText + "/-");
            }
            totalAmount.setText("Rs." + totalAmountText + "/-");
            cartTotalAmount.setText("Rs." + totalAmountText + "/-");
            savedAmount.setText("You saved Rs." + savedAmountText + "/- on this order.");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemPriceText == 0) {
                DBquries.cartItemModelList.remove(DBquries.cartItemModelList.size() - 1);
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }

}
