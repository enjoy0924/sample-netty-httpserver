package com.altas.gateway.utils;

import java.io.*;

public class SerializeHelper {

    public static Serializable deserialize(String string) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(EncryptHelper.decryptBase64Bytes(string));
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Serializable)ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("deserialize session error", e);
        }
    }

    public static String serialize(Serializable obj){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return EncryptHelper.encryptBytesBase64(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("serialize session error", e);
        }
    }
}
