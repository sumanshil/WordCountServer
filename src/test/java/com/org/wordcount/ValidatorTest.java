package com.org.wordcount;

import com.org.runnables.Validator;
import junit.framework.TestCase;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Queue;
import java.util.UUID;

public class ValidatorTest extends TestCase {

    static class MockServer extends Server {
        private boolean isTerminated = false;
        private boolean clientRelease = false;
        private int port = 0;
        public MockServer(int port) throws IOException {
            super(port);
        }

        public void terminate() {
            isTerminated = true;
        }

        public void releaseClientConnection(UUID uuid) {
            clientRelease = true;
        }
    }


    public void testValidatorValidValues() throws IOException {
        Queue<String> mockQueue = Mockito.mock(Queue.class);
        Server mockServer = Mockito.mock(Server.class);
        String input= "111111111";
        UUID uuid = UUID.randomUUID();
        Validator validator = new Validator(mockServer, uuid, input, mockQueue);
        validator.run();
        Mockito.verify(mockServer, Mockito.times(0)).terminate();
        Mockito.verify(mockQueue, Mockito.times(1)).offer(input);

        input = "000000111";
        validator = new Validator(mockServer, uuid, input, mockQueue);
        validator.run();
        Mockito.verify(mockServer, Mockito.times(0)).terminate();
        Mockito.verify(mockQueue, Mockito.times(1)).offer(input);

        input = "00000";
        validator = new Validator(mockServer, uuid, input, mockQueue);
        validator.run();
        Mockito.verify(mockServer, Mockito.times(0)).terminate();
        Mockito.verify(mockQueue, Mockito.times(0)).offer(input);
        Mockito.verify(mockServer, Mockito.times(1)).releaseClientConnection(uuid);

        input = "";
        mockServer = Mockito.mock(Server.class);
        validator = new Validator(mockServer, uuid, input, mockQueue);
        validator.run();
        Mockito.verify(mockServer, Mockito.times(0)).terminate();
        Mockito.verify(mockQueue, Mockito.times(0)).offer(input);
        Mockito.verify(mockServer, Mockito.times(1)).releaseClientConnection(uuid);
    }

    public void testValidatorTerminate() throws IOException {
        Server mockServer = Mockito.mock(Server.class);
        Validator validator = new Validator(mockServer,UUID.randomUUID(), "terminate", null);
        validator.run();
        Mockito.verify(mockServer, Mockito.times(1)).terminate();
    }
}
