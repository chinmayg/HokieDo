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

function getJSONfromURL(request) {
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
            var user = {_id:id.user, pass:id.pass, list:[]};
        
            // Insert user
            collection.insert(user, {w:1}, function (err, result) {
                console.log("creating user");
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

function findUser(id, callback) {
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
            var user = {_id:id.user, pass:id.pass};
        
            // Find user
            collection.find(user, function (err, result) {
                console.log("finding user in database");
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

function updateData(id, callback) {
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
            var user = {_id:id.user};
            console.log(id.list);
            // Update user data
            collection.update(user, {$set: {list: id.list}}, function (err, result) {
                console.log("updating data");
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

function findUserData(id, callback) {
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
            var user = {_id:id.user};
            var strJson;
            var response = {"success":false}
            // Find user
            collection.find(user).toArray(function (err, result) {
                console.log("finding user data");
                if(!err) {
                    var intCount = result.length;
                    if (intCount > 0) {
                        var json;
                        for (var i = 0; i < intCount; i++) {
                            console.log(result);
                            json = result[i].list 
                        }
                        response = {"success":true, strJson}
                        console.log(strJson);
                    }
                }
                if(response.success == false) {
                    return callback(response);
                }
                else {
                     response = {"success":true, strJson}
                     return callback(response);
                }
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

function sendHTTPCodeResponse(response,res) {
    if(response) {
        res.writeHead(200, {'Content-Type':'application/json'});
        var json = JSON.stringify({'status':200})
        res.end(json);
    } else {
        res.writeHead(400, {'Content-Type':'application/json'});
        var json = JSON.stringify({'status':400});
        res.end(json);
    }
}

//Possible httpGet calls
//Descriptions are in my_webserver_api.txt
dispatcher.onGet("/create", function(req, res) {
    var id = getJSONfromURL(req);
    if(createUser(id, function(response){
        sendHTTPCodeResponse(response, res);
    }));
});

dispatcher.onGet("/login", function(req, res) {
    var id = getJSONfromURL(req);
    if(findUser(id, function(response){
        sendHTTPCodeResponse(response, res);
    }));
});

dispatcher.onGet("/updateData", function(req, res) {
    var id = getJSONfromURL(req);
    if(updateData(id, function(response){
        sendHTTPCodeResponse(response, res);
    }));
});

dispatcher.onGet("/getData", function(req, res) {
    var id = getJSONfromURL(req);
    if(findUserData(id, function(response){
        if(response.success) {
            res.writeHead(201, {'Content-Type':'application/json'});
            var json = JSON.stringify({'list':response.list});
            res.end(json);
        } else {
            res.writeHead(401, {'Content-Type':'application/json'});
            var json = JSON.stringify({'status':401});
            res.end(json);
        }
    }));
});

//Create server
var server = http.createServer(handleRequest);

//Start server
server.listen(PORT, function() {
    //Callback when server is successfully listening.
    console.log("Server listening on: http://localhost:%s", PORT);
});
