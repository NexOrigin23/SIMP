package com.nexorigin.simp;

import static com.nexorigin.simp.RegisterActivity.onResetPasswordFragment;

import android.content.Context;
import android.content.Intent;
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
import com.nexorigin.simp.databinding.FragmentSignInBinding;


public class SignInFragment extends Fragment {

    public static boolean disableCloseBtn = false;
    private FragmentSignInBinding binding;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public SignInFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(LayoutInflater.from(mContext), container, false);
        firebaseAuth = FirebaseAuth.getInstance();
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

        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

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

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent();
            }
        });

        binding.forgetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });
    }

    private void checkEmailAndPassword() {
        if (binding.emailEt.getText().toString().matches(emailPattern)) {
            if (binding.passwordEt.length() >= 8) {

                binding.progressBar.setVisibility(View.VISIBLE);
                binding.signInBtn.setEnabled(false);

                firebaseAuth.signInWithEmailAndPassword(binding.emailEt.getText().toString(), binding.passwordEt.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    intent();
                                } else {
                                    binding.progressBar.setVisibility(View.INVISIBLE);
                                    binding.signInBtn.setEnabled(true);
                                    String error = task.getException().getMessage();
                                    Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(mContext, "Incorrect email address or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Incorrect email address or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInput() {
        if (!TextUtils.isEmpty(binding.emailEt.getText())) {
            if (!TextUtils.isEmpty(binding.passwordEt.getText())) {
                binding.signInBtn.setEnabled(true);
            } else {
                binding.signInBtn.setEnabled(false);
            }
        } else {
            binding.signInBtn.setEnabled(false);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(binding.parentFrame.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void intent() {
        if (disableCloseBtn) {
            disableCloseBtn = false;
            getActivity().finish();
        } else {
            Toast.makeText(mContext, "logged in successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

}