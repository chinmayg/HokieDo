package org.vt.ece4564;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetDataServlet extends HttpServlet {

	private static final long serialVersionUID = -2970081912950893898L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		req.getParameter("info");
		boolean isOk = true;
		String json = req.getParameterValues("info")[0];
		Object obj = JSONValue.parse(json);
		JSONObject jsObj = (JSONObject) obj;
		JSONObject respObj = new JSONObject();
		String usr = (String) jsObj.get("user");
		String sCurrentLine = "";
		BufferedReader br = null;
		JSONParser parser = new JSONParser();
		try {

			obj = parser.parse(new FileReader(usr + "tasks.json"));
			JSONArray jsonObject = (JSONArray) obj;
			respObj.put("tasks", jsonObject);
		} catch (IOException e) {
			isOk = false;
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.setStatus(401);

		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				isOk = false;
			}
		}
		
		resp.setContentType("application/json");
		if (isOk) {
			resp.setStatus(201);
			
		} else {
			resp.setStatus(401);
			respObj.put("Status", 401);
		}
		System.out.println(respObj.toString());
		resp.getWriter().write(respObj.toString());

	}
}
