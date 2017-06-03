package com.ruto.nineanimeandroid.api.data;

import com.ruto.nineanimeandroid.utils.Helper;

/**
 * Created by Ruto on 2/22/2017.
 */

public enum ContentFilter {
    RecentlyUpdated,
    RecentlyUpdatedSub,
    RecentlyUpdatedDub,
    Trending;

    public String titlefy() {
        return Helper.titlefy(values()[ordinal()].name());
    }
}
