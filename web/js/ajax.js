function compareModels(){
	var Opts = document.getElementById('selected').options;
	if (Opts.length == 2){
		//TODO: showLoading();
		
		var tmp = Opts[0].value.split("|");
		var idA = tmp[0];
		var versA = tmp[1];
		
		tmp = Opts[1].value.split("|");
		var idB = tmp[0];
		var versB = tmp[1];

		addGetParameter ('diffModA', idA);
		addGetParameter ('diffModB', idB);
		addGetParameter ('diffVersA', versA);
		addGetParameter ('diffVersB', versB);
		
		
		
		
		$.post("differ", { 
						modelA:  idA, versionA: versA, 
						modelB:  idB, versionB: versB
						},
		 function(data) {
							
							var crnjson = data.crnjson;
							var crndot = data.crndot;
							var crngraphml = data.crngraphml;
							var hierarchygraphml = data.hierarchygraphml;
							var hierarchyjson = data.hierarchyjson;
							var hierarchydot = data.hierarchydot;
							var htmlreport = data.htmlreport;
							var xmldiff = data.xmldiff;
			 
			 if (htmlreport)
			 {
				 $("#subtab_report_content").html (htmlreport);
				$("#subtab_report").show ();
			 }
			 else
			 {
				 $("#subtab_report_content").html ("");
				 $("#subtab_report").hide ();
			 } 
			 
			 
			 
			 if (xmldiff)
			 {
				$("#subtab_xml").show ();
				
				//$("#subtab_xml_content").html("<pre id='subtab_xml_content_pre'>" + escapeHtml(xmldiff) + "</pre>");
				//$("#subtab_xml_content_pre").snippet("xml",{style:"acid"});
				
				 var xmlcont = document.getElementById("subtab_xml_content");
				 removeChildren (xmlcont);
				 var pre = document.createElement("pre");
					xmlcont.appendChild (pre);
					pre.id = "subtab_xml_content_pre"
					pre.setAttribute ("class", "xml");
					pre.innerHTML = escapeHtml(xmldiff);
					hljs.highlightBlock(pre);
			 }
			 else
			 {
				 $("#subtab_xml_content").html("");
				 $("#subtab_xml").hide ();
			 }
			 
			 
			 
			 
			 if (hierarchyjson)
			 {
				 $("#subtab_hierarchy").show ();
					//drawHierarchyFlash (hierarchydiff);
				 var code = document.getElementById("subtab_hierarchy_code");
				 var pre = document.createElement("pre");
				 pre.id = "hierarchypre";
				 if (hierarchygraphml)
				 {
					 var link = document.createElement("button");
					 link.appendChild(document.createTextNode(" show graphml "));
					 link.addEventListener("click", function () {pre.innerHTML = escapeHtml (hierarchygraphml);
							pre.setAttribute ("class", "xml");
							hljs.highlightBlock(pre);
						 
						 
					}, false);
					 code.appendChild (link);
				 }
				 
				 if (hierarchydot)
				{
					 var link = document.createElement("button");
					 link.appendChild(document.createTextNode(" show dot "));
					 link.addEventListener("click", function () {pre.innerHTML = escapeHtml (hierarchydot);
							pre.setAttribute ("class", "");
							hljs.highlightBlock(pre);
						 
					}, false); 
					 code.appendChild (link);
				}
				 
				 if (hierarchyjson)
				{
					 var link = document.createElement("button");
					 link.appendChild(document.createTextNode(" show json "));
					 link.addEventListener("click", function () {pre.innerHTML = escapeHtml (JSON.stringify(JSON.parse(hierarchyjson), null, 4))
						 pre.setAttribute ("class", "json");
							hljs.highlightBlock(pre);
						 
					}, false); 
					 code.appendChild (link);
				}
				 code.appendChild (pre);
				 drawDiffHierarchyJS (hierarchyjson);
			 }
			 else
			 {
				 $("#subtab_hierarchy").hide ();
				 $("#subtab_hierarchy_code").html("");
			 }
			 
			 
			 
			 
			 if (crnjson)
			 {
				/*$("#graphmldiff").html("<pre id='graphmldiffpre'>" + escapeHtml(unescapeHtml(graph)) + "</pre>");
				$("#graphmldiffpre").snippet("xml",{style:"acid"});
				drawDiffFlash (graph);
				$("#grpahmldifftab").show ();
				$("#graphdifftab").show ();
				 showGraphDiff ();
				 //alert (graph);*/

				 $("#subtab_graph").show ();
					//drawHierarchyFlash (hierarchydiff);
				 var code = document.getElementById("subtab_graph_code");
				 removeChildren (code);
				 var pre = document.createElement("pre");
				 pre.id = "graphpre";
				 if (crngraphml)
				{
					 var link = document.createElement("button");
					 link.appendChild(document.createTextNode(" show graphml "));
					 link.addEventListener("click", function ()
						 {
						 	pre.innerHTML = escapeHtml (crngraphml);
						 	// TODO: delete jquery bullshit
						 	/*$("#graphpre").snippet("xml",{style:"acid"});*/
							pre.setAttribute ("class", "xml");
							hljs.highlightBlock(pre);
						 }, false);
					 code.appendChild (link);
				}
				 if (crndot)
				{
					 var link = document.createElement("button");
					 link.appendChild(document.createTextNode(" show dot "));
					 link.addEventListener("click", function () {pre.innerHTML = escapeHtml (crndot);
					 	/*$("#graphpre").snippet("xml",{style:"acid"});
						 */
							pre.setAttribute ("class", "");
							hljs.highlightBlock(pre);
					}, false); 
					 code.appendChild (link);
				}
				 
				 if (crnjson)
				{
					 var link = document.createElement("button");
					 link.appendChild(document.createTextNode(" show json "));
					 link.addEventListener("click", function () {pre.innerHTML = escapeHtml (JSON.stringify(JSON.parse(crnjson), null, 4));
					 	/*$("#graphpre").snippet("javascript",{style:"acid"});
						 */
							pre.setAttribute ("class", "json");
							hljs.highlightBlock(pre);
					}, false); 
					 code.appendChild (link);
				}
				 code.appendChild (pre);
				 drawDiffGraphJS (crnjson);
			 }
			 else
			 {
				 /*$("#graphmldiff").html("");
				 $("#grpahmldifftab").hide ();
				 $("#graphdifftab").hide ();
				 showDiff ();*/
				 $("#subtab_graph").hide ();
				 $("#subtab_graph_code").html("");
			 }
			 
				 $("#tab_diff_content").show ();
				 $("#tab_diff").show ();
				 //vis.draw(draw_options);
				 MathJax.Hub.Queue(["Typeset",MathJax.Hub]);
				 
				 
		 }).fail(function(xhr, textStatus, errorThrown)
		 {
			 // TODO
			 alert(xhr.responseText);
			 //alert(textStatus);
			 // hide tabs
			 /*$("#grpahmldifftab").hide ();
			 $("#graphdifftab").hide ();
			 $("#xmldifftab").hide ();
			 $("#reportdifftab").hide ();
			 // set error msgs
			 $("#reportdiff").html ("error");
			 $("#xmldiff").html("error");
			 $("#graphmldiff").html("error");
			 var vis = new org.cytoscapeweb.Visualization("graphdiffflash", options);
			 vis.draw({ network: '<graphml></graphml>' });
			 showAbout();*/
		});
		
		
	}
	 else{
	 	alert("please choose 2 models!");	 
	 }
}

function getInfo (preId, preVers)
{
	if (!preId || !preVers)
	{
		object = $('select[name*="available"] option:selected');
		if (!object)
			return;
	
		tmp = object.attr('value').split("|");
		id = tmp[0];
		vers = tmp[1];
	}
	else
	{
		id = preId;
		vers = preVers;
	}

	addGetParameter ('modInfoId', id);
	addGetParameter ('modInfoVers', vers);

	$.post("info", { 
					model:  id, version: vers, 
					},
	 function(data) {
		 data = $.trim(data); // leerzeichen entfernen

		 //alert (graph);
		 
		 //alert (diff);
		 //alert (graph);
		 //alert (pic);
		 
		$("#info").html("<div><h1>Model Info</h1>" + data + "</div>");
		 //$("#xmldiff").addClass("xml");
		 

		 $("#tab_info").show ();
		 

		 showInfo ();
					});
}

function genTree (preId, preVers){
	if (!preId || !preVers)
	{
		object = $('select[name*="available"] option:selected');
		if (!object)
			return;
		
		tmp = object.attr('value').split("|");
		id = tmp[0];
		vers = tmp[1];
	}
	else
	{
		id = preId;
		vers = preVers;
	}
//alert ("model:  "+id+", version: "+vers);

	addGetParameter ('treeId', id);
	addGetParameter ('treeVers', vers);
	
	$.post("tree", { 
					model:  id, version: vers, 
					},
	 function(data) {

		 //data = JSON.parse(data);
		 //alert (data);

		 //alert ("diff: " + match[1]);
		 //alert ("graphml: " + match[2]);
		 //alert ("graphviz: " + match[3]);
		 //alert ("pic: " + match[4]);
		 
		 //var graph = match[1];
		 //var pic = match[3];
		 //alert (graph);
		 
		 //alert (diff);
		 //alert (graph);
		 //alert (pic);
		 
		//$("#pictree").html("<p><img src='" + pic + "' alt='visualization of the graph' /></p>");
		 //$("#xmldiff").addClass("xml");
		 
		 //alert (data.graphmltree);

		 $("#tab_tree").show ();
		 

		 graphmlTree = data.graphmltree;//escapeKaufmannsUnd(graph);
			//window.graphmlTree = graphmlTree;
		
	
	drawTreeFlash (graphmlTree);
	
	var versions = data.versions;
	var matrix = document.getElementById("diffmatrix");
	
	if (matrix)
		matrix.removeChild (matrix.firstChild);
	
	if (matrix && versions)
	{
		var table = document.createElement("table");
		table.setAttribute ("id", "diffmatrix");
		
		for (var row = 0; row < versions.length; row++)
		{
			var tr = document.createElement("tr");
			for (var col = 0; col < versions.length; col++)
			{
				if (col == row)
				{
					var th = document.createElement("th");
					th.appendChild (document.createTextNode (versions[col]));
					tr.appendChild (th);
				}
				else
				{
					var td = document.createElement("td");
					var div = document.createElement ("div");
					div.setAttribute ("id", "diffmatrix-" + versions[row]+"-"+versions[col]);
					div.appendChild (document.createTextNode ("no graph available..."));
					td.appendChild (div);
					tr.appendChild (td);
				}
			}
			table.appendChild (tr);
		}
		matrix.appendChild (table);

		/*for (var i = 0; i < versions.length; i++)
			for (var j = i + 1; j < versions.length; j++)
			matrixDiff (id, versions[i], versions[j]);*/
	}
	
	console.log (data);
	
	if (data.diffs)
	{
		for (var i = 0; i < data.diffs.length; i++)
		{
			var diff = data.diffs[i];
			if (diff.crndiff)
			{
				drawMatrixFlash (diff.crndiff, "diffmatrix-" + diff.versionA + "-" + diff.versionB);
				drawMatrixFlash (diff.crndiff, "diffmatrix-" + diff.versionB + "-" + diff.versionA);
			}
			else
			{
				var div = document.getElementById("diffmatrix-" + diff.versionA + "-" + diff.versionB);
				div.replaceChild (document.createTextNode ("unable to produce graph"), div.firstChild);
				div = document.getElementById("diffmatrix-" + diff.versionB + "-" + diff.versionA);
				div.replaceChild (document.createTextNode ("unable to produce graph"), div.firstChild);
			}
		}
	}
	
	
        
		 showGraphTree ();
					});

}

/*function matrixDiff (model, versionA, versionB)
{
	$.post("differ", { 
		modelA:  model, versionA: versionA, 
		modelB:  model, versionB: versionB,
		get: "crn"
		},
		 function(data) {
			if (data.crngraphml)
			{
				drawMatrixFlash (graph, "diffmatrix-" + versionA + "-" + versionB);
				drawMatrixFlash (graph, "diffmatrix-" + versionB + "-" + versionA);
			}
		}
		);
}*/