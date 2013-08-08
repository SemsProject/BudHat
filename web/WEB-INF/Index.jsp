<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />
		<title>BUDHAT</title>
		<script type='text/javascript' src='js/cytoscape.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/MathJax.js?config=MML_HTMLorMML-full' charset='UTF-8'></script>
        <script type="text/javascript" src="js/min/json2.min.js"></script>
        <script type="text/javascript" src="js/min/AC_OETags.min.js"></script>
        <script type="text/javascript" src="js/min/cytoscapeweb.min.js"></script>
		<script type='text/javascript' src='js/jquery-1.7.1.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/jquery-ui-1.8.19.custom.min.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/functions.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/view.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/ajax.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/jquery.snippet.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/jquery.tipsy-1.7.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/jquery.cluetip.all.min.js' charset='UTF-8'></script>
		<script type='text/javascript' src='js/jquery.hoverIntent.js' charset='UTF-8'></script>
		<link href='css/general.css' rel='stylesheet' type='text/css'  />
		<link href='css/jquery-ui-1.8.19.custom.css' rel='stylesheet' type='text/css'  />
		<link href='css/jquery.snippet.css' rel='stylesheet' type='text/css'  />
		<link href='css/tipsy-1.7.css' rel='stylesheet' type='text/css'  />
		<link href='css/jquery.cluetip.css' rel='stylesheet' type='text/css'  />
		<link href='http://fonts.googleapis.com/css?family=Droid+Sans:700' rel='stylesheet' type='text/css' />
	</head>
	
	<body>
		<div class='general'>
			<div id='header'>
				<h1><strong>Project:</strong>BudHat</h1>
				<!--div id='navi'>
					<a class='navi' onclick='showAbout();'>main</a>
				</div><!-- #navi -->
				${UserWelcome}
			</div><!-- # header-->
			<div class='left' id='left'>
				<div id='modelchooser'>
						<div id='avail_models'>
							<h1>available models</h1>
							<select class='box' name='available' size='20' id='available_models_select'>
								${AvailModels}
							</select>
						</div><!-- #available models -->
						<div id='model_chooser'>
							<img src='images/plus.png' width='15' height='15' alt='+' onClick='addSelect();' class='tipsy' title='add modelversion to selection' />
							<img src='images/minus.png' width='15' height='15' alt='-' onClick='removeSelect();' class='tipsy' title='remove modelversion of selection' />
							<img src='images/tree.png' width='15' height='15' alt='show Tree' onClick='genTree(null, null);' class='tipsy' title='show versiontree' />
							<img src='images/info.png' width='15' height='15' alt='show Info' onClick='getInfo(null, null);' class='tipsy' title='show information' />
						</div><!-- #model_chooser -->
						<div id='selected_models'>
							<h1>selected models</h1>
							<select id='selected' class='box' name='selected' size='2'>
							</select>
						</div><!-- #selected_models -->
						<br>
						<div id='vergleichen'><input onClick='compareModels();' type='submit' name='unwichtig' value='compare' /></div>
				</div><!-- #modelchooser -->
			</div><!-- #header -->
			<div id='main'>
				${ProcessingErrors}
				${ProcessingNotifications}
				<div id="topnavi">
					<a class='tab navi' id="tab_main" onclick='showAbout ();'>Main</a>
					<a class='tab navi' id="tab_user" onclick='showUser ();'>User</a>
					<a class='tab navi hidden' id="tab_info" onclick='showInfo ();'>Info</a>
					<a class='tab navi hidden' id="tab_diff" onclick='showDiff ();'>Diff</a>
					<a class='tab navi hidden' id="tab_tree" onclick='showTree ();'>Tree</a>
				</div>
				<div id='loading' class='hidden'>
					<img src='images/loading142.gif' title='loading...' alt='loading...' />
				</div><!-- #loading -->
				<div id='info' class='hidden'>
					<p>modelinfo</p>
				</div><!-- #loading -->
				<div id='tree' class='hidden'>
					<div class='subnavi'>
						<a class='tab navi' id='graphtreetab' onclick='showGraphTree ();'>Graph</a>
						<!-- a class='tab navi' id='pictreetab' onclick='showPicTree ();'>Pic</a-->
					</div> <!-- .subnavi -->
					<!-- div id='pictree' class=''>
					</div><!-- #pictree -->
					<div id='graphtree' class='hidden'>
						<div id='graphtreeflash'>
							here comes the graph
						</div>
					</div><!-- #graphtree -->
				</div><!-- #tree -->
				
				<div id='diff' class='hidden'>
					<div class='subnavi'>
						<a class='tab navi' id='graphdifftab' onclick='showGraphDiff ();'>Graph</a>
						<a class='tab navi' id='reportdifftab' onclick='showReportDiff ();'>Report</a>
						<a class='tab navi' id='xmldifftab' onclick='showXMLDiff ();'>XML-Diff</a>
						<a class='tab navi' id='grpahmldifftab' onclick='showGraphmlDiff ();'>GraphML</a>
					</div> <!-- .subnavi -->
					<div id='xmldiff' class=''></div>
					<div id='graphmldiff' class='hidden'></div>
					<div id='reportdiff' class='hidden'></div>
					<div id='graphdiff' class='hidden'>
						<div id='graphdiffflash'>
							here comes the graph
						</div>
						<p>
							<em>Deletes</em> are colored in <span class="redlegend">red</span>, while <em>inserts</em> are <span class="bluelegend">blue</span> and <em>updates</em>, which don't affect the network, are <span class="yellowlegend">yellow</span>. 
						</p>
						<div class="legend"><img src="images/legend-species.png" alt="species"/> species</div>
						<div class="legend"><img src="images/legend-reaction.png" alt="reaction"/>reaction</div>
						<div class="legend"><img src="images/legend-reaction-participant.png" alt="participant in reaction"/>participant in reaction</div>
						<div class="legend"><img src="images/legend-mod-stimulator.png" alt="stimulator"/> stimulator</div>
						<div class="legend"><img src="images/legend-mod-inhibitor.png" alt="inhibitor"/>inhibitor</div>
						<div class="legend"><img src="images/legend-mod-unknown.png" alt="unnkown modifier"/>unnkown modifier</div>
					</div>
				</div><!-- #diff -->
				<div id='about' class=''>
					<h1>BudHat</h1>
					<p>BudHat is an implementation of the research work done in the <a href='http://sems.uni-rostock.de'>SEMS project</a>. Its main purpose is to demonstrate the capabilities of difference detection and visualization of computational models. Budhat is developed at the <a href='http://www.uni-rostock.de/'>University of Rostock</a>.<br /><br />
					Read more about BudHat at <a href="http://sems.uni-rostock.de/budhat/">http://sems.uni-rostock.de/budhat/</a>.
					<!-- Sample models have been taken from <a href='http://www.ebi.ac.uk/biomodels-main/'>BioModels Database</a>.--></p>
					<h2>Status</h2><p>This implementation is a beta version. Please keep in mind that this version is not necessarily bug free. If you find errors or have suggestions please contact us.</p>
					<h2>Acknowledgments</h2>
					<table id="acktbl">
					<tr><td><a class="noborder" href="http://cytoscapeweb.cytoscape.org/"><img src="images/logo_cw.png" alt="Cytoscape Web"/></a></td><td>We use <a href="http://cytoscapeweb.cytoscape.org/">Cytoscape Web</a> to visualize the reaction network and the version history.</td></tr>
					<tr><td><a class="noborder" href="http://www.ebi.ac.uk/biomodels-main/"><img src="images/logo_bmdb.png" alt="BioModels Database"/></a></td><td>We use computational models from the <a href="http://www.ebi.ac.uk/biomodels-main/">BioModels Database</a> project for demonstration.</td></tr>
					</table>
				</div><!-- #about -->
				
				<div id='user' class='hidden'>
					<c:choose>
						<c:when test="${UserValid}">
							<div class='subnavi'>
								<a class='tab navi' id='mymodelstab' onclick='showMyModels ();'>My Models</a>
								<a class='tab navi' id='myaccounttab' onclick='showMyAccount ();'>My Account</a>
								<!--a class='tab navi' onclick='showPicDiff ();'>pic</a-->
							</div> <!-- .subnavi -->
							<div id='mymodels' class=''>
								<h1>My Models</h1>
                               <form action="${WEB_URL}" method="post" enctype="multipart/form-data">
                                   Upload new model: <input type="file" name="newsbmlfile" /><br />
                                   Model ID: <input type="text" name="modelid" value="" /> <small>(at least 3 characters, leave blank to parse from file)</small><br />
                                   Version number: <input type="text" name="versionnumber" value="${DATE}" /><br />
                                   Model Type: <select name='modeltype'><option value="SBML">SBML</option><option value="CellML">CellML</option></select><br />
                                   <input type="submit" name='submitfile' value='submit' />
                               </form>
                               ${UserModels}
							</div> <!-- mymodels -->
							<div id='myaccount' class='hidden'>
                               <h1>My Account</h1>
                               <form action="${WEB_URL}" method="post">
                                   Your Mail:<br/><input type="text" name="usermail" value="${UserMail}" disabled="disabled" /> <small>(currently not changeable)</small><br/>
                                   Your Password:<br/><input type="password" name="password" /><br/>
                                   Repeat Password:<br/><input type="password" name="password-repeat" /><br/>
                                   <input type='submit' name='submitaccount' value='save' /><br/>
                               </form>
							</div> <!-- myaccount -->
						</c:when>
						<c:otherwise>
							<h1>User</h1>
							<p>You need to authenticate in order to upload a new model or to manage your settings:</p>
							<form action="${WEB_URL}" method='post'>
								Login:<br/><input type='text' name='login' /><br/>
								Password:<br/><input type='password' name='password' /><br/>
								<input type="checkbox" name="stayOnline" /> Remember me<br/>
								<input type='submit' name='submitlogin' value='login' /><br/>
								<small>Contact the <a href='mailto:dagmar.waltemath@uni-rostock.de'>BudHat team</a> to get an account.</small>
							</form>
						</c:otherwise>
					</c:choose>
				</div><!-- #user -->
			</div><!-- #main -->
		</div><!-- #general -->
		<script type='text/javascript' >
			${selectTab}
			parseLastTime ();
		</script>
	</body>
</html>