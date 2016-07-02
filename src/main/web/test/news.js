
var baseURL = "/feedreader/test";
var apiUrlStream = '/api_stream.jsp';
var apiUrlRead = '/api_markread.jsp';
var newsTmpl = {};
var streamId = 0;
var page = 0;
var position = 0;

var topUnread = undefined;

Handlebars.registerHelper("position", function() {
    return ++position;
});

function initNews()
{
    console.log("init news");
    newsTmpl =  Handlebars.compile($("#news_stream_tmpl").html());
    
    window.onscroll = function(obj)
    {
        if ($("#more").visible()) {
            more();
        }       
        
        if (!topUnread) return;
        var o = topUnread.get(0);
        
        if (typeof o.getBoundingClientRect === 'function')
        {
            // Use this native browser method, if available.
            var box = o.getBoundingClientRect();
            if (box.top < 30) 
            {
                topUnread = topUnread.next("p");
                lastTopY =  box.top;         
                markRead(o);
            }
        } 
    };      
}

function apiMarkRead(entries, callback) 
{
    var queryData = {};
    queryData.e = entries;
//    console.log(queryData);
    
    $.get(baseURL + apiUrlRead, queryData, function(data, status) 
    {
        if (callback) callback(data, status);
    });
}

function markAllRead()
{
    var entries = "";
    $(".news").not(".read").each( function(idx, o) 
    {
        $(o).addClass("read");
        $("#unread").text(""+ (--count));
        entries += $(o).attr("id") + ",";
    });
    
    apiMarkRead($(o).attr("id"), function(data, status) {
//            console.log("mark read " + status);
//            console.log(data);    
    });
//    console.log(entries.length)
//    if (entries.length > 0) entries = entries.slice(0, -1);
//
//    apiMarkRead(entries, function(data, status) {
//        console.log("mark all read " + status);
//        console.log(data);
//    });
    
    $("#mark_all_read").hide();
}

function more()
{
    loadStreamEntries(streamId, page++);
}

function renderEntries(data)
{
    console.log("rendering entries " + data.entries.length);
    
    $("#unread").text(data.unread);
    
    $("#stream_entries").append(newsTmpl({"entries" : data.entries}));
     
    if (data.entries.length == 0) {
        $("#more").hide();
        return;
    }
    
    $("#more").show();
    $("#mark_all_read").show();   
    
    if (topUnread == undefined) {
        topUnread = $(".news").first();
        console.log("top set to ");
        console.log(topUnread);
    }
    
    console.log("top  id : " + topUnread.get(0).id);
}

function markRead(obj)
{
    if(typeof obj == "number") {
        obj = $("#" + obj).get(0);
    }
    
//    console.log("marking entry as read " + entryId);
    var count = $("#unread").text();

    $("#unread").text(""+ (--count));
    obj.className = obj.className + " read";
            
    apiMarkRead(obj.id, function(data, status) {
//        console.log("mark read " + status);
//        console.log(data);
    });
}

function loadStreamEntries(streamId)
{
//    console.log("get entries for feed stream " + streamId + ", page " + page);
    this.streamId = streamId;
    var len = $(".news").not(".read").length;
   
//    if (len) console.log("skipping " + len);
    
    var queryData = {};
    queryData.id = streamId;
    queryData.offset = len;
    
    $.getJSON(baseURL + apiUrlStream, queryData, function(data) 
    {
        renderEntries(data);        
    });
}