package com.AzusaL.httputils;

import com.google.gson.JsonObject;

public class JsonUtils {
	//�����͸����������û���½���������װ��json���ݸ�ʽ
	public static JsonObject getjsonobject(String name, String password) {
		JsonObject object = new JsonObject();
		object.addProperty("name", name);
		object.addProperty("password", password);
		return object;
	}
}
