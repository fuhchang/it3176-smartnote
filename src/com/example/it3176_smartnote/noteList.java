package com.example.it3176_smartnote;

import java.util.ArrayList;
import java.util.List;

import com.example.it3176_smartnote_model.note;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class noteList extends ArrayAdapter<String>{
	private final Activity context;
	private final ArrayList<note> resultArray;
	
	
	public noteList(Activity context, ArrayList<note> resultArray) {
		super(context, R.layout.note_single);
		this.context = context;
		this.resultArray = resultArray;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//setup the infalter
				LayoutInflater inflater = context.getLayoutInflater();
				View rowView = inflater.inflate(R.layout.note_single, null, true);
				
				//link to xml 
				TextView txtTitle = (TextView) rowView.findViewById(R.id.noteTitle);
				TextView txtCate = (TextView) rowView.findViewById(R.id.noteType);
				
				//for loop for setting content to the list view
				
				txtTitle.setText(resultArray.get(position).getNoteName());
				txtCate.setText(resultArray.get(position).getCategory());
				
				
				return rowView;
	}

	

}
