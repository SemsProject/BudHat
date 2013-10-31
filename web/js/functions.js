function startsWith (str, prefix)
{
        return str.indexOf(prefix) == 0;
}

function endsWith(str, suffix)
{
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}


// Tabs initialisieren
$(document).ready(function() {
	//$( "#tabs" ).tabs();
	
	// Tipsy initialisieren
	/*$(".tipsy").tipsy({
			gravity:'s',
			opacity: 1,
			live: true,
		  html: true,
		  fade: false
		});*/
	$(".tab").click( function(){
//	    console.log ("click");
//	    console.log (this.id);

	                // for each parent get children
	                // -> starts with id / tab / subtab = visible
	                // otherwise hidden


		var containers = this.parentNode.children;
		for (var i = 0; i < containers.length; i++)
		if (containers[i].id == this.id)
		                containers[i].className = containers[i].className + " navi-active";
		                else
		                containers[i].className = containers[i].className.replace(/\bnavi-active\b/,'');

	containers = this.parentNode.parentNode.children;
	for (var i = 0; i < containers.length; i++)
	if (endsWith (containers[i].id, "content"))
	{
	        if (startsWith (containers[i].id, this.id))
	        {
	                containers[i].style.display = "block";
	        }
	        else
	        {
	                containers[i].style.display = "none";
	        }
//	      console.log (containers[i].id);
	}

	     });

});








function getUrlVars()
{
    var vars = [], hash;    
    var hashes = window.location.href.valueOf().split('?').slice(1)[0].split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}
function addGetParameter (param, value)
{
    var val = new RegExp('(\\?|\\&)' + param + '=.*?(?=(&|$))'), qstring = /\?.+$/;
    var url = window.location.toString();
    if (val.test(url))
			window.history.replaceState(null, document.title, url.replace(val, '$1' + param + '=' + value));
		else if (qstring.test(url))
			window.history.replaceState(null, document.title, url + '&' + param + '=' + value);
		else
			window.history.replaceState(null, document.title, url + '?' + param + '=' + value);
}
function rmGetParameter (param)
{
	var val = new RegExp('(\\?|\\&)' + param + '=.*?(?=(&|$))');
	var url = window.location.toString();
	if (val.test(url))
	{
		window.history.replaceState(null, document.title, url.replace(val, '$1'));
	}
	window.history.replaceState(null, document.title, window.location.toString().replace(/&&+/g, '&'));
}

function parseLastTime ()
{
	var vars = getUrlVars();
	rmGetParameter ("logout");
	rmGetParameter ("publishModel");
	rmGetParameter ("deleteModel");
	rmGetParameter ("unpublishModel");
	rmGetParameter ("addparent");
	rmGetParameter ("modelid");
	rmGetParameter ("parent");
	rmGetParameter ("rmparent");
	rmGetParameter ("somesubmit");
	if (vars["treeId"] && vars["treeVers"])
	{
		genTree (vars["treeId"], vars["treeVers"]);
	}
	if (vars["modInfoId"] && vars["modInfoVers"])
	{
		getInfo (vars["modInfoId"], vars["modInfoVers"]);
	}
	if (vars["diffModA"] && vars["diffModB"] && vars["diffVersA"] && vars["diffVersB"])
	{
		var opt1 = $("select[id='available_models_select'] option[value='"+vars["diffModA"] + "|" + vars["diffVersA"]+"']");
		var opt2 = $("select[id='available_models_select'] option[value='"+vars["diffModB"] + "|" + vars["diffVersB"]+"']");
		
		if (opt1.length == 1 && opt2.length == 1 && $("select[id='selected'] option").length < 1)
		{
			opt1.attr('selected','selected');
			addSelect();
			opt1.removeAttr('selected');
			opt2.attr('selected','selected');
			addSelect();
			compareModels ();
		}
	}
	if (vars["lastContainer"])
	{
		eval (vars["lastContainer"] + "();");
	}
}

function addSelect()
{
	if($("select[id='selected'] option").length < 2)
	{
		var object = $("select[id='available_models_select'] option:selected");
		
		if (!object.attr("value") || $("select[id='selected'] option[value='"+object.attr("value")+"']").length > 0)
			return ;
		
		object.addClass ("selected");
		
		$("select[id='selected']").append("<option value='"+object.attr("value")+"'>"+object.parent ().attr("label")+" - "+object.html()+"</option>").select();
	}
	else
	{
		alert("Detection of differences is only possible for exactly 2 models!");
	}
}

function removeSelect()
{
	if($("select[id='selected'] option:selected").length > 0)
	{
		object = $("select[id='selected'] option:selected");
		
		object.remove();
		
		$("select[id='available_models_select'] option[value='"+object.val()+"']").removeClass("selected");
	}
	else
	{
		var selAvail = $("select[id='available_models_select'] option:selected");
		if (!selAvail)
			return;
		
		var selItem = $("select[id='selected'] option[value='"+selAvail.attr("value")+"']");
		if (!selItem)
			return;
		
		selItem.remove ();
		selAvail.removeClass("selected");
	}
}

var topTabs=["tab_main","tab_user","tab_info","tab_diff","tab_tree"];

/*function selectTopTab (id)
{
	for (var t in topTabs)
	{
		$("#"+topTabs[t]).removeClass ("navi-active");
	}
	if (id)
	{
		$("#"+id).addClass ("navi-active");
		$("#"+id).removeClass ("hidden");
	}
}
function selectSubTab (ids)
{
	for (var t in ids)
	{
		if (ids[t])
			$("#"+t).addClass ("navi-active");
		else
			$("#"+t).removeClass ("navi-active");
	}
}

function hideAll ()
{
	$("#loading").hide ();
	$("#diff").hide ();
	$("#info").hide ();
	$("#about").hide ();
	$("#tree").hide();
	$("#user").hide();
}

function showLoading() {
	hideAll ();
	$("#loading").show ();
	selectTopTab (null);
}
function showInfo() {
	hideAll ();
	$("#info").show ();
	selectTopTab ("tab_info");
	addGetParameter ("lastContainer", "showInfo");
}
function showMyModels (){
	hideAll();
	$("#user").show();
	$("#myaccount").hide();
	$("#mymodels").show();
	selectTopTab ("tab_user");
	selectSubTab ({'myaccounttab': false,'mymodelstab': true});
	addGetParameter ("lastContainer", "showMyModels");
}
function showMyAccount ()
{
	hideAll();
	$("#user").show();
	$("#mymodels").hide();
	$("#myaccount").show();
	selectTopTab ("tab_user");
	selectSubTab ({'myaccounttab': true,'mymodelstab': false});
	addGetParameter ("lastContainer", "showMyAccount");
}
function showDiff() {
	hideAll();
	$("#diff").show();
	if ($("#graphdiff").is(":visible")){
		showGraphDiff();
		}
	selectTopTab ("tab_diff");
	addGetParameter ("lastContainer", "showDiff");
}
function showXMLDiff() {
	hideAll();
	$("#diff").show();
	$("#graphdiff").hide();
	$("#graphmldiff").hide();
	$("#xmldiff").show();
	$("#hierarchydiff").hide();
	$("#reportdiff").hide();
	selectTopTab ("tab_diff");
	selectSubTab ({'grpahmldifftab': false,'graphdifftab': false,'xmldifftab': true,'reportdifftab':false});
	addGetParameter ("lastContainer", "showXMLDiff");
}
function showGraphmlDiff() {
	hideAll();
	$("#diff").show();
	$("#xmldiff").hide();
	$("#graphdiff").hide();
	$("#reportdiff").hide();
	$("#hierarchydiff").hide();
	$("#graphmldiff").show();
	selectTopTab ("tab_diff");
	selectSubTab ({'grpahmldifftab': true,'graphdifftab': false,'xmldifftab': false,'reportdifftab':false});
	addGetParameter ("lastContainer", "showPicDiff");
}
function showReportDiff() {
	hideAll();
	$("#diff").show();
	$("#xmldiff").hide();
	$("#graphmldiff").hide();
	$("#graphdiff").hide();
	$("#hierarchydiff").hide();
	$("#reportdiff").show();
	selectTopTab ("tab_diff");
	selectSubTab ({'grpahmldifftab': false,'graphdifftab': false,'xmldifftab': false,'reportdifftab':true});
	addGetParameter ("lastContainer", "showReportDiff");
}
function showHierarchyDiff() {
	hideAll();
	$("#diff").show();
	$("#xmldiff").hide();
	$("#graphmldiff").hide();
	$("#reportdiff").hide();
	$("#graphdiff").hide();
	$("#hierarchydiff").show();
	selectTopTab ("tab_diff");
	selectSubTab ({'grpahmldifftab': false,'graphdifftab': true,'xmldifftab': false,'reportdifftab':false});
	addGetParameter ("lastContainer", "showGraphDiff");
}
function showGraphDiff() {
	hideAll();
	$("#diff").show();
	$("#xmldiff").hide();
	$("#graphmldiff").hide();
	$("#reportdiff").hide();
	$("#hierarchydiff").hide();
	$("#graphdiff").show();
	selectTopTab ("tab_diff");
	selectSubTab ({'grpahmldifftab': false,'graphdifftab': true,'xmldifftab': false,'reportdifftab':false});
	addGetParameter ("lastContainer", "showGraphDiff");
}
function showAbout() {
	hideAll();
	$("#about").show();
	selectTopTab ("tab_main");
	addGetParameter ("lastContainer", "showAbout");
}
function showUser ()
{
	hideAll();
	$("#user").show();
	selectTopTab ("tab_user");
	if ($("#mymodels").is(":visible")){
		showMyModels ();
		}
	else if ($("#myaccount").is(":visible")){
		showMyAccount();
		}
	addGetParameter ("lastContainer", "showUser");
}
function showTree() {
	hideAll();
	$("#tree").show();
	if ($("#graphtree").is(":visible")){
		showGraphTree();
		}
	selectTopTab ("tab_tree");
	addGetParameter ("lastContainer", "showTree");
}
function showPicTree() {
	hideAll();
	$("#tree").show();
	$("#graphtree").hide();
	$("#pictree").show();
	selectTopTab ("tab_tree");
	selectSubTab ({'graphtreetab': false,'pictreetab': true});
	addGetParameter ("lastContainer", "showPicTree");
}
function showGraphTree() {
	hideAll();
	$("#tree").show();
	$("#graphtree").show();
	$("#pictree").hide();
	selectTopTab ("tab_tree");
	selectSubTab ({'graphtreetab': true,'pictreetab': false});
	addGetParameter ("lastContainer", "showGraphTree");
}*/

// JS variante des PHP-Befehls "htmlspecialchars();"
function escapeHtml(unsafe) {
  return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
}
function removeChildren (elem)
{
	while (elem.firstChild)
		elem.removeChild(elem.firstChild);
}
/*
function escapeKaufmannsUnd(unsafe) {
  return unsafe.replace(/&/g, "&amp;");
}
// JS variante des PHP-Befehls "htmlspecialchars();"
function unescapeHtml(safe) {
  return safe
      .replace(/&lt;/g, "<")
      .replace(/&gt;/g, ">");
}*/