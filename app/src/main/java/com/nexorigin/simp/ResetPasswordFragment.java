package com.nexorigin.simp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.nexorigin.simp.databinding.FragmentResetPasswordBinding;


public class ResetPasswordFragment extends Fragment {


    private FragmentResetPasswordBinding binding;

    private Context mContext;

    private FirebaseAuth firebaseAuth;

    private ViewGroup emailIconContainer;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(LayoutInflater.from(mContext), container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        emailIconContainer.findViewById(R.id.container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkInputs();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        binding.resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                binding.emailText.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(emailIconContainer);
                binding.emailIcon.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);

                binding.resetBtn.setEnabled(false);

                firebaseAuth.sendPasswordResetEmail(binding.emailEt.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                     Toast.makeText(mContext, "Email send successfully!", Toast.LENGTH_LONG).show();
                                }else{
                                    String error = task.getException().getMessage();
                                    binding.resetBtn.setEnabled(true);
                                    binding.emailText.setText(error);
                                    binding.emailText.setTextColor(getResources().getColor(R.color.error));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    binding.emailText.setVisibility(View.VISIBLE);
                                }
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    private void checkInputs() {
        if (TextUtils.isEmpty(binding.emailEt.getText())) {
            binding.resetBtn.setEnabled(false);
        }else {
            binding.resetBtn.setEnabled(true);
        }
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(binding.frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}