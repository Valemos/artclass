package com.app.artclass;

import android.app.Application;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.app.artclass.database.DatabaseConverters;
import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.Student;
import com.app.artclass.fragments.AllStudentsListFragment;
import com.app.artclass.fragments.GroupListFragment;
import com.app.artclass.fragments.StudentCard;
import com.app.artclass.fragments.StudentsPresentList;
import com.app.artclass.list_adapters.LocalAdapter;
import com.app.artclass.list_adapters.SearchAdapter;
import com.google.android.material.navigation.NavigationView;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // very important
        // must be called before using singleton classes
        initBaseClasses(getApplication());

        // unhandled exceptions logged
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
            Logger.getInstance().appendLog(paramThrowable.getMessage());
            System.exit(2);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //start page
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, new StudentsPresentList(LocalDate.now())).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, new GroupListFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, new AllStudentsListFragment()).commit();
    }

    private void initBaseClasses(Application application) {
        StudentsRepository.getInstance(application);
        Logger.getInstance(this).appendLog(LocalDateTime.now().format(DatabaseConverters.getDateTimeFormatter())+": init complete");
//        StudentsRepository.getInstance().resetDatabase(this);
        StudentsRepository.getInstance().initDatabaseTest();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);

        //init search bar
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_item).getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                String[] from = new String[] {"_id", "name"};
                MatrixCursor suggestionsCursor;

                List<Student> students = new ArrayList<>();

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(students!=null && students.size()>0){
                        if(students.size()==1){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_content_id,
                                            new StudentCard(students.get(0))).addToBackStack(null).commit();
                        }else{
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_content_id,
                                            new AllStudentsListFragment(students)).addToBackStack(null).commit();
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    StudentsRepository.getInstance().getStudentsCursorByQuery(newText).observe(MainActivity.this,queryStudents -> {
                        this.students = queryStudents;
                        suggestionsCursor = new MatrixCursor(from);

                        int id = 0;
                        for (String name : students.stream().map(Student::getName).collect(Collectors.toList())) {
                            suggestionsCursor.newRow().add(from[0],id).add(from[1], name);
                            id++;
                        }

                        SearchAdapter searchAdapter = new SearchAdapter(
                                MainActivity.this,
                                getSupportFragmentManager(),
                                searchView,
                                R.layout.item_student_suggestion,
                                suggestionsCursor,
                                from[1],
                                R.id.student_name_view,
                                students);

                        searchView.setSuggestionsAdapter(searchAdapter);
                    });
                    return false;
                }
            });
            searchView.setIconifiedByDefault(false);
        }

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

        Fragment fragmentNew = null;

        if (id == R.id.nav_groups) {
            fragmentNew = new GroupListFragment();
        }else if (id == R.id.nav_allstudents){
            fragmentNew = new AllStudentsListFragment();
        }

        if(fragmentNew!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, fragmentNew).commit();
        }else{
            Logger.getInstance().appendLog(this.getClass(),"fragment not found");
            System.out.println(Logger.getInstance().getLogFileContent());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
