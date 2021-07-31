import java.net.*;
import java.io.*;

public class SDTPClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

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
            return "";
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
            System.out.println("Unknown command: " + msg);
            return "";
        }
    }

    public void send(String msg) throws IOException {
        out.println(msg);
        String res;
        while (!(res = in.readLine()).equals("")) {
            System.out.println(res);
        }
    }

    public static void stopConnection(SDTPClient client) throws IOException {
        client.in.close();
        client.out.close();
        client.clientSocket.close();
    }

    public static void main(String[] args) {
        // default IP and port
        String ip = "127.0.0.1";
        String port = "431";
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

                    // empty output (ie- help)
                    case "":
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
