package com.example.beaker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.beaker.HomeAct;
import com.example.beaker.R;
import com.example.beaker.Suggestion;
import com.example.beaker.news;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    CardView cardNews,cardEvents,cardSugges;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        cardNews=root.findViewById(R.id.news);
        cardNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),news.class);
                startActivity(intent);
            }
        });
        cardEvents=root.findViewById(R.id.events);
        cardEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),HomeAct.class);
                startActivity(intent);
            }
        });
        cardSugges=root.findViewById(R.id.sugg);
        cardSugges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), Suggestion.class);
                startActivity(intent);
            }
        });
        return root;
    }
}