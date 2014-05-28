package com.example.it3176_smartnote;

import com.SQLLite.it3176.mySQLLite;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class sqlTest extends Activity implements OnClickListener{
	Button sqlCreate;
	EditText noteName , noteContent;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		sqlCreate = (Button)findViewById(R.id.btnCreate);
		noteName = (EditText)findViewById(R.id.editName);
		noteContent = (EditText)findViewById(R.id.editContent);
		sqlCreate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 
		switch(v.getId()){
		case R.id.btnCreate:
			boolean didItWork = true;
			try{
			String name = noteName.getText().toString();
			String content = noteContent.getText().toString();
			 mySQLLite entry = new mySQLLite(sqlTest.this);
			 entry.open();
			 entry.createEntry(name, content);
			 entry.close();
			}catch(Exception e){
				didItWork = false;
				Dialog d = new Dialog(this);
				d.setTitle("Successful or Not");
				TextView tv = new TextView(this);
				tv.setText("failed");
				d.setContentView(tv);
				d.show();
			}finally{
				Dialog d = new Dialog(this);
				d.setTitle("Successful or Not");
				TextView tv = new TextView(this);
				tv.setText("success");
				d.setContentView(tv);
				d.show();
			}
			break;
		}
	}
	
	
}
