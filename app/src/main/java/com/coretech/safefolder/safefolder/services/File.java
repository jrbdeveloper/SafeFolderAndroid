package com.coretech.safefolder.safefolder.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.ListItem;

import java.util.ArrayList;

/**
 * Created by john bales on 7/23/2014.
 */
public class File {

	//region Private Members
	private ArrayList<ListItem> _collection;
	//endregion

	//region Public Members

	//endregion

	//region Constructor
	public File(){
		if(_collection == null){
			_collection = new ArrayList<ListItem>();
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

            //String inputfilename = i.getPath();
            //String outputfilename = getNameFromPath(inputfilename);

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
                //String inputfilename = uri.getPath();
                //String outputfilename = getNameFromPath(inputfilename);

				if(!_collection.contains(inputItem.getText())){
					_collection.add(inputItem);
				}
            }
        }

		return _collection;
    }
	//endregion

	//region Private Methods
    public String getNameFromPath(String input) {
        String[] temp;
        String delimeter = "/";
        temp = input.split(delimeter);
        return temp[temp.length - 1];
    }
	//endregion
}
