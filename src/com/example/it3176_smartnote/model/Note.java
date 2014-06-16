package com.example.it3176_smartnote.model;

public class Note {
	private int note_id;
	private String note_name;
	private String note_content;
	private String note_category;
	private String note_date;
	
	//Constructor
	public Note(int note_id, String note_name, String note_content, String note_category, String note_date){
		this.note_id = note_id;
		this.note_name = note_name;
		this.note_content = note_content;
		this.note_category = note_category;
		this.note_date = note_date;
	}
	
	//Getters and Setters
	public int getNote_id() {
		return note_id;
	}
	
	public void setNote_id(int note_id) {
		this.note_id = note_id;
	}
	
	public String getNote_name() {
		return note_name;
	}
	
	public void setNote_name(String note_name) {
		this.note_name = note_name;
	}
	
	public String getNote_content() {
		return note_content;
	}
	
	public void setNote_content(String note_content) {
		this.note_content = note_content;
	}
	
	public String getNote_category() {
		return note_category;
	}
	
	public void setNote_category(String note_category) {
		this.note_category = note_category;
	}
	
	public String getNote_date() {
		return note_date;
	}

	public void setNote_date(String note_date) {
		this.note_date = note_date;
	}
}
