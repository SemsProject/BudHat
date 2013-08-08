/**
 * 
 */
package de.unirostock.sems.budhat.model;

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
import java.net.URISyntaxException;
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

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.cellml.CellMLConnector;
import de.unirostock.sems.bives.algorithm.cellml.CellMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.CellMLReadException;
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
public class CellMLDiffer
{
	
	private static final long	serialVersionUID	= 9148294324749188855L;
	
	public static boolean diff (ModelVersion bmA, ModelVersion bmB, HttpServletResponse response, PrintWriter out) throws ParserConfigurationException, BivesDocumentParseException, SAXException, IOException, BivesConnectionException, BivesConsistencyException, BivesLogicalException, URISyntaxException, BivesCellMLParseException
	{
		System.out.println ("generating diff: " + bmA.toString ());
		System.out.println ("generating diff: " + bmB.toString ());

		// urghs.. but no time..
		/*File file1 = SBMLDiffer.disgusting (bmA.getModel ());
		File file2 = SBMLDiffer.disgusting (bmB.getModel ());*/
		
		
		
		CellMLDiff differ = new CellMLDiff (bmA.getModel (), bmB.getModel ());//file1, file2);
		differ.mapTrees ();

		Map<String, Object> json=new LinkedHashMap<String, Object>();

		try
		{
			json.put("crngraphml", differ.getCRNGraphML ());
		}
		catch (Exception e)
		{
			LOGGER.error ("error producing crn graph: " + e.getMessage ());
		}
		try
		{
			json.put("htmlreport", differ.getHTMLReport ());
		}
		catch (Exception e)
		{
			LOGGER.error ("error producing html report: " + e.getMessage ());
		}
		try
		{
			json.put("xmldiff", differ.getDiff ());
		}
		catch (Exception e)
		{
			LOGGER.error ("error producing xml diff: " + e.getMessage ());
		}

		response.setContentType("application/json");
		out.println (JSONValue.toJSONString(json));
		

		/*System.out.println ("generating diff: " + bmA.toString ());
		System.out.println ("generating diff: " + bmB.toString ());

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		TreeDocument docA = new TreeDocument (builder.parse (new ByteArrayInputStream(bmA.getModel ().getBytes ())), new XyWeighter ());
		TreeDocument docB = new TreeDocument (builder.parse (new ByteArrayInputStream(bmB.getModel ().getBytes ())), new XyWeighter ());


		Connector con = new CellMLConnector ();
		con.init (docA, docB);
		con.findConnections ();
		
		CellMLDiffInterpreter inter = new CellMLDiffInterpreter (con.getConnections (), docA, docB);
		inter.interprete ();
		
		Producer patcher = new PatchProducer ();
		patcher.init (con.getConnections (), docA, docB);
		
		Map<String, Object> json=new LinkedHashMap<String, Object>();
		
		json.put("htmlreport", inter.getReport ().generateHTMLReport ());
		json.put("xmldiff", patcher.produce ());
		
		

		response.setContentType("application/json");
		out.println (JSONValue.toJSONString(json));*/
		
		return true;
	}
}
