package edu.uga.cs.jobstrackersqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

/**
 * The main activity class.  It just sets listeners for the two buttons.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // assigning ID of the toolbar to a variable
        toolbar = findViewById( R.id.toolbar );

        // using toolbar as ActionBar
        setSupportActionBar( toolbar );

        // Find our drawer view
        drawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawerToggle = setupDrawerToggle();

        drawerToggle.setDrawerIndicatorEnabled( true );
        drawerToggle.syncState();

        // Connect DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener( drawerToggle );

        // Find the drawer view
        navigationView = findViewById( R.id.nvView );
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem( menuItem );
                    return true;
                });
    }

    public void selectDrawerItem( MenuItem menuItem ) {
        Fragment fragment = null;

        // Create a new fragment based on the used selection in the nav drawer
        switch( menuItem.getItemId() ) {
            case R.id.menu_add:
                fragment = new AddJobLeadFragment();
                break;
            case R.id.menu_review:
                fragment = new ReviewJobLeadsFragment();
                break;
            case R.id.menu_help:
                fragment = new HelpFragment();
                break;
            case R.id.menu_close:
                finish();
                break;
            default:
                return;
        }

        // Set up the fragment by replacing any existing fragment in the main activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace( R.id.fragmentContainerView, fragment).addToBackStack("main screen" ).commit();

        /*
        // this is included here as a possible future modification
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked( true );
        // Set action bar title
        setTitle( menuItem.getTitle());
         */

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close );
    }

    // onPostCreate is called when activity start-up is complete after onStart()
    @Override
    protected void onPostCreate( Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged( @NonNull Configuration newConfig ) {
        super.onConfigurationChanged( newConfig );
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged( newConfig );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if( drawerToggle.onOptionsItemSelected( item ) ) {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

}
