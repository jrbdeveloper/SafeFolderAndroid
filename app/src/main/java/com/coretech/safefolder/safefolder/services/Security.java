package com.coretech.safefolder.safefolder.services;

import android.os.AsyncTask;
import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.ListItem;
import com.coretech.safefolder.safefolder.entities.User;
import com.encrypticslibrary.api.response.EncrypticsResponseCode;
import com.encrypticslibrary.impl.SafeFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msneen on 7/22/2014.
 */
public class Security {

	//region Private Members
	//private OnFinishedListener callback;
	//endregion

	//region Constructor
	public Security(){
	}
	//endregion

	//region Encryption
	public void EncryptFiles(ArrayList<ListItem> fileList, ArrayList<ListItem> emailList, boolean sendEmail){
		new EncryptTask(this, sendEmail).execute(fileList, emailList);
	}

	private static class EncryptTask extends AsyncTask<List<ListItem>, Void, EncrypticsResponseCode> {

		boolean _sendEmail;
		//EncryptService callback;

		public EncryptTask(Security service, boolean sendEmail) {
			//this.callback = service;
			_sendEmail = sendEmail;
		}

		@Override
		protected EncrypticsResponseCode doInBackground(List<ListItem>... lists) {

			List<ListItem> fileList = lists[0];
			List<ListItem> recipientList = lists[1];

			// Authenticate the user
			if(!SafeFolder.Instance().User().IsLoggedIn()){
				EncrypticsResponseCode loginResponseCode = EncrypticsResponseCode.LOGIN_DENIED;
				User user = new User(SafeFolder.Instance().User().Account().getUsername(), SafeFolder.Instance().User().Account().getPassword());
				loginResponseCode = SafeFolder.Instance().User().Account().Authenticate(null);

				if(EncrypticsResponseCode.SUCCESS != loginResponseCode) {
					return loginResponseCode;
				}
			}

			EncrypticsResponseCode encryptResponseCode = EncrypticsResponseCode.UNKNOWN;

			try {

				for(ListItem item : fileList) {
					if(!item.getText().contains(SafeFolder.Instance().getSafeExtension())){
						File file = new File(item.getText());
						FileInputStream fis = new FileInputStream(file);
						OutputStream outputStream = new FileOutputStream(item + SafeFolder.Instance().getSafeExtension());

						SafeFile.Builder builder = new SafeFile.Builder();

						// The SafeFileType is the type you will use to identify your company's .SAFE file from other .SAFE files that may be also named .SAFE
						builder.setSafeFileType("com.coretech.safefolder")//TODO accept or reject this name
								// The SafeFileVersion is your company's version so you can better identify your own changes to the architecture of the .SAFE
								.setSafeFileVersion("1.2.0")//TODO settle on a version number, probably 1.0 or similar
										// Subject is required, but if you won't use it, it can be anything that's not an empty string
										// However, it's one of a few identifying pieces of a .SAFE file prior to its decryption (the subject is never encrypted)
								.setSubject("not used")
								.addContent(fis, (int)file.length()); // Adding your content here!

						for(ListItem recipient : recipientList) {
							builder.addRecipient(recipient.getText());
						}

						// Building the .SAFE file is another network operation, keeping it in the background would be best
						encryptResponseCode = builder.build(SafeFolder.Instance().User().Account().getContext(), outputStream);
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}

			return encryptResponseCode;
		}

		@Override
		protected void onPostExecute(EncrypticsResponseCode code) {

			if(EncrypticsResponseCode.SUCCESS == code) {
				//Log.d("EncryptService", "Successfully logged in and made .SAFE files.");
				//callback.onEncrypticsResponse(code);

				try{
					for(ListItem item : SafeFolder.Instance().File().Collection()){
						try{
							File currentFile = new File(item.getText());
							SafeFolder.Instance().File().Move(currentFile.getName(), "encrypt");
						}catch(IOException ex){
							ex.printStackTrace();
						}
					}

					if(_sendEmail){
						SafeFolder.Instance().Email().Send(SafeFolder.Instance().getCurrentActivity(), SafeFolder.Instance().File().GetCollection(), SafeFolder.Instance().Email().Collection());
					}
				}catch (Exception ex){
					ex.printStackTrace();
				}

				SafeFolder.Instance().Close();

			} else {
				// TODO handle the encryptics exceptions here
				//Log.d("EncryptService", "Failed to login or make .SAFE files: " + code);
			}
		}
	}
	//endregion

	//region Decryption
	public void DecryptFiles(ArrayList<ListItem> fileList, ArrayList<ListItem> emailList){
		new DecryptTask(this).execute(fileList, emailList);
	}

	private static class DecryptTask extends AsyncTask<List<ListItem>, Void, EncrypticsResponseCode>{

		public DecryptTask(Security service){
		}

		@Override
		protected EncrypticsResponseCode doInBackground(List<ListItem>... lists){
			List<ListItem> fileList = lists[0];
			List<ListItem> recipientList = lists[1];

			// Authenticate the user
			if(!SafeFolder.Instance().User().IsLoggedIn()){
				EncrypticsResponseCode loginResponseCode = EncrypticsResponseCode.LOGIN_DENIED;
				User user = new User(SafeFolder.Instance().User().Account().getUsername(), SafeFolder.Instance().User().Account().getPassword());
				loginResponseCode = SafeFolder.Instance().User().Account().Authenticate(null);

				if(EncrypticsResponseCode.SUCCESS != loginResponseCode) {
					return loginResponseCode;
				}
			}

			EncrypticsResponseCode decryptResponseCode = EncrypticsResponseCode.UNKNOWN;

			try{
				// clear the file list so we can reuse it below
				//SafeFolder.Instance().File().Collection().clear();

				for(ListItem item : fileList){
					File file = new File(item.getText());
					FileInputStream fis = new FileInputStream(file);
					byte fileContent[] = new byte[(int)file.length()];

					// Reads up to certain bytes of data from this input stream into an array of bytes.
					fis.read(fileContent);

					SafeFile safeFile = SafeFile.createSafeFile(ByteBuffer.wrap(fileContent)); // As previously presented
					decryptResponseCode = safeFile.decrypt(SafeFolder.Instance().User().Account().getContext());

					if(decryptResponseCode == EncrypticsResponseCode.SUCCESS){
						// need to construct a new file from the constructed safefile with an output stream
						if(safeFile.isDecrypted()) {

							try{
								String path = item.getText().substring(0,item.getText().lastIndexOf('.'));
								com.encrypticslibrary.impl.ContentBlob blob = safeFile.getBlob(0);
								InputStream inputStream = blob.getContent();
								OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path));

								byte[] buffer = new byte[1024];
								int len = 0;

								while ((len = inputStream.read(buffer)) != -1) {
									outputStream.write(buffer, 0, len);
								}

								if(outputStream != null){
									outputStream.close();
								}

								// Add the unsafe file to a list
								ListItem newItem = new ListItem();
								newItem.setText(path);
								SafeFolder.Instance().File().UnsafeFiles().add(newItem);
							}catch(IOException e){
								e.printStackTrace();
							}catch(NullPointerException ex){
								ex.printStackTrace();
							}
						}
					}
				}
			}catch(Exception exe){
				exe.printStackTrace();
			}

			return decryptResponseCode;
		}

		@Override
		protected void onPostExecute(EncrypticsResponseCode code){
			if(EncrypticsResponseCode.SUCCESS == code){
				//Log.d("EncryptService", "Successfully logged in and decrypt .SAFE files.");

				for(ListItem item : SafeFolder.Instance().File().UnsafeFiles()){
					try{
						File currentFile = new File(item.getText());
						SafeFolder.Instance().File().Move(currentFile.getName(), "decrypt");

						// Remove the file as we don't need it here after the move
						currentFile.delete();
					}catch(IOException ex){
						ex.printStackTrace();
					}
				}
			}else{
				//Log.d("EncryptService", "Failed to login or decrypt .SAFE files: " + code);
			}

			SafeFolder.Instance().Close();
		}

		//public interface OnFinishedListener {
		//	public void onFinished();
		//}

		//public void setOnFinishedListener(OnFinishedListener listener) {
		//	this.callback = listener;
		//}

		//public void onEncrypticsResponse(EncrypticsResponseCode code) {
			//TODO may want to check the code here
		//	callback.onFinished();
		//}
	}
	//endregion
}