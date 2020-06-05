package com.example.mymoviememoir;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mymoviememoir.entity.Person;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //adding the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);  //toolbar is defined in app_bar_main里面
        setSupportActionBar(toolbar);

        //initialise the drawer and navigation layout
        drawerLayout = findViewById(R.id.drawer_layout);  //drawer_layout is defined in activity_main.xml
        navigationView = findViewById(R.id.nv);  //nv is defined in activity_main.xml

        View headView = navigationView.getHeaderView(0);
        //set nav test
        TextView navTitle = headView.findViewById(R.id.navTitle);
        navTitle.setText("My Movie Memoir");

        ImageView imageIcon = headView.findViewById(R.id.imageIcon);
        imageIcon.setImageResource(R.drawable.navicon);


        //设置是nav drawer是开是关的状态
        toggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //these two lines of code show the navicon drawer icon top left hand side.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //register the drawer with the listener; 监听什么时候用户选择了nav里的内容
        navigationView.setNavigationItemSelectedListener(this);
//        replaceFragment(new HomePage(per));

        Bundle bundle = getIntent().getExtras();
        Person person = bundle.getParcelable("person");
        replaceFragment(new HomePage(person));
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        //receive data from multiple activity(signIn, signUp)
        Bundle bundle = getIntent().getExtras();
        Person person = bundle.getParcelable("person");

        switch (id) {
            //this id is menu/nav_id
            case R.id.homepage:
                replaceFragment(new HomePage(person));
                break;
            case R.id.movieSearch:
                replaceFragment(new MovieSearch(person));
                break;
            case R.id.watchlist:
                replaceFragment(new Watchlist(person));
                break;
            case R.id.watchlistFirebaseButton:
                replaceFragment(new WatchlistFirebase(person));
                break;
            case R.id.movieMemoir:
                replaceFragment(new MovieMemoir(person));
                break;
            case R.id.reports:
                replaceFragment(new Report(person));
                break;
            case R.id.map:
                replaceFragment(new Map(person));
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment nextFragment){
        //create Fragment manager first by calling getSupportFragmentManager()
        FragmentManager fragmentManager = getSupportFragmentManager();
        //begin transaction: prepare FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //replace the fragment  that is selected. parameter of replace():
        // 1st is container_id(which fragment inside it) which in content_main.xml, 2nd is selected fragment
        fragmentTransaction.replace(R.id.content_frame,nextFragment);     // content_frame 在content_main中的container
        //need commit, after done;
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

}
