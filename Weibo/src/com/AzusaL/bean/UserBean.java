package com.AzusaL.bean;

public class UserBean {
	private String name;
	private String password;
	private String introduction;
	private String head_img_path;

	public UserBean(String name, String password, String introduction, String head_img_path) {
		super();
		this.name = name;
		this.password = password;
		this.introduction = introduction;
		this.head_img_path = head_img_path;
	}

	public UserBean(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getHead_img_path() {
		return head_img_path;
	}

	public void setHead_img_path(String head_img_path) {
		this.head_img_path = head_img_path;
	}

}
