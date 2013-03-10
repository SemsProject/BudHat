/**
 * 
 */
package de.unirostock.sems.budhat.mgmt;

import java.util.Vector;


/**
 * @author martin
 *
 */
public class Notifications
{
	private Vector<String> err;
	private Vector<String> info;
	
	public Notifications ()
	{
		err = new Vector<String> ();
		info = new Vector<String> ();
	}
	
	public void addError (String e)
	{
		err.add (e);
	}
	
	public void addInfo (String e)
	{
		info.add (e);
	}
	
	public String getInfos ()
	{
		if (info.size () < 1)
			return "";
		
		String e = "<div id='notification'><h2>Notice</h2><ul>";
		for (int i = 0; i < info.size (); i++)
			e += "<li>"+info.elementAt (i)+"</li>";
		return e + "</ul></div>";
	}
	
	public String getErrors ()
	{
		if (err.size () < 1)
			return "";
		
		String e = "<div id='err'><h2>Following errors occured</h2><ul>";
		for (int i = 0; i < err.size (); i++)
			e += "<li>"+err.elementAt (i)+"</li>";
		return e + "</ul></div>";
	}
}
