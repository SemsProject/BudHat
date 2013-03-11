/**
 * 
 */
package de.unirostock.sems.budhat.mgmt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.unirostock.sems.budhat.db.MySQLDB;


/**
 * @author martin
 *
 */
public class UserManager
{
	MySQLDB db;
	HttpSession session;
	HttpServletRequest request;
	Notifications err;
	User user;
	private CookieManager	cookies;
	private boolean justLoggedIn;
	private boolean justChangedAccount;
	
	public UserManager (MySQLDB db, HttpSession session, HttpServletRequest request, HttpServletResponse response, Notifications err, CookieManager cookies)
	{
		this.cookies = cookies;
		this.err = err;
		this.request = request;
		this.db = db;
		this.session = session;
		checkLogin ();
		getUserInformation ();
		checkAccountChanges ();
	}
	
	private void checkAccountChanges ()
	{
		if (user != null && request.getParameter ("submitaccount")!=null)
		{
			justChangedAccount = true;
			String mail = request.getParameter ("usermail");
			String pw = request.getParameter ("password");
			String pw2 = request.getParameter ("password-repeat");
			if (pw.length () > 0)
			{
			if (pw.equals (pw2))
			{
				PreparedStatement st = db.prepareStatement ("UPDATE user SET pass=MD5(?) WHERE mail=?");
				
				try
				{
					st.setString (1, pw);
					st.setString (2, user.mail);
					st.execute ();
					err.addInfo ("Settings saved!");
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					err.addError ("SQLException: cannot execute prepared statement");
				}
			}
			else
				err.addError ("Passwords differ..");
			}
		}
			getUserInformation ();
	}
	
	public boolean justLoggedIn ()
	{
		return justLoggedIn;
	}
	
	public boolean justChangedAccount ()
	{
		return justChangedAccount;
	}
	
	private void checkLogin ()
	{

		if (request.getParameter ("logout")!=null && request.getParameter ("logout").equals ("logout"))
		{
			System.out.println ("logout: " + request.getParameter ("logout"));
			user = null;
			cookies.setCookie (CookieManager.BUDHAT_USER, "betatester rock", CookieManager.COOKIE_LIFE_TIME);
			session.setAttribute ("usermail", "nomail");
		}
		
		else if (request.getParameter ("submitlogin")!=null)
		{
			System.out.println ("submitlogin: " + request.getParameter ("submitlogin"));
			justLoggedIn = true;
			session.setAttribute ("usermail", "nomail");
			cookies.setCookie (CookieManager.BUDHAT_USER, "betatester rock", CookieManager.COOKIE_LIFE_TIME);
			String login = request.getParameter ("login");
			String password = request.getParameter ("password");
			
			if (login != null && password != null)
			{
				PreparedStatement st = db.prepareStatement ("SELECT * FROM user WHERE mail=? AND pass=MD5(?)");
				try
				{
					st.setString (1, login);
					st.setString (2, password);
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					err.addError ("SQLException: cannot fill prepared statement");
					return;
				}
				try
				{
					st.execute ();
					System.out.println(st.toString ());
					ResultSet rs = st.getResultSet();
					while(rs != null && rs.next())
					{
						int theInt= rs.getInt("id");
						String str = rs.getString("mail");
						String token = rs.getString("token");
						System.out.println("\tuser= " + theInt + "\tmail = " + str + " => " + token);
							String cookieToken = UUID.randomUUID().toString();
							st = db.prepareStatement ("UPDATE user SET cookie_token=? WHERE mail=?");
							
							try
							{
								st.setString (1, cookieToken);
								st.setString (2, login);
								st.execute ();
								
						session.setAttribute ("usermail", rs.getString("mail"));
						session.setAttribute ("usertoken", cookieToken);
						if (request.getParameter ("stayOnline") != null && request.getParameter ("stayOnline").equals ("on"))
						{
							System.out.println ("setting good cookie: " + login);
								cookies.setCookie (CookieManager.BUDHAT_COOKIE_TOKEN, cookieToken, CookieManager.COOKIE_LIFE_TIME);
								cookies.setCookie (CookieManager.BUDHAT_USER, login, CookieManager.COOKIE_LIFE_TIME);
								cookies.setCookie (CookieManager.COOKIE_TIME, CookieManager.COOKIE_TIME, CookieManager.COOKIE_LIFE_TIME);
						}
						else
						{
							System.out.println ("setting good cookie: " + login);
							cookies.setCookie (CookieManager.BUDHAT_COOKIE_TOKEN, cookieToken);
							cookies.setCookie (CookieManager.BUDHAT_USER, login);
							cookies.setCookie (CookieManager.COOKIE_TIME, CookieManager.COOKIE_TIME);
							
						}
							}
							catch (SQLException e)
							{
								e.printStackTrace();
								err.addError ("SQLException: cannot insert longtime cookie");
							}
							
					}
				}
				catch (SQLException e)
				{
					e.printStackTrace();
					err.addError ("SQLException: cannot execute prepared statement");
				}
				
			}
		}
	}
	
	private void getUserInformation ()
	{
		System.out.println (cookies.getCookie (CookieManager.BUDHAT_USER));
		
			String login = (String) session.getAttribute ("usermail");
			String token = (String) session.getAttribute ("usertoken") ;
		if (login == null || token == null)
		{
		 login = cookies.getCookie (CookieManager.BUDHAT_USER);
		 token = cookies.getCookie (CookieManager.BUDHAT_COOKIE_TOKEN);
		}
		if (login != null && token != null)
		{
			PreparedStatement st = db.prepareStatement ("SELECT * FROM user WHERE mail=? AND cookie_token=?");
			try
			{
				st.setString (1, login);
				st.setString (2, token);
				st.execute ();
				ResultSet rs = st.getResultSet();
				if (rs.next())
				{
					user = new User (rs.getString("mail"), rs.getString("token"), rs.getBoolean ("valid"), rs.getInt("id"));
						if (cookies.getCookie (CookieManager.COOKIE_TIME) != null)
						{
							cookies.setCookie (CookieManager.BUDHAT_USER, login, CookieManager.COOKIE_LIFE_TIME);
							cookies.setCookie (CookieManager.BUDHAT_COOKIE_TOKEN, token, CookieManager.COOKIE_LIFE_TIME);
							cookies.setCookie (CookieManager.COOKIE_TIME, CookieManager.COOKIE_TIME, CookieManager.COOKIE_LIFE_TIME);
						}
						return;
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				err.addError ("SQLException: cannot execute prepared statement while installing user");
			}
		}
					session.setAttribute ("usermail", "nomail");
	}
	
	public User getUser ()
	{
		return user;
	}
	
	public String getUserWelcome ()
	{
		if (user != null)
			return "<div id='welcome'>Welcome <span>"+user.mail+"</span>! <small>[<a href='?logout=logout'>logout</a>]</small></div>";
		else
			return "";
	}
	
	public class User
	{
		public String mail, token;
		public boolean valid;
		public int id;
		
		public User (String mail, String token, boolean valid, int id)
		{
			this.mail = mail;
			this.token = token;
			this.valid = valid;
			this.id = id;
		}
		
		public String toString ()
		{
			return mail + "["+id+","+valid+"]";
		}
	}
}
