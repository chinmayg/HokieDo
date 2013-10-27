package org.vt.ece4564;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GetDataServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2970081912950893898L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.getParameter("info");
		boolean isOk = true;
		String json = req.getParameterValues("info")[0];
		Object obj = JSONValue.parse(json);
		JSONObject jsObj = (JSONObject) obj;
		JSONObject respObj = new JSONObject();
		String usr = (String) jsObj.get("user");
		String sCurrentLine = "";
		BufferedReader br = null;
		 
		try {
 
 
			br = new BufferedReader(new FileReader(usr+"tasks.json"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				isOk = false;
			}
		}
		
		resp.setContentType("application/json");
		if (isOk) {
			resp.setStatus(201);
			respObj.put("tasks",sCurrentLine);
		} else {
			resp.setStatus(401);
			respObj.put("Status", 401);
		}
		resp.getWriter().write(respObj.toString());

	}
}
