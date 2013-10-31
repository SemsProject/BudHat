var options = {
		  layout: {
			    name: 'arbor',
			    edgeLength: 5
			  },
			  
			  style: cytoscape.stylesheet()
			    .selector('node')
			      .css({
			        'content': 'data(name)',
			        'text-valign': 'center',
			        'color': '#000',
			        'border-width':'1',
			        'border-color':'#666'
			      })
			  .selector('.compartment').css({
			        'text-valign': 'bottom',
			        'opacity':.9,
			        'background-color': '#eee',
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
			        'target-arrow-shape': 'triangle'
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
			        'background-color': '#99f',
			        'target-arrow-color': '#99f',
			        'line-color': '#99f'
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
	options.elements = graph.elements;
	  $("#subtab_graph_graph").html("");
	  console.log(graph.elements);

	  $("#subtab_graph_graph").cytoscape(options);
	
}
function drawDiffHierarchyJS (graph)
{console.log(graph);
graph = JSON.parse(graph);
	options.elements = graph.elements;
	  $("#subtab_hierarchy_graph").html("");
	  console.log(graph.elements);

	  $("#subtab_hierarchy_graph").cytoscape(options);
	
}
