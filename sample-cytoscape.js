$('#cy').cytoscape({
  layout: {
    name: 'breadthfirst'
  },
  
  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        //'shape': 'data(faveShape)',
        'content': 'data(name)',
        'text-valign': 'center',
        //'text-outline-width': 1,
        //'text-outline-color': '#000',
        //'background-color': 'data(faveColor)',
        'font-weight':'bold',
        'color': '#000',
        'border-width':'1',
        'border-color':'#666'
      })
  .selector('.compartment').css({
        'text-valign': 'bottom',
        'background-color': '#ddd',
        'border-width':'0'
    
  })
  .selector('.species').css({
        //'shape': 'ellipse'
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
        'width': 'mapData(strength, 70, 100, 2, 6)',
        'target-arrow-shape': 'triangle'
      })
    .selector('edge.questionable')
      .css({
        'line-style': 'dotted',
        'target-arrow-shape': 'diamond'
      })
    .selector('.faded')
      .css({
        'opacity': 0.25,
        'text-opacity': 0
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
        'line-color': '#00f'
  })
  .selector ('.bives-modified').css({
        'background-color': '#ff0',
        'target-arrow-color': '#ff0',
        'line-color': '#ff0'
  }),
  elements:{"edges":[{"classes":"bives-ioedge","data":{"source":"s2","target":"r1"}},{"classes":"bives-ioedge bives-deleted","data":{"source":"r1","target":"s3"}},{"classes":"bives-ioedge bives-inserted","data":{"source":"r1","target":"s5"}},{"classes":"bives-ioedge","data":{"source":"r1","target":"s4"}},{"classes":"bives-stimulator","data":{"source":"s1","target":"r1"}},{"classes":"bives-ioedge bives-inserted","data":{"source":"s5","target":"r2"}},{"classes":"bives-ioedge bives-inserted","data":{"source":"r2","target":"s3"}}],"nodes":[{"classes":"compartment","data":{"id":"c1","name":"compartment"}},{"classes":"species","data":{"id":"s2","name":"RB\/E2F","parent":"c1"}},{"classes":"species","data":{"id":"s3","name":"RB-Hypo","parent":"c1"}},{"classes":"species","data":{"id":"s1","name":"cycE\/cdk2","parent":"c1"}},{"classes":"species bives-inserted","data":{"id":"s5","name":"RB-Phos","parent":"c1"}},{"classes":"species","data":{"id":"s4","name":"free E3F","parent":"c1"}},{"classes":"reaction bives-modified","data":{"id":"r1","name":"r","parent":"c1"}},{"classes":"reaction bives-inserted","data":{"id":"r2","name":"s","parent":"c1"}}]},
  
  ready: function(){
    window.cy = this;
    
    // giddy up
  }
});
