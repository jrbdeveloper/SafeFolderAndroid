package com.coretech.safefolder.safefolder.entities;

/**
 * Created by ysneen on 08/08/2014.
 */
public class ListItem {
	//region Member Variables
    private String _text;
	//endregion

	//region Getters & Setters
    public String getText(){ return _text; }

    public void setText(String text){
        if(text != null){
			_text = text;
		}
    }
	//endregion

	//region Public Methods
    @Override
    public String toString(){
        return _text;
    }
	//endregion
}