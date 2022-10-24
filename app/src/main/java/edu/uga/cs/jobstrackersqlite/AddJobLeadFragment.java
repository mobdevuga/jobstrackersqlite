package edu.uga.cs.jobstrackersqlite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class AddJobLeadFragment extends Fragment {

    private static final String TAG = "AddJobLeadFragment";

    private EditText companyNameView;
    private EditText phoneView;
    private EditText urlView;
    private EditText commentsView;
    private Button   saveButton;

    private JobLeadsData jobLeadsData = null;

    public AddJobLeadFragment() {
        // Required empty public constructor
    }

    public static AddJobLeadFragment newInstance() {
        AddJobLeadFragment fragment = new AddJobLeadFragment();
        return fragment;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_job_lead, container, false);
    }

    @Override
    public void onViewCreated( @NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        companyNameView = getView().findViewById( R.id.editText1 );
        phoneView = getView().findViewById( R.id.editText2 );
        urlView = getView().findViewById( R.id.editText3 );
        commentsView = getView().findViewById( R.id.editText4 );
        saveButton = getView().findViewById( R.id.button );

        saveButton.setOnClickListener( new SaveButtonClickListener()) ;

        // Create a JobLeadsData instance, since we will need to save a new JobLead in a file.
        jobLeadsData = new JobLeadsData( getActivity() );
    }

    // This is an AsyncTask class (it extends AsyncTask) to perform DB writing of a job lead, asynchronously.
    public class JobLeadDBWriter extends AsyncTask<JobLead, JobLead> {

        // This method will run as a background process to write into db.
        // It will be automatically invoked by Android, when we call the execute method
        // in the onClick listener of the Save button.
        @Override
        protected JobLead doInBackground( JobLead... jobLeads ) {
            jobLeadsData.storeJobLead( jobLeads[0] );
            return jobLeads[0];
        }

        // This method will be automatically called by Android once the writing to the database
        // in a background process has finished.  Note that doInBackground returns a JobLead object.
        // That object will be passed as argument to onPostExecute.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute( JobLead jobLead ) {
            // Show a quick confirmation message
            Toast.makeText( getActivity(), "Job lead created for " + jobLead.getCompanyName(),
                    Toast.LENGTH_SHORT).show();

            // Clear the EditTexts for next use.
            companyNameView.setText( "" );
            phoneView.setText( "" );
            urlView.setText( "" );
            commentsView.setText( "" );

            Log.d( TAG, "Job lead saved: " + jobLead );
        }
    }

    private class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            String companyName = companyNameView.getText().toString();
            String phone = phoneView.getText().toString();
            String url = urlView.getText().toString();
            String comments = commentsView.getText().toString();

            JobLead jobLead = new JobLead( companyName, phone, url, comments );

            // Store this new job lead in the database asynchronously,
            // without blocking the UI thread.
            new JobLeadDBWriter().execute( jobLead );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // open the database in onResume
        if( jobLeadsData != null )
            jobLeadsData.open();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle( getResources().getString( R.string.app_name ) );
    }

    // We need to save job leads into a file as the activity stops being a foreground activity
    @Override
    public void onPause() {
        Log.d( TAG, "AddJobLeadFragment.onPause()" );
        super.onPause();
        // close the database in onPause
        if( jobLeadsData != null )
            jobLeadsData.close();
    }
}