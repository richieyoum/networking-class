import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SDTPServer {
    private ServerSocket serverSocket;

    public void start(int port){
        try{
            serverSocket = new ServerSocket(port);
            while (true){
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread{
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private boolean session = false;

        public ClientHandler(Socket socket){
            this.clientSocket = socket;
        }

        public void run(){
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out.println("HELLO CCCS 431 SDTP Server written by Richie Youm READY");
                out.println("Hit enter to STOP.\n");
                String msg = "";
                while (!msg.equals("BYE")) {
                    msg = in.readLine();
                    LocalDateTime now = LocalDateTime.now();
                    if (msg.equals("HELLO")) {
                        if (session){
                            out.println("ERROR Already in session.\n");
                        } else{
                            out.println("ALOHA " + clientSocket.getRemoteSocketAddress() + "\n");
                            session = true;
                        }
                    } else if (msg.equals("HELP")){
                        out.println("<SDTP Server Commands>");
                        out.println("\tHELP: Get description of available commands");
                        out.println("\tDOW: get date of week. Must initiate session with HELLO command first");
                        out.println("\tTIME: get current time. Must initiate session with HELLO command first");
                        out.println("\tDATE: get current date. Must initiate session with HELLO command first");
                        out.println("\tDATETIME: get current date and time. You can provide custom format with a " +
                                "whitespace after the command. Must initiate session with HELLO command first");
                        out.println("\tBYE (<Enter>): terminate the connection");
                        out.println(".\n");
                    } else if (msg.equals("DOW")) {
                        if (!session){
                            out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                        } else{
                            out.println(now.getDayOfWeek().toString().substring(0, 3) + "\n");
                        }
                    } else if (msg.equals("TIME")){
                        if (!session){
                            out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                        } else {
                            out.println(now.toLocalTime() + "\n");
                        }
                    } else if (msg.equals("DATE")){
                        if (!session){
                            out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                        } else {
                            out.println(now.toLocalDate() + "\n");
                        }
                    } else if (msg.contains("DATETIME")){
                      if (!session){
                          out.println("ERROR Not in session. Run HELLO to connect to a session.\n");
                      } else{
                        if (msg.strip().length() > 8){
                            // Treat everything within the line that come after "DATETIME" command as the format.
                            String format = msg.split(" ", 2)[1];
                            try{
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                                String formattedDate = now.format(formatter);
                                out.println(formattedDate + "\n");
                            } catch (Exception e){
                                out.println(now + "\n");
                            }
                        } else{
                            out.println(now + "\n");
                        }
                      }
                    } else if (msg.equals("BYE") || msg.length()==0) {
                        out.println("BYE\n");
                        stopConnection();
                    } else {
                        out.println("Unknown command: " + msg + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stopConnection() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
        }

        public static void main(String[] args){
            String ip="127.0.0.1";
            // Default port, but 431 requires sudo execution.
            int port=431;
            try{
                ip = args[0];
                port = Integer.parseInt(args[1]);
            } catch(Exception e){
                e.printStackTrace();
            }
            SDTPServer server = new SDTPServer();
            server.start(port);
        }

    }
}