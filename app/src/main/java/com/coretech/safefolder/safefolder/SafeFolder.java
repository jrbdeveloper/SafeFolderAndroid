package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.app.Application;
import java.util.ArrayList;

/**
 * Created by john bales on 7/28/2014.
 */
public class SafeFolder extends Application {

	//region Private Members
	private String _appState;
	private Activity _currentActivity;
	//endregion

	//region Public Members
	public ArrayList<String> FileList;
	//endregion

	//region Constructor
	public SafeFolder(Activity activity){
		FileList = new ArrayList<String>();

		setCurrentActivity(activity);
	}

	public SafeFolder(){}
	//endregion

	//region Getters & Setters
	public void setCurrentActivity(Activity activity){
		_currentActivity = activity;
	}

	public Activity getCurrentActivity(){
		return _currentActivity;
	}

	public String getState(){
		return _appState;
	}

	public void setState(String state){
		_appState = state;
	}

	public FileService getFileService(){
		return new FileService(this);
	}

	public EmailService getEmailSerivce(){ return new EmailService(this); }

	public EncryptService getEncryptService(){ return new EncryptService(this); }
	//endregion
}