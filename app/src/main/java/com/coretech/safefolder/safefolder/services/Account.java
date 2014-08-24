package com.coretech.safefolder.safefolder.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import com.coretech.safefolder.safefolder.EncryptActivity;
import com.coretech.safefolder.safefolder.LaunchActivity;
import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.User;
import com.coretech.safefolder.safefolder.interfaces.AuthenticationCompleted;
import com.encrypticsforandroid.encrypticsforandroid.AndroidAccountContextFactory;
import com.encrypticslibrary.api.response.EncrypticsResponseCode;
import com.encrypticslibrary.impl.AccountContext;
import com.encrypticslibrary.impl.AccountRegistration;

/**
 * Created by john bales on 8/1/2014.
 */
public class Account {

	public AuthenticationCompleted authenticationCompletedListener;
	//region Private Members
	private static AccountContext _accountContext;
	private static EncrypticsResponseCode _loginResponse;
	SharedPreferences _sharedPref;
	//endregion

	//region Constructor
	public Account(){

		if(_sharedPref == null){
			_sharedPref = SafeFolder.Instance().getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
		}
	}

	public void setAuthenticationListener(AuthenticationCompleted authenticationCompletedListener){
		this.authenticationCompletedListener = authenticationCompletedListener;
	}
	//endregion

	//region Getters
	/**
	 * Method to get the encryptics account context
	 * @return AccountContext
	 */
	public AccountContext getContext(){
		return _accountContext;
	}

	/**
	 * Method to get the current users username
	 * @return String
	 */
	public String getUsername(){
		//String defaultValue = SafeFolder.Instance().getResources().getString(R.string.default_username);
		String defaultValue = "";
		String username = _sharedPref.getString("the_username", defaultValue);

		return username;
	}

	/**
	 * Method to get the current users password
	 * @return String
	 */
	public String getPassword(){
		//String defaultValue = SafeFolder.Instance().getResources().getString(R.string.default_password);
		String defaultValue = "";
		String password = _sharedPref.getString("the_password", defaultValue);

		return password;
	}

	public boolean getRememberMe(){
		//String defaultValue = SafeFolder.Instance().getResources().getString(R.string.remember_me_default);
		String defaultValue = "";
		String rememberMe = _sharedPref.getString("remember_me", defaultValue);

		return rememberMe == "yes";
	}
	//endregion

	//region Setters
	public void setUsername(String username){
		SharedPreferences.Editor editor = _sharedPref.edit();
		editor.putString("the_username", username);
		editor.commit();
	}

	public void setPassword(String password){
		SharedPreferences.Editor editor = _sharedPref.edit();
		editor.putString("the_password", password);
		editor.commit();
	}

	public void setRememberMe(boolean remember){
		String _remember = (remember) ? "yes" : "no";

		SharedPreferences.Editor editor = _sharedPref.edit();
		editor.putString("remember_me", _remember);
		editor.commit();
	}
	//endregion

	//region Public Methods
	public EncrypticsResponseCode ShowLogin(SafeFolder.ActivityType activityType){
		Intent intent  = new Intent(SafeFolder.Instance().getApplicationContext(), LaunchActivity.class);
		SafeFolder.Instance().getCurrentActivity().startActivity(intent);

		SafeFolder.Instance().getCurrentActivity().finish();
		return EncrypticsResponseCode.NOT_AUTHORIZED;
	}

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

		new LoginTask(progress, authenticationCompletedListener).execute(SafeFolder.Instance().User().Username(), SafeFolder.Instance().User().Password());

		return _loginResponse;
	}
	//endregion

	private static class LoginTask extends AsyncTask<String, Void, EncrypticsResponseCode>{
		private ProgressDialog _progress;
		public AuthenticationCompleted authenticationCompletedListener;

		public LoginTask(ProgressDialog progress, AuthenticationCompleted authenticationCompletedListener){
			_progress = progress;
			this.authenticationCompletedListener = authenticationCompletedListener;
		}

		@Override
		protected EncrypticsResponseCode doInBackground(String... params){

			EncrypticsResponseCode responseCode;
			String username = SafeFolder.Instance().User().Username();
			String password = SafeFolder.Instance().User().Password();

			AndroidAccountContextFactory factory = new AndroidAccountContextFactory(SafeFolder.Instance().getApplicationContext());
			AccountContext accountContext = factory.generateAccountContext(username, password);
			_accountContext = accountContext;

			EncrypticsResponseCode loginResponse = accountContext.login();

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				SafeFolder.Instance().Log(e.getMessage());
			}

			if(loginResponse == EncrypticsResponseCode.SUCCESS){
				SafeFolder.Instance().User().IsLoggedIn(true);
				responseCode = EncrypticsResponseCode.SUCCESS;
			}else{
				SafeFolder.Instance().User().IsLoggedIn(false);
				responseCode = EncrypticsResponseCode.LOGIN_DENIED;
			}

			return responseCode;
		}

		@Override
		protected void onPostExecute(EncrypticsResponseCode code){

			if(EncrypticsResponseCode.SUCCESS == code){
				SafeFolder.Instance().User().IsLoggedIn(true);
			}else{
				SafeFolder.Instance().User().IsLoggedIn(false);
			}

			if(_progress != null){
				_progress.dismiss();
			}

			_loginResponse = code;

			authenticationCompletedListener.onAuthenticationCompleted();

			//TODO: May need to re-show the login activity here
		}
	}
}