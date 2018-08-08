	    function send_post() {
	    	
        	
	    $.ajax({
	        type: "POST",
	        url: "https://linuxconf.feedthepenguin.org/hehe/dologin",
	        data: {"email" : $('#email').val(), "password" : $('#password').val(), "loginimage" : "1"},
	        dataType: "json",
	        success: function(data, textStatus) {
	            if (data.redirect) {
	            	
	                // data.redirect contains the string URL to redirect to
	            	window.location.href = data.redirect;
	            }
	            else {
	                // data.form contains the HTML for the replacement form
	                $("#server_response").replaceWith(data.form);
	            	$('#password').val("");

	                
	            }
	        }
	    });
	    }
	    
