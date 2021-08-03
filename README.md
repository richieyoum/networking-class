# CCCS 431 Programming Assignment
Richie Youm (260749847)

Kosai Alchaghouri (260940618)

## Simple Date-Time Information Protocol
Client-server application to request current date, time, datetime, etc. using sockets.

### Steps to run
1. Run SDTPServer file, which would start the server. You may provide custom port in the argument.
2. Afterwards, you can run the SDTPClient file to start the application. You can now enter commands on the console.
3. In the SDTPClient app console, type `help` to show list of commands.
   1. You'll be able to type server commands (`server help` or `HELP`) once you connect to the server.

### Available Commands
Client:
- help: get help commands on the client end
- server help: get help commands on the server end. Note, you can't directly usethese server commands here! You need to execute it in something like a telnet connection.
- connect: connect to the SDTP server
- get DOW: get date of week. Must connect to server first
- get time: get current time. Must connect to server first
- get date: get current date. Must connect to server first
- get datetime: get current date and time. Must connect to server first
- exit: terminate the connection

Server:
- HELP: Get description of available commands
- DOW: get date of week. Must initiate session with HELLO command first
- TIME: get current time. Must initiate session with HELLO command first
- DATE: get current date. Must initiate session with HELLO command first
- DATETIME: get current date and time. You can provide custom format with a whitespace after the command. Must initiate session with HELLO command first
- BYE (or press enter): terminate the connection