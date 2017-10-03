package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBConnection conn = DBConnectionFactory.getDBConnection();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject msg = new JSONObject();
		try {
			//get userid, first name, lastname, password from resuest
			String userid = request.getParameter("user_id");
			String password = request.getParameter("password");
			String firstName = request.getParameter("first_name");
			String lastName = request.getParameter("last_name");
			
			if(conn.register(userid, firstName, lastName, password)){
				HttpSession session = request.getSession();
				session.setAttribute("user", userid);
				// setting session to expire in 10 minutes
				session.setMaxInactiveInterval(10 * 60);
				// Get user name
				String name = conn.getFullname(userid);
				msg.put("status", "OK");
				msg.put("user_id", userid);
				msg.put("name", name);
			}else {
				msg.put("status", "FAIL");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonObject(response, msg);
	}
}
