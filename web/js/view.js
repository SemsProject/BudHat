
// new model|new version of a model togglen
function hideInputs() {
   $("#altesModell").fadeOut(500, function(){$("#neuesModell").fadeIn();} );   	 
}

// new model|new version of a model togglen
function hideInputs2() {
   $("#neuesModell").fadeOut(500, function(){$("#altesModell").fadeIn();} );   	 
}

// Differenz und GraphML-Bereich einblenden
function flyIn(){
	  $("#left").removeClass("margin_auto").addClass("float_left");
   	$("#main_con").show('slide', {direction: 'right'}, 500);	
}


// Uploadbereich einblenden/ausblenden
function showUploadArea(){
	$("#upload_div").slideToggle();
}