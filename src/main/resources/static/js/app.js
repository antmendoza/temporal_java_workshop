

function loadPendingApprovals(){
    $.get("/api/pending-transfers", function(data){
        $( "#pa_count" ).html(data)
        $( "#pa_info" ).removeClass("pa_highlighted")
        if(data > 0){
            $( "#pa_info" ).addClass("pa_highlighted")
        }

    });
}

$(document).ready(function () {
    loadPendingApprovals();
    setInterval(loadPendingApprovals, 2500);
});

