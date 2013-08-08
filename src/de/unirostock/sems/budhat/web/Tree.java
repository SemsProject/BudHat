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
import java.util.LinkedHashMap;
import java.util.Map;
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
import javax.xml.transform.TransformerException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;

import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.budhat.db.MySQLDB;
import de.unirostock.sems.budhat.mgmt.CookieManager;
import de.unirostock.sems.budhat.mgmt.Notifications;
import de.unirostock.sems.budhat.mgmt.UserManager;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.model.BioModel;



/**
 * The Class SBMLDiffer.
 *
 * @author martin scharm
 * 
 * A servlet that will be querried via ajax to get a diff. It will speak to a model server to generate a diff, the resulting string will be send to the website..
 */
public class Tree
extends WebModule
{
	
	private static final long	serialVersionUID	= 9148294324749188855L;
	

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
			tree (request, response);
			db.closeConnection ();
	}

	private String getTree (String id, String version)
	{
		System.out.println ("generating tree: " + id + " => " + version);
		BioModel model = mm.getModel (id);
		
		if (model == null)
			return "model not found...";
			
		try
		{
			Document doc = model.getGraphMLTree (version);
			return Tools.prettyPrintDocument (doc, new Tools.SimpleOutputStream ()).toString ();
		}
		catch (ParserConfigurationException | IOException | TransformerException e)
		{
			e.printStackTrace();
		}
		
		return "error creating tree...";
	}
  
  
	/**
	 * Try to compute a diff for the client.
	 *
	 * @param request the request
	 */
	private void tree (HttpServletRequest request, HttpServletResponse response)
	{

		String id = request.getParameter("model");
		String vers = request.getParameter("version");
		
		if (id == null || vers == null)
		{
			invalid (response, HttpServletResponse.SC_BAD_REQUEST, null);
			return;
		}
		

		
		
		
		
		
			System.out.println ("req tree");
			String tree = getTree (id, vers);
			
			Map<String, Object> json=new LinkedHashMap<String, Object>();
		  json.put("graphmltree",tree);

			response.setContentType("application/json");
			out.println (JSONValue.toJSONString(json));
			System.out.println (JSONValue.toJSONString(json));
	}
	
}
