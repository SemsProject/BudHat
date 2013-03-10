/**
 * 
 */
package de.unirostock.sems.budhat.mgmt;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author martin scharm
 *
 */
public class CookieManager
{
	public final static String BUDHAT_USER = "budhatUser";
	public final static String BUDHAT_COOKIE_TOKEN = "budhatCookie";
	public final static String COOKIE_TIME = "saveCookie";
	public final static int COOKIE_LIFE_TIME = 365*24*60*60;
	
	private HttpServletResponse response;
	private HashMap<String, String> cookies;
	public CookieManager (HttpServletRequest request, HttpServletResponse response)
	{
		this.response = response;
		cookies = new HashMap<String, String> ();
		readCookies (request);
	}
	
	private void readCookies (HttpServletRequest request)
	{
		Cookie[] c = request.getCookies ();
		if (c != null)
			for (int i = 0; i < c.length; i++)
			{
				System.out.println ("have cookie: " + c[i].getName () + " => " + c[i].getValue ());
				cookies.put (c[i].getName (), c[i].getValue ());
			}
	}
	
	public void setCookie (String name, String value)
	{
		System.out.println ("set cookie: " + name + " => " + value);
		response.addCookie (new Cookie (name, value));
		cookies.put (name, value);
	}
	
	public void setCookie (String name, String value, int time)
	{
		System.out.println ("set cookie: " + name + " => " + value + " time: " + time);
		Cookie c = new Cookie (name, value);
		c.setMaxAge (time);
		response.addCookie (c);
		cookies.put (name, value);
	}
	
	public String getCookie (String key)
	{
		return cookies.get (key);
	}
}
