HokieDo
=======

Simple To Do List Android Application that allows you to add you todo items and check them off as you finish.

// Old Server implementation - (Android app might not work with the Jetty server)
Used a Java Serlet webserver to download and upload data (todo items)

Synced data with Java Servlet server

// New Server implemenation
Uses Nodejs webserver to download and upload data

Syncs using httpGet

##My Webserver API:
If an extension of the server i.e /create or /updateData requires a parameter, you must send a information formated as a JSON,
and converted to  a string before you send it through the HTTP Get Request

##How to run Jetty Webserver:
1. In order to run the webserver, import the webserver project into eclipse and press the Run button in eclipse to start the server.

##How to run NodeJs Webserver:
1. Install latest node.js from https://nodejs.org/en/ 
2. Navigate to NodeWebserver folder in the terminal and run "node index.js"

## Create user
####/create
```
parameter: 
	String with JSON data that holds the username and the password
	{"user":username, "pwd":pwd}
	Example: http://localhost:8080/create?user=test1&pass=test
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
	String with JSON data that holds the username and the password
	Sample JSON:
	{"user":username, "pwd":pwd}
	Example: http://localhost:8080/login?user=test1&pass=test
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
	String with JSON current data for the current user with the username
	{"user":username,"tasks":[tasklist]}
	Example: http://localhost:8080/updateData?user=test1&list=[1,2,3]
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
	String with current user name
	Sample JSON:
	{"user":username}
	Example: http://localhost:8080/getData?user=test1
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
