

function loadPendingApprovals(){
    $.get("/api/pending-approvals", function(data){
        $( "#pa_count" ).html(data)
    });
}

$(document).ready(function () {
    loadPendingApprovals();
    setInterval(loadPendingApprovals, 2500);
});

