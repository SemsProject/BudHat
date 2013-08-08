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
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.api.SBMLDiff;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
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
	
	/*public static File disgusting (String cont) throws IOException
	{
		File file = File.createTempFile ("bives", "muell");
		file.deleteOnExit ();
		BufferedWriter bw = new BufferedWriter(new FileWriter (file));
		bw.write (cont);
		bw.close ();
		return file;
	}*/
	
	public static boolean diff (ModelVersion bmA, ModelVersion bmB, HttpServletResponse response, PrintWriter out) throws ParserConfigurationException, BivesDocumentParseException, SAXException, IOException, BivesConnectionException, BivesConsistencyException
	{

		System.out.println ("generating diff: " + bmA.toString ());
		System.out.println ("generating diff: " + bmB.toString ());

		// urghs.. but no time..
		/*File file1 = disgusting (bmA.getModel ());
		File file2 = disgusting (bmB.getModel ());*/
		
		
		
		SBMLDiff differ = new SBMLDiff (bmA.getModel (), bmB.getModel ());
		differ.mapTrees ();
		
		
		
		
		
		
		/*DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		TreeDocument docA = new TreeDocument (builder.parse (new ByteArrayInputStream(bmA.getModel ().getBytes ())), new XyWeighter ());
		TreeDocument docB = new TreeDocument (builder.parse (new ByteArrayInputStream(bmB.getModel ().getBytes ())), new XyWeighter ());

		SBMLDocument doc1 = new SBMLDocument (docA);
		SBMLDocument doc2 = new SBMLDocument (docB);
		
		Connector con = new SBMLConnector (doc1, doc2);
		con.init (docA, docB);
		con.findConnections ();
		
		docA.getRoot ().resetModifications ();
		docA.getRoot ().evaluate (con.getConnections ());
		
		docB.getRoot ().resetModifications ();
		docB.getRoot ().evaluate (con.getConnections ());
		
		//System.out.println (con.getConnections ());
		
		SBMLDiffInterpreter inter = new SBMLDiffInterpreter (con.getConnections (), doc1, doc2);
		inter.interprete ();
		
		Producer patcher = new PatchProducer ();
		patcher.init (con.getConnections (), docA, docB);
		
		SBMLGraphProducer graphProd = new SBMLGraphProducer (con.getConnections (), doc1, doc2);*/
		
		Map<String, Object> json=new LinkedHashMap<String, Object>();

		try
		{
			json.put("crngraphml", differ.getCRNGraphML ());
			//json.put ("crngraphml", "<graphml     xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\"     xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">     <key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\" />     <key id=\"weight\" for=\"node\" attr.name=\"weight\" attr.type=\"double\" />     <key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\" />     <key id=\"label\" for=\"edge\" attr.name=\"label\" attr.type=\"string\" />     <key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\" />     <graph edgedefault=\"undirected\">         <node id=\"n3\">             <data key=\"label\">n3</data>         </node>         <node id=\"n4\">             <data key=\"label\">n4</data>         </node>         <node id=\"n16\">             <data key=\"label\">n16</data>         </node>         <node id=\"n17\">             <data key=\"label\">n17</data>         </node>         <node id=\"n18\">             <data key=\"label\">n18</data>         </node>         <node id=\"n19\">             <data key=\"label\">n19</data>         </node>         <node id=\"n2\">             <data key=\"label\">n2</data>             <graph edgedefault=\"undirected\">                 <node id=\"n22\">                     <data key=\"label\">n22</data>                 </node>                 <node id=\"n21\">                     <data key=\"label\">n21</data>                 </node>                 <node id=\"n23\">                     <data key=\"label\">n23</data>                 </node>                 <edge target=\"n21\" id=\"e15\" source=\"n23\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.2</data>                 </edge>                 <edge target=\"n22\" id=\"e16\" source=\"n23\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.1</data>                 </edge>                 <edge target=\"n21\" id=\"e17\" source=\"n22\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.2</data>                 </edge>             </graph>         </node>         <node id=\"n1\">             <data key=\"label\">n1</data>             <graph edgedefault=\"undirected\">                 <node id=\"n20\">                     <data key=\"label\">n20</data>                 </node>                 <node id=\"n11\">                     <data key=\"label\">n11</data>                 </node>                 <node id=\"n12\">                     <data key=\"label\">n12</data>                 </node>                 <node id=\"n13\">                     <data key=\"label\">n13</data>                     <graph edgedefault=\"undirected\">                         <node id=\"n131\">                             <data key=\"label\">n131</data>                         </node>                     </graph>                 </node>                 <edge target=\"n12\" id=\"e12\" source=\"n11\">                     <data key=\"label\">e12</data>                     <data key=\"weight\">0.2</data>                 </edge>                 <edge target=\"n11\" id=\"e13\" source=\"n20\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.1</data>                 </edge>                 <edge target=\"n11\" id=\"e20\" source=\"n13\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.3</data>                 </edge>             </graph>         </node>         <node id=\"n6\">             <data key=\"label\">n6</data>             <graph edgedefault=\"undirected\">                 <node id=\"n10\">                     <data key=\"label\">n10</data>                 </node>                 <node id=\"n8\">                     <data key=\"label\">n8</data>                 </node>                 <node id=\"n7\">                     <data key=\"label\">n7</data>                 </node>                 <node id=\"n9\">                     <data key=\"label\">n9</data>                     <graph edgedefault=\"undirected\">                         <node id=\"n14\">                             <data key=\"label\">n14</data>                         </node>                         <node id=\"n15\">                             <data key=\"label\">n15</data>                         </node>                         <edge target=\"n14\" id=\"e18\" source=\"n15\">                             <data key=\"label\"></data>                         </edge>                     </graph>                 </node>                 <edge target=\"n10\" id=\"e5\" source=\"n8\">                     <data key=\"label\"></data>                 </edge>                 <edge target=\"n10\" id=\"e6\" source=\"n7\">                     <data key=\"label\"></data>                 </edge>             </graph>         </node>         <edge target=\"n3\" id=\"e3\" source=\"n4\">             <data key=\"label\"></data>             <data key=\"weight\">0.7</data>         </edge>         <edge target=\"n3\" id=\"e4\" source=\"n2\">             <data key=\"label\"></data>             <data key=\"weight\">0.4</data>         </edge>         <edge target=\"n10\" id=\"e7\" source=\"n3\">             <data key=\"label\"></data>             <data key=\"weight\">0.2</data>         </edge>         <edge target=\"n19\" id=\"e8\" source=\"n18\">             <data key=\"label\"></data>             <data key=\"weight\">0.3</data>         </edge>         <edge target=\"n17\" id=\"e9\" source=\"n19\">             <data key=\"label\"></data>             <data key=\"weight\">0.9</data>         </edge>         <edge target=\"n16\" id=\"e10\" source=\"n17\">             <data key=\"label\"></data>             <data key=\"weight\">0.8</data>         </edge>         <edge target=\"n1\" id=\"e11\" source=\"n16\">             <data key=\"label\"></data>             <data key=\"weight\">0.4</data>         </edge>         <edge target=\"n18\" id=\"e14\" source=\"n17\">             <data key=\"label\"></data>             <data key=\"weight\">0.2</data>         </edge>         <edge target=\"n7\" id=\"e19\" source=\"n15\">             <data key=\"label\"></data>             <data key=\"weight\">0.1</data>         </edge>     </graph> </graphml>");
			//json.put ("crngraphml", "<graphml     xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\"     xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">     <key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\" />     <key id=\"weight\" for=\"node\" attr.name=\"weight\" attr.type=\"double\" />     <key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\" />     <key id=\"label\" for=\"edge\" attr.name=\"label\" attr.type=\"string\" />     <key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\" />     <graph edgedefault=\"undirected\">         <node id=\"n3\">             <data key=\"label\">n3</data>         </node>         <node id=\"n4\">             <data key=\"label\">n4</data>         </node>         <node id=\"n16\">             <data key=\"label\">n16</data>         </node>         <node id=\"n17\">             <data key=\"label\">n17</data>         </node>         <node id=\"n18\">             <data key=\"label\">n18</data>         </node>         <node id=\"n19\">             <data key=\"label\">n19</data>         </node>         <node id=\"n2\">             <data key=\"label\">n2</data>             <graph edgedefault=\"undirected\">                 <node id=\"n22\">                     <data key=\"label\">n22</data>                 </node>                 <node id=\"n21\">                     <data key=\"label\">n21</data>                 </node>                 <node id=\"n23\">                     <data key=\"label\">n23</data>                 </node>             </graph>         </node>         <node id=\"n1\">             <data key=\"label\">n1</data>             <graph edgedefault=\"undirected\">                 <node id=\"n20\">                     <data key=\"label\">n20</data>                 </node>                 <node id=\"n11\">                     <data key=\"label\">n11</data>                 </node>                 <node id=\"n12\">                     <data key=\"label\">n12</data>                 </node>                 <node id=\"n13\">                     <data key=\"label\">n13</data>                     <graph edgedefault=\"undirected\">                         <node id=\"n131\">                             <data key=\"label\">n131</data>                         </node>                     </graph>                 </node>                 <edge target=\"n12\" id=\"e12\" source=\"n11\">                     <data key=\"label\">e12</data>                     <data key=\"weight\">0.2</data>                 </edge>                 <edge target=\"n11\" id=\"e13\" source=\"n20\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.1</data>                 </edge>                 <edge target=\"n11\" id=\"e20\" source=\"n13\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.3</data>                 </edge>             </graph>         </node>         <node id=\"n6\">             <data key=\"label\">n6</data>             <graph edgedefault=\"undirected\">                 <node id=\"n10\">                     <data key=\"label\">n10</data>                 </node>                 <node id=\"n8\">                     <data key=\"label\">n8</data>                 </node>                 <node id=\"n7\">                     <data key=\"label\">n7</data>                 </node>                 <node id=\"n9\">                     <data key=\"label\">n9</data>                     <graph edgedefault=\"undirected\">                         <node id=\"n14\">                             <data key=\"label\">n14</data>                         </node>                         <node id=\"n15\">                             <data key=\"label\">n15</data>                         </node>                         <edge target=\"n14\" id=\"e18\" source=\"n15\">                             <data key=\"label\"></data>                         </edge>                     </graph>                 </node>                 <edge target=\"n10\" id=\"e5\" source=\"n8\">                     <data key=\"label\"></data>                 </edge>                 <edge target=\"n10\" id=\"e6\" source=\"n7\">                     <data key=\"label\"></data>                 </edge>             </graph>         </node>         <edge target=\"n3\" id=\"e3\" source=\"n4\">             <data key=\"label\"></data>             <data key=\"weight\">0.7</data>         </edge>         <edge target=\"n3\" id=\"e4\" source=\"n2\">             <data key=\"label\"></data>             <data key=\"weight\">0.4</data>         </edge>         <edge target=\"n10\" id=\"e7\" source=\"n3\">             <data key=\"label\"></data>             <data key=\"weight\">0.2</data>         </edge>         <edge target=\"n19\" id=\"e8\" source=\"n18\">             <data key=\"label\"></data>             <data key=\"weight\">0.3</data>         </edge>         <edge target=\"n17\" id=\"e9\" source=\"n19\">             <data key=\"label\"></data>             <data key=\"weight\">0.9</data>         </edge>         <edge target=\"n16\" id=\"e10\" source=\"n17\">             <data key=\"label\"></data>             <data key=\"weight\">0.8</data>         </edge>         <edge target=\"n1\" id=\"e11\" source=\"n16\">             <data key=\"label\"></data>             <data key=\"weight\">0.4</data>         </edge>         <edge target=\"n18\" id=\"e14\" source=\"n17\">             <data key=\"label\"></data>             <data key=\"weight\">0.2</data>         </edge>         <edge target=\"n7\" id=\"e19\" source=\"n15\">             <data key=\"label\"></data>             <data key=\"weight\">0.1</data>         </edge>                 <edge target=\"n21\" id=\"e15\" source=\"n23\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.2</data>                 </edge>                 <edge target=\"n22\" id=\"e16\" source=\"n23\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.1</data>                 </edge>                 <edge target=\"n21\" id=\"e17\" source=\"n22\">                     <data key=\"label\"></data>                     <data key=\"weight\">0.2</data>                 </edge>     </graph> </graphml>");
		}
		catch (Exception e)
		{
			e.printStackTrace ();
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
		return true;
	}
}
