



    function send_post(input){
    	
    	window.pauseAll=true;
    	var network = input;
        $.ajax({
            type: "POST",
	        url: "https://linuxconf.feedthepenguin.org/hehe/getcode",	
            data: {"user" : $('#user').val() , "token" : $('#token').val(), "type" : $('#type').val(), "network" : network },
            dataType: "json",
            success: function(data, textStatus) {
                if (data.redirect) {
                    // data.redirect contains the string URL to redirect to
                    window.pauseAll=false;

                	window.location.href = data.redirect;
                }
                else {
                    window.pauseAll=false;

                    // data.form contains the HTML for the replacement form
	                $("#status").replaceWith(data.form);
	                location.reload();
                }
            }
        });
        }
    
function message() {
	alert("downloading vcard, please click the link!");
}
    
    
 function send_email(input){
    	window.pauseAll=true;
    	var myemail = getCookie("client_email");
    	if(myemail == null) {
    		myemail = $('#emailform').val()
    	}
    	var network = input;
        $.ajax({
            type: "POST",
	        url: "https://linuxconf.feedthepenguin.org/hehe/getcode",	
            data: {"user" : $('#user').val() , "token" : $('#token').val(), "type" : $('#type').val(), "email" : myemail },
            dataType: "json",
            success: function(data, textStatus) {
                if (data.redirect) {
                    // data.redirect contains the string URL to redirect to
                    window.pauseAll=false;

                	window.location.href = data.redirect;
                }
                else {
                    window.pauseAll=false;

                    // data.form contains the HTML for the replacement form
	                $("#email_status").replaceWith(data.form);
                }
            }
        });
        }    
    
    function getCookie(name) {
  	  var value = "; " + document.cookie;
  	  var parts = value.split("; " + name + "=");
  	  if (parts.length == 2) return parts.pop().split(";").shift();
  	}
  
  
  function setCookie(name,value,days) {
      var expires = "";
      if (days) {
          var date = new Date();
          date.setTime(date.getTime() + (days*24*60*60*1000));
          expires = "; expires=" + date.toUTCString();
      }
      document.cookie = name + "=" + (value || "")  + expires + "; path=/";
  }
  
  function get(name){
  	   if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
  	      return decodeURIComponent(name[1]);
  	}
    
  

    
    
    	
    
    function delete_cookie( name ) {
    	  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    	}       

    
    