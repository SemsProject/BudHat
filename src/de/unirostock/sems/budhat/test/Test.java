/**
 * 
 */
package de.unirostock.sems.budhat.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * @author ms1484
 * 
 */
public class Test
	extends HttpServlet
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7104660769908632826L;
	
	
	@SuppressWarnings("deprecation")
	protected void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
		response.setContentType ("text/html");
		request.setCharacterEncoding ("UTF-8");
		PrintWriter out = response.getWriter ();
		
		out.println ("new File (\".\").getAbsolutePath () => " + new File (".").getAbsolutePath ());
		
		
		out.println ("request.getPathInfo () => " + request.getPathInfo ());
		out.println ("request.getPathTranslated () => " + request.getPathTranslated ());
		out.println ("request.getContextPath () => " + request.getContextPath ());
		out.println ("request.getRealPath(request.getServletPath()) => " + request.getRealPath (request.getServletPath ()));
		out.println ("request.getServletPath() => " + request.getServletPath ());
		out.println ("request.getRequestURI() => " + request.getRequestURI ());
		out.println ("request.getRequestURL() => " + request.getRequestURL ());
		
		
		out.println ("getServletContext ().getContextPath () => " + getServletContext ().getContextPath ());
		out.println ("getServletContext().getRealPath (\".\") => " + getServletContext ().getRealPath ("."));
	}
}
