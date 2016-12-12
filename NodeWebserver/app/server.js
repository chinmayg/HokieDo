//Import the Mongo DB
var mongodb = require('mongodb');
var MongoClient = mongodb.MongoClient;
var db_url = 'mongodb://localhost:27017/todo';

//Import Express and BodyParser
var express = require('express');
var app = express();
var bodyParser = require('body-parser');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));

//Set port we are listening too
const PORT=8080;

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
            if(id.user == null || id.pass == null)
                return callback(false);
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

function loginUser(id, callback) {
    console.log(id);

    // Use connect method to connect to the Server
    MongoClient.connect(db_url, function (err, db) {
        if (err) {
            console.log('Unable to connect to the mongoDB server. Error:', err);
        } else {
            console.log('Connection established to', db_url);
            // Get the documents collection
            var collection = db.collection('users');
            if(id.user == null || id.pass == null)
                return callback(false);
            // Find user and compare pass
            var user = {"_id":id.user, "pass":id.pass};
             collection.find(user).toArray(function (err, result) {
                console.log("finding user data");
                if(!err) {
                    var intCount = result.length;
                    if (intCount != 0) {
                        return callback(true);
                    }
                    else {
                        return callback(false);
                    }
                }
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

            // Query for user
            var user = {_id:id.user}; 

            // Update user data
            collection.update(user, {$set: {"list":id.list}}, function (err, result) {
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

            if(id.user == null) {
                return callback(false);
            }   

            // Create user
            var user = {_id:id.user};
            var json;
            var response = {"success":false}
            // Find user
            collection.find(user).toArray(function (err, result) {
                console.log("finding user data");
                if(!err) {
                    var intCount = result.length;
                    if (intCount > 0) {
                        for (var i = 0; i < intCount; i++) {
                            console.log(result);
                            json = result[i].list 
                        }
                        response = {"success":true, json}
                        console.log(json);
                    }
                }
                if(response.success == false) {
                    return callback(response);
                }
                else {
                     response = {"success":true, "list":json}
                     return callback(response);
                }
            });
            //Close connection
            db.close()
        }
    });   
}

function sendHTTPCodeResponse(err_code,response,res) {
    if(response) {
        res.status(err_code.success).json({'status':err_code.success});
    } else {
        res.status(err_code.error).json({'status':err_code.error});
    }
}

//Possible httpGet calls
//Descriptions are in my_webserver_api.txt
app.post("/create", function(req, res) {
    var err_code = {"success":201, "error":400};
    if(createUser(req.body, function(response){
        sendHTTPCodeResponse(err_code, response, res);
    }));
});

app.post("/login", function(req, res) {
    var err_code = {"success":200, "error":401};
    if(loginUser(req.body, function(response){
        sendHTTPCodeResponse(err_code, response, res);
    }));
});

app.post("/updateData", function(req, res) {
    var err_code = {"success":200, "error":400};
    if(updateData(req.body, function(response){
        sendHTTPCodeResponse(err_code, response, res);
    }));
});

app.get("/getData/user/:user", function(req, res) {
    var err_code = {"success":200, "error":400};
    if(findUserData(req.params, function(response){
        if(response) {
            res.status(err_code.success).json({"list":reponse.list});
        } else {
            res.status(err_code.success).json({'status':err_code.error});
        }
    }));
});

//Start server
app.listen(PORT, function() {
    //Callback when server is successfully listening.
    console.log("Server listening on: http://localhost:%s", PORT);
});
