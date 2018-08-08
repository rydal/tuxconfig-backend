function ValidateEmail() 
{
var email_address = document.getElementById('email').value
 if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email_address))
  {
	 document.getElementById("output").innerHTML+="";

    return (true)
  }
 document.getElementById("output").innerHTML+="<h3> invalid email format</font></h2>";

 return (false)
}

var cookie = false;




function validatePassword() {
    var newPassword = document.getElementById('pword').value;
    var minNumberofChars = 6;
    var maxNumberofChars = 16;
    var regularExpression  = /^[a-zA-Z0-9!@#$%^&*]{6,16}$/;
     
    if(newPassword.length < minNumberofChars || newPassword.length > maxNumberofChars){
    	document.getElementById("output").innerHTML+="<h3>password must be of at least 6 characters</h2>";
        
        return false;
    }
    if(!regularExpression.test(newPassword)) {
    	document.getElementById("output").innerHTML+="<h3>password should contain atleast one number and one special character</h2>";
        return false;
    }
    document.getElementById("output").innerHTML+="";

}

function login() {
	document.getElementById("form").submit();

};


//	function applyOrientation() {/
//	  if (window.innerHeight > window.innerWidth) {
//	   document.getElementById('portrait').style.display='block';
///	   document.getElementById('landscape').style.display='none';
//	  } else {
//		   document.getElementById('portrait').style.display='none';
//		   document.getElementById('landscape').style.display='block';
//	  }
//	}

//	function loadXMLDoc() {
//		var http = new XMLHttpRequest();
//		var url = "https://linuxconf.feedthepenguin.org/hehe/dologin";
//		 var email = document.getElementById('email').value;
//
//		 var password= document.getElementById('pword').value;
//		var params = "email=" + email + "&password=" + password + "&loginimage=1";
//		
//		http.open("POST", url, true);
//
//		//Send the proper header information along with the request
//		http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//
//		http.onreadystatechange = function() {//Call a function when the state changes.
//		    if(http.readyState == 4 && http.status == 200) {
//		    	var isurl = http.responseText;
//				if (isurl.includes("https")) {
//					window.location = isurl;
//				}
//
//		        document.getElementById("server_response").innerHTML=(http.responseText);
//		        document.getElementById("server_response2").innerHTML=(http.responseText);
//		    }
//		}
//		http.send(params);
//		
//	}
	
function getCookie(name) {
	  var value = "; " + document.cookie;
	  var parts = value.split("; " + name + "=");
	  if (parts.length == 2) return parts.pop().split(";").shift();
	}
	
	
	
	
	
	function facebook_redirect(source) {
	    {
	    	xhttp.open("POST", "https://linuxconf.feedthepenguin.org/hehe/dologin", true);
	    	xhttp.send(source + ".x");


	    }
	    
	}

	
	
	


	var elementIsClicked = false; // declare the variable that tracks the state
	function clickHandler(){ // declare a function that updates the state
		document.getElementById("portrait").style.display="block";
		document.getElementById("cookie").style.display="none";
	 
	}

	

	
	function showCookieFail(){
	  document.getElementById("server_response").innerHTML = "this website relies on cookies, please enable them";
	}
	function checkCookie(){
	    var cookieEnabled = navigator.cookieEnabled;
	    if (!cookieEnabled){ 
	        document.cookie = "testcookie";
	        cookieEnabled = document.cookie.indexOf("testcookie")!=-1;
	    }
	    return cookieEnabled || showCookieFail();
	}

	function showCookieFail(){
	  // do something here
		document.getElementById("server_response").innerHTML = "Cookies disabled, this site won't work";

	}

	// within a window load,dom ready or something like that place your:
	window.onload = checkCookie; 
	
	
	

	    function deleteCookie(cookiename)
	    {
	        var d = new Date();
	        d.setDate(d.getDate() - 1);
	        var expires = ";expires="+d;
	        var name=cookiename;
	        document.getElementById("cookie_magic").innerHTML += name + "deleted";
	        var value="";
	        document.cookie = name + "=" + value + expires + "; path=/hehe/";                    
	    }
	    function send_post(){
	    	
        	
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
	    
