package com.coretech.safefolder.safefolder.services;

import android.os.AsyncTask;
import com.coretech.safefolder.safefolder.SafeFolder;
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
	private static SafeFolder _safeFolder;

	//private OnFinishedListener callback;
	//endregion

	//region Constructor
	public Security(SafeFolder application){
		if(_safeFolder == null) {
			_safeFolder = application;
		}
	}
	//endregion

	//region Encryption
	public void EncryptFiles(ArrayList<String> fileList, ArrayList<String> emailList){
		new EncryptTask(this).execute(fileList, emailList);
	}

	private static class EncryptTask extends AsyncTask<List<String>, Void, EncrypticsResponseCode> {

		//EncryptService callback;

		public EncryptTask(Security service) {
			//this.callback = service;
		}

		@Override
		protected EncrypticsResponseCode doInBackground(List<String>... lists) {

			List<String> fileList = lists[0];
			List<String> recipientList = lists[1];

			// Authenticate the user
			EncrypticsResponseCode loginResponseCode = EncrypticsResponseCode.LOGIN_DENIED;
			User user = new User(_safeFolder.Account().getUsername(), _safeFolder.Account().getPassword());
			loginResponseCode = _safeFolder.Account().Authenticate(user);

			if(EncrypticsResponseCode.SUCCESS != loginResponseCode) {
				return loginResponseCode;
			}

			EncrypticsResponseCode encryptResponseCode = EncrypticsResponseCode.UNKNOWN;

			try {

				for(String item : fileList) {
					if(!item.contains(".safe")){
						File file = new File(item);
						FileInputStream fis = new FileInputStream(file);
						OutputStream outputStream = new FileOutputStream(item + ".safe");

						SafeFile.Builder builder = new SafeFile.Builder();

						// The SafeFileType is the type you will use to identify your company's .SAFE file from other .SAFE files that may be also named .SAFE
						builder.setSafeFileType("com.coretech.safefolder")//TODO accept or reject this name
								// The SafeFileVersion is your company's version so you can better identify your own changes to the architecture of the .SAFE
								.setSafeFileVersion("1.2.0")//TODO settle on a version number, probably 1.0 or similar
										// Subject is required, but if you won't use it, it can be anything that's not an empty string
										// However, it's one of a few identifying pieces of a .SAFE file prior to its decryption (the subject is never encrypted)
								.setSubject("not used")
								.addContent(fis, (int)file.length()); // Adding your content here!

						for(String recipient : recipientList) {
							builder.addRecipient(recipient);
						}

						// Building the .SAFE file is another network operation, keeping it in the background would be best
						encryptResponseCode = builder.build(_safeFolder.Account().getContext(), outputStream);
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

				_safeFolder.Email().Send(_safeFolder.getCurrentActivity(), _safeFolder.File().GetCollection(), _safeFolder.Email().Collection);

			} else {
				// TODO handle the encryptics exceptions here
				//Log.d("EncryptService", "Failed to login or make .SAFE files: " + code);
			}

			_safeFolder.Close();
		}
	}
	//endregion

	//region Decryption
	public void DecryptFiles(ArrayList<String> fileList, ArrayList<String> emailList){
		new DecryptTask(this).execute(fileList, emailList);
	}

	private static class DecryptTask extends AsyncTask<List<String>, Void, EncrypticsResponseCode>{

		public DecryptTask(Security service){
		}

		@Override
		protected EncrypticsResponseCode doInBackground(List<String>... lists){
			List<String> fileList = lists[0];
			List<String> recipientList = lists[1];

			EncrypticsResponseCode loginResponseCode = EncrypticsResponseCode.LOGIN_DENIED;
			User user = new User(_safeFolder.Account().getUsername(), _safeFolder.Account().getPassword());
			loginResponseCode = _safeFolder.Account().Authenticate(user);

			//TODO How will you handle login failure?
			if(EncrypticsResponseCode.SUCCESS != loginResponseCode) {
				return loginResponseCode;
			}

			EncrypticsResponseCode decryptResponseCode = EncrypticsResponseCode.UNKNOWN;

			try{
				for(String item : fileList){
					File file = new File(item);
					FileInputStream fis = new FileInputStream(file);
					byte fileContent[] = new byte[(int)file.length()];

					// Reads up to certain bytes of data from this input stream into an array of bytes.
					fis.read(fileContent);

					SafeFile safeFile = SafeFile.createSafeFile(ByteBuffer.wrap(fileContent)); // As previously presented
					decryptResponseCode = safeFile.decrypt(_safeFolder.Account().getContext());

					if(decryptResponseCode == EncrypticsResponseCode.SUCCESS){
						// need to construct a new file from the constructed safefile with an output stream
						if(safeFile.isDecrypted()) {

							try{
								String path = item.substring(0,item.lastIndexOf('.'));
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
			}else{
				//Log.d("EncryptService", "Failed to login or decrypt .SAFE files: " + code);
			}

			_safeFolder.Close();
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