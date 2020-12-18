package br.com.luisfga.talkingz.server.wsocket;

import br.com.luisfga.talkingz.commons.constants.Globals;
import br.com.luisfga.talkingz.commons.orchestration.Orchestration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

class UserSessionPartialsBuffer {

    private final Logger logger = LoggerFactory.getLogger(UserSessionPartialsBuffer.class);

    private byte[] jointFrames;

    private ExecutorService cachedThreadPool;

    void appendBytes(ByteBuffer byteBuffer){

        byte[] frame = new byte[byteBuffer.remaining()];
        byteBuffer.get(frame, 0, byteBuffer.remaining());

        if (jointFrames == null) {
            jointFrames = frame;
        } else {
            byte[] destination = new byte[jointFrames.length+frame.length];
            System.arraycopy(jointFrames, 0, destination, 0, jointFrames.length);
            System.arraycopy(frame, 0, destination, jointFrames.length, frame.length);
            jointFrames = destination;
        }

    }

    Object getMessageObject() {
        if (Objects.nonNull(jointFrames)) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(jointFrames);
                ObjectInput in = null;
                in = new ObjectInputStream(bis);

                return in.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    void clear(){
        jointFrames = null;
    }
}
