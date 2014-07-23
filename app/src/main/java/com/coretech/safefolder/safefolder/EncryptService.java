package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.encrypticslibrary.impl.SafeFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by msneen on 7/22/2014.
 */
public class EncryptService {

    public String EncryptFiles(Activity mainActivity, ArrayList fileList, ArrayList emailList){
        FileService fileService = new FileService();
        ArrayList<String> files = fileService.GetFileList(mainActivity);

        for(String item: files) {
            File inputFile = new File(item);
            Toast.makeText(mainActivity.getApplication().getBaseContext(), inputFile.getName(), Toast.LENGTH_LONG).show();

            // Call Encryptics API here
            //String response = Encryptics.EncryptFile(intputFile, new(){
            //
            //});
        }

        return "";
    }
}
