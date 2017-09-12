package com.org.data;

import com.org.runnables.FileWriter;
import com.org.threadpool.ThreadPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Result {
    private static Result resultContainer = new Result();

    private Result(){

    }

    public static Result getInstance(){
        return resultContainer;
    }


    private static Map<String, String> map = new ConcurrentHashMap<>();

    private AtomicInteger currentUnique = new AtomicInteger(0);
    private AtomicInteger currentDuplicate = new AtomicInteger(0);
    private AtomicInteger totalUnique  = new AtomicInteger(0);

    public void add(String value) {
        if (map.putIfAbsent(value, value) != null) {
            currentDuplicate.incrementAndGet();
        } else {
            currentUnique.incrementAndGet();
            totalUnique.incrementAndGet();
            map.put(value, value);
            ThreadPool.getInstance().getPool().submit(new FileWriter(value));
        }
    }

    public static class ResultObject {
        private int duplicate;
        private int unique;
        private int uniqueTotal;

        public ResultObject(int unique, int duplicate, int uniqueTotal){
            this.unique = unique;
            this.duplicate = duplicate;
            this.uniqueTotal = uniqueTotal;
        }

        public int getDuplicate() {
            return duplicate;
        }

        public int getUnique() {
            return unique;
        }

        public int getUniqueTotal() {
            return uniqueTotal;
        }
    }

    public ResultObject getResultObject() {
        ResultObject retVal =  new ResultObject(currentUnique.get(), currentDuplicate.get(), totalUnique.get());
        currentUnique = new AtomicInteger(0);
        currentDuplicate = new AtomicInteger(0);
        return retVal;
    }
}
