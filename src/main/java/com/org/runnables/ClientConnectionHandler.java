package com.org.runnables;

import com.org.threadpool.ThreadPool;
import com.org.wordcount.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.UUID;

public class ClientConnectionHandler implements Runnable {
    private Socket socket;
    private UUID uuid;
    private Server server;
    private Queue<String> queue;

    public  ClientConnectionHandler(Socket socket,
                                    Server server,
                                    UUID uuid,
                                    Queue<String> queue){
        this.socket = socket;
        this.uuid = uuid;
        this.server = server;
        this.queue = queue;
    }

    @Override
    public void run() {
            BufferedReader in = null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String input;
                char[] cbuf = new char[1024];
                int bufRead;
                while ((bufRead = in.read(cbuf, 0, cbuf.length)) > 0) {
                    input =  new String(cbuf, 0, bufRead);
                    Validator validator
                            = new Validator(server, uuid, input, queue);
                    ThreadPool.getInstance().getPool().submit(validator);
                    cbuf = new char[1024];
                }
            } catch (Exception e) {
                System.out.println("Releasing socket connection "+socket);
                server.releaseClientConnection(uuid);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
    }

    public void stop()  {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
