package com.org.wordcount;


import com.org.physical.FileStorage;
import com.org.runnables.ClientConnectionHandler;
import com.org.runnables.NumberStreamConsumer;
import com.org.runnables.TimerTask;
import com.org.threadpool.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class Server  extends Thread {

    private static Thread thread = null;
    private static ThreadPool threadPool = null;
    public volatile boolean stop = false;
    public static int numberOfConsumer = 20;
    private ServerSocket serverSocket;
    private static List<NumberStreamConsumer> numberStreamConsumers = new ArrayList<>();
    private static Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
    /**
     * Client socket connections will be assigned a UUID. This is used for keeping track of client connections.
     */
    private Map<UUID, ClientConnectionHandler> currentHandlerMap = new ConcurrentHashMap<>();

    private int numberOfOpenConnections = 5;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(40000);
    }

    public void run() {
        while(!stop) {
            try {
                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");
                System.out.println("Permits available "+ numberOfOpenConnections);
                synchronized (this) {
                    while (numberOfOpenConnections <= 0) {
                        System.out.println("No more available connections. Wait..");
                        try {
                            serverSocket.close();
                            wait();
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
                if (serverSocket.isClosed()){
                    serverSocket = new ServerSocket(4000);
                    serverSocket.setSoTimeout(40000);
                }
                Socket server = serverSocket.accept();

                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                UUID uuid = UUID.randomUUID();
                Runnable clientConnectionHandler = new ClientConnectionHandler(server, this,  uuid, concurrentQueue);
                currentHandlerMap.put(uuid, (ClientConnectionHandler) clientConnectionHandler);
                threadPool.getPool().submit(clientConnectionHandler);
                numberOfOpenConnections--;
            } catch(SocketTimeoutException s) {
                continue;
            } catch(IOException e) {
                e.printStackTrace();
                if (stop){
                    return;
                }
            }
        }
    }
    public static void main(String[] args) {
        if (args[0] != null) {
            try {
                numberOfConsumer = Integer.parseInt(args[0]);
            }catch (Exception e){
            }
        }
        startThreadPools();
        FileStorage.getInstance().initialize();
        startServer();
    }

    private static void startThreadPools() {
        threadPool = ThreadPool.getInstance();
        ThreadPool
                .getInstance()
                .getTimerTaskPool()
                .scheduleAtFixedRate(new TimerTask(), 10, 10, TimeUnit.SECONDS);
        for ( int i = 0 ; i < numberOfConsumer; i++ ) {
            NumberStreamConsumer numberStreamConsumer = new NumberStreamConsumer(concurrentQueue);
            numberStreamConsumers.add(numberStreamConsumer);
            ThreadPool.getInstance().getPool().submit(numberStreamConsumer);
        }
    }

    private static void startServer() {
        try {
            thread = new Server(4000);
            thread.start();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate()  {
        System.out.println("Terminate the server");
        for (Map.Entry<UUID, ClientConnectionHandler> entry : currentHandlerMap.entrySet()) {
            entry.getValue().stop();
        }
        this.stop = true;
        thread.interrupt();
        for (NumberStreamConsumer numberStreamConsumer : numberStreamConsumers){
            numberStreamConsumer.stop();
        }
        ThreadPool.getInstance().shutDown();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            System.err.println("Failed to close server socket");
            e.printStackTrace();
        }
    }

    public void releaseClientConnection(UUID uuid) {
        currentHandlerMap.get(uuid).stop();
        currentHandlerMap.remove(uuid);
        synchronized (this) {
            numberOfOpenConnections++;
            notify();
        }
    }
}
