package edu.uga.cs.jobstrackersqlite;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all job leads.
 */
public class JobLeadRecyclerAdapter
                extends RecyclerView.Adapter<JobLeadRecyclerAdapter.JobLeadHolder>
                implements Filterable {

    public static final String DEBUG_TAG = "JobLeadRecyclerAdapter";

    private final Context context;

    private List<JobLead> values;
    private List<JobLead> originalValues;

    public JobLeadRecyclerAdapter( Context context, List<JobLead> jobLeadList ) {
        this.context = context;
        this.values = jobLeadList;
        this.originalValues = new ArrayList<JobLead>( jobLeadList );
    }

    // reset the originalValues to the current contents of values
    public void sync()
    {
        originalValues = new ArrayList<JobLead>( values );
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    public static class JobLeadHolder extends RecyclerView.ViewHolder {

        TextView companyName;
        TextView phone;
        TextView url;
        TextView comments;

        public JobLeadHolder( View itemView ) {
            super( itemView );

            companyName = itemView.findViewById( R.id.companyName );
            phone = itemView.findViewById( R.id.phone );
            url = itemView.findViewById( R.id.url );
            comments = itemView.findViewById( R.id.comments );
        }
    }

    @NonNull
    @Override
    public JobLeadHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        // We need to make sure that all CardViews have the same, full width, allowed by the parent view.
        // This is a bit tricky, and we must provide the parent reference (the second param of inflate)
        // and false as the third parameter (don't attach to root).
        // Consequently, the parent view's (the RecyclerView) width will be used (match_parent).
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.job_lead, parent, false );
        return new JobLeadHolder( view );
    }

    // This method fills in the values of a holder to show a JobLead.
    // The position parameter indicates the position on the list of jobs list.
    @Override
    public void onBindViewHolder( JobLeadHolder holder, int position ) {

        JobLead jobLead = values.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + jobLead );

        holder.companyName.setText( jobLead.getCompanyName());
        holder.phone.setText( jobLead.getPhone() );
        holder.url.setText( jobLead.getUrl() );
        holder.comments.setText( jobLead.getComments() );
    }

    @Override
    public int getItemCount() {
        if( values != null )
            return values.size();
        else
            return 0;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<JobLead> list = new ArrayList<JobLead>( originalValues );
                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0) {
                    filterResults.count = list.size();
                    filterResults.values = list;
                }
                else{
                    List<JobLead> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for( JobLead jobLead : list ) {
                        // check if either the company name or the comments contain the search string
                        if( jobLead.getCompanyName().toLowerCase().contains( searchStr )
                                || jobLead.getComments().toLowerCase().contains( searchStr ) ) {
                            resultsModel.add( jobLead );
                        }
/*
                        // this may be a faster approach with a long list of items to search
                        if( jobLead.getCompanyName().regionMatches( true, i, searchStr, 0, length ) )
                            return true;

 */
                    }

                    filterResults.count = resultsModel.size();
                    filterResults.values = resultsModel;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                values = (ArrayList<JobLead>) results.values;
                notifyDataSetChanged();
                if( values.size() == 0 ) {
                    Toast.makeText( context, "Not Found", Toast.LENGTH_LONG).show();
                }
            }

        };
        return filter;
    }
}
