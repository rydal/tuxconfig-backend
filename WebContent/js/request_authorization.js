  function send_post(){
	    $.ajax({
	        type: "GET",
	        url: "https://linuxconf.feedthepenguin.org/hehe/forgotpassword",
	        data: {"email" : $('#email').val()},
	        
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

	