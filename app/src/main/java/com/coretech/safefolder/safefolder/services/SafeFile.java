package com.coretech.safefolder.safefolder.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.ListItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by john bales on 7/23/2014.
 */
public class SafeFile {

	//region Private Members
	private ArrayList<ListItem> _collection;
	private ArrayList<ListItem> _unsafeFiles;
	//endregion

	//region Public Members

	//endregion

	//region Constructor
	public SafeFile(){
		if(_collection == null){
			_collection = new ArrayList<ListItem>();
		}

		if(_unsafeFiles == null){
			_unsafeFiles = new ArrayList<ListItem>();
		}
	}
	//endregion

	public ArrayList<ListItem> Collection(){
		return _collection;
	}

	public void Collection(ArrayList<ListItem> collection){
		if(collection.size() > 0){
			_collection = collection;
		}
	}

	public ArrayList<ListItem> UnsafeFiles(){
		return _unsafeFiles;
	}

	public void UnsafeFiles(ArrayList<ListItem> files){
		if(files.size() > 0){
			_unsafeFiles = files;
		}
	}

	//region Public Methods
    //Call this from the encrypt and Send Button.  This method gets the file(s) that were selected in the previous app.
    public ArrayList<ListItem> GetCollection() {
		Intent theIntent = SafeFolder.Instance().getCurrentActivity().getIntent();
        String theAction = theIntent.getAction();

        if (Intent.ACTION_SEND.equals(theAction)) {
            Bundle bundle = SafeFolder.Instance().getCurrentActivity().getIntent().getExtras();
            Uri i = (Uri) bundle.get("android.intent.extra.STREAM");

            ListItem inputItem = new ListItem();
            ListItem outputItem = new ListItem();

            inputItem.setText(i.getPath());
            outputItem.setText(getNameFromPath(inputItem.getText()));

            //mws delete the toast and encrypt here
			if(!_collection.contains(inputItem.getText())){
				_collection.add(inputItem);
			}

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(theAction) && theIntent.hasExtra(Intent.EXTRA_STREAM)) {

            ArrayList list = theIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

            for (Object p : list) {
                Uri uri = (Uri) p; /// do something with it.

                //Create input and output paths
                ListItem inputItem = new ListItem();
                ListItem outputItem = new ListItem();

                inputItem.setText(uri.getPath());

				if(!_collection.contains(inputItem.getText())){
					_collection.add(inputItem);
				}
            }
        }

		return _collection;
    }

	/**
	 * Method that checks to see if there is an SDCard present
	 * @return boolean
	 */
	private boolean isExternalStoragePresent() {

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) { // We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) { // We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else { // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (!((mExternalStorageAvailable) && (mExternalStorageWriteable))) {
			Toast.makeText(SafeFolder.Instance().getApplicationContext(), "SD card not present", Toast.LENGTH_LONG).show();
		}

		return (mExternalStorageAvailable) && (mExternalStorageWriteable);
	}
	//endregion

	/**
	 * Method that creates the safe folder directory structure
	 */
	public void CreateSafeFolders(){
		if(isExternalStoragePresent()){
			File parent = new File(Environment.getExternalStorageDirectory().getPath() + "/SafeFolder");
			File safeFiles = new File(parent + "/safe");
			File unsafeFiles = new File(parent + "/unsafe");

			parent.mkdir();
			safeFiles.mkdir();
			unsafeFiles.mkdir();
		}
	}

	/**
	 * Method moves the file from the unsafe folder to the safe folder when its incrypted
	 * @param file
	 * @throws IOException
	 */
	public void Move(String file, String action) throws IOException {
		File baseFile = new File(file);
		String basePath = Environment.getExternalStorageDirectory().getPath() + "/SafeFolder";

		String safeDir = "/safe/";
		String unsafeDir = "/unsafe/";
		File sourceFile = null;
		File destFile = null;

		if(action == "encrypt"){
			sourceFile = new File(basePath + unsafeDir + baseFile.getName() + SafeFolder.Instance().getSafeExtension());
			destFile = new File(basePath + safeDir + baseFile.getName() + SafeFolder.Instance().getSafeExtension());
		}else{
			sourceFile = new File(basePath + safeDir + baseFile.getName());
			destFile = new File(basePath + unsafeDir + baseFile.getName());
		}

		if(!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();

			// previous code: destination.transferFrom(source, 0, source.size()); to avoid infinite loops, should be:
			long count = 0;
			long size = source.size();
			while((count += destination.transferFrom(source, count, size-count))<size);
		}
		finally {
			if(source != null) {
				sourceFile.delete();
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
	}

	//region Private Methods
    public String getNameFromPath(String input) {
        String[] temp;
        String delimeter = "/";
        temp = input.split(delimeter);
        return temp[temp.length - 1];
    }
	//endregion
}
