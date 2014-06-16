package com.example.it3176_smartnote.util;

import java.util.ArrayList;

import com.example.it3176_smartnote.R;
import com.example.it3176_smartnote.model.Note;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainAdapter extends ArrayAdapter<Note>{
	private final Activity context;
	private final ArrayList<Note> note_list;
	
	public MainAdapter(Activity context, ArrayList<Note> note_list){
		super(context, R.layout.note_list_single, note_list);
		this.context = context;
		this.note_list = note_list;
	}
	
	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.note_list_single, null, true);
		
		TextView note_title = (TextView) rowView.findViewById(R.id.tv_note_title);
		note_title.setText(note_list.get(position).getNote_name());
		
		return rowView;
	}
}