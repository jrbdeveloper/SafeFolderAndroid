package com.coretech.safefolder.safefolder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;

import java.util.ArrayList;

/**
 * Created by john bales on 7/28/2014.
 */
public class SafeFolder extends Application {

	//region Private Members
	private static SafeFolder _instance;
	private Activity _currentActivity;
	//endregion

	//region Public Members
	public ArrayList<String> FileList;
	public ArrayList<String> EmailList;
	//endregion

	//region Constructor
	//endregion

	//region Events
	public void onCreate(){
		super.onCreate();
		_instance = this;

		if(FileList == null){
			FileList = new ArrayList<String>();
		}

		if(EmailList == null){
			EmailList = new ArrayList<String>();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void Close(){

		FileList.clear();
		EmailList.clear();

		//getCurrentActivity().setResult(900);
//		this.getCurrentActivity().finishActivity(900);
//		getCurrentActivity().setResult(Activity.RESULT_OK, getCurrentActivity().getIntent());
//		getCurrentActivity().finish();
//		getCurrentActivity().setVisible(false);

		getCurrentActivity().finishAffinity();
	}
	//endregion

	//region Getters & Setters
	public static SafeFolder getInstance(){
		return _instance;
	}

	public void setCurrentActivity(Activity activity){
		if(activity != null){
			_currentActivity = activity;
		}
	}

	public Activity getCurrentActivity(){
		return _currentActivity;
	}

	public FileService FileService(){ return new FileService(_instance); }

	public EmailService EmailSerivce(){ return new EmailService(_instance); }

	public EncryptService EncryptService(){ return new EncryptService(_instance); }
	//endregion
}