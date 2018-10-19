package com.bootcamp.socialnetwork.util;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.CreateSharedLinkWithSettingsErrorException;

import java.io.IOException;
import java.io.InputStream;

public class DropboxClient {

    private final static String ACCESS_TOKEN = "YWdxrzwzgEAAAAAAAAADLZ8DVok6A_6gsn5MZAto_AbLDueAXGvSYoZTYQ2AhnDK";

    private final static String APP_KEY = "zcqnx2ltv4qlhdn";

    private final static DbxClientV2 client = new DbxClientV2(new DbxRequestConfig(APP_KEY), ACCESS_TOKEN);

    public static String uploadFile(InputStream in, String path) throws IOException, DbxException {
        client.files().uploadBuilder(path).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        String url;
        try {
            url = client.sharing().createSharedLinkWithSettings(path).getUrl();
        } catch (CreateSharedLinkWithSettingsErrorException e){
            return null;
        }
        // Look at https://cantonbecker.com/etcetera/2014/how-to-directly-link-or-embed-dropbox-images/
        url = url.replace("?dl=0", "?raw=1");

        return url;
    }

    public static void removeFile(String path) throws DbxException {
        client.files().delete(path);
    }

    public static void createFolder(String path) throws DbxException{
        client.files().createFolder(path);
    }

    public static void removeFolder(String path) throws DbxException{
        removeFile(path);
    }
}
