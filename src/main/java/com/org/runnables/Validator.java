package com.org.runnables;

import com.org.threadpool.ThreadPool;
import com.org.wordcount.Server;

import java.util.Queue;
import java.util.UUID;

public class Validator implements Runnable {

    private String value;
    private Server server;
    private UUID uuid;
    private String validValue;
    private Queue<String> queue;

    enum INPUT_TYPE {
        INVALID,
        TERMINATE,
        VALID
    }
    public Validator(Server server,
                     UUID uuid,
                     String value,
                     Queue<String> queue) {
        this.uuid = uuid;
        this.value = value;
        this.server = server;
        this.queue = queue;
    }

    @Override
    public void run() {
        removeTrailingNewLineChar();
        INPUT_TYPE validationResult = validate();
        if (validationResult  == INPUT_TYPE.INVALID){
            server.releaseClientConnection(this.uuid);
            return;
        } else if (validationResult == INPUT_TYPE.TERMINATE) {
            server.terminate();
            return;
        }
        if (queue != null) {
            queue.offer(this.validValue);
        }
    }

    private void removeTrailingNewLineChar() {
        this.value=this.value.replace("\r\n","");
    }

    public INPUT_TYPE validate() {
        if (this.value == null && this.value.length() == 0){
            return INPUT_TYPE.INVALID;
        }

        int count = 0;
        boolean isInvalid = false;
        StringBuilder stringBuilder = new StringBuilder(9);
        for ( int i = 0 ; i < this.value.length() ; i++){
            if (this.value.charAt(i) >= '0' && this.value.charAt(i) <= '9' ){
                stringBuilder.append(this.value.charAt(i));
            } else if ((int)this.value.charAt(i) == 0){
                break;
            } else {
                stringBuilder.append(this.value.charAt(i));
                isInvalid = true;
            }
            count++;
            if (count > 9){
                return INPUT_TYPE.INVALID;
            }
        }
        if (count != 9){
            return INPUT_TYPE.INVALID;
        }
        if (isInvalid && "terminate".equals(stringBuilder.toString())) {
            return INPUT_TYPE.TERMINATE;
        }
        if (isInvalid){
            return INPUT_TYPE.INVALID;
        }
        this.validValue = stringBuilder.toString();
        return INPUT_TYPE.VALID;
    }
}
