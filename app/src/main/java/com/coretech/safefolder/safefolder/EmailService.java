package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by John on 7/23/2014.
 */
public class EmailService {

	//region Private Members
	private SafeFolder _application;
	//endregion

	//region Constructor
	public EmailService(SafeFolder application){
		application = _application;
	}
	//endregion

    public void Send(Activity mainActivity, ArrayList encryptedFileList, ArrayList emailList){
        String toAddress = TextUtils.join(",", emailList);
        String subject = "";
        String body = "";
        ArrayList<String> attachmentPath = encryptedFileList;

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
                File file = new File(attachmentPath.get(i)+ ".safe");
                uri.add(Uri.fromFile(file));
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);

            mainActivity.startActivity(Intent.createChooser(intent, "Choose an email application..."));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
