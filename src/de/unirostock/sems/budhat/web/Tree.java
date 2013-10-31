/**
 * 
 */
package de.unirostock.sems.budhat.web;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.XmlTools;
import de.unirostock.sems.budhat.model.BioModel;
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

	private String getTree (BioModel model, String version)
	{
		if (model == null)
			return "model not found...";
		
		
		System.out.println ("generating tree: " + model.getName () + " => " + version);
			
		try
		{
			Document doc = model.getGraphMLTree (version);
			return XmlTools.prettyPrintDocument (doc, new Tools.SimpleOutputStream ()).toString ();
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
		

		
		
		BioModel model = mm.getModel (id);
		
		
		System.out.println ("req tree");
		String tree = getTree (model, vers);
		
		Map<String, Object> json=new LinkedHashMap<String, Object>();
	  json.put("graphmltree",tree);
	  
	  Vector<ModelVersion> versions = model.getVersions ();
	  JSONArray arr = new JSONArray ();
	  
	  for (int i = 0; i < versions.size (); i++)
	  {
	  	ModelVersion versionA = versions.elementAt (i);
	  	for (int j = i+1; j < versions.size (); j++)
	  	{
		  	ModelVersion versionB = versions.elementAt (j);
	  		JSONObject diff = new JSONObject ();
	  		diff.put ("versionA", versionA.getVersion ());
	  		diff.put ("versionB", versionB.getVersion ());
  			try
  			{
		  		if (!versionA.getModelType ().equals (versionB.getModelType ()))
		  			diff.put ("crndiff", null);
		  		
		  		else if (versionA.getModelType ().equals ("SBML"))
		  		{
		  				diff.put ("crndiff", SBMLDiffer.crndiffJson (versionA, versionB));
		  		}
		  		else if (versionA.getModelType ().equals ("CellML"))
		  		{
			  			diff.put ("crndiff", CellMLDiffer.crndiffJson (versionA, versionB));
		  		}
		  		arr.add (diff);
  			}
  			catch (Exception e)
  			{
  				LOGGER.error ("cannot create crn diff for model " + id + " versions " + versionA.getVersion () + ", " + versionB.getVersion (), e);
  			}
	  	}
	  }
	  json.put("diffs",arr);
	  
	  arr = new JSONArray ();
	  for (ModelVersion v : versions)
	  {
	  	arr.add (v.getVersion ());
	  }
	  json.put("versions",arr);
	  
	  
		response.setContentType("application/json");
		out.println (JSONValue.toJSONString(json));
		System.out.println (JSONValue.toJSONString(json));
	}
	
}
