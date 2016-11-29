//Import the HTTP module
var http = require('http');
var HttpDispatcher = require('httpdispatcher');
var dispatcher = new HttpDispatcher();
var url = require('url');
var mongodb = require('mongodb');
var MongoClient = mongodb.MongoClient;
var db_url = 'mongodb://localhost:27017/todo';

dispatcher.setStatic('/resources');
dispatcher.setStaticDirname('static');

//Set port we are listening too
const PORT=8080;

function getUserAndPass(request) {
    var parsedUrl = url.parse(request.url, true); // true to get query as object
    var queryAsObject = parsedUrl.query;
    return queryAsObject;
}

function createUser(id, callback) {
    console.log(id);
    // Use connect method to connect to the Server
    MongoClient.connect(db_url, function (err, db) {
        if (err) {
            console.log('Unable to connect to the mongoDB server. Error:', err);
        } else {
            console.log('Connection established to', db_url);
            // Get the documents collection
            var collection = db.collection('users');

            // Create user
            var user = {_id:id.user, name:id.user , pass:id.pass, list:[]};
        
            // Insert user
            collection.insert(user, {w:1}, function (err, result) {
                if(err)
                    return callback(false);
                else
                    return callback(true);
            });
            //Close connection
            db.close()
        }
    });
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
    res.writeHead(200, {'Content-Type':'text/plain'});
    if(createUser(id, function(response){
        if(response) {
            res.end('Success Create');
        } else {
            res.end('Error Create');
        }
    }));
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
