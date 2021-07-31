import java.net.*;
import java.io.*;

public class SDTPClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public SDTPClient(String ip, int port){
        try{
            clientSocket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // Print greeting msg from server
            String res;
            while(!(res=in.readLine()).equals("")){
                System.out.println(res);
            }
        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static boolean sendCommand(SDTPClient client, String msg){
        try{
            if (msg.equals("server help")){
                client.send("HELP");
            } else if (msg.equals("help")){
                System.out.println("<SDTP Client Commands>");
                System.out.println("\thelp: get help commands on the client end");
                System.out.println("\tserver help: get help commands on the server end");
                System.out.println("\tconnect: connect to the SDTP server");
                System.out.println("\tget DOW: get date of week. Must connect to server first");
                System.out.println("\tget time: get current time. Must connect to server first");
                System.out.println("\tget date: get current date. Must connect to server first");
                System.out.println("\tget datetime: get current date and time. Must connect to server first");
                System.out.println("\texit: terminate the connection");
            } else if (msg.equals("connect")){
                client.send("HELLO");
            } else if (msg.equals("get DOW")){
                client.send("DOW");
            } else if (msg.equals("get time")){
                client.send("TIME");
            } else if (msg.equals("get date")){
                client.send("DATE");
            } else if (msg.strip().contains("get datetime")){
                if (msg.length() > "get datetime".length()){
                    try{
                        String format = msg.replace("get datetime ", "");
                        client.send("DATETIME " + format);
                    } catch (Exception e){
                        client.send("DATETIME");
                        e.printStackTrace();
                    }
                } else{
                    client.send("DATETIME");
                }
            } else if (msg.equals("exit") | msg.equals("BYE")){
                client.send("BYE");
                return true;
            } else{
                client.send(msg);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public void send(String msg) throws IOException {
        out.println(msg);
        String res;
        while (!(res=in.readLine()).equals("") && !res.equals('.')){
            System.out.println(res);
        }
    }

    public static void stopConnection(SDTPClient client) throws IOException {
        client.in.close();
        client.out.close();
        client.clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        String ip="127.0.0.1";
        // Default port, but 431 requires sudo execution.
        int port=431;

        try{
            ip = args[0];
            port = Integer.parseInt(args[1]);
        } catch(Exception e){
            e.printStackTrace();
        }

        SDTPClient client = new SDTPClient(ip, port);
        while (true){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line = br.readLine();
            boolean terminate = sendCommand(client, line);
            if (terminate){
                stopConnection(client);
                break;
            }
        }
    }
}
