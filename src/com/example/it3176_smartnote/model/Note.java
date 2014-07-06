package com.example.it3176_smartnote.model;

/**
 * This is the model class for Note
 * @author Lee Zhuo Xun
 *
 */
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
	
	/**
	 * This is the constructor to retrieve note.
	 * @param note_id
	 * @param note_name
	 * @param note_content
	 * @param note_category
	 * @param note_date
	 * @param note_img
	 * @param note_video
	 * @param note_audio
	 * @param note_address
	 * @param note_tags
	 * @param note_status
	 */
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
	
	/**
	 * This is the constructor to retrieve note id
	 * @param note_id
	 */
	public Note(int note_id){
		this.note_id = note_id;
	}
	
	/**
	 * This is the constructor to retrieve note without the attachments.
	 * @param note_name
	 * @param note_content
	 * @param note_category
	 */
	public Note(String note_name, String note_content, String note_category){
		this.note_name = note_name;
		this.note_content = note_content;
		this.note_category = note_category;
	}
	
	/**
	 * This is the constructor for testing the inserting of attachments only.
	 * @param note_name
	 * @param note_content
	 * @param note_category
	 * @param note_img
	 * @param note_video
	 * @param note_address
	 */
	public Note(String note_name, String note_content, String note_category, String note_img, String note_video, String note_address){
		this.note_name = note_name;
		this.note_content = note_content;
		this.note_category = note_category;
		this.note_img = note_img;
		this.note_video = note_video;
		this.note_address = note_address;
	}
	
	/**
	 * This is the Note's default constructor
	 */
	public Note() {}

	//Getters and Setters
	/**
	 * This is the getter method for note id
	 * @return int
	 */
	public int getNote_id() {
		return note_id;
	}
	/**
	 * This is the setter method for note id
	 * @param note_id
	 */
	public void setNote_id(int note_id) {
		this.note_id = note_id;
	}
	/**
	 * This is the getter method for note name
	 * @return String
	 */
	public String getNote_name() {
		return note_name;
	}
	/**
	 * This is the setter method for note name
	 * @param note_name
	 */
	public void setNote_name(String note_name) {
		this.note_name = note_name;
	}
	/**
	 * This is the getter method for note content
	 * @return String
	 */
	public String getNote_content() {
		return note_content;
	}
	/**
	 * This is the setter method for note content
	 * @param note_content
	 */
	public void setNote_content(String note_content) {
		this.note_content = note_content;
	}
	/**
	 * This is the getter method for note category
	 * @return String
	 */
	public String getNote_category() {
		return note_category;
	}
	/**
	 * This is the setter method for note category
	 * @param note_category
	 */
	public void setNote_category(String note_category) {
		this.note_category = note_category;
	}
	/**
	 * This is the getter method for note date
	 * @return String
	 */
	public String getNote_date() {
		return note_date;
	}
	/**
	 * This is the setter method for note date
	 * @param note_date
	 */
	public void setNote_date(String note_date) {
		this.note_date = note_date;
	}
	/**
	 * This is the getter method for note image
	 * @return String
	 */
	public String getNote_img() {
		return note_img;
	}
	/**
	 * This is the setter method for note image
	 * @param note_img
	 */
	public void setNote_img(String note_img) {
		this.note_img = note_img;
	}
	/**
	 * This is the getter method for note video
	 * @return String
	 */
	public String getNote_video() {
		return note_video;
	}
	/**
	 * This is the setter method for note video
	 * @param note_video
	 */
	public void setNote_video(String note_video) {
		this.note_video = note_video;
	}
	/**
	 * This is the getter method for note audio
	 * @return String
	 */
	public String getNote_audio() {
		return note_audio;
	}
	/**
	 * This is the setter method for note audio
	 * @param note_audio
	 */
	public void setNote_audio(String note_audio) {
		this.note_audio = note_audio;
	}
	/**
	 * This is the getter method for note address
	 * @return String
	 */
	public String getNote_address() {
		return note_address;
	}
	/**
	 * This is the setter method for note address
	 * @param note_address
	 */
	public void setNote_address(String note_address) {
		this.note_address = note_address;
	}
	/**
	 * This is the getter method for note tags
	 * @return String
	 */
	public String getNote_tags() {
		return note_tags;
	}
	/**
	 * This is the setter method for note tags
	 * @param note_tags
	 */
	public void setNote_tags(String note_tags) {
		this.note_tags = note_tags;
	}
	/**
	 * This is the getter method for note status
	 * @return String
	 */
	public String getNote_status() {
		return note_status;
	}
	/**
	 * This is the setter method for note status
	 * @param note_status
	 */
	public void setNote_status(String note_status) {
		this.note_status = note_status;
	}
}
