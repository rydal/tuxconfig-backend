function subimit_reposities() {

	var i = 0;
	var success = true;
	var result = "";

	while (document.getElementById("device_checkbox" + i) != undefined) {
		console.log(i + " around the loop");
		if (document.getElementById("device_checkbox" + i).checked) {
			console.log("Entered outer if");
			if (document.getElementById("device_id" + i).value === "") {

				success = false;
				result += "Device ID not filled out\n";
			}
			if (document.getElementById("device_name" + i).value === "") {
				success = false;
				result += "Device Name not filled out\n";
			}
			if (document.getElementById("git_commit" + i).value === "") {
				success = false;
				result += "Git Commit not filled out\n";
			}
			
			if (document.getElementById("device_id" + i).value != "" ) {
			var usb_id = document.getElementById("device_id" + i).value;
			var res = usb_id.split(":", 1);
			
			if (! res != undefined && ( res[0].length == 4 || res[1].length == 4 )) {

			result += "Usb Id number not in the correct format, NNNN:NNNN\n");
				success = false;
			}
			if (success == false) {
				result += "For device number " + i + "\n\n";
			}
		}
		}
		
		i++;
	}
	if (success == false) {
		alert(result);
		return false;
	} else {
		return true;
	}
	if (handleChange == true) {
// document.getElementById("myForm").submit();
	}



var dataString = "";

function ajax() {

	$.ajax({
		type : "POST",
		url : "https://mycode.feedthepenguin.org/mycard/getnetworks",
		data : {
			"logout" : input
		},
		dataType : "json",
		success : function(data, textStatus) {
			if (data.redirect) {
				// data.redirect contains the string URL to redirect to

				window.location.href = data.redirect;
			} else {
				// data.form contains the HTML for the replacement form
				$("#server_response").replaceWith(data.form);
				$("#server_response2").replaceWith(data.form);
				location.reload();
			}
		}
	});
}

function handleChange() {
	var x = document.getElementById("description").value;
	var y = document.getElementById("url").value;

	var i = 0;
	while (document.getElementById("device_id" + i) != undefined) {
		var usb_id = document.getElementById("device_id" + i).value;
		var res = usb_id.split(":", 1);
		if (res[0].length != 4 || res[1].length != 4 || res == null) {

			alert("Usb Id number " + i + 1
					+ " not in the correct format, NNNN:NNNN");
			return false;
		}
		console.log(res[0] + ":" + res[1]);
		i++;
	}
	return true;

}