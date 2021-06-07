package org.cuiyang.assistant.util;

import com.alibaba.fastjson.JSONObject;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;

import java.io.IOException;

public class PBUtils {

    public static PbObject decode(byte[] data, boolean valueBytes) {
        PbObjectSchema schema = new PbObjectSchema(valueBytes);
        PbObject message = schema.newMessage();
        ProtobufIOUtil.mergeFrom(data, message, schema);
        return message;
    }

    public static class PbObject extends JSONObject {
    }

    public static class PbObjectSchema implements Schema<PbObject> {

        private boolean bytes;

        public PbObjectSchema(boolean bytes) {
            this.bytes = bytes;
        }

        @Override
        public String getFieldName(int i) {
            throw new IllegalStateException();
        }

        @Override
        public int getFieldNumber(String s) {
            throw new IllegalStateException();
        }

        @Override
        public boolean isInitialized(PbObject pbObject) {
            throw new IllegalStateException();
        }

        @Override
        public PbObject newMessage() {
            return new PbObject();
        }

        @Override
        public String messageName() {
            throw new IllegalStateException();
        }

        @Override
        public String messageFullName() {
            throw new IllegalStateException();
        }

        @Override
        public Class<? super PbObject> typeClass() {
            throw new IllegalStateException();
        }

        @Override
        public void mergeFrom(Input input, PbObject pbObject) throws IOException {
            while (true) {
                int number = input.readFieldNumber(this);
                if (number == 0) {
                    return;
                } else {
                    if (bytes) {
                        pbObject.put("a" + number, input.readByteArray());
                    } else {
                        pbObject.put("a" + number, input.readString());
                    }
                }
            }
        }

        @Override
        public void writeTo(Output output, PbObject pbObject) {
            throw new IllegalStateException();
        }
    }
}
