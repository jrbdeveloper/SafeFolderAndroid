package com.coretech.safefolder.safefolder.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coretech.safefolder.safefolder.R;
import com.coretech.safefolder.safefolder.entities.ListItem;

import java.util.ArrayList;

/**
 * Created by ysneen on 08/08/2014.
 */
public class ListAdapter extends BaseAdapter {
    private ArrayList _list;
    private LayoutInflater layoutInflater;

    public ListAdapter(Context context, ArrayList list){
        _list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return _list.size();
    }

    @Override
    public Object getItem(int position){
        return _list.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.listItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem listItem = (ListItem)_list.get(position);
        holder.textView.setText(listItem.getText());

        return convertView;
    }

    public static class ViewHolder{
        public TextView textView;
    }
}
