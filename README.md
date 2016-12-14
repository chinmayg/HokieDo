HokieDo
=======

Simple To Do List Android Application that allows you to add you todo items and check them off as you finish.

// Old Server implementation - (Android app might not work with the Jetty server)
Used a Java Serlet webserver to download and upload data (todo items)

Synced data with Java Servlet server

// New Server implemenation
Uses Nodejs webserver to download and upload data

Syncs using http post to create/login/update data and http get to retreive ata

##My Webserver API:
If an extension of the server i.e /create or /updateData requires a parameter, you must format it similar to the examples below.

##How to run Jetty Webserver (depercated) - This will not work with android app, no longer being developed:
1. In order to run the webserver, import the webserver project into eclipse and press the Run button in eclipse to start the server.

##How to run NodeJs Webserver:
1. Install latest node.js from https://nodejs.org/en/ 
2. Install mongoDB from https://www.mongodb.com/
2. Navigate to NodeWebserver folder in the terminal and run "node index.js"

## Create user
####/create
```
parameter: 
	Send an HTTP post
	Body:
		POST /login HTTP/1.1
		Host: localhost:8080
		Content-Type: application/x-www-form-urlencoded
		Cache-Control: no-cache
		Postman-Token: be95dc7e-4c6e-397d-c532-d6068e24afe4
		user=user3&pass=abcdef
```
```
response:

	Confirm creating user or Error Creating User
	If there is any error, it will be because the user already exists on the system.

	Sample JSON:
	{"status":OK|ERROR Code}
	201 - OK Login
	400 - Unable to create
```
##Login User
####/login
```
parameter:
	Send an HTTP post
	Body:
		POST /login HTTP/1.1
		Host: localhost:8080
		Content-Type: application/x-www-form-urlencoded
		Cache-Control: no-cache
		Postman-Token: be95dc7e-4c6e-397d-c532-d6068e24afe4
		user=user3&pass=abcdef
```
```
response:
	Confirm user login or Error
	If there is any error, there might be an issue with the usernam or password

	Sample JSON:
	{"status":OK|ERROR Code}
	200 - OK Login
	401 - Failed Auth. of User
```

## Update user data
####/updateData
```
parameter:
	Send an HTTP POST with URL
	Body:
		POST /updateData HTTP/1.1
		Host: localhost:8080
		Content-Type: application/x-www-form-urlencoded
		Cache-Control: no-cache
		Postman-Token: 9f3c8811-a981-bde8-7a95-0004d5664383

		user=user2&list=%5B%22a%22%2C%22b%22%2C%22c%22%5D = {"user":"user2","list":["a","b","c"]}
```
```
reponse:
	Confirm data has been updated or error
	If there is any error, issues with the formating of the JSON

	Sample JSON:
	{"status":OK|ERROR CODE}
	200 - Success
	400 - Failed to update
```

## Get user data
####/getData
```
parameter:
	Send an HTTP get with URL
	http://server_ip:PORT/getData/user/[username]
	Example: http://localhost:8080/getData/user/test1
```
```
reponse:
	Returns an JSON of the tasks or Status if there was no data was found for the user or an error
	If there is any error, issues with the formating of the JSON or if data does not exist

	Sample JSON:
	{"tasks":[tasklist]}

		OR

	{"status":ERROR CODE}
	400 - Failed to retrieve task list
```
