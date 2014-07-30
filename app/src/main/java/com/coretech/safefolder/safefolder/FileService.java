package com.coretech.safefolder.safefolder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.util.ArrayList;

/**
 * Created by jabe on 7/23/2014.
 */
public class FileService {

	//region Private Members
	private SafeFolder _application;
	//endregion

	//region Constructor
	public FileService(SafeFolder application){
		_application = application;
	}
	//endregion

	//region Public Methods
    //Call this from the encrypt and Send Button.  This method gets the file(s) that were selected in the previous app.
    public ArrayList<String> GetFileList() {
		Intent theIntent = _application.getCurrentActivity().getIntent();
        String theAction = theIntent.getAction();

        if (Intent.ACTION_SEND.equals(theAction)) {
            Bundle bundle = _application.getCurrentActivity().getIntent().getExtras();
            Uri i = (Uri) bundle.get("android.intent.extra.STREAM");

            String inputfilename = i.getPath();
            String outputfilename = getFileNameFromPath(inputfilename);

            //mws delete the toast and encrypt here
			_application.FileList.add(inputfilename);

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(theAction) && theIntent.hasExtra(Intent.EXTRA_STREAM)) {

            ArrayList list = theIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

            for (Object p : list) {
                Uri uri = (Uri) p; /// do something with it.

                //Create input and output paths
                String inputfilename = uri.getPath();
                String outputfilename = getFileNameFromPath(inputfilename);

                //mws delete the toast and encrypt here
				_application.FileList.add(inputfilename);
            }
        }

		return _application.FileList;
    }
	//endregion

	//region Private Methods
    private String getFileNameFromPath(String input) {
        String[] temp;
        String delimeter = "/";
        temp = input.split(delimeter);
        return temp[temp.length - 1];
    }
	//endregion
}
