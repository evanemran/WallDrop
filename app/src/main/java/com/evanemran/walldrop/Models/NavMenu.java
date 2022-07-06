package com.evanemran.walldrop.Models;


import com.evanemran.walldrop.R;

public enum NavMenu {
    HOME("Home", R.drawable.ic_home),
    CATEGORIES("Categories", R.drawable.ic_category),
    COLLECTIONS("Collections", R.drawable.ic_collection),
    FAVORITES("Favorites", R.drawable.ic_fav),
    SETTINGS("Settings", R.drawable.ic_settings),
    ;

    private String title = "";
    private int icon = 0;

    NavMenu(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
