package com.nexorigin.simp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyRewardsFragment extends Fragment {

    private RecyclerView rewardsRecyclerView;

    public MyRewardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        rewardsRecyclerView = view.findViewById(R.id.my_rewards_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rewardsRecyclerView.setLayoutManager(linearLayoutManager);

        List<RewardModel> rewardModelList = new ArrayList<>();
        rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
        rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
        rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
        rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
        rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
        rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));
        rewardModelList.add(new RewardModel("Cashback", "till 13th, Aug 2015", "GET 20% OFF on any product above Rs.500/- and below Rs.2500/-."));

        MyRewardAdapter myRewardAdapter = new MyRewardAdapter(rewardModelList,false);
        rewardsRecyclerView.setAdapter(myRewardAdapter);
        myRewardAdapter.notifyDataSetChanged();

        return view;
    }
}