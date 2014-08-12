package com.coretech.safefolder.safefolder.entities;

/**
 * Created by ysneen on 08/08/2014.
 */
public class ListItem {
    private String _text;

    public String getText(){
        return _text;
    }

    public void setText(String text){
        _text = text;
    }

    @Override
    public String toString(){
        return _text;
    }
}
