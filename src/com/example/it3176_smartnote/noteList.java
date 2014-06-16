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
	private ArrayList<note> resultArray = new ArrayList<note>();
	
	public noteList(Context context, ArrayList<note> resultArray) {
		super(context, R.layout.note_single);
		// TODO Auto-generated constructor stub
		this.context = (Activity) context;
		this.resultArray = resultArray;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//setup the infalter
				LayoutInflater inflater = context.getLayoutInflater();
				View rowView = inflater.inflate(R.layout.note_single, null, true);
				
				//link to widgets
				TextView txtTitle = (TextView) rowView.findViewById(R.id.noteTitle);
				TextView txtCate = (TextView) rowView.findViewById(R.id.noteType);
				
				//setting text to widgets
				txtTitle.setText(resultArray.get(position).getNoteName());
				txtCate.setText(resultArray.get(position).getCategory());
				
				
				return rowView;
	}

	

}
