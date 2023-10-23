package com.nexorigin.simp;

import static com.nexorigin.simp.MainActivity.showCart;
import static com.nexorigin.simp.RegisterActivity.setSignUpFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean ALREADY_ADDED_TO_CART = false;
    public static boolean running_cart_query = false;
    public static boolean running_rating_query = false;
    public static Activity productDetailsActivity;
    public static TextView couponTitle;
    public static TextView couponExpiry;
    public static TextView couponBody;
    public static String productId;
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    public static DocumentSnapshot documentSnapshot;
    public static MenuItem cartItem;
    private static RecyclerView couponRecyclerview;
    private static LinearLayout selectedCoupon;
    private final List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    public ImageView CODINDICATORIV;
    public TextView reward_title;
    public TextView reward_body;
    public TextView productTitle;
    public TextView averageRating;
    public TextView totalRatingMiniView;
    public TextView productPrice;
    public TextView cuttedPrice;
    public TextView CODINDICATORTV;
    private FirebaseUser currentUser;
    private ConstraintLayout product_details_only_layout;
    private ConstraintLayout product_details_tabs_layout;
    private Toolbar toolbar;
    private ViewPager productImageViewPager;
    private TabLayout viewPagerIndicator;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private TextView totalRatings;
    private LinearLayout ratingNumbersContainer;
    private LinearLayout ratingsProgressBarContainer;
    private TextView totalRatingsFigure;
    private Button buyBtn;
    private LinearLayout cartBtn;
    private LinearLayout couponRedepmtionLayout;
    private Button couponRedeemBtn;
    private TextView productOnlyDescriptionBody;
    private TextView averageRatings;
    private Dialog signInDialog;
    private String productDescription;
    private String productOtherDetails;
    private Dialog loadingDialog;
    private TextView badgeCount;
    private FirebaseFirestore firebaseFirestore;

    public static void showDialogRecyclerView() {
        if (couponRecyclerview.getVisibility() == View.GONE) {
            couponRecyclerview.setVisibility(View.VISIBLE);
            selectedCoupon.setVisibility(View.GONE);
        } else {
            couponRecyclerview.setVisibility(View.GONE);
            selectedCoupon.setVisibility(View.VISIBLE);
        }
    }

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.progress_dailog_layout);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getStringExtra("PRODUCT_ID");
            if (productId != null) {
            } else {
            }
        } else {
        }
        productImageViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        couponRedeemBtn = findViewById(R.id.coupon_redemption_btn);
        productTitle = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        averageRating = findViewById(R.id.tv_product_rating_mini_view);
        totalRatingMiniView = findViewById(R.id.total_ratings_mini_view);
        CODINDICATORIV = findViewById(R.id.cod_indicator_imageview);
        CODINDICATORTV = findViewById(R.id.cod_indicator_textview);
        reward_title = findViewById(R.id.reward_title);
        reward_body = findViewById(R.id.reward_body);
        product_details_tabs_layout = findViewById(R.id.product_details_tabs_container);
        product_details_only_layout = findViewById(R.id.product_details_container);
        totalRatings = findViewById(R.id.total_ratings);
        ratingNumbersContainer = findViewById(R.id.rating_numbers_container);
        totalRatingsFigure = findViewById(R.id.total_rating_figure);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);
        ratingsProgressBarContainer = findViewById(R.id.rating_progressbar_container);
        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);
        buyBtn = findViewById(R.id.buy_now_btn);
        averageRatings = findViewById(R.id.average_rating);
        cartBtn = findViewById(R.id.add_to_cart_btn);
        couponRedepmtionLayout = findViewById(R.id.coupen_redemption_layout);

        initialRating = -1;

        firebaseFirestore = FirebaseFirestore.getInstance();

        List<String> productImages = new ArrayList<>();
        firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                        productImages.add(String.valueOf(documentSnapshot.get("product_image_" + x)));
                    }
                    ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                    productImageViewPager.setAdapter(productImagesAdapter);
                    productTitle.setText(documentSnapshot.get("product_title").toString());
                    averageRating.setText(documentSnapshot.get("average_rating").toString());
                    totalRatings.setText("(" + documentSnapshot.get("total_ratings") + ")ratings");
                    productPrice.setText("Rs." + documentSnapshot.get("product_price").toString());
                    cuttedPrice.setText("Rs." + documentSnapshot.get("cutted_price").toString());
                    if ((boolean) documentSnapshot.get("COD")) {
                        CODINDICATORIV.setVisibility(View.VISIBLE);
                        CODINDICATORTV.setVisibility(View.VISIBLE);
                    } else {
                        CODINDICATORIV.setVisibility(View.INVISIBLE);
                        CODINDICATORTV.setVisibility(View.INVISIBLE);
                    }

                    reward_title.setText(documentSnapshot.get("free_coupons") + " " + "- " + documentSnapshot.get("free_coupon_title"));
                    reward_body.setText(documentSnapshot.get("free_coupon_body").toString());

                    if ((boolean) documentSnapshot.get("use_tab_layout")) {
                        product_details_only_layout.setVisibility(View.VISIBLE);
                        product_details_tabs_layout.setVisibility(View.GONE);
                        productDescription = (String) documentSnapshot.get("product_description");
                        productOtherDetails = (String) documentSnapshot.get("product_other_details");
                        productOnlyDescriptionBody.setText(productOtherDetails);
                    }
                    totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");

                    for (int x = 0; x < 5; x++) {
                        TextView rating = (TextView) ratingNumbersContainer.getChildAt(x);
                        rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                        ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                        int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                        progressBar.setMax(maxProgress);
                        progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));

                    }
                    totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                    totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");
                    averageRatings.setText(String.valueOf(documentSnapshot.get("average_rating")));
//                    productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout,productDescription,productOtherDetails,productSpecificationModelList));

                    if (currentUser != null) {
                        if (DBquries.myRating.size() == 0) {
                            DBquries.loadRatingList(ProductDetailsActivity.this);
                        }
                        if (DBquries.cartList.size() == 0) {
                            DBquries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                        }
                    } else {
                        loadingDialog.dismiss();
                    }


                    if (DBquries.myRateIds.contains(productId)) {
                        int index = DBquries.myRateIds.indexOf(productId);
                        initialRating = Integer.parseInt(String.valueOf(DBquries.myRating.get(index))) - 1;
                        setRating(initialRating);
                    }

                    ALREADY_ADDED_TO_CART = DBquries.cartList.contains(productId);
                    loadingDialog.dismiss();

                    if ((boolean) documentSnapshot.get("in_stock")) {
                        cartBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentUser == null) {
                                    signInDialog.show();
                                } else {
                                    if (!running_cart_query) {
                                        running_cart_query = true;
                                        if (ALREADY_ADDED_TO_CART) {
                                            running_cart_query = false;
                                            Toast.makeText(ProductDetailsActivity.this, "Already added to cart", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Map<String, Object> addProduct = new HashMap<>();
                                            addProduct.put("product_ID_" + DBquries.cartList.size(), productId);
                                            addProduct.put("list_size", (long) (DBquries.cartList.size() + 1));

                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART").update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (DBquries.cartItemModelList.size() != 0) {
                                                            DBquries.cartItemModelList.add(0, new CartItemModel(
                                                                    CartItemModel.CART_ITEM,
                                                                    productId,
                                                                    documentSnapshot.get("product_image_1").toString(),
                                                                    documentSnapshot.get("product_title").toString(),
                                                                    (long) documentSnapshot.get("free_coupons"),
                                                                    documentSnapshot.get("product_price").toString(),
                                                                    documentSnapshot.get("cutted_price").toString(),
                                                                    (long) 1,
                                                                    (long) 0,
                                                                    (long) 0,
                                                                    (boolean) documentSnapshot.get("in_stock"),
                                                                    (long)documentSnapshot.get("max_quantity")));
                                                        }
                                                        ALREADY_ADDED_TO_CART = true;
                                                        DBquries.cartList.add(productId);
                                                        Toast.makeText(ProductDetailsActivity.this, "Item added to cart successfully", Toast.LENGTH_SHORT).show();
                                                        invalidateOptionsMenu();
                                                        running_cart_query = false;
                                                    } else {
                                                        running_cart_query = false;
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        buyBtn.setVisibility(View.GONE);
                        TextView outOfStock = (TextView) cartBtn.getChildAt(0);
                        outOfStock.setText("Out Of Stock");
                        outOfStock.setTextColor(getResources().getColor(R.color.app_background));
                        outOfStock.setCompoundDrawables(null, null, null, null);
                    }
                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPagerIndicator.setupWithViewPager(productImageViewPager, true);

        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @NonNull
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    productDetailsViewPager.setCurrentItem(tab.getPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    if (DeliveryActivity.cartItemModelList == null) {
                        DeliveryActivity.cartItemModelList = new ArrayList<>();
                    }
                    DeliveryActivity.cartItemModelList = new ArrayList<>();

                    DeliveryActivity.cartItemModelList.add(new CartItemModel(
                            CartItemModel.CART_ITEM,
                            productId,
                            documentSnapshot.get("product_image_1").toString(),
                            documentSnapshot.get("product_title").toString(),
                            (long) documentSnapshot.get("free_coupons"),
                            documentSnapshot.get("product_price").toString(),
                            documentSnapshot.get("cutted_price").toString(),
                            (long) 1,
                            (long) 0,
                            (long) 0,
                            (boolean) documentSnapshot.get("in_stock"),
                            (long)documentSnapshot.get("max_quantity")));

                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                    if (DBquries.addressesModelList.size() == 0) {
                        DBquries.loadAddresses(ProductDetailsActivity.this, loadingDialog);
                    } else {
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });

        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;
                                setRating(starPosition);
                                Map<String, Object> updateRatings = new HashMap<>();
                                if (DBquries.myRateIds.contains(productId)) {

                                    TextView oldRating = (TextView) ratingNumbersContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingNumbersContainer.getChildAt(5 - starPosition - 1);

                                    updateRatings.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRatings.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRatings.put("average_rating", calculateAverageRating((long) starPosition - initialRating, true));
                                } else {
                                    updateRatings.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRatings.put("average_rating", calculateAverageRating((long) starPosition + 1, false));
                                    updateRatings.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }

                                firebaseFirestore.collection("PRODUCTS").document(productId).update(updateRatings).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> myRating = new HashMap<>();
                                            if (DBquries.myRateIds.contains(productId)) {
                                                myRating.put("rating_" + DBquries.myRateIds.indexOf(productId), (long) starPosition + 1);
                                            } else {
                                                myRating.put("list_size", (long) DBquries.myRateIds.size() + 1);
                                                myRating.put("product_ID_" + DBquries.myRateIds.size(), productId);
                                                myRating.put("rating_" + DBquries.myRateIds.size(), (long) starPosition + 1);
                                            }

                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS").update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBquries.myRateIds.contains(productId)) {

                                                            DBquries.myRating.set(DBquries.myRateIds.indexOf(productId), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingNumbersContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratingNumbersContainer.getChildAt(5 - starPosition - 1);

                                                            oldRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));

                                                        } else {
                                                            DBquries.myRateIds.add(productId);
                                                            DBquries.myRating.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingNumbersContainer.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            totalRatingMiniView.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                            Toast.makeText(ProductDetailsActivity.this, "Thank you for rating 😊 ", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingfigures = (TextView) ratingNumbersContainer.getChildAt(5 - starPosition + 1);
                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                            if (ratingfigures != null && progressBar != null) {
                                                                if (totalRatingsFigure != null) {
                                                                    int maxProgress = Integer.parseInt(totalRatingsFigure.getText().toString());
                                                                    progressBar.setMax(maxProgress);
                                                                }

                                                                // Check if ratingfigures text is not null
                                                                if (ratingfigures.getText() != null) {
                                                                    progressBar.setProgress(Integer.parseInt(ratingfigures.getText().toString()));
                                                                }
                                                            }

                                                        }
                                                        initialRating = starPosition;
                                                        averageRating.setText(calculateAverageRating(0, true));
                                                        averageRatings.setText(calculateAverageRating(0, true));

                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                    }
                }
            });
        }


        couponRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog checkCouponPriceDialog = new Dialog(ProductDetailsActivity.this);
                checkCouponPriceDialog.setContentView(R.layout.coupon_redeem_dialog);
                checkCouponPriceDialog.setCancelable(true);
                checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                ImageView toggleRecyclerview = checkCouponPriceDialog.findViewById(R.id.toggle_recyclerview);
                couponRecyclerview = checkCouponPriceDialog.findViewById(R.id.coupon_recyclerview);
                selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);
                couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
                couponExpiry = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
                couponBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);
                TextView originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
                TextView discountPrice = checkCouponPriceDialog.findViewById(R.id.redemeed_price);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                couponRecyclerview.setLayoutManager(linearLayoutManager);

                List<RewardModel> rewardModelList = new ArrayList<>();
                rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
                rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
                rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
                rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
                rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
                rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
                rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));

                MyRewardAdapter myRewardAdapter = new MyRewardAdapter(rewardModelList, true);
                couponRecyclerview.setAdapter(myRewardAdapter);
                myRewardAdapter.notifyDataSetChanged();

                toggleRecyclerview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogRecyclerView();
                    }
                });

                checkCouponPriceDialog.show();
            }
        });

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);
        Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            couponRedepmtionLayout.setVisibility(View.GONE);
        } else {
            couponRedepmtionLayout.setVisibility(View.VISIBLE);
        }

        if (currentUser != null) {
            if (DBquries.myRating.size() == 0) {
                DBquries.loadRatingList(ProductDetailsActivity.this);
            }
        } else {
            loadingDialog.dismiss();
        }

        if (DBquries.myRateIds.contains(productId)) {
            int index = DBquries.myRateIds.indexOf(productId);
            initialRating = Integer.parseInt(String.valueOf(DBquries.myRating.get(index))) - 1;
            setRating(initialRating);
        }

        ALREADY_ADDED_TO_CART = DBquries.cartList.contains(productId);
        invalidateOptionsMenu();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        invalidateOptionsMenu();
//    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingNumbersContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFigure.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFigure.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        cartItem = menu.findItem(R.id.main_cart_icon);
        cartItem.setActionView(R.layout.cart_badge_layout);
        ImageView bagdeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        bagdeIcon.setImageResource(R.drawable.cart_ic);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (currentUser != null) {
            if (DBquries.cartList.size() == 0) {
                DBquries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
            } else {
                badgeCount.setVisibility(View.VISIBLE);
                if (DBquries.cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(DBquries.cartList.size()));
                } else {
                    badgeCount.setText("99");
                }
            }
        }

        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            return true;
        } else if (id == R.id.main_cart_icon) {
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }
}
