package com.app.artclass;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.app.artclass.database.StudentsRepository;
import com.app.artclass.fragments.AllStudentsListFragment;
import com.app.artclass.fragments.DialogHandler;
import com.app.artclass.fragments.GroupListFragment;
import com.app.artclass.fragments.StudentsPresentList;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // very important
        // must call initialisation
        // before using singleton classes
        initSingltones(getApplication());

        assert StudentsRepository.getInstance() != null;
        StudentsRepository.getInstance().initDefaultSettings();

        //start page
        StudentsPresentList list = new StudentsPresentList(LocalDate.now());
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentmain, list).commit();
    }

    private void initSingltones(Application application) {
        StudentsRepository.getInstance(application);
        DialogHandler.getInstance(application);
        UserSettings.getInstance().writeSettingsToRepository(StudentsRepository.getInstance());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_students_present){

            StudentsPresentList listFragment = new StudentsPresentList();
            fragmentManager.beginTransaction().replace(R.id.contentmain, listFragment).commit();

        }else if (id == R.id.nav_groups) {

            GroupListFragment groupListFragment = new GroupListFragment();
            fragmentManager.beginTransaction().replace(R.id.contentmain, groupListFragment).commit();

        }else if (id == R.id.nav_allstudents){

            AllStudentsListFragment allStudentsListFragment = new AllStudentsListFragment();
            fragmentManager.beginTransaction().replace(R.id.contentmain, allStudentsListFragment).commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
