package com.coretech.safefolder.safefolder;

import android.app.Application;

/**
 * Created by john bales on 7/28/2014.
 */
public class SafeFolder extends Application {

	private String _appState;
	private FileService _fileService = new FileService();
	private EmailService _emailService = new EmailService();
	private EncryptService _encryptService = new EncryptService();

	public String getState(){
		return _appState;
	}

	public void setState(String state){
		_appState = state;
	}

	public FileService getFileService(){
		return _fileService;
	}

	public EmailService getEmailSerivce(){ return _emailService; }

	public EncryptService getEncryptService(){ return _encryptService; }
}