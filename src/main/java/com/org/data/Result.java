package com.org.data;

import com.org.runnables.FileWriter;
import com.org.threadpool.ThreadPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Result {
    private static Result resultContainer = new Result();

    private Result(){

    }

    public static Result getInstance(){
        return resultContainer;
    }


    private static Map<String, String> map = new ConcurrentHashMap<>();

    private int currentUnique = 0;
    private int currentDuplicate = 0;
    private int totalUnique = 0;

    public void add(String value) {
        if (map.putIfAbsent(value, value) != null) {
            currentDuplicate++;
        } else {
            currentUnique++;
            totalUnique++;
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

    public ResultObject getResultObject(){
        ResultObject retVal =  new ResultObject(currentUnique, currentDuplicate, totalUnique);
        currentUnique = 0;
        currentDuplicate = 0;
        return retVal;
    }
}
