package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.app.Application;
import java.io.File;

import com.coretech.safefolder.safefolder.entities.ListItem;
import com.coretech.safefolder.safefolder.entities.User;
import com.coretech.safefolder.safefolder.services.Email;
import com.coretech.safefolder.safefolder.services.Security;
import com.coretech.safefolder.safefolder.services.SafeFile;

/**
 * Created by john bales on 7/28/2014.
 */
public class SafeFolder extends Application {

	//region Private Members
	private static SafeFolder _instance;
	private Activity _currentActivity;
	private SafeFile _file;
	private Email _email;
	private Security _security;
	private User _user;
	private String _safeExtension;
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

	public void Close(){

		if(SafeFolder.Instance().File().Collection().size() > 0){
			for(ListItem item : SafeFolder.Instance().File().Collection()){

				File fileToRemove = new File(item.getText() + SafeFolder.Instance().getSafeExtension());
				if(fileToRemove.exists()){
					fileToRemove.delete();
				}
			}
		}

		if(_file.Collection().size() > 0){
			_file.Collection().clear();
		}

		//getCurrentActivity().moveTaskToBack(true);
		getCurrentActivity().finish();
		//getCurrentActivity().finishActivity(0);
		//System.exit(0);
	}
	//endregion

	//region Singleton
	public static SafeFolder Instance(){
		return _instance;
	}
	//endregion

	//region Getters
	public String getSafeExtension(){
		if(_safeExtension == null){
			_safeExtension = ".safe";
		}

		return _safeExtension;
	}

	public Activity getCurrentActivity(){
		return _currentActivity;
	}

	public SafeFile File(){
		if(_file == null) {
			_file = new SafeFile();
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

	//region Setters
	public void setSafeExtension(String extension){
		_safeExtension = extension;
	}

	public void setCurrentActivity(Activity activity){
		if(activity != null){
			_currentActivity = activity;
		}
	}
	//endregion

	//region Public Methods
	public boolean hasFiles() {
		return _file.Collection().size() > 0;
	}
	//endregion
}