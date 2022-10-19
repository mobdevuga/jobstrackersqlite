package edu.uga.cs.jobstrackersqlite;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreen extends Fragment {

    private Button newLeadButton;
    private Button reviewLeadsButton;

    public MainScreen() {
        // Required empty public constructor
    }

    public static MainScreen newInstance() {
        MainScreen fragment = new MainScreen();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState ) {
        //Log.d( TAG, "onActivityCreated()" );

        super.onViewCreated(view,savedInstanceState);

        /*

        newLeadButton = getView().findViewById( R.id.button1 );
        reviewLeadsButton = getView().findViewById( R.id.button2 );

        newLeadButton.setOnClickListener( new NewLeadButtonClickListener() );
        reviewLeadsButton.setOnClickListener( new ReviewLeadsButtonClickListener() );

         */
        //View fragment = getActivity().findViewById(R.id.fragment);
    }

    private class NewLeadButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // create the new fragment
            AddJobLeadFragment addJobLeadFragment = new AddJobLeadFragment();

            // transition to the new fragment
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, addJobLeadFragment);

            // add it to the back stack to enable the back button
            fragmentTransaction.addToBackStack("main screen");

            // commit the transaction, i.e. make the changes
            fragmentTransaction.commit();
        }
    }

    private class ReviewLeadsButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // create the new fragment
            ReviewJobLeadsFragment reviewJobLeadsFragment = new ReviewJobLeadsFragment();

            // transition to the new fragment
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, reviewJobLeadsFragment);

            // add it to the back stack to enable the back button
            fragmentTransaction.addToBackStack("main screen");

            // commit the transaction, i.e. make the changes
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onResume() {
        //Log.d( TAG, "MainScreen.onResume()"  );
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle( getResources().getString( R.string.app_name ) );
    }
}