package com.app.artclass;

import android.app.Application;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.app.artclass.database.StudentsRepository;
import com.app.artclass.database.entity.Student;
import com.app.artclass.fragments.AllStudentsListFragment;
import com.app.artclass.fragments.GroupListFragment;
import com.app.artclass.fragments.HelpFragment;
import com.app.artclass.fragments.MainPageFragment;
import com.app.artclass.fragments.SettingsFragment;
import com.app.artclass.fragments.StudentCard;
import com.app.artclass.list_adapters.SearchAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem searchMenuItem;

    public static String signInSuccessfullTag = "SInSuccess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.getInstance().appendLog(getClass(),"main activity init");

        // unhandled exceptions logged
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
            Logger.getInstance().appendLog(paramThread.getName() + " : " + paramThrowable.getMessage());
            System.exit(2);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // setup sign in button if account not found
        boolean signInSuccessful = getIntent().getExtras().getBoolean(signInSuccessfullTag);
        if(signInSuccessful){
            GoogleSignInAccount account = UserSettings.getInstance().getUserAccount();

            if(account!=null){
                SignInButton signInButton = headerView.findViewById(R.id.nav_sign_in_btn);
                signInButton.setVisibility(View.GONE);

                ImageView accountImageView = headerView.findViewById(R.id.nav_header_image);
                Uri personPhotoUrl = account.getPhotoUrl();
                Picasso.get().load(personPhotoUrl)
                        .error(R.mipmap.ic_launcher_round)
                        .into(accountImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                Logger.getInstance().appendLog(this.getClass(), e.getMessage());
                            }
                        });


                TextView nicknameView = headerView.findViewById(R.id.nav_header_nickname);
                nicknameView.setVisibility(View.VISIBLE);
                nicknameView.setText(account.getDisplayName()!=null?account.getDisplayName():account.getEmail());
            }else{
                Logger.getInstance().appendLog(getClass(),"account not found while got sign in success");
            }

        }
        else{
            SignInButton signInButton = findViewById(R.id.nav_sign_in_btn);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setVisibility(View.VISIBLE);

            signInButton.setOnClickListener(v -> {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken(UserSettings.getGoogleAppToken())
                        .build();

                Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
                startActivityForResult(signInIntent, UserSettings.signInRequestCode);
            });

            View userInfoView = headerView.findViewById(R.id.user_info_layout);
            userInfoView.setVisibility(View.GONE);

            UserSettings.getInstance().initGoogleAccount(null);
        }


        //test start page
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, new StudentsPresentList(LocalDate.now())).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, new GroupListFragment()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, new AllStudentsListFragment()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, new HelpFragment()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UserSettings.signInRequestCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            UserSettings.getInstance().initGoogleAccount(account);

            Intent switchToMainApp = new Intent(this,MainActivity.class);
            switchToMainApp.putExtra(MainActivity.signInSuccessfullTag, true);
            startActivity(switchToMainApp);
        } catch (ApiException e) {
            Log.println(Log.ERROR, "Sign In problem","signInResult:failed code=" + e.getStatusCode());
            Logger.getInstance().appendLog("Sign In problem: signInResult:failed code=" + e.getStatusCode());
            Intent switchToMainApp = new Intent(this,MainActivity.class);
            switchToMainApp.putExtra(MainActivity.signInSuccessfullTag, false);
            startActivity(switchToMainApp);
        }
    }

    @Override
    public void onBackPressed() {
        if(searchMenuItem!=null){
            searchMenuItem.collapseActionView();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!getSupportFragmentManager().popBackStackImmediate()){
                this.moveTaskToBack(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);

        //init search bar
        searchMenuItem = menu.findItem(R.id.search_menu_item);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                String[] from = new String[] {"_id", "name"};
                MatrixCursor suggestionsCursor;
                SearchAdapter searchAdapter;

                List<Student> students = new ArrayList<>();

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(students!=null && students.size()>0){
                        if(students.size()==1){
                            if(getSupportFragmentManager().findFragmentByTag("StudentCardSearch")==null){
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_content_id,
                                                new StudentCard(students.get(0)), "StudentCardSearch").addToBackStack(null).commit();
                            }
                        }
                        else{
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_content_id,
                                            new AllStudentsListFragment(students), "StudentCardSearch").addToBackStack(null).commit();
                        }
                        searchView.post(() -> {
                            searchView.setQuery("", false);
                            searchMenuItem.collapseActionView();
                            getSupportFragmentManager().findFragmentByTag("StudentCardSearch").getView().requestFocus();
                        });
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    StudentsRepository.getInstance().getStudentsListByQuery(newText).observe(MainActivity.this, queryStudents -> {
                        this.students = queryStudents;
                        suggestionsCursor = new MatrixCursor(from);

                        int id = 0;
                        for (String name : students.stream().map(Student::getName).collect(Collectors.toList())) {
                            if(!name.equals("")) {
                                suggestionsCursor.newRow().add(from[0], id).add(from[1], name);
                                id++;
                            }
                        }

                        searchAdapter = new SearchAdapter(
                                MainActivity.this,
                                getSupportFragmentManager(),
                                searchMenuItem,
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
            searchView.setIconifiedByDefault(true);
            searchView.setSubmitButtonEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Fragment fragmentNew;
        if (id == R.id.action_settings) {
            fragmentNew = new SettingsFragment();
        }else if(id == R.id.action_help){
            fragmentNew = new HelpFragment();
        }else{
            Logger.getInstance().appendLog(getClass(),"menu item have no fragment");
            return super.onOptionsItemSelected(item);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_id, fragmentNew, fragmentNew.getTag()).addToBackStack(null).commit();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragmentNew = null;

        if(id == R.id.nav_main_page){
            fragmentNew = new MainPageFragment();
        }else if (id == R.id.nav_groups) {
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
