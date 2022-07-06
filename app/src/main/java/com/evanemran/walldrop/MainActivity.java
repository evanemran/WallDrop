package com.evanemran.walldrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evanemran.walldrop.Adapters.CuratedRecyclerAdapter;
import com.evanemran.walldrop.Adapters.DrawerAdapter;
import com.evanemran.walldrop.Listeners.ClickListener;
import com.evanemran.walldrop.Listeners.CuratedClickListener;
import com.evanemran.walldrop.Listeners.CuratedResponseListener;
import com.evanemran.walldrop.Listeners.SearchResponseListener;
import com.evanemran.walldrop.Models.CuratedApiResponse;
import com.evanemran.walldrop.Models.NavMenu;
import com.evanemran.walldrop.Models.Photo;
import com.evanemran.walldrop.Models.SearchApiResponse;
import com.evanemran.walldrop.fragment.CategoryFragment;
import com.evanemran.walldrop.fragment.CollectionFragment;
import com.evanemran.walldrop.fragment.FavoritesFragment;
import com.evanemran.walldrop.fragment.HomeFragment;
import com.evanemran.walldrop.fragment.SettingsFragment;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CuratedClickListener, NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView_home;
    CuratedRecyclerAdapter curatedRecyclerAdapter;
    RequestManager manager;
    Button button_next, button_prev;
    int page;
    int search_page;
    boolean isSearched = false;
    String new_query = "";
    Toolbar toolbar;
    ProgressBar progressBar;
    DrawerLayout drawer;
    TextView version_name;
    RecyclerView recycler_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView_home = findViewById(R.id.recycler_home);
        button_next = findViewById(R.id.button_next);
        button_prev = findViewById(R.id.button_prev);
        version_name = findViewById(R.id.version_name);
        recycler_nav = findViewById(R.id.recycler_nav);

        try {
            PackageInfo pInfo =
                    getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version_name.setText("Version: " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        replaceFragment(new HomeFragment());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        toolbar.inflateMenu(R.menu.menu);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(
                this,drawer,toolbar,R.string.open_nav_drawer, R.string.close_nav_drawer
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupNavMenu();

        //toolbar
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.action_search)
                {
                    SearchView searchView = (SearchView) item.getActionView();
                    searchView.setQueryHint("Type here to search.");
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            new_query = query;
                            isSearched = true;
                            hideSoftKeyboard(MainActivity.this, searchView);
                            manager.searchWallpapers(search_listener, query, "1");
                            progressBar.setVisibility(View.VISIBLE);
                            recyclerView_home.setVisibility(View.GONE);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    });
                }

                return false;
            }
        });
    }

    private void setupNavMenu() {
        List<NavMenu> navMenuList = new ArrayList<>();

        navMenuList.add(NavMenu.HOME);
        navMenuList.add(NavMenu.CATEGORIES);
        navMenuList.add(NavMenu.COLLECTIONS);
        navMenuList.add(NavMenu.FAVORITES);
        navMenuList.add(NavMenu.SETTINGS);

        recycler_nav.setHasFixedSize(true);
        recycler_nav.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DrawerAdapter drawerAdapter = new DrawerAdapter(this, navMenuList, navMenuClickListener);
        recycler_nav.setAdapter(drawerAdapter);
    }

    private final CuratedResponseListener listener = new CuratedResponseListener() {
        @Override
        public void onFetch(CuratedApiResponse response, String message) {
            progressBar.setVisibility(View.GONE);
            recyclerView_home.setVisibility(View.VISIBLE);
            page = response.getPage();
            showData(response.getPhotos());
        }

        @Override
        public void onError(String message) {
            progressBar.setVisibility(View.GONE);
            recyclerView_home.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private void showData(List<Photo> photos) {
        recyclerView_home.setHasFixedSize(true);
        recyclerView_home.setLayoutManager(new GridLayoutManager(this, 2));
        curatedRecyclerAdapter = new CuratedRecyclerAdapter(this, photos, this);
        recyclerView_home.setAdapter(curatedRecyclerAdapter);
    }

    @Override
    public void onClick(Photo photo) {
//        Toast.makeText(MainActivity.this, photo.getPhotographer(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, WallpaperActivity.class)
        .putExtra("photo", photo));
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }

    private final ClickListener<NavMenu> navMenuClickListener = new ClickListener<NavMenu>() {
        @Override
        public void onCLicked(NavMenu object) {
            switch (object){
                case HOME:
                    replaceFragment(new HomeFragment());
                    break;
                case CATEGORIES:
                    replaceFragment(new CategoryFragment());
                    break;
                case COLLECTIONS:
                    replaceFragment(new CollectionFragment());
                    break;
                case FAVORITES:
                    replaceFragment(new FavoritesFragment());
                    break;
                case SETTINGS:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search.");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                new_query = query;
                isSearched = true;
                manager.searchWallpapers(search_listener, query, "1");
//                dialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private final SearchResponseListener search_listener = new SearchResponseListener() {
        @Override
        public void onFetch(SearchApiResponse response, String message) {
            progressBar.setVisibility(View.GONE);
            recyclerView_home.setVisibility(View.VISIBLE);
            if (response.getPhotos().isEmpty()){
                Toast.makeText(MainActivity.this, "No Image Found!!", Toast.LENGTH_SHORT).show();
                return;
            }
            search_page = response.getPage();
            showData(response.getPhotos());
        }

        @Override
        public void onError(String message) {
            progressBar.setVisibility(View.GONE);
            recyclerView_home.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

        }
    };

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}