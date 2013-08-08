/**
 * 
 */
package de.unirostock.sems.budhat.web;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONValue;
import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.budhat.db.MySQLDB;
import de.unirostock.sems.budhat.mgmt.CookieManager;
import de.unirostock.sems.budhat.mgmt.Notifications;
import de.unirostock.sems.budhat.mgmt.UserManager;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.model.CellMLDiffer;
import de.unirostock.sems.budhat.model.ModelVersion;
import de.unirostock.sems.budhat.model.SBMLDiffer;


/**
 * The Class SBMLDiffer.
 *
 * @author martin scharm
 * 
 * A servlet that will be querried via ajax to get a diff. It will speak to a model server to generate a diff, the resulting string will be send to the website..
 */
public class Differ
extends WebModule
{

  protected TreeDocument docA;
  protected TreeDocument docB;
	
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
		
		
		try
		{
			if (!diff (request, response))
				invalid (response, HttpServletResponse.SC_BAD_REQUEST, null);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			invalid (response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			invalid (response, HttpServletResponse.SC_BAD_REQUEST, e);
		}

		db.closeConnection ();
	}
	
	/**
	 * Try to compute a diff for the client.
	 *
	 * @param request the request
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws BivesDocumentParseException 
	 */
	private boolean diff (HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String idA = request.getParameter("modelA");
		String idB = request.getParameter("modelB");
		String versA = request.getParameter("versionA");
		String versB = request.getParameter("versionB");
		
		if (idA == null || versA == null || idB == null || versB == null)
		{
			return false;
		}
		
		
		

		ModelVersion bmA = mm.getVersion (idA, versA), bmB = mm.getVersion (idB, versB);
		System.out.println ("versions: " + bmA + " - " + bmB);
		
		if (bmA == null || bmB == null)
		{
			out.println ("no models found");
			return false;
		}
		
		if (!bmA.getModelType ().equals (bmB.getModelType ()))
		{
			out.println ("models differ in type.");
			return false;
		}
		
		if (bmA.getModelType ().equals ("SBML"))
		{
			return SBMLDiffer.diff (bmA, bmB, response, out);
		}
		if (bmA.getModelType ().equals ("CellML"))
		{
			return CellMLDiffer.diff  (bmA, bmB, response, out);
		}
		
		out.println ("unknown type of models");
		return false;
	}
}
