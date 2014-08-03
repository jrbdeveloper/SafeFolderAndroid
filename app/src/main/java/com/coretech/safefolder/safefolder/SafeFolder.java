package com.coretech.safefolder.safefolder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import java.util.ArrayList;

import com.coretech.safefolder.safefolder.services.Account;
import com.coretech.safefolder.safefolder.services.Email;
import com.coretech.safefolder.safefolder.services.Security;
import com.coretech.safefolder.safefolder.services.File;

/**
 * Created by john bales on 7/28/2014.
 */
public class SafeFolder extends Application {

	//region Private Members
	private static SafeFolder _instance;
	private Activity _currentActivity;
	private File _file;
	private Email _email;
	private Security _security;
	private Account _account;
	//endregion

	//region Constructor
	//endregion

	//region Events
	public void onCreate(){
		super.onCreate();
		_instance = this;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void Close(){

		_file.Collection.clear();
		//_email.Collection.clear();

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

	public File File(){
		if(_file == null) {
			_file = new File(_instance);
		}

		return _file;
	}

	public Email Email(){
		if(_email == null){
			_email = new Email(_instance);
		}

		return _email;
	}

	public Security Security(){
		if(_security == null){
			_security = new Security(_instance);
		}

		return _security;
	}

	public Account Account(){
		if(_account == null){
			_account = new Account(_instance);
		}

		return _account;
	}
	//endregion

	//region Public Methods
	public boolean hasFiles() {
		return _file.Collection.size() > 0;
	}
	//endregion
}