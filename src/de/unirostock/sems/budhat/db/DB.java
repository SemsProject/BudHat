/**
 * 
 */
package de.unirostock.sems.budhat.db;

import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.model.BioModel;
import de.unirostock.sems.budhat.model.ModelVersion;


/**
 * @author Martin Scharm
 *
 */
public abstract class DB
{
	protected User user;
	
	public void setUser (User user)
	{
		this.user = user;
	}
	
	public static class VersionRealtives
	{
		public Vector<Integer> kids;
		public Vector<Integer> parents;
		
		public VersionRealtives ()
		{
			kids = new Vector<Integer> ();
			parents = new Vector<Integer> ();
		}
	}
	
	public abstract String getModelCode (String id, String version);
	public abstract String getModelCodeByDbId (String id);
	public abstract ModelVersion getVersion (String id, String version);
	public abstract HashMap<String, BioModel> getAvailableModels ();
	public abstract boolean insterModel (String modelid, String version, String model, String type);
	public abstract boolean deleteModel (int id);
	public abstract boolean publishModel (int id, boolean pub);
	public abstract boolean addHierarchy (int child, int parent);
	public abstract boolean remHierarchy (int child, int parent);
	public abstract VersionRealtives getRelativesOf (int id);
}
