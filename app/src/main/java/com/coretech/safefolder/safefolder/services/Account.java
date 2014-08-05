package com.coretech.safefolder.safefolder.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.coretech.safefolder.safefolder.R;
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
	private AccountContext _accountContext;
	//endregion

	//region Constructor
	public Account(){
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
	 * @return EncrypticsResponseCode
	 */
	public EncrypticsResponseCode Authenticate(){
		EncrypticsResponseCode responseCode;
		String username = SafeFolder.Instance().User().EmailAddress();
		String password = SafeFolder.Instance().User().Password();

		AndroidAccountContextFactory factory = new AndroidAccountContextFactory(SafeFolder.Instance().getApplicationContext());
		_accountContext =  factory.generateAccountContext(username, password);

		EncrypticsResponseCode loginResponse = _accountContext.login();

		if(loginResponse == EncrypticsResponseCode.SUCCESS){
			SafeFolder.Instance().User().IsLoggedIn(true);
			responseCode = EncrypticsResponseCode.SUCCESS;
		}else{
			SafeFolder.Instance().User().IsLoggedIn(false);
			responseCode = EncrypticsResponseCode.UNKNOWN;
		}

		return responseCode;
	}

	/**
	 * Method to get the current users username
	 * @return String
	 */
	public String getUsername(){

		SharedPreferences sharedPref = SafeFolder.Instance().getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
		String defaultValue = SafeFolder.Instance().getResources().getString(R.string.default_username);
		String username = sharedPref.getString(SafeFolder.Instance().getString(R.string.the_username), defaultValue);

		return username;
	}

	public void setUsername(String username){
		SharedPreferences sharedPref = SafeFolder.Instance().getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(SafeFolder.Instance().getString(R.string.the_username), username);
		editor.commit();
	}

	/**
	 * Method to get the current users password
	 * @return String
	 */
	public String getPassword(){
		SharedPreferences sharedPref = SafeFolder.Instance().getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
		String defaultValue = SafeFolder.Instance().getResources().getString(R.string.default_password);
		String password = sharedPref.getString(SafeFolder.Instance().getString(R.string.the_password), defaultValue);

		return password;
	}

	public void setPassword(String password){
		SharedPreferences sharedPref = SafeFolder.Instance().getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(SafeFolder.Instance().getString(R.string.the_password), password);
		editor.commit();
	}

	public boolean getRememberMe(){
		SharedPreferences sharedPref = SafeFolder.Instance().getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
		String defaultValue = SafeFolder.Instance().getResources().getString(R.string.remember_me_default);
		String rememberMe = sharedPref.getString(SafeFolder.Instance().getString(R.string.remember_me), defaultValue);

		return rememberMe == "yes";
	}

	public void setRememberMe(boolean remember){
		String _remember = (remember) ? "yes" : "no";

		SharedPreferences sharedPref = SafeFolder.Instance().getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(SafeFolder.Instance().getString(R.string.remember_me), _remember);
		editor.commit();
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