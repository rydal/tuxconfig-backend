function submit_reposities() {

	var i = 0;
	var success = true;
	var result = "";

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

	if (success == false) {
		alert(result);
		return false;
	} else {
		$('theform').submit(function(event) {

			// get the form data
			// there are many ways to get this data using jQuery (you can
			// use the class or id also)

			// process the form
			$.ajax({
				type : 'POST', // define the type of HTTP verb we
				// want to use (POST for our form)
				url : $('http://linuxconf.feedthepenguin.org/hehe/createuser'),
				data : $("theform"),
				dataType : 'json', // what type of data do we
				// expect back from the
				// server
				encode : true
			})
			// using the done promise callback
			.done(function(data) {

				// log data to the console so we can see
				console.log(data);

				// here we will handle errors and validation messages
			});

			// stop the form from submitting the normal way and refreshing
			// the page
			event.preventDefault();
		});

	}
}
