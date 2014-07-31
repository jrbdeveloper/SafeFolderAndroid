package com.coretech.safefolder.safefolder;

import android.os.AsyncTask;
import android.util.Log;

import com.encrypticsforandroid.encrypticsforandroid.AndroidAccountContextFactory;
import com.encrypticslibrary.api.response.EncrypticsResponseCode;
import com.encrypticslibrary.impl.AccountContext;
import com.encrypticslibrary.impl.ContentBlob;
import com.encrypticslibrary.impl.SafeFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msneen on 7/22/2014.
 */
public class EncryptService {

	//region Private Members
	private static SafeFolder _application;
	private String ENCRYPTICS_ACCOUNT_USERNAME = "michael.sneen@gmail.com";
	private String ENCRYPTICS_ACCOUNT_PASSWORD = "password";

	//private OnFinishedListener callback;
	private static ArrayList<String> _emailArrayList;
	//endregion

	//region Constructor
	public EncryptService(SafeFolder application){
		if(_application == null) {
			_application = application;
		}
	}
	//endregion

	public String EncryptFiles(ArrayList<String> fileList, ArrayList<String> emailList){

		_emailArrayList = emailList;

		AndroidAccountContextFactory factory = new AndroidAccountContextFactory(_application.getApplicationContext());
		AccountContext context = factory.generateAccountContext(ENCRYPTICS_ACCOUNT_USERNAME, ENCRYPTICS_ACCOUNT_PASSWORD);

		EncryptTask task = new EncryptTask(this, context);
		task.execute(fileList, emailList);

		return "";
	}

	public String DecryptFiles(ArrayList<String> fileList, ArrayList<String> emailList){
		AndroidAccountContextFactory factory = new AndroidAccountContextFactory(_application.getApplicationContext());
		AccountContext context = factory.generateAccountContext(ENCRYPTICS_ACCOUNT_USERNAME, ENCRYPTICS_ACCOUNT_PASSWORD);

		DecryptTask task = new DecryptTask(this, context);
		task.execute(fileList, emailList);

		return "";
	}

	private static class EncryptTask extends AsyncTask<List<String>, Void, EncrypticsResponseCode> {

		AccountContext context;
		//EncryptService callback;

		public EncryptTask(EncryptService service, AccountContext context) {
			this.context = context;
			//this.callback = service;
		}

		@Override
		protected EncrypticsResponseCode doInBackground(List<String>... lists) {

			List<String> fileList = lists[0];
			List<String> recipientList = lists[1];
			EncrypticsResponseCode codeToReturn = EncrypticsResponseCode.UNKNOWN;

			// Perform Encryptics Login. Looking for SUCCESS (10000)
			codeToReturn = context.login();

			//TODO How will you handle login failure?
			if(EncrypticsResponseCode.SUCCESS != codeToReturn) {
				// BUILDING .SAFE WILL NOT SUCCEED! Break.
				return codeToReturn;
			}

			try {
				for(String item : fileList) {
					if(!item.contains(".safe")){
						File file = new File(item);
						FileInputStream fis = new FileInputStream(file);

						OutputStream outputStream = new FileOutputStream(item +".safe");

						SafeFile.Builder builder = new SafeFile.Builder();

						// The SafeFileType is the type you will use to identify your company's .SAFE file
						// from other .SAFE files that may be also named .SAFE
						builder.setSafeFileType("com.coretech.safefolder")//TODO accept or reject this name
								// The SafeFileVersion is your company's version so you can
								// better identify your own changes to the architecture of the .SAFE
								.setSafeFileVersion("1.2.0")//TODO settle on a version number, probably 1.0 or similar
										// Subject is required, but if you won't use it, it can be anything that's not an empty string
										// However, it's one of a few identifying pieces of a .SAFE file prior to its decryption (the subject
										// is never encrypted)
								.setSubject("not used")
										// Adding your content here!
								.addContent(fis, (int)file.length());

						for(String recipient : recipientList) {
							builder.addRecipient(recipient);
						}

						// Building the .SAFE file is another network operation, keeping it in the background
						// would be best
						codeToReturn = builder.build(context, outputStream);
					}
				}
			}
			catch(Exception ex){
				//throw ex;
				//TODO handle this error
				String x = ex.getMessage();
			}

			return codeToReturn;
		}

		@Override
		protected void onPostExecute(EncrypticsResponseCode code) {

			if(EncrypticsResponseCode.SUCCESS == code) {
				Log.d("EncryptService", "Successfully logged in and made .SAFE files.");
				//callback.onEncrypticsResponse(code);

				_application.EmailSerivce().Send(_application.getCurrentActivity(), _application.FileService().GetFileList(), _emailArrayList);

			} else {
				// TODO handle the encryptics exceptions here
				Log.d("EncryptService", "Failed to login or make .SAFE files: " + code);
			}

			_application.Close();
		}
	}

	private static class DecryptTask extends AsyncTask<List<String>, Void, EncrypticsResponseCode>{

		AccountContext context;

		public DecryptTask(EncryptService service, AccountContext context){
			this.context = context;
		}

		@Override
		protected EncrypticsResponseCode doInBackground(List<String>... lists){
			List<String> fileList = lists[0];
			List<String> recipientList = lists[1];
			EncrypticsResponseCode codeToReturn = EncrypticsResponseCode.UNKNOWN;

			// Perform Encryptics Login. Looking for SUCCESS (10000)
			codeToReturn = context.login();

			//TODO How will you handle login failure?
			if(EncrypticsResponseCode.SUCCESS != codeToReturn) {
				return codeToReturn;
			}

			try{
				for(String item : fileList){
					File file = new File(item);
					FileInputStream fis = new FileInputStream(file);
					byte fileContent[] = new byte[(int)file.length()];

					// Reads up to certain bytes of data from this input stream into an array of bytes.
					fis.read(fileContent);

					SafeFile safeFile = SafeFile.createSafeFile(ByteBuffer.wrap(fileContent)); // As previously presented
					codeToReturn = safeFile.decrypt(context);

					if(codeToReturn == EncrypticsResponseCode.SUCCESS){
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
			}catch(Exception ex){
				String x = ex.getMessage();
			}

			return codeToReturn;
		}

		@Override
		protected void onPostExecute(EncrypticsResponseCode code){
			if(EncrypticsResponseCode.SUCCESS == code){
				Log.d("EncryptService", "Successfully logged in and decrypt .SAFE files.");
				_application.Close();
			}else{
				Log.d("EncryptService", "Failed to login or decrypt .SAFE files: " + code);
			}
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
}