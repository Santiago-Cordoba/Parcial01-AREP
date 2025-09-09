package escuelaing.arep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class FacadeServer {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:45000";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
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
            System.out.println("este es el path que llega a facade: " + path);
            System.out.println(request);

            if(path.startsWith("/index")){
                outputLine = staticFile();
            }else if(path.startsWith("/setkv")){
                outputLine = connectToHttpServer(path);
            }else if(path.startsWith("/getkv")){
                outputLine = connectToHttpServer(path);
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

    private static String staticFile(){
        String html = """
                <!DOCTYPE html>
                <html>
                               
                <head>
                    <title>Parcial Arep</title>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                               
                <body>
                <h1>Form with SET</h1>
                <form action="/hello">
                    <label for="name">Key:</label><br>
                    <input type="text" id="name" name="Llave"><br><br>
                    <label for="name">Value:</label><br>
                    <input type="text" id="value" name="Value"><br><br>
                    <input type="button" value="Submit" onclick="loadGetMsg()">
                </form>
                <div id="getrespmsg"></div>
                               
                <script>
                    function loadGetMsg() {
                        let nameVar = document.getElementById("name").value;
                        let nameValue = document.getElementById("value").value;
                        const xhttp = new XMLHttpRequest();
                        xhttp.onload = function () {
                            document.getElementById("getrespmsg").innerHTML =
                                this.responseText;
                        }
                        xhttp.open("GET", "/setkv?key={" + nameVar + "}" + "&value{" + nameValue + "}" + "e");
                        xhttp.send();
                    }
                </script>
                               
                </body>
                               
                <body>
                <h1>Form with GET</h1>
                <form action="/hello">
                    <label for="name">Key:</label><br>
                    <input type="text" id="key" name="Llave"><br><br>
                    <input type="button" value="Submit" onclick="loadGetMsga()">
                </form>
                               
                <div id="getrespmsgg"></div>
                               
                <script>
                    function loadGetMsga() {
                        let nameVar = document.getElementById("key").value;
                        const xhttp = new XMLHttpRequest();
                        xhttp.onload = function () {
                            document.getElementById("getrespmsgg").innerHTML =
                                this.responseText;
                        }
                        xhttp.open("GET", "/getkv?key={" + nameVar + "}");
                        xhttp.send();
                    }
                </script>
                               
                </body>
                               
                </html>
                               
                """;
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + html;
    }

    private static String connectToHttpServer(String path) throws IOException {
        URL obj = new URL(GET_URL + path.replace("index","/BackServer"));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + response;
        } else {
            System.out.println("GET request not worked");
            return "HTTP/1.1 400 Not found\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n";
        }

    }


}
