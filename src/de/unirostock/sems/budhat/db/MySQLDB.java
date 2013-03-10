/**
 * 
 */
package de.unirostock.sems.budhat.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import de.unirostock.sems.budhat.mgmt.Notifications;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.sbml.BioModel;
import de.unirostock.sems.budhat.sbml.ModelVersion;



/**
 * @author martin
 *
 */
public class MySQLDB extends DB
{
	private final static Logger LOGGER = Logger.getLogger(MySQLDB.class.getName());
	Connection con;
	Notifications err;
	public MySQLDB (Notifications err) throws SQLException, NamingException
	{
		this.err = err;
		Context initCtx = new InitialContext();
	Context envCtx = (Context) initCtx.lookup("java:comp/env");
	DataSource ds = (DataSource) envCtx.lookup("jdbc/BioModelsDB");
	
	 con = ds.getConnection();
	}
	
	public void closeConnection ()
	{
		try
		{
			if (con != null)
			{
				con.close ();
				con = null;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public ResultSet execute (String query) throws SQLException
	{
		Statement stmt = con.createStatement();
		return stmt.executeQuery(query);
	}
	
	public PreparedStatement prepareStatement (String query)
	{
		try
		{
			return con.prepareStatement (query);
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			err.addError ("SQLException: cannot prepare statement");
		}
		return null;
	}

	@Override
	public ModelVersion getVersion (String id, String version)
	{
		PreparedStatement st = prepareStatement ("Select * from models where modelid=? and modelversion=?");

		try
		{
			st.setString (1, id);
			st.setString (2, version);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			err.addError ("SQLException preparing stmt to retrieve model: " + e.getMessage ());
			return null;
		}

		try
		{
			st.execute ();
			ResultSet rs = st.getResultSet();
			rs.last ();
			return new ModelVersion (this, rs, err);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			err.addError ("SQLException retrieving model: " + e.getMessage ());
		}
		
		
		return null;
	}

	@Override
	public HashMap<String, BioModel> getAvailableModels ()
	{
		//Vector<BioModel> modelVector = new Vector<BioModel> ();
		HashMap<String, BioModel> models = new HashMap<String, BioModel> ();
		ResultSet rs;
		try
		{
			PreparedStatement st = prepareStatement ("SELECT * FROM models WHERE public=1 OR user=?");
			if (user != null)
				st.setInt (1, user.id);
			else
				st.setInt (1, -1);
			st.execute ();
			rs = st.getResultSet ();//db.execute ("SELECT * FROM models WHERE public=1 OR user=?");
		  while(rs.next())
		  {
		  	ModelVersion v = new ModelVersion (this, rs, err);
				BioModel bm = models.get (v.getName ());
				if (bm != null)
					bm.addVersion (v);
				else
				{
					bm = new BioModel (v);
					models.put (v.getName (), bm);
				}
				v.setBioModel (bm);
		  }
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			err.addError ("SQLException reading models: " + e.getMessage ());
		}

		for (String bm : models.keySet ())
		{
			BioModel m = models.get (bm);
			// is there a description?
			if (m.getName () != null && m.getName ().length () > 0)
			{
				try
				{
					PreparedStatement st = prepareStatement ("SELECT description FROM modeldescription WHERE modelid=?");
					st.setString (1, m.getName ());
					st.execute ();
					rs = st.getResultSet ();
					if (rs.next())
						m.setDescription (rs.getString ("description"));
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					err.addError ("SQLException reading description for model: " + e.getMessage ());
				}
			}
			//modelVector.add (m);
		}
	  
		//Collections.sort (modelVector);
		
		return models;
	}

	@Override
	public boolean insterModel (String modelid, String version, String model, String type)
	{
			// check if already in db...
			PreparedStatement st = prepareStatement ("INSERT INTO models (user, modelid, modelversion, model, modeltype, public) VALUES (?,?,?,?,?,0)");
			try 
			{
				st.setInt (1, user.id);
				st.setString (2, modelid);
				st.setString (3, version);
				st.setString (4, model);
				st.setString (5, type);
				st.execute ();
				err.addInfo ("Added a new model: " + modelid + " with version " + version);
				return true;
			}
			catch (SQLException e)
			{
				LOGGER.error ("error inserting model", e);
				err.addError ("DB error inserting new model: " + e.getMessage ());
				return false;
			}
	}

	@Override
	public String getModelCode (String id, String version)
	{
		PreparedStatement st = prepareStatement ("Select model from models where modelid=? and modelversion=?");

		try
		{
			st.setString (1, id);
			st.setString (2, version);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error retrieving model code by id/version", e);
			err.addError ("SQLException preparing stmt to retrieve model: " + e.getMessage ());
			return null;
		}
		
		return getModelCode (st);
	}

	@Override
	public String getModelCodeByDbId (String id)
	{
		PreparedStatement st = prepareStatement ("Select model from models where id=?");
		try
		{
			st.setString (1, id);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error retrieving model code by id", e);
			err.addError ("SQLException preparing stmt to retrieve model: " + e.getMessage ());
			return null;
		}
		
		return getModelCode (st);
	}
	
	private String getModelCode (PreparedStatement st)
	{

		try
		{
			st.execute ();
			ResultSet rs = st.getResultSet();
			rs.last ();
			return rs.getString ("model");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error retrieving model code", e);
			err.addError ("SQLException retrieving model: " + e.getMessage ());
		}
		
		
		return null;
	}

	@Override
	public boolean deleteModel (int id)
	{
		// get parents of this model
		Vector<Integer> parents = new Vector<Integer> ();
		PreparedStatement st = prepareStatement ("SELECT parent FROM modelhierarchy WHERE child=?");
		try
		{
			st.setInt (1, id);
			st.execute ();
			ResultSet rs = st.getResultSet ();
			while(rs.next())
		  {
				parents.add (rs.getInt ("parent"));
		  }
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error retrieving parents of model to delete", e);
			err.addError ("Error updating DB: " + e.getMessage ());
			return false;
		}
		
		// reset parents of this models children
		st = prepareStatement ("SELECT child FROM modelhierarchy WHERE parent=?");
		try
		{
			st.setInt (1, id);
			st.execute ();
			ResultSet rs = st.getResultSet ();
			while(rs.next())
		  {
				int child = rs.getInt ("child");
				for (Integer parent : parents)
				{
					PreparedStatement st2 = prepareStatement ("INSERT IGNORE INTO modelhierarchy (child, parent) VALUES (?, ?)");
					st2.setInt (1, child);
					st2.setInt (1, parent);
					st2.execute ();
				}
		  }
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error creating new hierarchy", e);
			err.addError ("Error updating DB: " + e.getMessage ());
			return false;
		}
		
		// delete this models hierarchy
		st = prepareStatement ("DELETE FROM modelhierarchy WHERE child=? OR parent=?");
		try
		{
			st.setInt (1, id);
			st.setInt (2, id);
			st.execute ();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error dropping old hierarchy", e);
			err.addError ("Error updating DB: " + e.getMessage ());
			return false;
		}
		
		// delete this model
		st = prepareStatement ("DELETE FROM models WHERE id=?");
		try
		{
			st.setInt (1, id);
			st.execute ();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error dropping model", e);
			err.addError ("Error updating DB: " + e.getMessage ());
			return false;
		}
		return true;
	}

	@Override
	public boolean publishModel (int id, boolean pub)
	{
		
		PreparedStatement st = prepareStatement ("UPDATE models SET public=? WHERE id=?");
		try
		{
			st.setBoolean (1, pub);
			st.setInt (2, id);
			st.execute ();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error publishing model", e);
			err.addError ("Error updating DB: " + e.getMessage ());
			return false;
		}
		return true;
	}

	@Override
	public boolean addHierarchy (int child, int parent)
	{

		PreparedStatement st2 = prepareStatement ("INSERT IGNORE INTO modelhierarchy (child, parent) VALUES (?, ?)");
		try
		{
			st2.setInt (1, child);
			st2.setInt (2, parent);
			st2.execute ();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error inserting hierarchy", e);
			err.addError ("Error updating DB: " + e.getMessage ());
			return false;
		}
		return true;
	}

	@Override
	public boolean remHierarchy (int child, int parent)
	{
		PreparedStatement st = prepareStatement ("DELETE FROM modelhierarchy WHERE child=? AND parent=?");
		try
		{
			st.setInt (1, child);
			st.setInt (2, parent);
			st.execute ();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error dropping hierarchy", e);
			err.addError ("Error updating DB: " + e.getMessage ());
			return false;
		}
		return true;
	}

	@Override
	public VersionRealtives getRelativesOf (int id)
	{
		VersionRealtives vr = new VersionRealtives ();

		PreparedStatement st = prepareStatement ("SELECT * FROM modelhierarchy WHERE parent=? OR child=?");
		try
		{
			st.setInt (1, id);
			st.setInt (2, id);
			st.execute ();
			ResultSet rs = st.getResultSet ();
			while(rs.next())
		  {
				if (rs.getInt ("child") == id)
					vr.parents.add (rs.getInt ("parent"));
				else
					vr.kids.add (rs.getInt ("child"));
		  }
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LOGGER.error ("error calling hierarchy", e);
			err.addError ("Error calling DB: " + e.getMessage ());
			return null;
		}
		return vr;
	}
}
