package edu.uga.cs.jobstrackersqlite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ReviewJobLeadsFragment extends Fragment
        implements AddJobLeadDialogFragment.AddJobLeadDialogListener {

    private static final String TAG = "ReviewJobLeadsFragment";

    private JobLeadsData jobLeadsData = null;
    private List<JobLead> jobLeadsList;

    private RecyclerView recyclerView;
    private JobLeadRecyclerAdapter recyclerAdapter;

    private SearchFragmentMenuProvider searchFragmentMenuProvider;

    // Required default constructor
    public ReviewJobLeadsFragment() {
        // Required empty public constructor
    }

    // And our newInstance static method
    public static ReviewJobLeadsFragment newInstance() {
        ReviewJobLeadsFragment fragment = new ReviewJobLeadsFragment();
        return fragment;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_review_job_leads, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        recyclerView = getView().findViewById( R.id.recyclerView );
        FloatingActionButton floatingButton = getView().findViewById( R.id.floatingActionButton );

        floatingButton.setOnClickListener(v -> {
            AddJobLeadDialogFragment newFragment = new AddJobLeadDialogFragment();
            newFragment.setHostFragment( ReviewJobLeadsFragment.this );
            newFragment.show( getParentFragmentManager(), null );
        });

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager( layoutManager );

        jobLeadsList = new ArrayList<JobLead>();

        // Create a JobLeadsData instance, since we will need to save a new JobLead to the dn.
        // Note that even though more activites may create their own instances of the JobLeadsData
        // class, we will be using a single instance of the JobLeadsDBHelper object, since
        // that class is a singleton class.
        jobLeadsData = new JobLeadsData( getActivity() );

        // Open that database for reading of the full list of job leads.
        // Note that onResume() hasn't been called yet, so the db open in it
        // was not called yet!
        jobLeadsData.open();

        // Execute the retrieval of the job leads in an asynchronous way,
        // without blocking the main UI thread.
        new JobLeadDBReader().execute();

        // Prepare the MenuProvider for the search capability
        searchFragmentMenuProvider = new SearchFragmentMenuProvider();
        requireActivity().addMenuProvider( searchFragmentMenuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED );
    }

    // This is an AsyncTask class (it extends AsyncTask) to perform DB reading of job leads, asynchronously.
    private class JobLeadDBReader extends AsyncTask<Void, List<JobLead>> {
        // This method will run as a background process to read from db.
        // It returns a list of retrieved JobLead objects.
        // It will be automatically invoked by Android, when we call the execute method
        // in the onCreate callback (the job leads review activity is started).
        @Override
        protected List<JobLead> doInBackground( Void... params ) {
            List<JobLead> jobLeadsList = jobLeadsData.retrieveAllJobLeads();

            Log.d( TAG, "JobLeadDBReader: Job leads retrieved: " + jobLeadsList.size() );

            return jobLeadsList;
        }

        // This method will be automatically called by Android once the db reading
        // background process is finished.  It will then create and set an adapter to provide
        // values for the RecyclerView.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute( List<JobLead> jobsList ) {
            Log.d( TAG, "JobLeadDBReader: jobsList.size(): " + jobsList.size() );
            jobLeadsList.addAll( jobsList );

            // create the RecyclerAdapter and set it for the RecyclerView
            recyclerAdapter = new JobLeadRecyclerAdapter( getActivity(), jobLeadsList );
            recyclerView.setAdapter( recyclerAdapter );

            // Once we have the RecyclerAdapter, provide it to our MenuProvider
            // We will be filtering JobLeads on the RecyclerAdapter while searching in it.
            searchFragmentMenuProvider.setRecyclerAdapter( recyclerAdapter );
        }
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
            // Update the recycler view to include the new job lead
            jobLeadsList.add( jobLead );

            // Sync the originalValues list in the recyler adapter to the new updated list (JoLeadsList)
            recyclerAdapter.sync();

            // Notify the adapter that an item has been inserted
            recyclerAdapter.notifyItemInserted(jobLeadsList.size() - 1 );

            // Reposition the view to show to newly added item by smoothly scrolling to it
            recyclerView.smoothScrollToPosition( jobLeadsList.size() - 1 );

            Log.d( TAG, "Job lead saved: " + jobLead );
        }
    }

    // This is an implementation of a callback for the AddJobLeadDialogFragment, which saves
    // a new job lead.
    // This method is called from the AddJobLeadDialogFragment in a listener for the "OK" button.
    @Override
    public void saveNewJobLead( JobLead jobLead ) {

        // add the new job lead
        new JobLeadDBWriter().execute( jobLead );

        // Reposition the RecyclerView to show the JobLead most recently added (as the last item on the list).
        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
        // reposition the item into view (show the last item on the list).
        // the post method adds the argument (Runnable) to the message queue to be executed
        // by Android on the main UI thread.  It will be done *after* the setAdapter call
        // updates the list items, so the repositioning to the last item will take place
        // on the complete list of items.
        recyclerView.post( new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition( jobLeadsList.size()-1 );
            }
        } );
    }

    // This is the new method for adding a menu; we are adding a search item to the toolbar.
    // You may find online examples with the onCreateOptionsMenu callback,
    // but this callback has been deprecated. Hence, we are using a MenuProvider now.
    private class SearchFragmentMenuProvider implements MenuProvider {

        // We will be filtering items on this RecyclerAdapter
        private JobLeadRecyclerAdapter recyclerAdapter;

        @Override
        public void onCreateMenu( @NonNull Menu menu, @NonNull MenuInflater menuInflater ) {

            //inflate the menu with the search box
            menuInflater.inflate( R.menu.search_menu, menu ); // Inflate your menu XML

            // Get the search menu item and then the SearchView
            MenuItem searchMenu = menu.findItem( R.id.appSearchBar );
            SearchView searchView = (SearchView) searchMenu.getActionView();

            // Provide a search hint
            searchView.setQueryHint( "Search terms" );

            // Get the EditText we will use for searching
            EditText searchEditText = searchView.findViewById( androidx.appcompat.R.id.search_src_text );

            // Chanage the colors for the background, text, and hint text in the search box
            searchEditText.setBackgroundColor( getResources().getColor( R.color.white ) );
            searchEditText.setTextColor( getResources().getColor( R.color.colorPrimaryDark ) );
            searchEditText.setHintTextColor( getResources().getColor( R.color.colorPrimary ) );

            // Set the listener associated with the search box
            searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit( String query ) {
                    Log.d(TAG, "Query submitted");
                    return false;
                }

                // This method will implement an incremental search for the search words.
                // It is called every time there is a change in the search terms in the search box.
                @Override
                public boolean onQueryTextChange( String newText ) {
                    recyclerAdapter.getFilter().filter( newText );
                    return true;
                }
            });
        }

        // This callback will not be used, but must be there, as required by MenuProvider
        @Override
        public boolean onMenuItemSelected( @NonNull MenuItem searchMenu ) {
            // Nothing to do here...
            return false;
        }

        // Set the Recycler adapter for the search;
        // we will be filtering the items here based on search terms.
        public void setRecyclerAdapter(JobLeadRecyclerAdapter recyclerAdapter ) {
            this.recyclerAdapter = recyclerAdapter;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Open the database
        if( jobLeadsData != null && !jobLeadsData.isDBOpen() ) {
            jobLeadsData.open();
            Log.d( TAG, "ReviewJobLeadsFragment.onResume(): opening DB" );
        }

        // Update the app name in the Action Bar to be the same as the app's name
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle( getResources().getString( R.string.app_name ) );
    }

    // We need to save job leads into a file as the activity stops being a foreground activity
    @Override
    public void onPause() {
        super.onPause();

        // close the database in onPause
        if( jobLeadsData != null ) {
            jobLeadsData.close();
            Log.d( TAG, "ReviewJobLeadsFragment.onPause(): closing DB" );
        }
    }
}