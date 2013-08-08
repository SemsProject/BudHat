/**
 * 
 */
package de.unirostock.sems.budhat.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import de.unirostock.sems.bives.algorithm.cellml.CellMLGraphProducer;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorGraphML;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.budhat.db.DB;
import de.unirostock.sems.budhat.db.MySQLDB;
import de.unirostock.sems.budhat.db.DB.VersionRealtives;
import de.unirostock.sems.budhat.mgmt.Notifications;



/**
 * The Class ModelVersion, representing a specific version of a biomodel
 * 
 * @author martin scharm
 * 
 */
public class ModelVersion implements Comparable<ModelVersion>
{
	
	/** The file of the model, we'll need it later on to compute the diff. */
	//private File		file;
	
	/** id, name and date of the model/version. */
	private String	name;
	
	/** The pattern for a date. */
	//private Pattern	pattern	= Pattern.compile ("(\\d{4}-\\d{2}-\\d{2})");
	private String version, modeltype;
	
	private VersionRealtives relatives;

	private boolean avail;
	private int user, id;
	private BioModel biomodel;
	
	public void setBioModel (BioModel bm)
	{
		biomodel = bm;
	}

	public String getModelType ()
	{
		return modeltype;
	}

	public int getId ()
	{
		return id;
	}
	
	/**
	 * get infos about the version in form of a html description
	 * 
	 * @return html desription of the model
	 */
	public String getInfo ()
	{
		String info = "<div id='modelinfo'>";
		
		info += "<p>Model Name: <em><large>" + name + "</large></em>";
		String descr = biomodel.getDescription ();
		if (descr != null && descr.length () > 0)
			info += "<blockquote>" + descr + "</blockquote>";
		info += "</p>";
		info += "<p>Model Version: <em><large>" + version + "</large></em></p>";
		info += "<p>Type: <code>" + modeltype + "</code></p>";
		info += "<p><a href='download?downloadModel=" + id + "'>download this version</a></p>";
		
		try
		{
			if (modeltype.equals ("SBML"))
			{
				DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
					.newDocumentBuilder ();
				TreeDocument docA = new TreeDocument (builder.parse (new ByteArrayInputStream(getModel ().getBytes ())), new XyWeighter (), null);
				SBMLDocument sbmldoc = new SBMLDocument (docA);
				SBMLGraphProducer graphProd = new SBMLGraphProducer (sbmldoc);
				info += "<div id='graphmodelvizflash'></div><script type='text/javascript'>drawModelVizFlash ('"+new GraphTranslatorGraphML ().translate (graphProd.getCRN ()).replaceAll ("\n", "")+"');</script>";
			}
			else if (modeltype.equals ("CellML"))
			{
				DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
					.newDocumentBuilder ();
				TreeDocument docA = new TreeDocument (builder.parse (new ByteArrayInputStream(getModel ().getBytes ())), new XyWeighter (), null);
				CellMLDocument sbmldoc = new CellMLDocument (docA);
				CellMLGraphProducer graphProd = new CellMLGraphProducer (sbmldoc);
				info += "<div id='graphmodelvizflash'></div><script type='text/javascript'>drawModelVizFlash ('"+new GraphTranslatorGraphML ().translate (graphProd.getCRN ()).replaceAll ("\n", "")+"');</script>";
			}
		}
		catch (ParserConfigurationException | SAXException | IOException | BivesConsistencyException | BivesCellMLParseException | BivesLogicalException | URISyntaxException e)
		{
			e.printStackTrace();
				info += "<p>no visualization available ("+e.getMessage ()+")</p>";
		}

		/*if (modeltype.equals ("SBML"))
			try
			{
				DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
					.newDocumentBuilder ();
				TreeDocument docA = new TreeDocument (builder.parse (new ByteArrayInputStream(getModel ().getBytes ())), new XyWeighter ());
				
				SBMLModelViz viz = new SBMLModelViz ();
				String graph = viz.getGraphML (docA).replaceAll("\n", "");
				System.out.println (graph);
				
				info += "<div id='graphmodelvizflash'></div><script type='text/javascript'>drawModelVizFlash ('"+graph+"');</script>";
				
			}
			catch (ParserConfigurationException | SAXException | IOException e)
			{
				e.printStackTrace();
			}*/
		
		return info + "<div>";
	}
	


	private DB db;
	private Notifications err;
	public ModelVersion (DB db, ResultSet dbExtract, Notifications err) throws SQLException
	{
		this.db = db;
		this.err = err;
		id = dbExtract.getInt ("id");
		name = dbExtract.getString ("modelid");
		version = dbExtract.getString ("modelversion");
		avail = dbExtract.getBoolean ("public");
		user = dbExtract.getInt ("user");
		modeltype = dbExtract.getString ("modeltype");
		relatives = db.getRelativesOf (id);
		//System.out.println ("kids:" + relatives.kids);
		//System.out.println ("parents: " + relatives.parents);
	}
	
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName ()
	{
		return name;
	}
	
	public boolean isPublic ()
	{
		return avail;
	}
	
	public int getUser ()
	{
		return user;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return version;
	}
	
	public Vector<Integer> getKids ()
	{
		return relatives.kids;
	}
	
	public Vector<Integer> getParents ()
	{
		return relatives.parents;
	}
	
	public String getVersion ()
	{
		return version;
	}
	
	public boolean hasParent (int p)
	{
		for (int i : relatives.parents)
			if (i == p)
				return true;
		return false;
	}
	
	public String getModel ()
	{
		return db.getModelCode (name, version);
	}


	@Override
	public int compareTo (ModelVersion v)
	{
		return version.compareTo (v.version);
	}
}
