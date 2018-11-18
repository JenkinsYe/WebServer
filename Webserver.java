
public class Webserver {
    
    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 2807;
        Server webServer = new Server(ip, port);
        webServer.start();
    }
    
}
