var params  = "";
var myObj = {};
var ticked = false;
var myarray = [];

function selected(pic_id) {
    	var result = pic_id.replace(/banner/g,"");
    	document.getElementById(result).src="tick.png";
    	myObj[result] = "1";
    	ticked = true;
    	myarray.push(result);
    }

function send_post(type){
	if (  ticked == false) {
        $("#server_response").replaceWith("No entries selected");
return;
	}
		
	
	if (type === "publish") {
	if (confirm("Publish personal details online?")) {
    	myObj[type] = "1";
   
	} else {
    	
        $("#server_response").replaceWith("publish cancelled");
        return;
    }
	}
	if (type === "done") {
		myObj[type] = "1";
	}
	params = JSON.stringify( myObj);
	
	$.ajax({
        type: "POST",
        url: "https://linuxconf.feedthepenguin.org/hehe/chooseshare",
        data: myObj,
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
    });
}




function clearquestions() {
	for (var i = 0; i < myarray.length; i++) {
		document.getElementById(myarray[i]).src="./img/question.png";
	}	
}

