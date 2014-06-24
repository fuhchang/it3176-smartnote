package com.example.it3176_smartnote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.it3176_smartnote.model.Note;

public class noteList extends ArrayAdapter<Note>{
	private final Activity context;
	private ArrayList<Note> resultArray = new ArrayList<Note>();
	private Integer[] imageId;
	private SparseBooleanArray mSelectedItemsIds;
	
	public noteList(Context context, ArrayList<Note> resultArray2, Integer[] imageId) {
		super(context, R.layout.note_single,resultArray2);
		// TODO Auto-generated constructor stub
		this.context = (Activity) context;
		this.resultArray = resultArray2;
		this.imageId = imageId;
		mSelectedItemsIds = new SparseBooleanArray();
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
				ImageView imgView = (ImageView) rowView.findViewById(R.id.noteImg);
				
				//setting text to widgets
				txtTitle.setText(resultArray.get(position).getNote_name());
				
				txtCate.setText(resultArray.get(position).getNote_category());
				txtDate.setText(resultArray.get(position).getNote_date());
				if(resultArray.get(position).getNote_category().equals("Meeting Notes")){
					imgView.setImageResource(imageId[1]);
				}else if(resultArray.get(position).getNote_category().equals("Personal")){
					imgView.setImageResource(imageId[2]);
				}else if(resultArray.get(position).getNote_category().equals("Client")){
					imgView.setImageResource(imageId[0]);
				}else{
					imgView.setImageResource(imageId[3]);
				}
				
				return rowView;
	}


	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

}
