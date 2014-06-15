package com.example.it3176_smartnote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class noteList extends ArrayAdapter<String>{
	private final Activity context;
	private final ArrayList<String> contentBody;
	
	public noteList(Activity context, ArrayList<String> resultArray) {
		super(context, R.layout.note_single, resultArray);
		this.context = context;
		this.contentBody = resultArray;
	}

	public View getView(int position, View view, ViewGroup parent){
		
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.note_single, null, true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.noteTitle);
		txtTitle.setText(contentBody.get(position));
		
		
		return rowView;
		
	}

}
