import java.net.*;
import java.io.*;

public class SDTPClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /** Constructor for SDTPClient
     *
     * @param ip ip address to connect to
     * @param port port to listen to
     */
    public SDTPClient(String ip, String port) {
        try {
            clientSocket = new Socket(ip, Integer.parseInt(port));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // Print greeting msg from server
            String res;
            while (!(res = in.readLine()).equals("")) {
                System.out.println(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Converts client command to corresponding server command
     * The converted command is further handled in switch statement in main method
     * @param msg client command
     * @return
     */
    public static String convertToCommand(String msg) {
        if (msg.equals("server help") || msg.equals("HELP")) {
            return "HELP";
        } else if (msg.equals("help")) {
            System.out.println("<SDTP Client Commands>");
            System.out.println("\thelp: get help commands on the client end");
            System.out.println("\tserver help: get help commands on the server end. Note, you can't directly use" +
                    "these server commands here! You need to execute it in something like a telnet connection.");
            System.out.println("\tconnect: connect to the SDTP server");
            System.out.println("\tget DOW: get date of week. Must connect to server first");
            System.out.println("\tget time: get current time. Must connect to server first");
            System.out.println("\tget date: get current date. Must connect to server first");
            System.out.println("\tget datetime: get current date and time. Must connect to server first");
            System.out.println("\texit: terminate the connection");
            System.out.println(".");
            return ".";
        } else if (msg.equals("connect")) {
            return "HELLO";
        } else if (msg.equals("get DOW")) {
            return "DOW";
        } else if (msg.equals("get time")) {
            return "TIME";
        } else if (msg.equals("get date")) {
            return "DATE";
        } else if (msg.strip().contains("get datetime")) {
            if (msg.length() > "get datetime".length()) {
                try {
                    String format = msg.replace("get datetime ", "");
                    return "DATETIME " + format;
                } catch (Exception e) {
                    return "DATETIME";
                }
            } else {
                return "DATETIME";
            }
        } else if (msg.equals("exit")) {
            return "BYE";
        } else {
            return msg;
        }
    }

    /** Send command to the server and print responses from the server
     *
     * @param msg
     * @throws IOException
     */
    public void send(String msg) throws IOException {
        out.println(msg);
        String res;
        while (!(res = in.readLine()).equals("")) {
            System.out.println(res);
        }
    }

    /** Terminate connection
     * Terminate BufferedReader, PrintWriter and socket instances
     * @param client
     * @throws IOException
     */
    public static void stopConnection(SDTPClient client) throws IOException {
        client.in.close();
        client.out.close();
        client.clientSocket.close();
    }

    public static void main(String[] args) {
        // default IP and port
        String ip = "127.0.0.1";
        // Default port. Since port 431 requires sudo execution, using 4444 instead.
        String port = "4444";
        SDTPClient client = null;

        // continuously accept user input until termination command
        try {
            label:
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String msg = br.readLine();
                String command = convertToCommand(msg);

                // Connection request prompts user for ip and port, then initialize the client and send HELLO to server.
                switch (command) {
                    case "HELLO":
                        // close current connection if exists
                        if (client != null) {
                            stopConnection(client);
                        }
                        // prompt user for ip and port of the server
                        try {
                            System.out.println("Enter the ip address");
                            ip = br.readLine();
                            System.out.println("Enter the port");
                            port = br.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        client = new SDTPClient(ip, port);
                        client.send(command);
                        break;
                    // termination request
                    case "BYE":
                        // terminate connection if already connected. Otherwise, just exit.
                        if (client != null) {
                            client.send(command);
                            stopConnection(client);
                        }
                        break label;
                    // help output - do nothing
                    case ".":
                        break;
                    // empty output means termination on server end if already connected
                    case "":
                        if (client != null) {
                            client.send(command);
                            stopConnection(client);
                            break label;
                        }
                        // do nothing when command empty
                        break;
                    // the rest of commands are sent to the server
                    default:
                        if (client != null) {
                            client.send(command);
                        } else {
                            System.out.println("ERROR Not in session. Run connect command to connect to a session.");
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
