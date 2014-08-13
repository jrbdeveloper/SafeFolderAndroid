package com.coretech.safefolder.safefolder.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coretech.safefolder.safefolder.R;
import com.coretech.safefolder.safefolder.SafeFolder;
import com.coretech.safefolder.safefolder.entities.ListItem;

import java.util.ArrayList;

/**
 * Created by ysneen on 08/08/2014.
 */
public class ListAdapter extends BaseAdapter {
    //region Member variables
    private ArrayList _list;
    private LayoutInflater layoutInflater;
    //endregion

    //region Events
    public void HandleMinusClick(View convertView, final ViewGroup parent, final ListItem listItem) {
        final ListView emailListView = (ListView) parent;
        ImageView image = (ImageView)  convertView.findViewById(R.id.minus);

        image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SafeFolder.Instance().Email().Collection().remove(listItem);
                Bind(emailListView);
            }

        });
    }
    //endregion

    //region Public Methods
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

        final ListItem listItem = (ListItem)_list.get(position);
        holder.textView.setText(listItem.getText());

        HandleMinusClick(convertView, parent, listItem);

        return convertView;
    }
    //endregion

    //region Private Methods
    private void Bind(ListView emailListView) {
        emailListView.setAdapter(this);
        this.notifyDataSetChanged();
    }
    //endregion

    public static class ViewHolder{
        public TextView textView;
    }
}
