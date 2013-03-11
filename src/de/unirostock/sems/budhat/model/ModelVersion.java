/**
 * 
 */
package de.unirostock.sems.budhat.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


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
