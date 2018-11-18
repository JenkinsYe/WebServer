import java.net.Socket;

public class WebThread extends Thread{
    private Socket socket;
    
    public WebThread(Socket s) {
    	this.socket = s;
    }
    
    public void run() {
    	try {
			HttpRequest request = new HttpRequest(this.socket);
			request.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
}
