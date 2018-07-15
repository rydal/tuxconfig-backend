function submit_repositries() {

	var i = 0;
	var success = true;
	var result = "";
	;
	
	while (document.getElementById("device_checkbox" + i) != undefined) {
		if (document.getElementById("device_checkbox" + i).checked) {
			if (document.getElementById("device_id" + i).value === "") {

				success = false;
				result += "Device ID not filled out\n";
			}
			if (document.getElementById("device_name" + i).value === "") {
				success = false;
				result += "Device Name not filled out\n";
			}

			if (document.getElementById("device_id" + i).value != "") {
				var usb_id = document.getElementById("device_id" + i).value;
				var res = usb_id.split(':', 2);

				if (res != undefined
						&& (res[0].length != 4 || res[1].length != 4)) {

					result += "Usb Id number not in the correct format, NNNN:NNNN\n";
					success = false;
				}
			}
			if (success == false) {
				result += "For device number " + i + "\n\n";
			}

		}

		i++;
	}
	}

	if (success == false) {
		alert(result);
		return false;
	} else {
		dataString = JSON.stringify(dataString);
		 $.ajax({
		        type: "POST",
		        url: "https://linuxconf.feedthepenguin.org/hehe/createuser",
		        data: $("#theform").serializeArray(),
		        dataType: "json",
		        success: function(data) {
		            if (data.result1) {
		                // data.redirect contains the string URL to redirect to
		                
		            	alert("yeah");
		            }
		            else {
		                alert("no");
		            }
		        }
		    });
	
}
