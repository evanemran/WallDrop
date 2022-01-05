package com.evanemran.walldrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.evanemran.walldrop.Adapters.CuratedRecyclerAdapter;
import com.evanemran.walldrop.Listeners.CuratedClickListener;
import com.evanemran.walldrop.Listeners.CuratedResponseListener;
import com.evanemran.walldrop.Listeners.SearchResponseListener;
import com.evanemran.walldrop.Models.CuratedApiResponse;
import com.evanemran.walldrop.Models.Photo;
import com.evanemran.walldrop.Models.SearchApiResponse;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CuratedClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView_home = findViewById(R.id.recycler_home);
        button_next = findViewById(R.id.button_next);
        button_prev = findViewById(R.id.button_prev);
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);

        progressBar = (ProgressBar)findViewById(R.id.loader);
        Sprite anim = new Wave();
        progressBar.setIndeterminateDrawable(anim);

        progressBar.setVisibility(View.VISIBLE);

        manager = new RequestManager(this);
        manager.getCuratedWallpapers(listener, "1");

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView_home.setVisibility(View.GONE);

                if (isSearched){
                    String search_next_page = String.valueOf(search_page+1);
                    manager.searchWallpapers(search_listener, new_query, search_next_page);
                }
                else{
                    String next_page = String.valueOf(page+1);
                    manager.getCuratedWallpapers(listener, next_page);
                }
            }
        });
        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page>1){
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView_home.setVisibility(View.GONE);
                    String prev_page = String.valueOf(page-1);
                    manager.getCuratedWallpapers(listener, prev_page);
                }
            }
        });

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

}