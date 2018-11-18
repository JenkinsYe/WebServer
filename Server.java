import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private String IP_address;
    private int Port;
    private ServerSocket server_socket;
    
    public Server(String ip, int port) {
    	this.IP_address = ip;
    	this.Port = port;
    }
    
	public void start() {
		System.out.print("Server is running!\n");
		// create the socket
		try {
			server_socket = new ServerSocket(this.Port, 1, InetAddress.getByName(IP_address));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// accept request constantly
		while(true) {
			try {
				// accept a socket, create a thread
				Socket client_socket = server_socket.accept();
				WebThread webThread = new WebThread(client_socket);
				webThread.start();
			} catch (IOException e) {		
				e.printStackTrace();
			} 
		}
	}
	
}
