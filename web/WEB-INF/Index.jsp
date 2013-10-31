<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />
		<title>BUDHAT</title>
		<script type='text/javascript' src='res/js/cytoscape.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/MathJax.js?config=MML_HTMLorMML-full' charset='UTF-8'></script>
        <script type="text/javascript" src="res/js/min/json2.min.js"></script>
        <script type="text/javascript" src="res/js/min/AC_OETags.min.js"></script>
        <script type="text/javascript" src="res/js/min/cytoscapeweb.min.js"></script>
		<script type='text/javascript' src='res/js/jquery-1.7.1.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/functions.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/view.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/ajax.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/cytoscapejs/cytoscape.js-2.0.2/arbor.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/cytoscapejs/cytoscape.js-2.0.2/cytoscape.min.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/cytoscape-js.js' charset='UTF-8'></script>
		<script type='text/javascript' src='res/js/highlight.js/highlight.pack.js' charset='UTF-8'></script>
		<link href='res/css/general.css' rel='stylesheet' type='text/css'  />
		<link href='res/js/highlight.js/styles/tomorrow.css' rel='stylesheet' type='text/css'  />
	</head>
	
	<body>
		<div class='general'>
			<div id='header'>
				<h1><strong>Project:</strong>BudHat</h1>
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
							<img src='res/images/plus.png' id="modeladder" width='15' height='15' alt='+' title='add model version to selection' />
							<img src='res/images/minus.png' id="modelremover" width='15' height='15' alt='-' title='remove model version from selection' />
							<img src='res/images/tree.png' id="treegenerator" width='15' height='15' alt='show Tree' title='show versiontree' />
							<img src='res/images/info.png' id="infogenerator" width='15' height='15' alt='show Info' title='show information' />
						</div><!-- #model_chooser -->
						<div id='selected_models'>
							<h1>selected models</h1>
							<select id='selected' class='box' name='selected' size='2'>
							</select>
						</div><!-- #selected_models -->
						<br>
						<div id='vergleichen'><button id="compareBtn" >compare</button></div>
				</div><!-- #modelchooser -->
			</div><!-- #header -->
			
			
			
			<div id='main'>
				${ProcessingErrors}
				${ProcessingNotifications}
				<div id="topnavi">
					<a class='tab navi' id="tab_main">Main</a>
					<a class='tab navi' id="tab_user">User</a>
					<a class='tab navi hidden' id="tab_info">Info</a>
					<a class='tab navi hidden' id="tab_diff">Diff</a>
					<a class='tab navi hidden' id="tab_tree">Tree</a>
				</div>
				
				
				<div id='loading' class='hidden'>
					<img src='res/images/loading142.gif' title='loading...' alt='loading...' />
				</div><!-- #loading -->
				
				
				<div id='tab_info_content' class='hidden'>
					<p>modelinfo</p>
				</div><!-- #loading -->
				
				
				<div id='tab_tree_content' class='hidden'>
					<div id='graphtree'>
						<div id='graphtreeflash'>
							here comes the graph
						</div>
					</div><!-- #graphtree -->
					<div id='diffmatrix'>
						here comes the matrix
					</div>
				</div><!-- #tree -->
				
				
				
				<div id='tab_diff_content' class='hidden'>
					<div class='subnavi'>
						<a class='tab navi' id='subtab_graph'>Graph</a>
						<a class='tab navi' id='subtab_report'>Report</a>
						<a class='tab navi' id='subtab_hierarchy'>Hierarchy</a>
						<a class='tab navi' id='subtab_xml'>XML-Diff</a>
					</div> <!-- .subnavi -->
					<div id='subtab_xml_content' class='hidden'></div>
					<div id='subtab_report_content' class='hidden'></div>
					<div id='subtab_graph_content' class=''>
						<div id='subtab_graph_graph'>
							<button id="subtab_graph_graph_draw_js">draw with Cytoscape.js</button>
							<button id="subtab_graph_graph_draw_flash">draw with CytoscapeWeb</button>
							<div id='subtab_graph_graph_js'>
								here comes the graph
							</div>
							<div id='subtab_graph_graph_flash'>
								here comes the graph
							</div>
						</div>
						<div id='legend'>
							<p>
								<em>Deletes</em> are colored in <span class="redlegend">red</span>, while <em>inserts</em> are <span class="bluelegend">blue</span> and <em>updates</em>, which don't affect the network, are <span class="yellowlegend">yellow</span>. 
							</p>
							<div class="legend"><img src="res/images/legend-species.png" alt="species"/> species</div>
							<div class="legend"><img src="res/images/legend-reaction.png" alt="reaction"/>reaction</div>
							<div class="legend"><img src="res/images/legend-reaction-participant.png" alt="participant in reaction"/>participant in reaction</div>
							<div class="legend"><img src="res/images/legend-mod-stimulator.png" alt="stimulator"/> stimulator</div>
							<div class="legend"><img src="res/images/legend-mod-inhibitor.png" alt="inhibitor"/>inhibitor</div>
							<div class="legend"><img src="res/images/legend-mod-unknown.png" alt="unnkown modifier"/>unnkown modifier</div>
						</div>
						<div id='subtab_graph_code'></div>
					</div>
					<div id='subtab_hierarchy_content' class='hidden'>
						<div id='subtab_hierarchy_graph'>
							<button id="subtab_hierarchy_graph_draw_js">draw with Cytoscape.js</button>
							<button id="subtab_hierarchy_graph_draw_flash">draw with CytoscapeWeb</button>
							<div id='subtab_hierarchy_graph_js'>
								here comes the graph
							</div>
							<div id='subtab_hierarchy_graph_flash'>
								here comes the graph
							</div>
						</div>
						<div id='legend'>
							<p>
								<em>Deletes</em> are colored in <span class="redlegend">red</span>, while <em>inserts</em> are <span class="bluelegend">blue</span> and <em>updates</em>, which don't affect the network, are <span class="yellowlegend">yellow</span>. 
							</p>
							<div class="legend"><img src="res/images/legend-species.png" alt="species"/> species</div>
							<div class="legend"><img src="res/images/legend-reaction.png" alt="reaction"/>reaction</div>
							<div class="legend"><img src="res/images/legend-reaction-participant.png" alt="participant in reaction"/>participant in reaction</div>
							<div class="legend"><img src="res/images/legend-mod-stimulator.png" alt="stimulator"/> stimulator</div>
							<div class="legend"><img src="res/images/legend-mod-inhibitor.png" alt="inhibitor"/>inhibitor</div>
							<div class="legend"><img src="res/images/legend-mod-unknown.png" alt="unnkown modifier"/>unnkown modifier</div>
						</div>
						<div id='subtab_hierarchy_code'></div>
					</div>
				</div><!-- #diff -->
				
				
				
				<div id='tab_main_content' class=''>
					<h1>BudHat</h1>
					<p>BudHat is an implementation of the research work done in the <a href='http://sems.uni-rostock.de'>SEMS project</a>. Its main purpose is to demonstrate the capabilities of difference detection and visualization of computational models. Budhat is developed at the <a href='http://www.uni-rostock.de/'>University of Rostock</a>.<br /><br />
					Read more about BudHat at <a href="http://sems.uni-rostock.de/budhat/">http://sems.uni-rostock.de/budhat/</a>.
					<!-- Sample models have been taken from <a href='http://www.ebi.ac.uk/biomodels-main/'>BioModels Database</a>.--></p>
					<h2>Status</h2><p>This implementation is a beta version. We currently support models encoded in <a class="noborder" href="http://models.cellml.org/cellml"><img class="text" src="res/images/logo-cellml.png" alt="CellML" title="CellML" /></a> and <img class="text" src="res/images/sbml.png" alt="SBML" title="SBML" /></a>. Please keep in mind that this version is not necessarily bug free. If you find errors or have suggestions please contact us.</p>
					<h2>Acknowledgments</h2>
					<table id="acktbl">
					<tr><td><a class="noborder" href="http://cytoscapeweb.cytoscape.org/"><img src="res/images/logo_cw.png" alt="Cytoscape Web"/></a></td><td>We use <a href="http://cytoscapeweb.cytoscape.org/">Cytoscape Web</a> and <a href="http://cytoscape.github.io/cytoscape.js/">Cytoscape.js</a> to visualize the reaction network, the hierarchical model structure, and the version history.</td></tr>
					<tr><td><a class="noborder" href="http://www.ebi.ac.uk/biomodels-main/"><img src="res/images/logo_bmdb.png" alt="BioModels Database"/></a></td><td rowspan="2">We use computational models from the <a href="http://www.ebi.ac.uk/biomodels-main/">BioModels Database</a> project and the <a href="http://models.cellml.org/cellml">CellML Model Repository
					</a> for demonstration.</td></tr>
					<tr><td><a class="noborder" href="http://models.cellml.org/cellml"><img src="res/images/logo-cellml.png" alt="CellML Model Repository
					"/></a></td></tr>
					</table>
				</div><!-- #about -->
				
				<div id='tab_user_content' class='hidden'>
					<c:choose>
						<c:when test="${UserValid}">
							<div class='subnavi'>
								<a class='tab navi' id='subtab_models'>My Models</a>
								<a class='tab navi' id='subtab_account'>My Account</a>
							</div> <!-- .subnavi -->
							<div id='subtab_models_content' class=''>
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
							<div id='subtab_account_content' class='hidden'>
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