package com.coretech.safefolder.safefolder.entities;

/**
 * Created by john bales on 8/1/2014.
 */
public class User {

	//region Private Members
	private String _firstName;
	private String _lastName;
	private String _emailAddress;
	private String _password;
	//endregion

	//region Constructors
	public User(String username, String password){
		_emailAddress = username;
		_password = password;
	}

	public User(){}
	//endregion

	//region Getters
	public String FirstName(){ return _firstName; }
	public String LastName(){ return _lastName; }
	public String EmailAddress(){ return _emailAddress; }
	public String Password(){ return _password; }
	//endregion

	//region Setters
	public void FirstName(String firstName){ _firstName = firstName; }
	public void LastName(String lastName){ _lastName = lastName; }
	public void EmailAddress(String emailAddress){ _emailAddress = emailAddress; }
	public void Password(String password){ _password = password; }
	//endregion
}