package escuelaing.arep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private static Map<String,String> llaves = new HashMap<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(45000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        boolean running = true;
        while(running){
            clientSocket = serverSocket.accept();

            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean firstLine = true;
            String request = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if(firstLine){
                    request = inputLine;
                    firstLine = false;
                }
                if (!in.ready()) {break; }
            }

            String path = request.split(" ")[1];
            System.out.println("Este es el path que recibe back: " + path);
            System.out.println(request);

            if(path.startsWith("/setkv")){
                String response = setValueKey(path);
                outputLine= "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: text/html\r\n"
                            + "\r\n"
                            + response;
            }else if(path.startsWith("/getkv")){
                String response = getValueKey(path);
                outputLine= "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + response;
            }else{
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "<meta charset=\"UTF-8\">\n"
                        + "<title>Title of the document</title>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "<h1>Mi propio mensaje</h1>\n"
                        + "</body>\n"
                        + "</html>\n";
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }




    private static String getValueKey(String path) {
        try{
            int start = path.indexOf("={") +2 ;
            int end = path.indexOf("}");
            String llave = path.substring(start,end);

            StringBuilder sb = new StringBuilder();
            sb.append("key: " + llave + " value: " + llaves.get(llave));
            return sb.toString();
        }catch(Exception e){
            return "no existe la llave";
        }
    }

    private static String setValueKey(String path) {
        int start = path.indexOf("={") +2 ;
        int end = path.indexOf("}&");
        String llave = path.substring(start,end);
        int startValue = path.indexOf("e{") +2;
        int endValue = path.indexOf("}e");
        String value = path.substring(startValue,endValue);
        llaves.put(llave,value);
        System.out.println("key: " + llave + "value: " + llaves.values());
        StringBuilder sb = new StringBuilder();
        sb.append("key: " + llave + " value: " + llaves.values() + " status: created");
        return sb.toString();
    }


}
