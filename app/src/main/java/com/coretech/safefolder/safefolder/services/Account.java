package com.coretech.safefolder.safefolder.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.coretech.safefolder.safefolder.R;
import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.User;
import com.encrypticsforandroid.encrypticsforandroid.AndroidAccountContextFactory;
import com.encrypticslibrary.api.response.EncrypticsResponseCode;
import com.encrypticslibrary.impl.AccountContext;
import com.encrypticslibrary.impl.AccountRegistration;

import java.util.List;

/**
 * Created by john bales on 8/1/2014.
 */
public class Account {

	//region Private Members
	private static AccountContext _accountContext;
	private static EncrypticsResponseCode _loginResponse;
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
	public EncrypticsResponseCode Authenticate(ProgressDialog progress){

		new LoginTask(progress).execute(SafeFolder.Instance().User().Username(), SafeFolder.Instance().User().Password());

		return _loginResponse;
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

	private static class LoginTask extends AsyncTask<String, Void, EncrypticsResponseCode>{
		private ProgressDialog _progress;

		public LoginTask(ProgressDialog progress){
			_progress = progress;
		}

		@Override
		protected EncrypticsResponseCode doInBackground(String... params){

			EncrypticsResponseCode responseCode;
			String username = SafeFolder.Instance().User().Username();
			String password = SafeFolder.Instance().User().Password();

			AndroidAccountContextFactory factory = new AndroidAccountContextFactory(SafeFolder.Instance().getApplicationContext());
			_accountContext =  factory.generateAccountContext(username, password);

			EncrypticsResponseCode loginResponse = _accountContext.login();

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(loginResponse == EncrypticsResponseCode.SUCCESS){
				SafeFolder.Instance().User().IsLoggedIn(true);
				responseCode = EncrypticsResponseCode.SUCCESS;
			}else{
				SafeFolder.Instance().User().IsLoggedIn(false);
				responseCode = EncrypticsResponseCode.UNKNOWN;
			}

			return responseCode;
		}

		@Override
		public void onPreExecute(){
		}

		@Override
		protected void onPostExecute(EncrypticsResponseCode code){

			if(EncrypticsResponseCode.SUCCESS == code){
				SafeFolder.Instance().User().IsLoggedIn(true);
			}else{
				SafeFolder.Instance().User().IsLoggedIn(false);
			}

			_progress.dismiss();
			_loginResponse = code;
			SafeFolder.Instance().Close();
		}
	}
}