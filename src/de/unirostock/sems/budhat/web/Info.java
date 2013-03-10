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

import de.unirostock.sems.budhat.db.MySQLDB;
import de.unirostock.sems.budhat.mgmt.CookieManager;
import de.unirostock.sems.budhat.mgmt.Notifications;
import de.unirostock.sems.budhat.mgmt.UserManager;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.sbml.ModelManager;
import de.unirostock.sems.budhat.sbml.ModelVersion;



/**
 * The Class SBMLDiffer.
 *
 * @author martin scharm
 * 
 * A servlet that will be querried via ajax to get a diff. It will speak to a model server to generate a diff, the resulting string will be send to the website..
 */
@WebServlet("/SBMLDiffer")
public class Info
extends WebModule
{
	
	private static final long	serialVersionUID	= 9148294324749188855L;
	
	/**
	 * This function will be called if the request is not valid. E.g. if we're missing some parameters or smth like that...
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void invalid ()// throws IOException
	{
		out.println ("invalid request!");
	}

	/**
	 * Start.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void start (HttpServletRequest request, HttpServletResponse response)
		throws IOException
	{
		super.init (request, response, "post");

		if (!validRequest)
			return;
		
		
			info (request);
			db.closeConnection ();
	}
	
	

	private String getInfo (String id, String version)
	{
		
		ModelVersion v = mm.getVersion (id, version);
		
		if (v != null)
			return v.getInfo ();
		
		return "model not found...";
	}
  
  
	/**
	 * Try to compute a diff for the client.
	 *
	 * @param request the request
	 */
	private void info (HttpServletRequest request)
	{

		String id = request.getParameter("model");
		String vers = request.getParameter("version");
		
		if (id == null || vers == null)
		{
			invalid ();
			return;
		}
		

		
		
		
		
		
			System.out.println ("req info");

			String info = getInfo (id, vers);
			
			
			 
			out.println (info);
		
	}
}
