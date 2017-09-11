package com.org.runnables;

import com.org.data.Result;

public class TimerTask implements Runnable {
    @Override
    public void run() {
        try {
            Result.ResultObject result = Result.getInstance().getResultObject();
            System.out.println("Received " + result.getUnique() + " unique numbers, " + result.getDuplicate() + " duplicates. Unique total: " + result.getUniqueTotal());
        } catch (Exception e){
            System.out.println("Exception encountered while printing result");
        }
    }
}
