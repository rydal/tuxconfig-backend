window.jQuery = window.$ = function() {
	return {
		on : function() {
		}
	};
};
window.$.fn = {};
var input = document.querySelector("input"), form = document
		.querySelector("form"), result = document.querySelector("#result");

var iti = intlTelInput(input, {
	initialCountry : "us"
});

form.addEventListener("submit", function(e) {
	e.preventDefault();
	var num = iti.getNumber(), valid = iti.isValidNumber();
	result.textContent = "Number: " + num + ", valid: " + valid;
}, false);

input.addEventListener("focus", function() {
	result.textContent = "";
}, false);



function passwordComplex() {
	
    var newPassword = document.getElementById('chkpword').value;
    var minNumberofChars = 6;
    var maxNumberofChars = 16;
    var regularExpression  = /^[a-zA-Z0-9!@#$%^&*]{6,16}$/;
     
    if(newPassword.length < minNumberofChars || newPassword.length > maxNumberofChars){
    	document.getElementById("output").innerHTML+="<h2 style=color:red;>password must be of at least 6 characters</h2>";
        
        return false;
    }
    if(!regularExpression.test(newPassword)) {
    	document.getElementById("output").innerHTML+="<h2 style=color:red;>password should contain atleast one number and one special character</h2>";
        return false;
    }
    document.getElementById("output").innerHTML="";
    return true;	

}
function samePassword(){

	var password = document.getElementById("pword")
	, confirm_password = document.getElementById("chkpword");

	if(password.value != confirm_password.value) {
    document.getElementById("output").innerHTML+="<h2 style=color:red;>passwords not equal</h2>";
    return false;	
} else {
    document.getElementById("output").innerHTML="";}
	return true;
}


function ValidateEmail() {
var email_address = document.getElementById('email').value
if (/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email_address))
 {
	 document.getElementById("output").innerHTML="";

   return true;
 }
document.getElementById("output").innerHTML+="<h2 style=color:red;> invalid email format</font></h2>";

return false;
}

function status_check() {
	document.getElementById("output").innerHTML="";
	if(ValidateEmail() && passwordComplex() && samePassword()) {
	
		send_post();
		return true;
	} else {
		document.getElementById("output").innerHTML+="<h2 style=color:red;> Please correct errors";
return false;
	}
}
function send_post(){
    $.ajax({
        type: "POST",
        url: "https://linuxconf.feedthepenguin.org/hehe/doregister",
        data: {"email" : $('#email').val(), "password" : $('#pword').val()},
        
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

function get(name) {
	if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)'))
			.exec(location.search)) {
		if (decodeURIComponent(name[1]) == "password") {
			return "Please create a password";
		}							 
		if (decodeURIComponent(name[1]) == "facebook") {
			return "Logged in with facebook.<br> please create password";
			
		}
	} else {
		return "";

	}
}

