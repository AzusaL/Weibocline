package com.AzusaL.bean;

import java.util.ArrayList;

public class WeiboBean {
	private int id;
	private String name;
	private String time;
	private String content;
	private ArrayList<String> photourls;

	public WeiboBean() {
		super();
	}

	public WeiboBean(int id, String name, String time, String content, ArrayList<String> photourls) {
		super();
		this.id = id;
		this.name = name;
		this.time = time;
		this.content = content;
		this.photourls = photourls;
	}

	public WeiboBean(String name, String time, String content, ArrayList<String> photourls) {
		super();
		this.name = name;
		this.time = time;
		this.content = content;
		this.photourls = photourls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<String> getPhotourls() {
		return photourls;
	}

	public void setPhotourls(ArrayList<String> photourls) {
		this.photourls = photourls;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
