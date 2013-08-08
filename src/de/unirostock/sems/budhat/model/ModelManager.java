/**
 * 
 */
package de.unirostock.sems.budhat.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.tools.FileRetriever;
import de.unirostock.sems.budhat.db.DB;
import de.unirostock.sems.budhat.mgmt.Notifications;
import de.unirostock.sems.budhat.mgmt.UserManager.User;


/**
 * @author Martin Scharm
 *
 */
public class ModelManager
{
	private final static Logger LOGGER = Logger.getLogger(ModelManager.class.getName());
  private static final int THRESHOLD_SIZE = 1024 * 1024 * 10; // 10MB
	private HashMap<String, BioModel> modelVector;
	private User user;
	private HttpServletRequest request;
	private Notifications notifications;
	private HashMap<Integer, ModelVersion> versionById;
	private boolean showModels;
	
	public ModelManager (DB db, User user, HttpServletRequest request, Notifications err)
	{
		updateModels (db);
		showModels = false;
		this.notifications = err;
		this.user = user;
		this.request = request;
		if (checkUpdate (db))
		{
			updateModels (db);
			showModels = true;
		}
	}
	
	public BioModel getModel (String id)
	{
		return modelVector.get (id);
	}
	
	public boolean showModels ()
	{
		return showModels;
	}
	
	private void updateModels (DB db)
	{
		modelVector = db.getAvailableModels ();
		versionById = null;
	}
	
	private void updateIdMapper ()
	{
		versionById = new HashMap<Integer, ModelVersion> ();
		for (BioModel model : modelVector.values ())
			model.updateIdMapper (versionById);
	}

	public String getAvailableModels ()
	{
		String ret = "";
		SortedSet<String> names = new TreeSet<String>(modelVector.keySet ());
		for (String name : names)
			ret += modelVector.get (name).getList (user);
		return ret;
	}
	
	public String getMyModels ()
	{
		String ret = "";
		SortedSet<String> names = new TreeSet<String>(modelVector.keySet ());
		for (String name : names)
			ret += modelVector.get (name).getOwnModelTable (user);
		return ret;
	}
	
	public ModelVersion getVersionById (int id)
	{
		if (versionById == null)
			updateIdMapper ();
		
		return versionById.get (id);
	}
	
	public ModelVersion getVersion (String modelid, String modelversion)
	{
		BioModel model = modelVector.get (modelid);
		if (model == null)
			return null;
		
		return model.getVersion (modelversion);
	}
	
	
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	public boolean checkUpdate (DB db)
	{
		System.out.println (user + " -- " + request.getParameter ("submitfile") + " -- " + ServletFileUpload.isMultipartContent(request));
		
		if (user != null)
		{
			if (ServletFileUpload.isMultipartContent(request))
			{
				String model = null;
				String modelid = null;
				String version  = null;
				String modeltype  = null;
				
		    DiskFileItemFactory factory = new DiskFileItemFactory();
		    factory.setSizeThreshold(THRESHOLD_SIZE);
		    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		     
		    ServletFileUpload upload = new ServletFileUpload(factory);
				try
				{
					List formItems = upload.parseRequest(request);
	
	        Iterator iter = formItems.iterator();
	         
	        // iterates over form's fields
					while (iter.hasNext ())
					{
						FileItem item = (FileItem) iter.next ();
						if (!item.isFormField ())
						{
							if (item.getFieldName ().equals ("newsbmlfile"))
							{
								model = item.getString ();
							}
						}
						else
						{
							if (item.getFieldName ().equals ("versionnumber"))
								version = item.getString ();
							if (item.getFieldName ().equals ("modeltype"))
								modeltype = item.getString ();
							if (item.getFieldName ().equals ("modelid"))
								modelid = item.getString ();
						}
	        }
				}
				catch (FileUploadException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (model != null)
				{
					if (version == null)
						version = new SimpleDateFormat( "yyyy-MM-dd" ).format (new Date ());
					else if (!version.matches ("^[a-zA-Z0-9.-]+$"))
					{
						notifications.addError ("Version identifier contains invalid characters! Only 'a-z', 'A-Z', '0-9', '.' and '-' are allowed.");
						return false;
					}
					if (version.length () > 100)
					{
						notifications.addError ("Version identifier must not contain more than 100 chars!");
						return false;
					}
					if (modeltype == null)
					{
						notifications.addError ("no model type chosen!");
						return false;
					}
					FileRetriever.FIND_LOCAL = false;
					try
					{
						DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
							.newDocumentBuilder ();
						TreeDocument td = new TreeDocument (builder.parse (new ByteArrayInputStream(model.getBytes ())), null, null);
					
						if (modeltype.equals ("SBML"))
						{
							SBMLDocument doc = new SBMLDocument (td);
							if (modelid == null || modelid.trim ().length () < 3)
								modelid = doc.getModel ().getID ();
							/*if (modelid == null || modelid.trim ().length () < 3)
								modelid = doc.getModel ().getName ();*/
							if (modelid == null || modelid.trim ().length () < 3)
							{
								LOGGER.error ("cannot read modelid from sbml file");
								notifications.addError ("cannot read modelid from sbml file");
								return false;
							}
							// check validity by bives
							
							// test model : can jsbml read it!?
							/*SBMLValidator val = new SBMLValidator ();
							val.validate (model);
							if (modelid == null || modelid.trim ().length () < 3)
								modelid = val.getModelID ();
							if (modelid == null || modelid.trim ().length () < 3)
							{
								LOGGER.error ("cannot read modelid from sbml file");
								notifications.addError ("cannot read modelid from sbml file");
								return false;
							}
							
							// check validity by bives
							DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
								.newDocumentBuilder ();
							TreeDocument td = new TreeDocument (builder.parse (new ByteArrayInputStream(model.getBytes ())), null);*/
						}
						else if (modeltype.equals ("CellML"))
						{
							CellMLDocument doc = new CellMLDocument (td);
							if (modelid == null || modelid.trim ().length () < 3)
								modelid = doc.getModel ().getName ();
							if (modelid == null || modelid.trim ().length () < 3)
							{
								LOGGER.error ("cannot read modelid from sbml file");
								notifications.addError ("cannot read modelid from sbml file");
								return false;
							}
						}
						else
						{
							notifications.addError ("unknown model type");
							return false;
						}
					}
					catch (Exception e)
					{
						LOGGER.error ("model cannot be parsed", e);
						notifications.addError ("Error parsing file! " + e.getMessage ());
						return false;
					}
						/*try
						{
							// check validity by bives
							System.out.println ("start parsing");
							DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
								.newDocumentBuilder ();
							TreeDocument td = new TreeDocument (builder.parse (new ByteArrayInputStream(model.getBytes ())), null);
							System.out.println ("parsed to treedoc");
							
							// test model : can jsbml read it!?
							CellMLValidator val = new CellMLValidator ();
							val.validate (td);
							if (modelid == null || modelid.trim ().length () < 3)
								modelid = val.getModelID ();
							if (modelid == null || modelid.trim ().length () < 3)
							{
								LOGGER.error ("cannot read modelid from cellml file");
								notifications.addError ("cannot read modelid from cellml file");
								return false;
							}
							System.out.println ("finished parsing");
						}
						catch (Exception e)
						{
							LOGGER.error ("model cannot be parsed", e);
							notifications.addError ("Error parsing file! " + e.getMessage ());
							return false;
						}*/
					
					db.insterModel (modelid, version, model, modeltype);
					return true;
				}
			}
			else
			{
				if (request.getParameter ("deleteModel") != null)
				{
					// publish a model
					String model = request.getParameter ("deleteModel");
					int modelid = -1;
					try
					{
						modelid = Integer.parseInt (model);
					}
					catch (NumberFormatException e)
					{
						notifications.addError ("Error parsing form fields -- NFE");
						return false;
					}
					ModelVersion currentVersion = getVersionById (modelid);
					if (currentVersion == null || user.id != currentVersion.getUser ())
					{
						notifications.addError ("Not allowed to do that!");
						return false;
					}
					
					return db.deleteModel (currentVersion.getId ());
				}
				if (request.getParameter ("publishModel") != null)
				{
					// publish a model
					String model = request.getParameter ("publishModel");
					int modelid = -1;
					try
					{
						modelid = Integer.parseInt (model);
					}
					catch (NumberFormatException e)
					{
						notifications.addError ("Error parsing form fields -- NFE");
						return false;
					}
					ModelVersion currentVersion = getVersionById (modelid);
					if (currentVersion == null || user.id != currentVersion.getUser ())
					{
						notifications.addError ("Not allowed to do that!");
						return false;
					}
					
					return db.publishModel (currentVersion.getId (), true);
				}
				if (request.getParameter ("unpublishModel") != null)
				{
					// unpublish a model
					String model = request.getParameter ("unpublishModel");
					int modelid = -1;
					try
					{
						modelid = Integer.parseInt (model);
					}
					catch (NumberFormatException e)
					{
						notifications.addError ("Error parsing form fields -- NFE");
						return false;
					}
					ModelVersion currentVersion = getVersionById (modelid);
					if (currentVersion == null || user.id != currentVersion.getUser ())
					{
						notifications.addError ("Not allowed to do that!");
						return false;
					}
					
					return db.publishModel (currentVersion.getId (), false);
				}
				if (request.getParameter ("addparent") != null)
				{
					String parent = request.getParameter ("parentchooser");
					String model = request.getParameter ("modelid");
					System.out.println (model + " -->> " + parent);
					
					if (model == null || parent == null)
					{
						notifications.addError ("Error parsing form fields -- No input");
						return false;
					}
					
					int modelid = -1;
					try
					{
						modelid = Integer.parseInt (model);
					}
					catch (NumberFormatException e)
					{
						notifications.addError ("Error parsing form fields -- NFE");
						return false;
					}
					int parentId = -1;
					try
					{
						parentId = Integer.parseInt (parent);
					}
					catch (NumberFormatException e)
					{
						notifications.addError ("Error parsing form fields -- NFE");
						return false;
					}
					System.out.println (modelid + " -->> " + parentId);
					ModelVersion parentVersion = getVersionById (parentId);
					ModelVersion currentVersion = getVersionById (modelid);
					System.out.println (currentVersion + " -->> " + parentVersion);
					if (parentVersion == null || currentVersion == null || !parentVersion.getName ().equals (currentVersion.getName ()))
					{
						notifications.addError ("Error parsing form fields -- no such parent");
						return false;
					}
					if (user.id != currentVersion.getUser ())
					{
						notifications.addError ("Not allowed to do that!");
						return false;
					}
					
					return db.addHierarchy (currentVersion.getId (), parentVersion.getId ());
				}
				if (request.getParameter ("rmparent") != null)
				{
					String parent = request.getParameter ("parent");
					String model = request.getParameter ("modelid");
					System.out.println (model + " -->> " + parent);
					
					if (model == null || parent == null)
					{
						notifications.addError ("Error parsing form fields -- No input");
						return false;
					}
					
					int modelid = -1;
					try
					{
						modelid = Integer.parseInt (model);
					}
					catch (NumberFormatException e)
					{
						notifications.addError ("Error parsing form fields -- NFE");
						return false;
					}
					int parentId = -1;
					try
					{
						parentId = Integer.parseInt (parent);
					}
					catch (NumberFormatException e)
					{
						notifications.addError ("Error parsing form fields -- NFE");
						return false;
					}
					ModelVersion parentVersion = getVersionById (parentId);
					ModelVersion currentVersion = getVersionById (modelid);
					if (parentVersion == null || currentVersion == null || !parentVersion.getName ().equals (currentVersion.getName ()))
					{
						notifications.addError ("Error parsing form fields -- no such parent");
						return false;
					}
					if (user.id != currentVersion.getUser ())
					{
						notifications.addError ("Not allowed to do that!");
						return false;
					}
					return db.remHierarchy (currentVersion.getId (), parentVersion.getId ());
					
				}
			}
		}
		return false;
	}
}
