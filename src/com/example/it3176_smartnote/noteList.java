package com.example.it3176_smartnote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.it3176_smartnote.model.Note;

public class noteList extends ArrayAdapter<Note>{
	private final Activity context;
	private ArrayList<Note> resultArray = new ArrayList<Note>();
	
	public noteList(Context context, ArrayList<Note> resultArray2) {
		super(context, R.layout.note_single,resultArray2);
		// TODO Auto-generated constructor stub
		this.context = (Activity) context;
		this.resultArray = resultArray2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
				//setup the infalter
				LayoutInflater inflater = context.getLayoutInflater();
				View rowView = inflater.inflate(R.layout.note_single, null, true);
				
				//link to widgets
				TextView txtTitle = (TextView) rowView.findViewById(R.id.noteTitle);
				TextView txtCate = (TextView) rowView.findViewById(R.id.noteType);
				TextView txtDate = (TextView) rowView.findViewById(R.id.noteDate);
				//setting text to widgets
				txtTitle.setText(resultArray.get(position).getNote_name());
				txtCate.setText(resultArray.get(position).getNote_category());
				txtDate.setText(resultArray.get(position).getNote_date());
				
				return rowView;
	}

	

}
