package com.coretech.safefolder.safefolder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import java.util.ArrayList;

import com.coretech.safefolder.safefolder.services.AccountService;
import com.coretech.safefolder.safefolder.services.EmailService;
import com.coretech.safefolder.safefolder.services.EncryptService;
import com.coretech.safefolder.safefolder.services.FileService;

/**
 * Created by john bales on 7/28/2014.
 */
public class SafeFolder extends Application {

	//region Private Members
	private static SafeFolder _instance;
	private Activity _currentActivity;
	private FileService _fileService;
	private EmailService _emailService;
	private EncryptService _encryptService;
	private AccountService _accountService;
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

	public FileService FileService(){
		if(_fileService == null) {
			_fileService = new FileService(_instance);
		}

		return _fileService;
	}

	public EmailService EmailSerivce(){
		if(_emailService == null){
			_emailService = new EmailService(_instance);
		}

		return _emailService;
	}

	public EncryptService EncryptService(){
		if(_encryptService == null){
			_encryptService = new EncryptService(_instance);
		}

		return _encryptService;
	}

	public AccountService AccountService(){
		if(_accountService == null){
			_accountService = new AccountService(_instance);
		}

		return _accountService;
	}
	//endregion

	//region Public Methods
	public boolean hasFiles() {
		return FileList.size() > 0;
	}
	//endregion
}