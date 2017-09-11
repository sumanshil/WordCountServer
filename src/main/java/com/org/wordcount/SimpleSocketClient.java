package com.org.wordcount;

import java.io.*;
import java.net.*;

public class SimpleSocketClient
{

    public static void main(String[] args)
    {
        new SimpleSocketClient();
    }

    public SimpleSocketClient()
    {
        String testServerName = "localhost";
        int port = 4000;
        try
        {
            Socket socket = openSocket(testServerName, port);
            writeToAndReadFromSocket(socket);
            socket.close();
        } catch (Exception e) {
        }
        System.out.println("Client connection closed");
    }

    private void writeToAndReadFromSocket(Socket socket) throws Exception
    {
        try
        {
            String name= null;
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Please enter input a nine digit number, (type \"terminate\" to close the application): ");
                name = reader.readLine();
                BufferedWriter bufferedWriter
                        = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bufferedWriter.write(name);
                bufferedWriter.flush();
                socket.getOutputStream().flush();
            }
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * Open a socket connection to the given server on the given port.
     * This method currently sets the socket timeout value to 10 seconds.
     */
    private Socket openSocket(String server, int port) throws Exception
    {
        Socket socket;

        // create a socket with a timeout
        try
        {
            InetAddress inteAddress = InetAddress.getByName(server);
            SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);

            // create a socket
            socket = new Socket();

            // this method will block no more than timeout ms.
            int timeoutInMs = 10*1000;   // 10 seconds
            socket.connect(socketAddress, timeoutInMs);

            return socket;
        }
        catch (SocketTimeoutException ste)
        {
            System.err.println("Timed out waiting for the socket.");
            ste.printStackTrace();
            throw ste;
        }
    }

}
