package com.nexorigin.simp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nexorigin.simp.databinding.FragmentSignUpBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignUpFragment extends Fragment {

    public static boolean disableCloseBtn = false;
    private FragmentSignUpBinding binding;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public SignUpFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(LayoutInflater.from(mContext), container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (disableCloseBtn) {
            binding.closeBtn.setVisibility(View.GONE);
        } else {
            binding.closeBtn.setVisibility(View.VISIBLE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.cPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.haveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signInBtn.setEnabled(true);
                checkEmailAndPassword();
            }
        });

        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });
    }

    private void checkEmailAndPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());

        if (binding.emailEt.getText().toString().matches(emailPattern)) {
            if (binding.passwordEt.getText().toString().equals(binding.cPasswordEt.getText().toString())) {

                binding.progressBar.setVisibility(View.VISIBLE);
//                binding.signInBtn.setEnabled(false);

                firebaseAuth.createUserWithEmailAndPassword(binding.emailEt.getText().toString(), binding.passwordEt.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");

                            Map<String, Object> userdata = new HashMap<>();
                            userdata.put("name", binding.nameEt.getText().toString());
                            userdata.put("email", binding.emailEt.getText().toString());

                            Map<String, Object> ratingsMap = new HashMap<>();
                            ratingsMap.put("list_size", (long) 0);

                            Map<String, Object> cartMap = new HashMap<>();
                            ratingsMap.put("list_size", (long) 0);

                            Map<String, Object> addressesMap = new HashMap<>();
                            ratingsMap.put("list_size", (long) 0);

                            List<String> documentNames = new ArrayList<>();
                            documentNames.add("MY_RATINGS");
                            documentNames.add("MY_CART");
                            documentNames.add("MY_ADDRESSES");

                            List<Map<String, Object>> documentFields = new ArrayList<>();
                            documentFields.add(ratingsMap);
                            documentFields.add(cartMap);
                            documentFields.add(addressesMap);

                            for (int x = 0; x < documentNames.size(); x++) {
                                int finalX = x;
                                userDataReference.document(documentNames.get(x)).set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (finalX == documentNames.size() - 1) {
                                                mainIntent();
                                            }
                                        } else {
                                            binding.progressBar.setVisibility(View.INVISIBLE);
                                            binding.signInBtn.setEnabled(true);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).set(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                binding.cPasswordEt.setError("Password doesn't match!", customErrorIcon);
            }
        } else {
            binding.emailEt.setError("Invalid email address!", customErrorIcon);
        }
    }

    private void checkInput() {
        if (!TextUtils.isEmpty(binding.emailEt.getText())) {
            if (!TextUtils.isEmpty(binding.nameEt.getText())) {
                if (!TextUtils.isEmpty(binding.passwordEt.getText()) && binding.passwordEt.length() >= 8) {
                    if (!TextUtils.isEmpty(binding.cPasswordEt.getText())) {
                    } else {

                    }
                } else {
                }
            } else {
            }
        } else {
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(binding.parentFrame.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void mainIntent() {
        if (disableCloseBtn) {
            disableCloseBtn = false;
            getActivity().finish();
        } else {
            Toast.makeText(mContext, "Account created successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

}