package com.ruto.nineanimeandroid.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Ruto on 2/22/2017.
 */

public class Helper {

    public static String titlefy(String str) {
        return str.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
    }


    public static void playMedia(Context ctx, String link, String mime) {
        Uri path = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, path);
        intent.setDataAndType(path, "video/" + mime);
        ctx.startActivity(intent);
    }
}
