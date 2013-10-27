package org.vt.ece4564;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UpdateDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1562548384159643795L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.getParameter("info");
		String json = req.getParameterValues("info")[0];
		Object obj = JSONValue.parse(json);
		JSONObject jsObj = (JSONObject) obj;
		JSONObject respObj = new JSONObject();
		resp.setContentType("application/json");
		if (writeToFile(jsObj)) {
			resp.setStatus(200);
			respObj.put("Status", 200);
		} else {
			resp.setStatus(401);
			respObj.put("Status", 400);
		}
		resp.getWriter().write(respObj.toString());

	}
	
	
	private boolean writeToFile(JSONObject jsObj){
		boolean isOk = false;
		try { 
			String usr = (String) jsObj.get("user");
			String data = (String) jsObj.get("tasks");
			File file = new File(usr + "tasks.json");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(data);
			bw.close();
 
			System.out.println("Done");
			isOk = true;
		} catch (IOException e) {
			e.printStackTrace();
			return isOk;
		}
		return isOk;
	}
}
