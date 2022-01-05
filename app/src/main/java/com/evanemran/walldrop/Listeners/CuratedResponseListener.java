package com.evanemran.walldrop.Listeners;

import com.evanemran.walldrop.Models.CuratedApiResponse;

public interface CuratedResponseListener {
    void onFetch(CuratedApiResponse response, String message);
    void onError(String message);
}
