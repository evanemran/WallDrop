package com.evanemran.walldrop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evanemran.walldrop.Adapters.CuratedRecyclerAdapter;
import com.evanemran.walldrop.Listeners.CuratedClickListener;
import com.evanemran.walldrop.Listeners.CuratedResponseListener;
import com.evanemran.walldrop.Listeners.SearchResponseListener;
import com.evanemran.walldrop.MainActivity;
import com.evanemran.walldrop.Models.CuratedApiResponse;
import com.evanemran.walldrop.Models.Photo;
import com.evanemran.walldrop.Models.SearchApiResponse;
import com.evanemran.walldrop.R;
import com.evanemran.walldrop.RequestManager;
import com.evanemran.walldrop.WallpaperActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

import java.util.List;

public class HomeFragment extends Fragment {
    View view;
    RecyclerView recyclerView_home;
    CuratedRecyclerAdapter curatedRecyclerAdapter;
    RequestManager manager;
    Button button_next, button_prev;
    int page;
    int search_page;
    boolean isSearched = false;
    String new_query = "";
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView_home = view.findViewById(R.id.recycler_home);
        button_next = view.findViewById(R.id.button_next);
        button_prev = view.findViewById(R.id.button_prev);

        progressBar = (ProgressBar)view.findViewById(R.id.loader);
        Sprite anim = new Wave();
        progressBar.setIndeterminateDrawable(anim);

        progressBar.setVisibility(View.VISIBLE);

        manager = new RequestManager(getContext());
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

        return view;
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
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    };

    private void showData(List<Photo> photos) {
        recyclerView_home.setHasFixedSize(true);
        recyclerView_home.setLayoutManager(new GridLayoutManager(getContext(), 2));
        curatedRecyclerAdapter = new CuratedRecyclerAdapter(getContext(), photos, curatedClickListener);
        recyclerView_home.setAdapter(curatedRecyclerAdapter);
    }

    private final SearchResponseListener search_listener = new SearchResponseListener() {
        @Override
        public void onFetch(SearchApiResponse response, String message) {
            progressBar.setVisibility(View.GONE);
            recyclerView_home.setVisibility(View.VISIBLE);
            if (response.getPhotos().isEmpty()){
                Toast.makeText(getContext(), "No Image Found!!", Toast.LENGTH_SHORT).show();
                return;
            }
            search_page = response.getPage();
            showData(response.getPhotos());
        }

        @Override
        public void onError(String message) {
            progressBar.setVisibility(View.GONE);
            recyclerView_home.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        }
    };

    private final CuratedClickListener curatedClickListener = new CuratedClickListener() {
        @Override
        public void onClick(Photo photo) {
            startActivity(new Intent(getContext(), WallpaperActivity.class)
                    .putExtra("photo", photo));
        }
    };
}
