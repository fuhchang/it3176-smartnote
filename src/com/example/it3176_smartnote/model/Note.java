package com.example.it3176_smartnote.model;

public class Note {
	private int note_id;
	private String note_name;
	private String note_content;
	private String note_category;
	private String note_date;
	private String note_img;
	private String note_video;
	private String note_audio;
	private String note_address;
	private String note_tags;
	private String note_status;
	
	//Constructor
	public Note(int note_id, String note_name, String note_content, String note_category, String note_date, String note_img, String note_video, String note_audio, String note_address, String note_tags, String note_status){
		this.note_id = note_id;
		this.note_name = note_name;
		this.note_content = note_content;
		this.note_category = note_category;
		this.note_date = note_date;
		this.note_img = note_img;
		this.note_video = note_video;
		this.note_audio = note_audio;
		this.note_address = note_address;
		this.note_tags = note_tags;
		this.note_status = note_status;
	}
	
	public Note(int note_id){
		this.note_id = note_id;
	}
	
	public Note(String note_name, String note_content, String note_category){
		this.note_name = note_name;
		this.note_content = note_content;
		this.note_category = note_category;
	}
	
	/**FOR TESTING OF INSERTING ATTACHMENTS ONLY**/
	public Note(String note_name, String note_content, String note_category, String note_img, String note_video, String note_audio){
		this.note_name = note_name;
		this.note_content = note_content;
		this.note_category = note_category;
		this.note_img = note_img;
		this.note_video = note_video;
		this.note_audio = note_audio;
	}
	
	
	
	public Note() {}

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

	public String getNote_img() {
		return note_img;
	}

	public void setNote_img(String note_img) {
		this.note_img = note_img;
	}

	public String getNote_video() {
		return note_video;
	}

	public void setNote_video(String note_video) {
		this.note_video = note_video;
	}

	public String getNote_audio() {
		return note_audio;
	}

	public void setNote_audio(String note_audio) {
		this.note_audio = note_audio;
	}

	public String getNote_address() {
		return note_address;
	}

	public void setNote_address(String note_address) {
		this.note_address = note_address;
	}

	public String getNote_tags() {
		return note_tags;
	}

	public void setNote_tags(String note_tags) {
		this.note_tags = note_tags;
	}

	public String getNote_status() {
		return note_status;
	}

	public void setNote_status(String note_status) {
		this.note_status = note_status;
	}
}
