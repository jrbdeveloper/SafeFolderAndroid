package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.encrypticslibrary.impl.ContentBlob;
import com.encrypticslibrary.impl.SafeFile;
import com.encrypticslibrary.impl.AccountContext;
import com.encrypticsforandroid.encrypticsforandroid.AndroidAccountContextFactory;
import com.encrypticslibrary.api.response.EncrypticsResponseCode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by msneen on 7/22/2014.
 */
public class EncryptService {

   // String ENCRYPTICS_ACCOUNT_USERNAME = "michael.sneen@gmail.com";
    //String ENCRYPTICS_ACCOUNT_PASSWORD = "password";
    String ENCRYPTICS_ACCOUNT_USERNAME = "burgetest22@mailinator.com";
    String ENCRYPTICS_ACCOUNT_PASSWORD = "asdfasdf1A";

    public String EncryptFiles(Activity mainActivity, ArrayList<String> fileList, ArrayList<String> emailList){

        android.content.Context applicationContext = mainActivity.getApplicationContext(); // Assume this == Activity. If in a Fragment, use getActivity().getApplicationContext()
        AccountContext context = new AndroidAccountContextFactory(applicationContext).generateAccountContext(ENCRYPTICS_ACCOUNT_USERNAME, ENCRYPTICS_ACCOUNT_PASSWORD);

        EncrypticsResponseCode loginResponseCode = context.login();

        try {
            for(String item:fileList) {
                File file = new File(item);
                FileInputStream inputStream = new FileInputStream(file);

                ContentBlob fileBlob = new ContentBlob(inputStream, (int)file.length());
                OutputStream outputStream = new FileOutputStream(item +".safe");

                //what is fis and where does it gome from
                SafeFile.Builder builder = new SafeFile.Builder();

                builder.setSafeFileType("com.encryptics.email")
                        .setSafeFileVersion("1.2.0")
                        .setSubject("this is a subject")
                        .addContent(fileBlob)
                        .addRecipient("encrypt.ios@gmail.com");

                //for(String recipient : emailList) {
                //    builder.addRecipient(recipient);
                //}
                EncrypticsResponseCode code = builder.build(context, outputStream);
// Check for success
                if (EncrypticsResponseCode.SUCCESS == code) {
                    // .SAFE created successfully
                } else {
                    // Show Error or reconcile as appropriate
                }
            }
        }
        catch(Exception ex){
            //throw ex;
        }










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
