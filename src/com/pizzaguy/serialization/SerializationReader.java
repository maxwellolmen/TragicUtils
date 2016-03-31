
package com.pizzaguy.serialization;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SerializationReader {
    //read a certain amount of bytes from an source
    public static byte[] readBytes(int pointer, byte[] src, int length) {
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++)
            data[i] = src[pointer + i];
        return data;
    }

    //read a byte and interpret it as a char
    public static char readChar(int pointer, byte[] src) {
        return (char) src[pointer];
    }

    //read a short(2 bytes)
    public static short readShort(int pointer, byte[] src) {
        byte[] data = Arrays.copyOfRange(src, pointer, pointer + Short.BYTES);
        return ByteBuffer.wrap(data).getShort();
    }

    //read a integer(4 bytes)
    public static int readInt(int pointer, byte[] src) {
        byte[] data = Arrays.copyOfRange(src, pointer, pointer + Integer.BYTES);
        return ByteBuffer.wrap(data).getInt();
    }

    //read a long(4 bytes)
    public static long readLong(int pointer, byte[] src) {
        byte[] data = Arrays.copyOfRange(src, pointer, pointer + Long.BYTES);
        return ByteBuffer.wrap(data).getLong();
    }

    //read a float(8 bytes)
    public static float readFloat(int pointer, byte[] src) {
        byte[] data = Arrays.copyOfRange(src, pointer, pointer + Float.BYTES);
        return ByteBuffer.wrap(data).getFloat();
    }

    //read a double
    public static double readDouble(int pointer, byte[] src) {
        byte[] data = Arrays.copyOfRange(src, pointer, pointer + Double.BYTES);
        return ByteBuffer.wrap(data).getDouble();
    }

    //read a string with a length specified before it in a short
    public static String readString(int pointer, byte[] src) {
        short length = readShort(pointer, src);
        byte[] data = readBytes(pointer + 2, src, length);
        return new String(data);
    }

    //read a boolean from 1 byte
    public static boolean readBoolean(int pointer, byte[] src) {
        return (src[pointer] & 0xff) >= 1;
    }

}
