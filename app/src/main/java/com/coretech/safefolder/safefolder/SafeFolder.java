package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
	private SharedPreferences _appPreferences;
	private boolean _appIsInstalled = false;
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

		CreateShortcut();
		_instance.File().CreateSafeFolders();
	}

	public void Close(){

		CleanupFiles();

		if(_instance.Email().Collection().size() > 0){
			_instance.Email().Collection().clear();
		}

		getCurrentActivity().finish();
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
		return _instance.File().Collection().size() > 0;
	}

	/**
	 * Method to centralize the writing to logcat
	 * @param message
	 */
	public void Log(String message){
		if(message != null){
			Log.d("SafeFolderIssue:", message);
		}else{
			Log.d("SafeFolderIssue:", "Unknown Error");
		}
	}

	/**
	 * Method to centralize the writing to logcat
	 * @param tag
	 * @param message
	 */
	public void Log(String tag, String message){
		if(message != null){
			Log.d(tag, message);
		}else{
			Log.d(tag, "Unknown Error");
		}
	}

	public enum ActivityType{
		Encrypt,
		Decrypt,
		Register,
		FileManager,
		Launch
	}
	//endregion

	//region Private Methods
	private void CreateShortcut(){
		//check if application is running first time, only then create shorcut
		_appPreferences = PreferenceManager.getDefaultSharedPreferences(_instance);
		_appIsInstalled = _appPreferences.getBoolean("isAppInstalled",false);
		if(!_appIsInstalled) {

			// create short code
			Intent shortcutIntent = new Intent(getApplicationContext(), LaunchActivity.class);
			shortcutIntent.setAction(Intent.ACTION_MAIN);
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Safe Folder");
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.safefolder));
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			getApplicationContext().sendBroadcast(intent);

			//Make preference true
			SharedPreferences.Editor editor = _appPreferences.edit();
			editor.putBoolean("isAppInstalled", true);
			editor.commit();
		}
	}

	/**
	 * Method to cleanup files after an encryption or decryption
	 */
	public void CleanupFiles(){
		if(_instance.File().Collection().size() > 0){
			for(ListItem item : _instance.File().Collection()){

				File fileToRemove = new File(item.getText() + getSafeExtension());
				if(fileToRemove.exists()){
					fileToRemove.delete();
				}
			}
		}

		if(_instance.File().Collection().size() > 0){
			_instance.File().Collection().clear();
		}
	}
	//endregion
}