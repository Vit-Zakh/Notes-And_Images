package com.sab.notesandimages.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sab.notesandimages.R;
import com.sab.notesandimages.databinding.FragmentHomeScreenBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeScreenFragment extends Fragment {
    FragmentHomeScreenBinding homeScreenBinding;

    public HomeScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        homeScreenBinding = FragmentHomeScreenBinding.inflate(inflater, container, false);

        homeScreenBinding.startBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeScreenFragment_to_authorizationFragment));

        return homeScreenBinding.getRoot();
    }
}
