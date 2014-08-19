package com.coretech.safefolder.safefolder.services;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
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

	//region Public Members
	//endregion

	public ArrayList<ListItem> Collection(){
		return _collection;
	}

	public void Collection(ArrayList<ListItem> collection){
		if(collection.size() > 0){
			_collection = collection;
		}
	}

	//region Constructor
	public Email(){
		if(_collection == null){
			_collection = new ArrayList<ListItem>();
		}
	}
	//endregion

	//region Public Methods
    public void Send(Activity mainActivity, ArrayList encryptedFileList, ArrayList<ListItem> emailList){
        String toAddress = TextUtils.join(", ", emailList);
        String subject = "Safe Folder Security Notification";
        String body = "Attached are your encrypted files. Thank you for using Safe Folder";
        ArrayList<ListItem> attachmentPath = encryptedFileList;

        try {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { toAddress });
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            //intent.setType("message/rfc822");
			intent.setType("*/*");



            ArrayList<Uri> uri = new ArrayList<Uri>();
            for (int i = 0; i < attachmentPath.size(); i++) {
                File file = new File(attachmentPath.get(i).getText() + SafeFolder.Instance().getSafeExtension());
                uri.add(Uri.fromFile(file));
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);

            mainActivity.startActivity(Intent.createChooser(intent, "Choose an email application..."));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	//endregion
}