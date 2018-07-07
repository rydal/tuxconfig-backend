
function subimit_reposities(){
 if (handleChange == true) {
	 document.getElementById("myForm").submit();
 }
 
}

function handleChange() {
    var x = document.getElementById("description").value;
    var y = document.getElementById("url").value;

    var i = 0;
    while ( document.getElementById("device_id" + i) != undefined) {
    	var usb_id = 	document.getElementById("device_id" + i).value;
    	 var res = usb_id.split(":",1);
    	 if (res[0].length != 4 || res[1].length != 4 || res == null) {
    		 
    		 alert("Usb Id number " + i + 1 + " not in the correct format, NNNN:NNNN");
    		 return false;
    	 }
    	 console.log(res[0] + ":" + res[1]);
    	 i++;
	}
        return true;
        
     
  }