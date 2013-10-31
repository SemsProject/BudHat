var cytoscapeJSoptions = {
		  layout: {
			    name: 'arbor'//,
			    //edgeLength: 2
			  },
			  
			  style: cytoscape.stylesheet()
			    .selector('node')
			      .css({
			        'content': 'data(name)',
			        'text-valign': 'center',
							'color': '#000',
							'background-color': '#fff',
			        'border-width':'1',
			        'border-color':'#666'
			      })
			  .selector('.compartment').css({
			        'text-valign': 'bottom',
			        'opacity':.9,
			        'background-color': '#ddd',
			        'border-width':'0'
			    
			  })
			  .selector('.species').css({
			        'shape': 'ellipse'
			  })
			  .selector('.reaction').css({
			        'shape': 'hexagon'
			  })
			    .selector(':selected')
			      .css({
			        'border-width': 3,
			        'border-color': '#333'
			      })
			    .selector('edge')
			      .css({
							'target-arrow-shape': 'triangle',
							'target-arrow-color': '#000',
							'line-color': '#000'
			      })
			  .selector ('.bives-unkwnmod').css({
			        'target-arrow-shape': 'circle',
			    'line-style':'dashed'
			    
			  })
			  .selector ('.bives-inhibitor').css({
			        'target-arrow-shape': 'tee',
			    'line-style':'dashed'
			    
			  })
			  .selector ('.bives-stimulator').css({
			        'target-arrow-shape': 'triangle',
			    'line-style':'dashed'
			    
			  })
			  
			  
			  .selector ('.bives-deleted').css({
			        'background-color': '#f00',
			        'target-arrow-color': '#f00',
			        'line-color': '#f00'
			  })
			  .selector ('.bives-inserted').css({
			        'background-color': '#44f',
			        'target-arrow-color': '#44f',
			        'line-color': '#44f'
			  })
			  .selector ('.bives-modified').css({
			        'background-color': '#ff0',
			        'target-arrow-color': '#ff0',
			        'line-color': '#ff0'
			  })
              .selector('edge.bives-deleted').css({'width': '2'})
              .selector('edge.bives-modified').css({'width': '2'})
              .selector('edge.bives-inserted').css({'width': '2'}),
			  ready: function(){
			      cy = this;
			    }
  };


function drawDiffGraphJS (graph)
{console.log(graph);
graph = JSON.parse(graph);
cytoscapeJSoptions.elements = graph.elements;
	  $("#subtab_graph_graph_js").html("");
	  console.log(graph.elements);

		$("#subtab_graph_graph_js").cytoscape(cytoscapeJSoptions);
	
}
function drawDiffHierarchyJS (graph)
{console.log(graph);
graph = JSON.parse(graph);
cytoscapeJSoptions.elements = graph.elements;
	$("#subtab_hierarchy_graph_js").html("");
	  console.log(graph.elements);

		$("#subtab_hierarchy_graph_js").cytoscape(cytoscapeJSoptions);
	
}

function drawMatrixJs (graph, divid)
{//console.log(graph);
	graph = JSON.parse(graph);
	cytoscapeJSoptions.elements = graph.elements;
	$("#" + divid).html("");
	//console.log(graph.elements);
	
	$("#" + divid).cytoscape(cytoscapeJSoptions);
	
}
