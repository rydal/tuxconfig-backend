  function send_post(clicked_id){
	    $.ajax({
	        type: "GET",
	        url: "https://linuxconf.feedthepenguin.org/hehe/adminconsole",
	        data: {"email" : clicked_id },
	        
	        dataType: "json",
	        success: function(data, textStatus) {
	            if (data.redirect) {
	                // data.redirect contains the string URL to redirect to
	            	window.location.href = data.redirect;
	            }
	            else {
	                // data.form contains the HTML for the replacement form
	                $("#server_response").replaceWith(data.form);
	            }
	        }
	        
	    })
  }