HokieDo
=======

Simple To Do List Android Application that allows you to add you todo items and check them off as you finish.

// Old Server implementation
Used a Java Serlet webserver to download and upload data (todo items)

Synced data with Java Servlet server

// New Server implemenation
Uses Nodejs webserver to download and upload data

Syncs using httpGet

My Webserver API:
If an extension of the server i.e /create or /updateData requires a parameter, you must send a information formated as a JSON,
and converted to  a string before you send it through the HTTP Get Request

How to run Jetty Webserver:
In order to run the webserver, import the webserver project into eclipse and press the Run button in eclipse to start the server.

How to run NodeJs Webserver:
Navigate to NodeWebserver folder in the terminal and run "node index.js"

/create
/login
/updateData
/getData

/create -
parameter: 
	String with JSON data that holds the username and the password
	{"user":username, "pwd":pwd}

response:

	Confirm creating user or Error Creating User
	If there is any error, it will be because the user already exists on the system.

	Sample JSON:
	{"status":OK|ERROR}
	Sample JSON:
	{"status":OK|ERROR Code}
	200 - OK Login
	401 - Failed Auth. of User

/login -
parameter:
	String with JSON data that holds the username and the password
	Sample JSON:
	{"user":username, "pwd":pwd}

response:
	Confirm user login or Error
	If there is any error, there might be an issue with the usernam or password

	Sample JSON:
	{"status":OK|ERROR Code}
	200 - OK Login
	400- Failed Auth. of User


/updateData
parameter:
	String with JSON current data for the current user with the username
	{"user":username,"tasks":[tasklist]}

reponse:
	Confirm data has been updated or error
	If there is any error, issues with the formating of the JSON

	Sample JSON:
	{"status":OK|ERROR}


/getData

parameter:
	String with current user name
	Sample JSON:
	{"user":username}

reponse:
	Returns an JSON of the tasks or Status if there was no data was found for the user or an error
	If there is any error, issues with the formating of the JSON or if data does not exist

	Sample JSON:
	{"tasks":[tasklist]}

		OR

	{"status":IF FILE DOESNT EXIST|ERROR}

