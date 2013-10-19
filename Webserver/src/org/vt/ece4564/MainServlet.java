package org.vt.ece4564;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = -5111152333462774884L;
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		
		WebAppContext context = new WebAppContext();
		context.setWar("war");
		context.setContextPath("/");
		server.setHandler(context);
		
		server.start();
		server.join();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.getWriter().write("Main Servlet");
	}

}
