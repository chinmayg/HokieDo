package org.vt.ece4564;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 137922189559146113L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		req.getParameter("info");
		String json = req.getParameterValues("info")[0];
		Object obj = JSONValue.parse(json);
		JSONObject jsObj = (JSONObject) obj;
		JSONObject respObj = new JSONObject();
		resp.setContentType("application/json");
		String user = (String) jsObj.get("user");
		String pwd = (String) jsObj.get("pwd");
		if (checkUsrAndPwd(user,pwd)) {
			resp.setStatus(200);
			respObj.put("Status", 200);
		} else {
			resp.setStatus(401);
			respObj.put("Status", 400);
		}
		resp.getWriter().write(respObj.toString());

	}

	private boolean checkUsrAndPwd(String usr, String pwd) throws IOException {
		boolean isOk = false;

		BufferedReader br = new BufferedReader(new FileReader("usr.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] data = line.split(":");

			if (data[0].equals(usr) && data[1].equals(pwd)) {
				isOk = true;
			}
		}
		br.close();

		return isOk;
	}

}
