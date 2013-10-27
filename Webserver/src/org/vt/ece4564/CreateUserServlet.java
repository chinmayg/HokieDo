package org.vt.ece4564;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class CreateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 8730334386335056291L;

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
		if (createUser(user,pwd)) {
			resp.setStatus(401);
			respObj.put("Status", 401);
		} else {
			resp.setStatus(201);
			respObj.put("Status", 201);
		}
		resp.getWriter().write(respObj.toString());

	}
	
	//If this function returns true, then the user already exists in the system
	private boolean checkUsrAndPwd(String usr, String pwd) throws IOException {
		boolean isOk = false;

		BufferedReader br = new BufferedReader(new FileReader("usr.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] data = line.split(":");

			if (data[0].equals(usr)) {
				isOk = true;
			}
		}
		br.close();

		return isOk;
	}
	
	private boolean createUser(String usr, String pwd) throws IOException{
		boolean isOk = checkUsrAndPwd(usr,pwd);
		if(!isOk){
			writeToFile(usr+":"+pwd);
		}
		return isOk;
	}
	
	private void writeToFile(String data){
		try {
			File file = new File("usr.txt");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.newLine();
			bw.append(data);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
