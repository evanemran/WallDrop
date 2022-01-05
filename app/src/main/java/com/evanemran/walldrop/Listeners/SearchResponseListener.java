package com.evanemran.walldrop.Listeners;

import com.evanemran.walldrop.Models.SearchApiResponse;

public interface SearchResponseListener {
    void onFetch(SearchApiResponse response, String message);
    void onError(String message);
}
