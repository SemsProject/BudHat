package de.unirostock.sems.budhat.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.unirostock.sems.budhat.db.MySQLDB;
import de.unirostock.sems.budhat.mgmt.CookieManager;
import de.unirostock.sems.budhat.mgmt.Notifications;
import de.unirostock.sems.budhat.mgmt.UserManager;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.model.BioModel;
import de.unirostock.sems.budhat.model.ModelManager;



/**
 * The Class Index, implementing the main servlet.
 * 
 * @author martin scharm
 * 
 */
public class Index
	extends HttpServlet
{

	public static String WEB_URL = "";
	
	private static final long		serialVersionUID	= 1L;
	
	
	
	/**
	 * Default constructor.
	 */
	public Index ()
	{
	}
	
	
	public static void init (HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException
	{
		//WEB_URL = request.getRequestURL().toString ();
		
		response.setContentType ("text/html");
		request.setCharacterEncoding ("UTF-8");

		//WEB_URL = request.getRequestURL().toString ();
		request.setAttribute ("WEB_URL", WEB_URL);
		
		
	}
	
	
	/**
	 * This function will always be called from the POST/GET entry.
	 * 
	 * @param request
	 *          the request
	 * @param response
	 *          the response
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ServletException
	 */
	private void start (HttpServletRequest request, HttpServletResponse response)
		throws IOException,
			ServletException
	{
		init (request, response);
		
		Notifications errors = new Notifications ();
		HttpSession session = request.getSession (true);
		MySQLDB db = null;
		
		try
		{
			db = new MySQLDB (errors);
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			errors.addError ("SQLException: couldn't establish connection");
		}
		catch (NamingException e)
		{
			e.printStackTrace ();
			errors.addError ("NamingException: couldn't retrieve context env");
		}
		CookieManager cm = new CookieManager (request, response);
		UserManager um = new UserManager (db, session, request, response, errors,
			cm);
		
		System.out.println (um.getUser ());
		//siteStdVisibility ();
		User user = um.getUser ();
		db.setUser (user);
		//ModelManager mm = new ModelManager (db, request, errors, user);
		ModelManager mm = new ModelManager (db, user, request, errors);
		request.setAttribute ("AvailModels", mm.getAvailableModels ());
		if (user != null)
		{
			request.setAttribute ("UserValid", true);
			// mm.addModel (request,errors);
			request.setAttribute ("UserWelcome", um.getUserWelcome ());
			request.setAttribute ("UserMail", um.getUser ().mail);
			request.setAttribute ("DATE",
				new SimpleDateFormat ("yyyy-MM-dd-HH-mm-ss").format (new Date ()));
			request.setAttribute ("UserWelcome", um.getUserWelcome ());
			request.setAttribute ("ProcessingErrors", errors.getErrors ());
			request.setAttribute ("ProcessingNotifications", errors.getInfos ());
			request.setAttribute ("UserModels", mm.getMyModels ());

			request.setAttribute ("selectTab", "showAbout ();");
			
			if (um.justLoggedIn ())
			{
				// if user logged in: setting user site to visible, and about site to
				// hidden
				request.setAttribute ("selectTab", "showUser ();");
			}
			
			if (um.justChangedAccount ())
			{
				// if user logged in: setting user->account site to visible, and about
				// site to hidden
				request.setAttribute ("selectTab", "showMyAccount ();");
			}
			
			if (mm.showModels ())
			{
				// if user logged in: setting user->models site to visible, and about
				// site to hidden
				request.setAttribute ("selectTab", "showMyModels ();");
			}
			
		}
		
		
		
		//for (String id : tabVisibility.keySet ())
		//	request.setAttribute (id + VIZID, tabVisibility.get (id));
		
		db.closeConnection ();
		
		request.getRequestDispatcher ("/WEB-INF/Index.jsp").forward (request,
			response);
		
	}
	
	
	/**
	 * This functions will be called in case of a GET-request.
	 * 
	 * @param request
	 *          the request
	 * @param response
	 *          the response
	 * @throws ServletException
	 *           the servlet exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
		start (request, response);
	}
	
	
	/**
	 * This functions will be called in case of a POST-request.
	 * 
	 * @param request
	 *          the request
	 * @param response
	 *          the response
	 * @throws ServletException
	 *           the servlet exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost (HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		start (request, response);
	}
	
}
