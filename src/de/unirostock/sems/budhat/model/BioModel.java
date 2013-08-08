package de.unirostock.sems.budhat.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.bives.ds.graph.CRN;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorGraphML;
import de.unirostock.sems.budhat.mgmt.UserManager.User;
import de.unirostock.sems.budhat.web.Index;



/**
 * The Class BioModel.
 * 
 * This class represents a single model identified by an ID. It covers multiple
 * versions of the model and is comparable regarding its name.
 * 
 * @author martin scharm
 */
public class BioModel
	implements Comparable<BioModel>
{
	
	/** The versions. */
	private Vector<ModelVersion> models;
	
	/** The id and the name. */
	private String					name;
	private String					description;
	
	private boolean sort;
	
	public void setDescription (String descr)
	{
		this.description = descr;
	}
	
	public String getDescription ()
	{
		return this.description;
	}
	
	/**
	 * Instantiates a new bio model.
	 * 
	 * @param name
	 *          the name
	 * @param id
	 *          the id
	 */
	public BioModel (String name)
	{
		models = new Vector<ModelVersion> ();
		this.name = name;
		description = "";
	}
	public BioModel (ModelVersion v)
	{
		models = new Vector<ModelVersion> ();
		addVersion (v);
		description = "";
	}
	public String getName ()
	{
		return name;
	}
	
	public void debugOut ()
	{
		System.out.println (name);
		for (int i = 0; i < models.size (); i++)
			System.out.println (models.elementAt (i).getVersion ());
	}
	
	/**
	 * Gets the version.
	 *
	 * @param id the id
	 * @return the version
	 */
	public ModelVersion getVersion (String id)
	{
		System.out.println ("searching for version: " + id + " in " + this.name);
		for (int i = 0; i < models.size (); i++)
		{
			//System.out.println ("cmp " + models.elementAt (i).getVersion ());
			if (models.elementAt (i).getVersion ().equals (id))
				return models.elementAt (i);
		}
		return null;
	}
	
	/**
	 * Adds the version.
	 *
	 * @param v the v
	 */
	public void addVersion (ModelVersion v)
	{
		sort = true;
		if (models.size () < 1)
		{
			name = v.getName ();
		}
		models.add (v);
	}
	
	
	/**
	 * Adds a new version of the model.
	 * 
	 * @param s
	 *          the version code
	 */
	/*public void addVersion (String s)
	{
		versions.add (s.substring (0, s.length () - 1));
	}*/
	
	
	/**
	 * Serves the list of versions as an html-optgroup to be used in the website.
	 * 
	 * @return the list of versions optgroup in html format
	 */
	public String getList (User user)
	{
		// sort the version lexicographically
		if (sort)
		Collections.sort (models);
		
		String ret = "<optgroup label='" + name + "'>";
		
		int id = user == null ? -1 : user.id;
		
		for (int i = 0; i < models.size (); i++)
		{
			String c = "";
			if (models.elementAt (i).getUser () == id)
			{
				c = "my_private_model";
				if (models.elementAt (i).isPublic ())
					c = "my_public_model";
			}
			ret += "<option class='"+c+"' value='" + name + "|" + models.elementAt (i) + "' >"
				+ models.elementAt (i);
			if (models.elementAt (i).getModelType ().length () > 0)
				ret += " ("+models.elementAt (i).getModelType()+")";
			ret += "</option>";
		}
		
		return ret + "</optgroup>";
	}
	
	public ModelVersion getVersionById (int id)
	{

		for (int i = 0; i < models.size (); i++)
		{
			if (models.elementAt (i).getId () == id)
				return models.elementAt (i);
		}
		return null;
	}
	
	public String getOwnModelTable (User user)
	{
		// sort the version lexicographically
		if (sort)
			Collections.sort (models);
		boolean ownsVersion= false;
		String ret = "<h2>" + name + "</h2><table class='mymodels'><thead><th>Version</th><th>Parents</th><th>Public?</th><th>Delete</th></thead>";
		
		for (int i = 0; i < models.size (); i++)
			if (models.elementAt (i).getUser () == user.id)
			{
				String c = "private";
				if (models.elementAt (i).isPublic ())
					c = "public";
				ret += "<tr><td class='"+c+"'><a href='#' onclick=\"getInfo ('"+name+"', '"+models.elementAt (i).getVersion ()+"')\">" + models.elementAt (i) + "</a></td>";
				
				
				ret += "<td class='"+c+"'>";
				
				String parents = "", unrelated = "";
				for (int j = 0; j < models.size (); j++)
				{
					if (i == j)
						continue;
					if (models.elementAt (i).hasParent (models.elementAt (j).getId ()))
						parents += models.elementAt (j).getVersion () + " (<a href='?rmparent=29a&amp;parent="+models.elementAt (j).getId ()+"&amp;modelid="+models.elementAt (i).getId ()+"'>remove</a>)<br/>";
					else
						unrelated += "<option value='"+models.elementAt (j).getId ()+"'>"+models.elementAt (j).getVersion ()+"</option>";
				}
				
				ret += parents;
				ret += "<form action='" + Index.WEB_URL + "?somesubmit=true' method='POST'><input type='hidden' name='modelid' value='"+models.elementAt (i).getId ()+"'><select name='parentchooser'>";
				ret += unrelated;
				ret += "</select><input type='submit' name='addparent' value='add parent'></form>";
				
				ret += "</td>";
				
				
					if (models.elementAt (i).isPublic ())
						ret += 
							"<td class='"+c+"'>public - <a href='?unpublishModel="+models.elementAt (i).getId ()+"'>make private</a></td>";
					else
						ret +=
							"<td class='"+c+"'>private - <a href='?publishModel="+models.elementAt (i).getId ()+"'>publish</a></td>";
					ret += "<td class='"+c+"'><a href='?deleteModel="+models.elementAt (i).getId ()+"'>delete</a> <small>(no add. question, <strong>unundoable</strong>!)</small></td>";
					ret += "</tr>";
				ownsVersion = true;
			}
		
		if (ownsVersion)
			return ret + "</table>";
		else
			return "";
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo (BioModel bm)
	{
		return name.compareTo (bm.name);
	}
	
	
	
	public Document getGraphMLTree (String id) throws ParserConfigurationException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
	
		Element graph = GraphTranslatorGraphML.addGraphMLPreamble (doc);
		
		HashMap<Integer, ModelVersion> versionById = new HashMap<Integer, ModelVersion> ();
		updateIdMapper (versionById);
			
			for (int i = 0; i < models.size (); i++)
			{
				ModelVersion v = models.elementAt (i);

				Element element = doc.createElement ("node");
				element.setAttribute ("id", "" + v.getId ());
				element.setAttribute ("name", "" + v.getVersion ());
				graph.appendChild (element);

				Element nsElement = doc.createElement ("data");
				nsElement.setAttribute ("key", "ns");
				nsElement.appendChild (doc.createTextNode ("species"));
				element.appendChild (nsElement);
				
				
				Element srcElement = doc.createElement ("data");
				srcElement.setAttribute ("key", "vers");
				if (v.getVersion ().equals (id))
					srcElement.appendChild (doc.createTextNode (CRN.INSERT + ""));
				else if (v.getParents () == null || v.getParents ().size () < 1)
					srcElement.appendChild (doc.createTextNode (CRN.DELETE + ""));
				else
					srcElement.appendChild (doc.createTextNode ("0"));
				element.appendChild (srcElement);
				

				Element nameElement = doc.createElement ("data");
				nameElement.setAttribute ("key", "name");
				nameElement.appendChild (doc.createTextNode (v.getVersion ()));
				element.appendChild (nameElement);
				
				
				for (Integer p : v.getParents ())
				{
					ModelVersion par = versionById.get (p);
					if (par != null)
					{
						Element edge = doc.createElement ("edge");
						
						edge.setAttribute ("source", "" + par.getId ());
						edge.setAttribute ("target", "" + v.getId ());
						
						graph.appendChild (edge);
					}
				}
			}
			
			return doc;
	}
	
	public void updateIdMapper (HashMap<Integer, ModelVersion> mapper)
	{
		for (int i = 0; i < models.size (); i++)
			mapper.put (models.elementAt (i).getId (), models.elementAt (i));
	}
}
