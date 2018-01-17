package uk.ac.reading.sis05kol.mooc;

/**
 * Created by ALDRICH FERNANDES on 19/04/2016.
 */

        import java.io.BufferedReader;
        import java.io.DataOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.PrintWriter;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.net.SocketTimeoutException;
        import java.util.ArrayList;

public class SimpleHTTPServer2 {

    private static String RELPATH = "serverFiles";

    public static byte[] getFile(File f) {
        final int SIZE = 1024; // server files blackboard code form week 3 spring term//
        if (f == null)
            return null;
        if (!f.isFile()){
            System.out.println("<getFile> WARNING: file not found: " + f.getAbsolutePath());
            return null;
        } else {
            System.out.println("<getFile> OK: " + f.getAbsolutePath());
            System.out.println("<getFile> file len: " + f.length());
        }

        long len = f.length();
        int fileLen = (int) len;
        //TODO: need to fix this for large files.

        byte buffer[] = new byte[fileLen];
        try {
            byte b;
            FileInputStream iStream = new FileInputStream(f);
            for(int i=0; i<fileLen; i++){
                b = (byte) iStream.read();
                buffer[i] = b;
            }
            iStream.close();
        } catch (IOException e) {
            System.err.println("Error: " + e);
            return null;
        }

        return buffer;
    }

    public static void main(String[] args) {
        final int MAXREQ=10;
        ServerSocket server = null;
        int reqCount = 1;
        File theFile=null;
        try {
            server = new ServerSocket(8080);
            server.setSoTimeout(30000);
        } catch (IOException e) {
            System.err.println("Cannot create socket on port 8080.");
            System.exit(1);
        }

        System.out.println("Server started. Listening on port 8080...\n");
        while (reqCount < MAXREQ) {
            Socket connection = null;
            try {
                connection = server.accept();
            } catch (SocketTimeoutException e) {
                System.err.println("Server timeout: " + e);
                break;
            } catch (IOException e) {
                System.err.println("Client accept failed: " + e);
                continue;
            }
            try {
                PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
                DataOutputStream outB = new DataOutputStream(connection.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                boolean doGet = false;
                System.out.println("*** Client request:");
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    if (inputLine.indexOf("GET") != -1) {
                        doGet = true;
                        String fields[] = inputLine.split(" ");
                        String filename = fields[1].substring(1);
                        if (filename.length() == 0)
                            filename = "index.html";
                        System.out.println(">>> GET: " + filename);
                        theFile = new File(RELPATH + File.separator + filename);
                        if (!theFile.isFile()) {
                            System.out.println("WARNING: file not found error: "
                                    + theFile.getAbsolutePath());
                            theFile = null;
                        } else {
                            System.out.println("... will GET file: " + theFile.getAbsolutePath());
                        }
                    }
                    if (inputLine.length() == 0)
                        break;
                }

                System.out.println("*** Server reply:");
                // send header (for both HEAD and GET queries)

                if (theFile != null) {
                    byte data[] = getFile(theFile);
                    System.out.println("\tFile: " + theFile.getName());
                    System.out.println("\tData length: " + data.length + " bytes");
                    outB.write(data);
                    System.out.println("\tData sent");
                } else {

                    String data = "<html>\n<head><title> "
                            + "SimpleHTTPServer 2.0</title></head>\n"
                            + "<body>\n<h2>SimpleHTTPServer 2.0: "
                            + "hello world!<br>Received requests: "
                            + reqCount
                            + ".<br>"
                            + "<"
                            + (5 - reqCount - 1)
                            + "> requests to quit: reload the page.<br>"
                            + "<a href=\"http://www.google.com\"><img src=\"moon0.jpg\" alt=\"Image file not found!\">  <br>"
                            + "</h2>\n" + "</body></html>\n";
                    out.println("HTTP/1.0 200 ok");
                    out.println("Date: " + (new java.util.Date()));
                    out.println("Server: Simple java web server/1.0");
                    out.println("Last-Modified: " + (new java.util.Date()));

                    if (doGet) {
                        out.println("Content-Length: " + data.length());
                    }

                    out.println("Content-Type: text/html");
                    out.println();

                    System.out.println("\tHeader sent");

                    if (doGet) { // only if received a GET query
                        out.println(data);
                        System.out.println("\tData sent");
                    }
                    System.out.println();
                }

                out.close();
                outB.close();
                in.close();
                connection.close();
            } catch (IOException ioe) {
                System.err.println("I/O error: " + ioe);
            }
            reqCount++;
        }

        try {
            server.close();
        } catch (IOException e) {
            System.err.println("Closing server socket failed: " + e);
        }

        System.out.println("QUIT.");
    }
}