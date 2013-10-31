function startsWith (str, prefix)
{
	return str.indexOf(prefix) == 0;
}

function endsWith(str, suffix)
{
	return str.indexOf(suffix, str.length - suffix.length) !== -1;
}


$(document).ready(function()
{
	$(".tab").click( function()
	{
		
		
		var containers = this.parentNode.children;
		for (var i = 0; i < containers.length; i++)
			if (containers[i].id == this.id)
				containers[i].className = containers[i].className + " navi-active";
			else
				containers[i].className = containers[i].className.replace(/\s*\bnavi-active\b\s*/g,'');
			
			
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
			}
			
			
			if (startsWith (this.id, "sub"))
			{
				document.getElementById (this.parentNode.parentNode.id.replace("_content","")).click();
			}
			
	});
	
	$("#modeladder").click (function () {addSelect();});
	$("#modelremover").click (function () {removeSelect();});
	$("#treegenerator").click (function () {genTree();});
	$("#infogenerator").click (function () {getInfo();});
	
	$("#compareBtn").click (function () {compareModels();});
	
	
	var hier_flash_div = document.getElementById ("subtab_hierarchy_graph_flash");
	var hier_js_div = document.getElementById ("subtab_hierarchy_graph_js");
	
	hier_flash_div.style.display = "none";
	$("#subtab_hierarchy_graph_draw_flash").click( function() {hier_flash_div.style.display = "block"; hier_js_div.style.display = "none";} );
	$("#subtab_hierarchy_graph_draw_js").click( function() {hier_flash_div.style.display = "none"; hier_js_div.style.display = "block";} );
	
	var graph_flash_div = document.getElementById ("subtab_graph_graph_flash");
	var graph_js_div = document.getElementById ("subtab_graph_graph_js");
	
	graph_flash_div.style.display = "none";
	$("#subtab_graph_graph_draw_flash").click( function() {graph_flash_div.style.display = "block"; graph_js_div.style.display = "none";} );
	$("#subtab_graph_graph_draw_js").click( function() {graph_flash_div.style.display = "none"; graph_js_div.style.display = "block";} );
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