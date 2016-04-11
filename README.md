# HTTPServer
HTTPServer is a simple, messy, no-nonsense Java HTTP server coded as an exercise for learning how guts of the internet work. HTTPServer also implements a basic FTP style directory listing feature for remotely browsing the subdirectories of the server's document root. Not intended for use in a production setting or really any setting where reliability or security are major concerns.

## Configuration
Basic server parameters can be changed by editing the constants in the HTTPServer class. The available options are listed below:

- `PORT`: The port number the HTTP server binds to
- `INDEX_FILE`: Redirect requests for the top level directory to the specified relative path. Set to `null` to disable redirecting.

## Compilation and Execution
Compile with the standard Java compiler.  Example syntax: 

    `javac HTTPServer.java`

HTTPServer does not take any command line arguments. Example execution:

    `java HTTPServer`

HTTPServer may require root permissions to bind to port. To run in background, logging output to server.log:

    `nohup sudo java HTTPServer &>server.log &`

While executing, HTTPServer will log any received requests and debug information about the subsequent response to `stdout`.
