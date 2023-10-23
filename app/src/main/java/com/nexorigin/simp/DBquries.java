package com.nexorigin.simp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBquries {
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();
    public static List<String> myRateIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();
    public static int selectedAddresses = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static void loadCategories(RecyclerView recyclerView, final Context context) {

        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                    }
                    CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                    recyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void loadFragmentData(RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORIES").document(categoryName.toUpperCase()).collection("TOP_DEALS").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<HomePageModel> currentList = new ArrayList<>(); // Create a new list for the current data

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        if ((long) documentSnapshot.get("view_type") == 0) {
                            List<SliderModel> sliderModelList = new ArrayList<>();
                            long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                            for (long x = 1; x < no_of_banners + 1; x++) {
                                sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(), documentSnapshot.get("banner_" + x + "_background").toString()));
                            }
                            lists.get(index).add(new HomePageModel(0, sliderModelList));
                        } else if ((long) documentSnapshot.get("view_type") == 1) {
                            lists.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString(), documentSnapshot.get("background").toString()));

                        } else if ((long) documentSnapshot.get("view_type") == 2) {
                            List<WishlistModel> viewAllProductList = new ArrayList<>();
                            List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                            long no_of_products = (long) documentSnapshot.get("no_of_products");
                            for (long x = 1; x < no_of_products + 1; x++) {
                                horizontalProductScrollModelList.add(new HorizontalProductScrollModel((String) documentSnapshot.get("product_ID_" + x), (String) documentSnapshot.get("product_image_" + x), (String) documentSnapshot.get("product_title_" + x), (String) documentSnapshot.get("product_subtitle_" + x), (String) documentSnapshot.get("product_price_" + x)));

                                String productImage = documentSnapshot.get("product_image_" + x) != null ? documentSnapshot.get("product_image_" + x).toString() : "";
                                String productFullTitle = (String) documentSnapshot.get("product_full_title_" + x);
                                Long freeCoupon = documentSnapshot.get("free_coupons_" + x) != null ? (long) documentSnapshot.get("free_coupons_" + x) : 0L; // Provide a default value if it's null
                                String averageRating = documentSnapshot.get("average_rating_" + x) != null ? documentSnapshot.get("average_rating_" + x).toString() : "";
                                Long totalRatings = documentSnapshot.get("total_ratings_" + x) != null ? (long) documentSnapshot.get("total_ratings_" + x) : 0L; // Provide a default value if it's null
                                String productPrice = documentSnapshot.get("product_price_" + x) != null ? documentSnapshot.get("product_price_" + x).toString() : "";
                                String cuttedPrice = documentSnapshot.get("cutted_price_" + x) != null ? documentSnapshot.get("cutted_price_" + x).toString() : "";
                                Boolean isCOD = documentSnapshot.get("COD_" + x) != null && (boolean) documentSnapshot.get("COD_" + x); // Provide a default value if it's null

                                viewAllProductList.add(new WishlistModel(productImage, productFullTitle, freeCoupon, averageRating, totalRatings, productPrice, cuttedPrice, isCOD));
                            }
                            lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));

                        } else if ((long) documentSnapshot.get("view_type") == 3) {
                            List<HorizontalProductScrollModel> gridProductScrollModelList = new ArrayList<>();
                            long no_of_products = (long) documentSnapshot.get("no_of_products");
                            for (long x = 1; x < no_of_products + 1; x++) {
                                gridProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString(), documentSnapshot.get("product_image_" + x).toString(), documentSnapshot.get("product_title_" + x).toString(), documentSnapshot.get("product_subtitle_" + x).toString(), documentSnapshot.get("product_price_" + x).toString()));
                            }
                            lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridProductScrollModelList));
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                    HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                    homePageRecyclerView.setAdapter(homePageAdapter);

                    lists.set(index, currentList);
                    if (homePageRecyclerView.getAdapter() != null) {
                        homePageRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    homePageAdapter.notifyDataSetChanged();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public static void loadRatingList(Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;

            myRateIds.clear();
            myRating.clear();

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Object listSizeObj = task.getResult().get("list_size");
                        if (listSizeObj != null) {
                            long listSize = (long) listSizeObj;
                            for (long x = 0; x < listSize; x++) {
                                myRateIds.add((String) task.getResult().get("product_ID_" + x));
                                Object ratingObject = task.getResult().get("rating_" + x);
                                if (ratingObject != null) {
                                    long ratingValue = (long) ratingObject;
                                    myRating.add(ratingValue);
                                }
                                Object productIDObject = task.getResult().get("product_ID_" + x);

                                if (productIDObject != null && ProductDetailsActivity.productId != null && ProductDetailsActivity.productId.equals(productIDObject.toString()) && ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                    if (ProductDetailsActivity.rateNowContainer != null) {
                                        ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                    }
                                }
                            }
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, Dialog dialog, boolean loadProductData, final TextView badgeCount, TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Object listSizeObj = task.getResult().get("list_size");
                    if (listSizeObj != null) {
                        long listSize = (long) listSizeObj;
                        for (long x = 0; x < (long) listSize; x++) {
                            cartList.add(task.getResult().get("product_ID_" + x).toString());

                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = DBquries.cartList.contains(ProductDetailsActivity.productId);

                            if (loadProductData) {
                                cartItemModelList.clear();
                                final String productId = task.getResult().get("product_ID_" + x).toString();
                                firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        int index = 0;
                                        if (cartList.size() >= 2) {
                                            index = cartList.size() - 2;
                                        }
                                        if (task.isSuccessful()) {
                                            cartItemModelList.add(
                                                    new CartItemModel(
                                                            CartItemModel.CART_ITEM,
                                                            productId,
                                                            task.getResult().get("product_image_1").toString(),
                                                            task.getResult().get("product_title").toString(),
                                                            (long) task.getResult().get("free_coupons"),
                                                            task.getResult().get("product_price").toString(),
                                                            task.getResult().get("cutted_price").toString(),
                                                            (long) 1,
                                                            (long) 0,
                                                            (long) 0,
                                                            (boolean) task.getResult().get("in_stock"),
                                                            (long)task.getResult().get("max_quantity")));

                                            if (cartList.size() == 1) {
                                                cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                parent.setVisibility(View.VISIBLE);
                                            }
                                            if (cartList.size() == 0) {
                                                cartItemModelList.clear();
                                            }

                                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        } else {
                                            dialog.dismiss();
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            dialog.dismiss();
                        }

                        if (cartList.size() != 0) {
                            badgeCount.setVisibility(View.VISIBLE);
                        } else {
                            badgeCount.setVisibility(View.INVISIBLE);
                        }
                        if (DBquries.cartList.size() < 99) {
                            badgeCount.setText(String.valueOf(DBquries.cartList.size()));
                        } else {
                            badgeCount.setText("99");
                        }
                    } else {
                        dialog.dismiss();
                    }

                }
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, TextView totalCartAmount) {
        final String removedProductId = cartList.get(index);

        cartList.remove(index);

        Map<String, Object> updateCartList = new HashMap<>();
        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART").set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (cartItemModelList.size() > index) {
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout) totalCartAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }
                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    cartList.add(index, removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false;
            }
        });
    }

    public static void loadAddresses(Context context, Dialog loadingDialog) {
        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    Intent deliveryIntent;
                    Object listSize = task.getResult().get("list_size");
                    if (listSize != null && (long) listSize == 0) {
                        deliveryIntent = new Intent(context, AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT", "deliveryIntent");
                    } else {
                        Long LonglistSize = (Long) task.getResult().get("list_size");

                        if (listSize != null) {
                            for (long x = 1; x <= LonglistSize; x++) {
                                String fullname = task.getResult().get("fullname_" + x) != null ? task.getResult().get("fullname_" + x).toString() : "";
                                String address = task.getResult().get("address_" + x) != null ? task.getResult().get("address_" + x).toString() : "";
                                String pincode = task.getResult().get("pincode_" + x) != null ? task.getResult().get("pincode_" + x).toString() : "";
                                String mobileNo = task.getResult().get("mobile_no_" + x) != null ? task.getResult().get("pincode_" + x).toString() : "";
                                Boolean selected = task.getResult().get("selected_" + x) != null && (boolean) task.getResult().get("selected_" + x);

                                addressesModelList.add(new AddressesModel(fullname, address, pincode, selected, mobileNo));

                                if (selected) {
                                    selectedAddresses = (int) (x - 1);
                                }
                            }
                        }
                        deliveryIntent = new Intent(context, DeliveryActivity.class);
                    }
                    context.startActivity(deliveryIntent);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    public static void clearData() {
        cartItemModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRateIds.clear();
        addressesModelList.clear();
        myRating.clear();
    }

}