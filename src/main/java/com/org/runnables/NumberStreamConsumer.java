package com.org.runnables;


import com.org.data.Result;

import java.util.Queue;

public class NumberStreamConsumer implements Runnable {

    private Queue<String> queue;
    private volatile boolean stopped;

    public NumberStreamConsumer(Queue<String> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        while(!stopped){
            String object = queue.poll();
            if (object == null) {
                continue;
            } else {
                System.out.println("NumberStreamConsumer Received object : "+ object);
                Result.getInstance().add(object);
            }
        }
    }

    public void stop(){
        this.stopped = true;
    }
}
