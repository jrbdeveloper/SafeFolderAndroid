package com.coretech.safefolder.safefolder.services;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import java.io.File;
import java.util.ArrayList;

import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.ListItem;

/**
 * Created by john bales on 7/23/2014.
 */
public class Email {

	//region Private Members
	private ArrayList<ListItem> _collection;
	//endregion

	//region Getters
	public ArrayList<ListItem> Collection(){
		return _collection;
	}
	//endregion

	//region Setters
	public void Collection(ArrayList<ListItem> collection){
		if(collection.size() > 0){
			_collection = collection;
		}
	}
	//endregion

	//region Constructor
	public Email(){
		if(_collection == null){
			_collection = new ArrayList<ListItem>();
		}
	}
	//endregion

	//region Public Methods
    public void Send(Activity mainActivity, ArrayList encryptedFileList, ArrayList<ListItem> emailList){
        String subject = "Safe Folder Security Notification";
        String body = "Attached are your encrypted files. Thank you for using Safe Folder";
        ArrayList<ListItem> attachmentPath = encryptedFileList;

        try {
			String[] newEmailList = new String[emailList.size()];

			for(int x = 0; x < emailList.size(); x++){
				newEmailList[x] = emailList.get(x).getText();
			}

            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_EMAIL, newEmailList);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            //intent.putExtra(Intent.EXTRA_TEXT, body);
			ArrayList<String> extra_text = new ArrayList<String>();
			extra_text.add(body);
			intent.putStringArrayListExtra(android.content.Intent.EXTRA_TEXT, extra_text);

            //intent.setType("message/rfc822");
			intent.setType("*/*");

            ArrayList<Uri> uri = new ArrayList<Uri>();
            for (int i = 0; i < attachmentPath.size(); i++) {
                File file = new File(attachmentPath.get(i).getText().replace("unsafe","safe") + SafeFolder.Instance().getSafeExtension());
                if(!uri.contains(Uri.fromFile(file))){
					uri.add(Uri.fromFile(file));
				}
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);

            mainActivity.startActivity(Intent.createChooser(intent, "Choose an email application..."));

        } catch (Exception ex) {
            ex.printStackTrace();
			SafeFolder.Instance().Log(ex.getMessage());
        }
	}
	//endregion
}