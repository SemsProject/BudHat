/**
 * 
 */
package de.unirostock.sems.budhat.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import de.unirostock.sems.budhat.db.MySQLDB;
import de.unirostock.sems.budhat.mgmt.CookieManager;
import de.unirostock.sems.budhat.mgmt.Notifications;
import de.unirostock.sems.budhat.mgmt.UserManager;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.model.ModelManager;



/**
 * The Class SBMLDiffer.
 *
 * @author martin scharm
 * 
 * A servlet that will be querried via ajax to get a diff. It will speak to a model server to generate a diff, the resulting string will be send to the website..
 */
@WebServlet("/SBMLDiffer")
public abstract class WebModule
extends HttpServlet
{
	protected static final String DOT = "/usr/bin/dot";
	protected static final String SLASH = System.getProperty("file.separator");
	protected static final String IMGDIR = SLASH + "graphs" + SLASH;
	protected static final String IMGWEBDIR = "/graphs/";
	public static final String	NEWLINE		= System.getProperty ("line.separator");
	protected PrintWriter				out;
	protected ModelManager mm;
	protected Notifications notifications;
	protected MySQLDB db;
	protected boolean validRequest;
	
	/**
	 * This function will be called if the request is not valid. E.g. if we're missing some parameters or smth like that...
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void invalid (HttpServletResponse response, int type, Exception e)// throws IOException
	{
		response.setStatus (type);
		if (e == null)
			out.print ("invalid request!");
		else
			out.print ("invalid request: " + e.getMessage ());
		db.closeConnection ();
	}
	
	protected abstract void start (HttpServletRequest request, HttpServletResponse response)
		throws Exception;

	/**
	 * Start.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void init (HttpServletRequest request, HttpServletResponse response, String expectedMethod)
		throws IOException
	{
		validRequest = false;
		
		System.out.println ("starting");
		response.setContentType("text/plain");
		request.setCharacterEncoding ("UTF-8");
		out = response.getWriter ();
		
		if (expectedMethod != null && !request.getMethod().toLowerCase ().equals (expectedMethod))
		{
			invalid (response, HttpServletResponse.SC_BAD_REQUEST, null);
			return;
		}
		
		notifications = new Notifications ();
		HttpSession session = request.getSession(true);

		try
		{
			db = new MySQLDB (notifications);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			notifications.addError ("SQLException: couldn't establish connection");
		}
		catch (NamingException e)
		{
			e.printStackTrace();
			notifications.addError ("NamingException: couldn't retrieve context env");
		}
		CookieManager cm = new CookieManager (request, response);
		UserManager um = new UserManager (db, session, request, response, notifications, cm);
		User user = um.getUser ();
		db.setUser (user);
		//if (user != null)
		mm = new ModelManager (db, user, request, notifications);
		

		validRequest = true;
	}
	
	


	
	/**
	 * This functions will be called in case of a GET-request.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 * response)
	 */
	protected void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
		try
		{
			start (request, response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ServletException (e);
		}
	}
	
	
	/**
	 * This functions will be called in case of a POST-request.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 * response)
	 */
	protected void doPost (HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			start (request, response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ServletException (e);
		}
	}
}
