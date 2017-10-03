package rpc;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;

public class RpcHelper {
	//Parse JSONObject from http request
	public static JSONObject readJsonObject(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		
		try {
			BufferedReader br = request.getReader();
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			return new JSONObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Write a JSONObject to http response
	public static void writeJsonObject(HttpServletResponse response, JSONObject object) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter writer = response.getWriter();
			writer.print(object);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Write a JSONArray to http response
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter writer = response.getWriter();
			writer.print(array);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Converts a list of Item objects to JSONArray.
		public static JSONArray getJSONArray(List<Item> items) {
			JSONArray result = new JSONArray();
			try {
				for (Item item : items) {
					result.put(item.toJSONObject());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

}