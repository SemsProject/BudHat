/**
 * 
 */
package de.unirostock.sems.budhat.sbml;

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
import de.unirostock.sems.budhat.web.WebModule;


/**
 * The Class SBMLDiffer.
 *
 * @author martin scharm
 * 
 * A servlet that will be querried via ajax to get a diff. It will speak to a model server to generate a diff, the resulting string will be send to the website..
 */
public class SBMLDiffer
{
	
	private static final long	serialVersionUID	= 9148294324749188855L;
	
	public static boolean diff (ModelVersion bmA, ModelVersion bmB, HttpServletResponse response, PrintWriter out) throws ParserConfigurationException, BivesDocumentParseException, SAXException, IOException
	{

		System.out.println ("generating diff: " + bmA.toString ());
		System.out.println ("generating diff: " + bmB.toString ());

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		TreeDocument docA = new TreeDocument (builder.parse (new ByteArrayInputStream(bmA.getModel ().getBytes ())), new XyWeighter ());
		TreeDocument docB = new TreeDocument (builder.parse (new ByteArrayInputStream(bmB.getModel ().getBytes ())), new XyWeighter ());


		Connector con = new SBMLConnector ();
		con.init (docA, docB);
		con.findConnections ();
		
		SBMLDiffInterpreter inter = new SBMLDiffInterpreter (con.getConnections (), docA, docB);
		inter.interprete ();
		
		Producer patcher = new PatchProducer (con.getConnections (), docA, docB);
		
		Map<String, Object> json=new LinkedHashMap<String, Object>();
		
		json.put("crngraphml", inter.getCRNGraph ());
		json.put("htmlreport", inter.getReport ().generateHTMLReport ());
		json.put("xmldiff", patcher.produce ());
		
		

		response.setContentType("application/json");
		out.println (JSONValue.toJSONString(json));
		
		return true;
	}
}
