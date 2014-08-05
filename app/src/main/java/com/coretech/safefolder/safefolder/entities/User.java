package com.coretech.safefolder.safefolder.entities;

import com.coretech.safefolder.safefolder.services.Account;

/**
 * Created by john bales on 8/1/2014.
 */
public class User {

	//region Private Members
	private String _firstName;
	private String _lastName;
	private String _emailAddress;
	private String _userName;
	private String _password;
	private boolean _isLoggedIn;
	private boolean _rememberMe;
	private Account _account;
	//endregion

	//region Constructors
	public User(String username, String password){
		_emailAddress = username;
		_password = password;
	}

	public User(){}
	//endregion

	//region Getters
	public Account Account(){
		if(_account == null){
			_account = new Account();
		}

		return _account;
	}
	public String FirstName(){ return _firstName; }
	public String LastName(){ return _lastName; }
	public String EmailAddress(){ return _emailAddress; }
	public String Username(){ return _userName; }
	public String Password(){ return _password; }
	public boolean IsLoggedIn(){ return _isLoggedIn; }
	public boolean RememberMe(){ return _rememberMe; }
	//endregion

	//region Setters
	public void FirstName(String firstName){ _firstName = firstName; }
	public void LastName(String lastName){ _lastName = lastName; }
	public void EmailAddress(String emailAddress){ _emailAddress = emailAddress; }
	public void Username(String username){ _userName = username; }
	public void Password(String password){ _password = password; }
	public void IsLoggedIn(boolean loggedIn){ _isLoggedIn = loggedIn; }
	public void RememberMe(boolean rememberMe){ _rememberMe = rememberMe; }
	//endregion
}