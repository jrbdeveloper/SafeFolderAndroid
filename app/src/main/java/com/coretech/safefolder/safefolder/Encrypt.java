package com.coretech.safefolder.safefolder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;


public class Encrypt extends Activity {

    String[] items = {};
    ArrayAdapter<String> adapter; //= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        final ListView emailList = (ListView) findViewById(R.id.listView);
        final ArrayList<String> stringArray1 = new ArrayList();

        adapter = new ArrayAdapter<String>(Encrypt.this, android.R.layout.simple_expandable_list_item_1,stringArray1);

        Button addEmail = (Button) findViewById(R.id.add_email);


        addEmail.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //add email button click event.  Add email to list
                TextView editText = (TextView) findViewById(R.id.editText);
                String emailAddress = (String) editText.getText().toString();
                editText.setText(""); // Clear the textbox

                stringArray1.add(emailAddress);
                emailList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // Take the text from the box and add it to the list view
               // adapter.add(emailAddress);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.encrypt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
