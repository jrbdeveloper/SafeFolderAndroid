package com.coretech.safefolder.safefolder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;

import com.coretech.safefolder.safefolder.entities.User;
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
	private User _user;
	//endregion

	//region Constructor
	public SafeFolder(){
		if(_instance == null){
			_instance = this;
		}
	}
	//endregion

	//region Events
	public void onCreate(){
		super.onCreate();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void Close(){

		_file.Collection().clear();
		//_email.Collection.clear();

		getCurrentActivity().finishAffinity();
	}
	//endregion

	//region Getters & Setters
	public static SafeFolder Instance(){
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
			_file = new File();
		}

		return _file;
	}

	public Email Email(){
		if(_email == null){
			_email = new Email();
		}

		return _email;
	}

	public Security Security(){
		if(_security == null){
			_security = new Security();
		}

		return _security;
	}

	public User User(){
		if(_user == null){
			_user = new User();
		}

		return _user;
	}
	//endregion

	//region Public Methods
	public boolean hasFiles() {
		return _file.Collection().size() > 0;
	}
	//endregion
}