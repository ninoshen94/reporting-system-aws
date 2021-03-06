function loadAll() {
    $("#report_list_body").html("");

    $.getJSON('/report',
        function (data, textStatus, jqXHR) {  // success callback
            console.info(data);
            data.data.forEach((report, index)=>{
                $("#report_list_body").append(
                    $('<tr>').append(
                        $('<td>').append(index + 1)
                    ).append(
                        $('<td>').append(report.submitter)
                    ).append(
                        $('<td>').append(report.description)
                    ).append(
                        $('<td>').append(formatTime(report.createdTime))
                    ).append(
                        $('<td>').append(report.pdfReportStatus)
                    ).append(
                        $('<td>').append(report.excelReportStatus)
                    ).append(
                        "<td>" + actionLinks(report.pdfReportStatus, report.excelReportStatus, report.id) + "</td>"
                    )
                );
            });

        },function(e){
            alert('error' + e.error);
        }
    );
}
function formatTime(time) {
    if(!time){
        return "N/A";
    }
    const d = new Date(time);
    return singleDigit(d.getMonth() + 1) + '/'+singleDigit(d.getDate()) + ' ' + singleDigit(d.getHours()) + ':' + singleDigit(d.getMinutes());
}
function singleDigit(dig) {
    return ('0' + dig).slice(-2)
}
function downloadPDF(reqId){
    downloadFile('/report/content/'+reqId+ '/PDF');
}
function downloadExcel(reqId){
    downloadFile('/report/content/'+reqId+ '/EXCEL');
}
function downloadFile(urlToSend) {
    var req = new XMLHttpRequest();
    req.open("GET", urlToSend, true);
    req.responseType = "blob";
    req.onload = function (event) {
        console.info(event);
        if(req.status === 200) {
            var blob = req.response;
            var fileName = req.getResponseHeader("fileName")
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = fileName;
            link.click();
        } else{
            alert('Error in downloading')
        }
    };
    req.send();
}
function showDelete(reqId){
    if(confirm("Are you sure to delete report?")){
        $("#mask").removeClass("invisible");
        $.ajax({
            url : "report/" + reqId,
            type: "DELETE",
            data : {},
            contentType: "application/json",
            dataType: "json",
            success: function(data, textStatus, jqXHR)
            {
                alert("Deleted the entry successfully!")
                $("#mask").addClass("invisible");
                console.info(data);
                loadAll();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + ' : ' + jqXHR.responseJSON.message);
                $("#mask").addClass("invisible");
                console.error(jqXHR);
                console.error(jqXHR.responseJSON.message);
            }
        });
    }
}
function showEdit(reqId) {
    $.ajax({
        url : "report/info/" + reqId,
        type: "GET",
        data: {},
        contentType: "application/json",
        dataType: "json",
        success: function(data, textStatus, jqXHR)
        {
            console.info(data);
            console.log(data.data);
            let newHeader = asJSON(data.data.headers);
            let newData = data.data.data.substr(2, data.data.data.length - 4);
            newData = newData.split("], [");
            for (let i = 0; i < newData.length; i++) {
                newData[i] = "[" + newData[i] + "]";
                newData[i] = asJSON(newData[i]);
            }
            newData = newData.join(", ");
            newData = "[" + newData + "]";
            $('#inputData_edit').val("{\n"
                + " \"description\":\"" + data.data.description + "\",\n"
                + " \"headers\":" + newHeader + ",\n"
                + " \"data\":" + newData + ",\n"
                + " \"submitter\":\"" + data.data.submitter + "\"\n"
                + "}"
            );
            $('#edit_report').text(data.data.sync ? "Update Using Sync API (Default)" : "Update Using Async API (Default)")
            $('#editing_report_model').modal('toggle');
            $("#edit_report").off("click");
            $("#edit_report").on("click", function() {
                edit(reqId, data.data.sync);
            })
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
            console.error(jqXHR);
            console.error(jqXHR.responseJSON.message);
        }
    });
}

function asJSON(data) {
    data = data.substr(1, data.length - 2);
    data = data.split(", ");
    Object.keys(data).forEach(function(id) {
        data[id] = "\"" + data[id] + "\"";
    })
    data = data.join(", ");
    data = "[" + data + "]";
    return data;
}

function edit(reqId, isSync){
    let data = validateInput($("#inputData_edit"));
    if (!data) {
        return false;
    }
    $("#mask").removeClass("invisible");
    $.ajax({
        url : isSync ? "report/" + reqId + "/sync" : "report/" + reqId + "/async",
        type: "PUT",
        data : JSON.stringify(data),
        contentType: "application/json",
        dataType: "json",
        success: function(data, textStatus, jqXHR)
        {
            console.info(data);
            alert("Updated successfully!")
            $("#mask").addClass("invisible");
            $('#editing_report_model').modal('toggle');
            loadAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
            $("#mask").removeClass("invisible");
            console.error(jqXHR);
            console.error(jqXHR.responseJSON.message);
        }
    });
}
function actionLinks(ps, es, id) {
    return (ps === 'COMPLETED'?"<a onclick='downloadPDF(\""+id+"\")' href='#'>Download PDF</a>":"")
        + (es === 'COMPLETED'?"<a onclick='downloadExcel(\""+id+"\")' style='margin-left: 1em' href='#'>Download Excel</a>":"")
        +"<a onclick='showDelete(\""+id+"\")' style='margin-left: 1em' href='#'>Delete</a>"
        +"<a onclick='showEdit(\""+id+"\")' style='margin-left: 1em' href='#'>Edit</a>";
}
function validateInput(element){
    try {
        return JSON.parse(element.val());
    }catch(err) {
        alert("This is not a valid Json.");
        return "";
    }
}

function submit(async) {
    let data = validateInput($("#inputData"));
    if(!data) {
        return false;
    }
    $("#mask").removeClass("invisible");
    $.ajax({
        url : async?"report/async":"report/sync",
        type: "POST",
        data : JSON.stringify(data),
        contentType: "application/json",
        dataType: "json",
        success: function(data, textStatus, jqXHR)
        {
            console.info(data);
            $('#create_report_model').modal('toggle');
            $("#mask").addClass("invisible");
            loadAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
            $("#mask").addClass("invisible");
            console.error(jqXHR);
            console.error(jqXHR.responseJSON.message);
        }
    });
}
$( document ).ready(function() {
    loadAll();
    $("#loadAllBtn").on("click",function () {
        loadAll();
    });
    $("#generateBtn").on("click",function () {
        $('#create_report_model').modal('toggle');
    });
    $("#create_report").on("click",function () {
        submit(false);
    });
    $("#create_report_async").on("click",function () {
        submit(true);
    });
});
