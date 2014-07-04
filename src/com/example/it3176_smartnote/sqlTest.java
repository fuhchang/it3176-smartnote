package com.example.it3176_smartnote;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

public class sqlTest extends Activity implements OnClickListener{
	Button sqlCreate;
	EditText noteName , noteContent;
	
	@Override
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
		case R.id.btnCreate :
			boolean result = true;
			try{
			String name = noteName.getText().toString();
			String con = noteContent.getText().toString();
			
			SQLiteController controller = new SQLiteController(this);
			controller.open();
			controller.insertNote(new Note(name, con, "cat"));
			controller.close();
			
			}catch(Exception e){
				result = false;
			}finally{
				if(result){
					Dialog d = new Dialog(this);
					d.setTitle("Successful");
					TextView tv = new TextView(this);
					tv.setText("success");
					d.setContentView(tv);
					d.show();
				}
			}
			break;
		}
	}
	
	
}
