import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	// constant variables
	public static final String User = "3160102807";
	public static final String Password = "2807";
	public static final String path = "D:\\document";
	public static final int bufferSize = 2048;
	
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private String method;
    private String resource;
    private String resourcePath;
    private String content;
    
    
	public HttpRequest(Socket socket) throws Exception {
		this.inputStream = socket.getInputStream();
		this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
		
		// Parse Request-Line Information
		String s = bufferedReader.readLine();
		parseRequestLine(s);
		parseContent(bufferedReader);
		this.resourcePath = path + "/" + this.resource;

		outputStream = socket.getOutputStream();
		// Two diffrent response according to the type of request
		if(method.equals("GET")) {
		    response_Get();
		}
		else if(method.equals("POST")) {
			response_Post();
		}
	}
    
	public void parseRequestLine(String s) {
		// Parse method
		System.out.println(s);
        String[] items = s.split(" ");
        method = items[0].trim();
        //System.out.println("Method = \n" + method);
        // Parse resource 
		resource = s.substring(s.indexOf(' ') + 1);
        resource = resource.substring(0, resource.indexOf(' '));
        
        if ("".equals(resource)) {
            resource = "index.html";
        }
        if (resource.startsWith("/")) {
            resource = resource.substring(1);
        }
	}
	
	public void parseContent(BufferedReader bufferedReader) throws Exception {
		String s = bufferedReader.readLine();
		int contentLength = 0;
		// There is an empty line between request header and body
		// Meanwhile, we find the length of content in the header
		while(s != null) {
			if(s.equals("")) {
				break;
			}
			else if(s.startsWith("Content-Length")) {
                String[] strings = s.split(":");
                if (strings.length == 2) {
                    contentLength = Integer.parseInt(strings[1].trim());
                }
			}
			s = bufferedReader.readLine();
		}
		char[] temp = new char[contentLength];
		bufferedReader.read(temp, 0, contentLength);
		content = new String(temp);
		//inputStream.close();
	}
	
	public void response_Get() throws IOException {
		System.out.println("Resourse Path:" + resourcePath);
		File file = new File(resourcePath);
        PrintWriter printer = new PrintWriter(outputStream);
        
        
        if (file.exists()) {
            System.out.println(file.getName() + " start send");
            printer.println("HTTP/1.0 200 OK");
            printer.println("MIME_version:1.0");
            if (file.getName().endsWith(".jpg")) {
                printer.println("Content_Type:image/jpeg");
            } else {
                printer.println("Content_Type:text/html");
            }
            int len = (int) file.length();
            printer.println("Content_Length:" + len);
            //empty line
            printer.println("");
            printer.flush();
            try {
                byte[] bytes = new byte[bufferSize];
                FileInputStream fileInputStream = new FileInputStream(file);
                int ch = fileInputStream.read(bytes);
                while (ch != -1) {
                    outputStream.write(bytes);
                    ch = fileInputStream.read(bytes);
                }
                outputStream.flush();
                printer.close();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            printer.println("HTTP/1.1 404 Not Found");
            printer.println("2");
            printer.println("Content-Length:23");
            printer.println("");
            printer.println("<h1>File Not Found</h1>");
            try {
                printer.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
	}
	
	public void response_Post() throws Exception {
		System.out.println("Resource : " + resource);
		resource = resource.substring(resource.lastIndexOf('/') + 1);
        PrintWriter printWriter = new PrintWriter(outputStream);
        if ("dopost".equals(resource)) {
            Map<String, String> map = parseContentToMap(content);
            String result;
            if (User.equals(map.get("login")) && Password.equals(map.get("pass"))) {
                result = "<html><body>login successfully!</body></html>";

            } 
            else {
                result = "<html><body>login fail!</body></html>";
            }
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html");
            printWriter.println("Content-Length:" + result.length());
            printWriter.println("");
            printWriter.println(result);
        } 
        else {
            printWriter.println("HTTP/1.1 404 File Not Found");
            printWriter.println("Content-Type:text/html");
            printWriter.println("Content-Length:0");
            printWriter.println("");
        }
        try {
            printWriter.flush();
            printWriter.close();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public void close() throws IOException {
		inputStream.close();
	}
	
    private Map<String, String> parseContentToMap(String content) {
        Map<String, String> map = new HashMap<>();
        String[] items = content.split("&");

        for (String item : items) {
            String[] splitItem = item.split("=");
            if (splitItem.length == 2) {
                map.put(splitItem[0], splitItem[1]);
            }
        }
        return map;
    }
}
