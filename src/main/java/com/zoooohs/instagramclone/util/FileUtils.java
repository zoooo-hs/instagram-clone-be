package com.zoooohs.instagramclone.util;

public class FileUtils {
    public static String getExtension(String path) {
        int pos = path.lastIndexOf( "." );
        if (pos == -1) {
            return "";
        }
        return "." + path.substring( pos + 1 );
    }

}
