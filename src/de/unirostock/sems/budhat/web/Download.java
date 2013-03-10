/**
 * 
 */
package de.unirostock.sems.budhat.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.unirostock.sems.budhat.db.MySQLDB;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.sbml.ModelManager;
import de.unirostock.sems.budhat.sbml.ModelVersion;


/**
 * @author Martin Scharm
 *
 */
public class Download
	extends WebModule
{
	
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
	protected void start (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		super.init (request, response, "get");

		if (!validRequest)
			return;
		
		if (request.getParameter ("downloadModel") != null)
		{
			String model = request.getParameter ("downloadModel");
			int modelid = -1;
			try
			{
				modelid = Integer.parseInt (model);
			}
			catch (NumberFormatException e)
			{
				//err ("Error parsing form fields -- NFE", response);
				invalid (response, HttpServletResponse.SC_BAD_REQUEST, e);
				db.closeConnection ();
				return;
			}
			ModelVersion currentVersion = mm.getVersionById (modelid);
			if (currentVersion == null)
			{
				//err ("No such version", response);
				invalid (response, HttpServletResponse.SC_BAD_REQUEST, null);
				db.closeConnection ();
				return;
			}

			response.setContentType ("application/xml");
			response.setHeader ("Content-Disposition", "attachment; filename=\""+currentVersion.getName () + "-" + currentVersion.getVersion () + "\"");

			System.out.println (currentVersion.getModel ());
			out.print (currentVersion.getModel ());
			db.closeConnection ();
			return;
		}
		
		
		db.closeConnection ();
	}
	
	private void err (String msg, HttpServletResponse response) throws IOException
	{
		response.setContentType ("text/plain");
		out = response.getWriter ();
		out.println (msg);
		return;
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
}
