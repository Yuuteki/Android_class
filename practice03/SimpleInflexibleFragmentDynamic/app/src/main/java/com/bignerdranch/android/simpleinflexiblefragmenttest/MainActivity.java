package com.bignerdranch.android.simpleinflexiblefragmenttest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragmentOne = fragmentManager.findFragmentById(R.id.fragment_one_container);
        if (fragmentOne == null) {
            fragmentOne = new OneFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_one_container, fragmentOne)
                    .commit();
        }

        Fragment fragmentTwo = fragmentManager.findFragmentById(R.id.fragment_two_container);
        if (fragmentTwo == null) {
            fragmentTwo = new TwoFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_two_container, fragmentTwo)
                    .commit();
        }
    }
}
