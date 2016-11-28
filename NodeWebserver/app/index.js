//Import the HTTP module
var http = require('http');
var HttpDispatcher = require('httpdispatcher');
var dispatcher = new HttpDispatcher();
var url = require('url');


const createUser = require('./createUser');

dispatcher.setStatic('/resources');
dispatcher.setStaticDirname('static');

//Set port we are listening too
const PORT=8080;

function getUserAndPass(request) {
    var parsedUrl = url.parse(request.url, true); // true to get query as object
    var queryAsObject = parsedUrl.query;
    return queryAsObject;
}

//Function for handling requests and send respose
function handleRequest(request, response) {
    try {
        console.log(request.url);
        dispatcher.dispatch(request, response);
    } catch(err) {
        console.log(err); 
    }
}

//Possible httpGet calls
//Descriptions are in my_webserver_api.txt
dispatcher.onGet("/create", function(req, res) {
    var id = getUserAndPass(req);
    if(createUser.checkCred(id)){

    }
    res.writeHead(200, {'Content-Type':'text/plain'});
    res.end('Page for Create');
});

dispatcher.onGet("/login", function(req, res) {
    res.writeHead(200, {'Content-Type':'text/plain'});
    res.end('Page Login');
});

dispatcher.onGet("/updateData", function(req, res) {
    res.writeHead(200, {'Content-Type':'text/plain'});
    res.end('Page update Date');
});

dispatcher.onGet("/getData", function(req, res) {
    res.writeHead(200, {'Content-Type':'text/plain'});
    res.end('Page get data');
});

//Create server
var server = http.createServer(handleRequest);

//Start server
server.listen(PORT, function() {
    //Callback when server is successfully listening.
    console.log("Server listening on: http://localhost:%s", PORT);
});
