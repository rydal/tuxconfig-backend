
function subimit_reposities(type){
    $.ajax({
        type: "POST",
        url: "https://mycode.feedthepenguin.org/hehe/setrepositires",
        data: {"logout" : input},
        dataType: "json",
        success: function(data, textStatus) {
            if (data.redirect) {
                // data.redirect contains the string URL to redirect to
                
            	window.location.href = data.redirect;
            }
            else {
                // data.form contains the HTML for the replacement form
                $("#server_response").replaceWith(data.form);
                $("#server_response2").replaceWith(data.form);
             location.reload();
            }
        }
    });
    }