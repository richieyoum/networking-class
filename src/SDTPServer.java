import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SDTPServer {

    /**
     * Starts TCP connection and listens to the port
     * creates server socket and continuously initiate handler objects that handles multiple clients.
     *
     * @param port port to bind to
     */
    public void start(String port) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles client requests in a thread
     */
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        // session indicator
        private boolean session = false;

        /**
         * Constructor for ClientHandler
         *
         * @param socket client socket to handle
         */
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * Handles server commands
         */
        public void run() {
            try {
                // out writer to the client
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                // input reader from client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // send greeting msg
                out.println("HELLO CCCS 431 SDTP Server written by Richie Youm READY");
                out.println("Hit enter to STOP.\n");
                String msg = "";
                while (!msg.equals("BYE")) {
                    // read request from client
                    msg = in.readLine();
                    // capture current datetime
                    LocalDateTime now = LocalDateTime.now();
                    // connection initiation request
                    if (msg.equals("HELLO")) {
                        // print error if already connected
                        if (session) {
                            out.println("ERROR Already in session.\n");
                        }
                        // otherwise, indicate connection success and client address
                        else {
                            out.println("ALOHA " + clientSocket.getRemoteSocketAddress() + "\n");
                            // client is now connected to session
                            session = true;
                        }
                    }
                    // help command
                    else if (msg.equals("HELP")) {
                        out.println("<SDTP Server Commands>");
                        out.println("\tHELP: Get description of available commands");
                        out.println("\tDOW: get date of week. Must initiate session with HELLO command first");
                        out.println("\tTIME: get current time. Must initiate session with HELLO command first");
                        out.println("\tDATE: get current date. Must initiate session with HELLO command first");
                        out.println("\tDATETIME: get current date and time. You can provide custom format with a " +
                                "whitespace after the command. Must initiate session with HELLO command first");
                        out.println("\tBYE (<Enter>): terminate the connection");
                        out.println(".\n");
                    }
                    // date of week command
                    else if (msg.equals("DOW")) {
                        // must be connected to session first
                        if (!session) {
                            out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                        } else {
                            // first 3 letter of the DOW only
                            out.println(now.getDayOfWeek().toString().substring(0, 3) + "\n");
                        }
                    }
                    // time command
                    else if (msg.equals("TIME")) {
                        // must be connected to session first
                        if (!session) {
                            out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                        } else {
                            out.println(now.toLocalTime() + "\n");
                        }
                    }
                    // date command
                    else if (msg.equals("DATE")) {
                        // must be connected to session first
                        if (!session) {
                            out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                        } else {
                            out.println(now.toLocalDate() + "\n");
                        }
                    }
                    // datetime command
                    else if (msg.contains("DATETIME")) {
                        // must be connected to session first
                        if (!session) {
                            out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                        } else {
                            // if there's any additional command that follows DATETIME, use it as the date format
                            if (msg.strip().length() > "DATETIME".length()) {
                                String format = msg.split(" ", 2)[1];
                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                                    String formattedDate = now.format(formatter);
                                    out.println(formattedDate + "\n");
                                } catch (Exception e) {
                                    out.println(now + "\n");
                                }
                            } else {
                                out.println(now + "\n");
                            }
                        }
                    }
                    // exit (BYE) command
                    else if (msg.equals("BYE") || msg.equals("")) {
                        out.println("BYE\n");
                        // terminate connection
                        stopConnection();
                    }
                    // in the case of invalid commands, show error and terminate connection
                    else {
                        out.println("Unknown command: " + msg + "\n");
                        // terminate connection
                        stopConnection();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Terminates connection with the client
         */
        public void stopConnection() {
            try {
                session = false;
                in.close();
                out.close();
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
            // Default port. Since port 431 requires sudo execution, using 4444 instead.
            String port = "4444";
            // If port is provided in the arg, use it instead.
            try {
                port = args[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            // initialize the server
            SDTPServer server = new SDTPServer();
            // start the server and bind to the port
            server.start(port);
        }
    }
}