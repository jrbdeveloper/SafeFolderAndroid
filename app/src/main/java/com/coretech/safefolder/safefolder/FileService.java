package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by jabe on 7/23/2014.
 */
public class FileService {
    //Call this from the encrypt and Send Button.  This method gets the file(s) that were selected in the previous app.
    public ArrayList<String> GetFileList(Activity mainActivity) {
        Intent theIntent = mainActivity.getIntent();
        String theAction = theIntent.getAction();
        ArrayList<String> fileArray = new ArrayList<String>();

        if (Intent.ACTION_SEND.equals(theAction)) {
            Bundle bundle = mainActivity.getIntent().getExtras();
            Uri i = (Uri) bundle.get("android.intent.extra.STREAM");

            String inputfilename = i.getPath(); //getRealPathFromURI(i);
            String outputfilename = getFileNameFromPath(inputfilename);

            //mws delete the toast and encrypt here
            fileArray.add(inputfilename);

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(theAction) && theIntent.hasExtra(Intent.EXTRA_STREAM)) {

            ArrayList list = theIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

            for (Object p : list) {
                Uri uri = (Uri) p; /// do something with it.
                //Create input and output paths
                String inputfilename = uri.getPath(); //getRealPathFromURI(uri);
                String outputfilename = getFileNameFromPath(inputfilename);

                //mws delete the toast and encrypt here
                fileArray.add(inputfilename);
            }
        }

        return fileArray;
    }

    public String getFileNameFromPath(String input) {
        String[] temp;
        String delimeter = "/";
        temp = input.split(delimeter);
        return temp[temp.length - 1];
    }

    // Convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Activity mainActivity, Uri contentUri) {
        /*
        MediaStore.Images.Media.DATA,
        MediaStore.Video.Media.DATA,
        MediaStore.Audio.Media.DATA,
        MediaStore.Files.FileColumns.DATA
        */

        String [] proj = { MediaStore.MediaColumns.DATA };

        Cursor cursor = mainActivity.managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
