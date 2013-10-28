package org.vt.ece4564;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UpdateDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1562548384159643795L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		req.getParameter("info");
		String json = req.getParameterValues("info")[0];
		JSONObject respObj = new JSONObject();

		resp.setContentType("application/json");
		if (writeToFile(json)) {
			resp.setStatus(200);
			respObj.put("Status", 200);
		} else {
			resp.setStatus(401);
			respObj.put("Status", 400);
		}
		resp.getWriter().write(respObj.toString());

	}

	private boolean writeToFile(String json) {
		boolean isOk = false;
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(json);

			JSONObject jsonObject = (JSONObject) obj;

			String usr = (String) jsonObject.get("user");

			// loop array
			JSONArray msg = (JSONArray) jsonObject.get("task");

			File file = new File(usr + "tasks.json");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			System.out.println(msg.toString());
			bw.append(msg.toString());
			bw.close();

			System.out.println("Done");
			isOk = true;
		} catch (IOException e) {
			e.printStackTrace();
			return isOk;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return isOk;
	}
}
