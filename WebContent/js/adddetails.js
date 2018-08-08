function get(name) {
	if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)'))
			.exec(location.search)) {
		document.getElementById("redirect_message").innerHTML = decodeURIComponent(name[1]);
		return true;
	}

}

$.validator.addMethod("phoneNumValidation", function(value) {
	return $("#phone").intlTelInput("isValidNumber")
}, 'Please enter a valid number');
var validate = function() {
	$("#userPhoneForm").validate({
		rules : {
			phone : {
				required : true,
				phoneNumValidation : true,
			}
		},
		messages : {
			phone : {
				required : "Phone number is required field."
			}
		},
		errorPlacement : function(error, element) {
			error.insertAfter($("#userPhoneDiv"));
		}
	});
}
$(document).ready(function() {
	console.log("in ready");
	$("#phone").intlTelInput({
		nationalMode : true,
		utilsScript : "js/utils.js"
	});

});
$.get("https://ipinfo.io?token=f4c5337e769b81", function(response) {
	$("#ip").html("IP: " + response.ip);
	$("#address").html("Location: " + response.city + ", " + response.region);
	$("#details").html(JSON.stringify(response, null, 4));
	$("#city").html(response.city);
	document.getElementById('country').value = response.country;
}, "jsonp");

$(document).ready(function() {

	$.get("https://ipinfo.io", function(response) {
		$("#phone").intlTelInput("setCountry", response.country);
		if ($("#country").val == "") {
			$("#country").val(response.country);
		}

	}, "jsonp");

});

function clicky() {
	{
		if ($("#phone").intlTelInput("getNumber") != "") {
			if ($("#phone").intlTelInput("isValidNumber")) {

				var number = $("#phone").intlTelInput("getNumber",
						intlTelInputUtils.numberFormat.E164);
				document.getElementById("hiddenField").value = number;
				document.myform.submit();
			} else {
				var intlNumber = $("#phone").intlTelInput("getNumber"); // get
																		// full
																		// number
																		// eg
																		// +17024181234
				var countryData = $("#phone").intlTelInput(
						"getSelectedCountryData"); // get country data as obj

				document.getElementById("redirect_message").innerHTML = ("<font color=\"red\">  Error in number</font>");
				return false;

			}
		} else {
			document.getElementById("hiddenField").value = number;
			document.myform.submit();
		}
	}
}