/**
 * 
 */
package com.automic.openstack.util;

/**
 * @author sumitsamson
 *
 */
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSON2XMLAdapter {

	private static final String regex = "\\A(?!XML)[a-zA-Z_][\\w0-9-]*";

	
	public static JSONObject adoptJsonToXml(JSONObject json) {
		String[] names = JSONObject.getNames(json);
		if (names != null) {
		    int cnt = 0;
			for (int i = 0; i < names.length; i++) {		
				// Iterating all the keys for the specified json object and
				// check key should be valid xml element name.			 
				if (!Pattern.matches(regex, names[i])) {					
					// As key is invalid so take json as string and clear it and
					// put it again under value.
				    Object jsonObject = json.remove(names[i]);
				    JSONObject temp = new JSONObject();
				    temp.put("jsonkey", names[i]);
				    temp.put("jsonvalue", jsonObject);
				    json.put("jsonobject"+(cnt++), temp);				    
				    iterateObject(jsonObject);
				} else {
					// If key is valid then recursively iterate it.
					Object obj = json.get(names[i]);
					iterateObject(obj);
				}
			}
		}
		return json;
	}

	private static void iterateObject(Object obj) {
		if (obj instanceof JSONObject) {
			adoptJsonToXml((JSONObject) obj);
		} else if (obj instanceof JSONArray) {
			traverseJSONArray((JSONArray) obj);
		}
	}

	private static void traverseJSONArray(JSONArray jsona) {
		for (int i = 0; i < jsona.length(); i++) {
			Object obj = jsona.get(i);
			iterateObject(obj);
		}
	}

}
