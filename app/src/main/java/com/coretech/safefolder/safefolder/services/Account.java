package com.coretech.safefolder.safefolder.services;

import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.User;
import com.encrypticsforandroid.encrypticsforandroid.AndroidAccountContextFactory;
import com.encrypticslibrary.api.response.EncrypticsResponseCode;
import com.encrypticslibrary.impl.AccountContext;
import com.encrypticslibrary.impl.AccountRegistration;

/**
 * Created by john bales on 8/1/2014.
 */
public class Account {

	//region Private Members
	private SafeFolder _safeFolder;
	private AccountContext _accountContext;
	//endregion

	//region Constructor
	public Account(SafeFolder application){
		if(_safeFolder == null){
			_safeFolder = application;
		}
	}
	//endregion

	//region Public Methods

	/**
	 * Method to register user accounts with the encryptics service
	 * @param user
	 */
	public void RegisterAccount(User user){
		AccountRegistration accountRegistration = new AccountRegistration(user.EmailAddress(), user.Password());
		accountRegistration.activate(accountRegistration.getRequestID());
	}

	/**
	 * Method to authenticate a user with the encryptics service
	 * @param user
	 * @return EncrypticsResponseCode
	 */
	public EncrypticsResponseCode Authenticate(User user){
		AndroidAccountContextFactory factory = new AndroidAccountContextFactory(_safeFolder.getApplicationContext());
		_accountContext =  factory.generateAccountContext(user.EmailAddress(), user.Password());

		return _accountContext.login();
	}

	/**
	 * Method to get the current users username
	 * @return String
	 */
	public String getUsername(){
		return "michael.sneen@gmail.com";
	}

	/**
	 * Method to get the current users password
	 * @return String
	 */
	public String getPassword(){
		return "password";
	}

	/**
	 * Method to get the encryptics account context
	 * @return AccountContext
	 */
	public AccountContext getContext(){
		return _accountContext;
	}
	//endregion
}