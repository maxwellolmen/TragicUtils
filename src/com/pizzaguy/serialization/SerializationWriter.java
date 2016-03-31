
package com.pizzaguy.serialization;

public class SerializationWriter {

    //write a byte at a specific point
    public static int writeBytes(int pointer, byte[] data, byte value) {
        data[pointer++] = value;
        return pointer;
    }

    //write a char at a specific point
    public static int writeBytes(int pointer, byte[] data, char value) {
        data[pointer++] = (byte) value;
        return pointer;
    }

    //write a byte array starting at a specific point
    public static int writeBytes(int pointer, byte[] data, byte[] values) {
        for (int i = 0; i < values.length; i++) {
            pointer = writeBytes(pointer, data, values[i]);
        }
        return pointer;
    }

    //write a short at a specific point
    public static int writeBytes(int pointer, byte[] data, short value) {
        data[pointer++] = (byte) ((value >> 8) & 0xff);
        data[pointer++] = (byte) ((value >> 0) & 0xff);
        return pointer;
    }

    //write a int at a specific point
    public static int writeBytes(int pointer, byte[] data, int value) {
        data[pointer++] = (byte) ((value >> 24) & 0xff);
        data[pointer++] = (byte) ((value >> 16) & 0xff);
        data[pointer++] = (byte) ((value >> 8) & 0xff);
        data[pointer++] = (byte) ((value) & 0xff);
        return pointer;
    }

    //write a long at a specific point
    public static int writeBytes(int pointer, byte[] data, long value) {
        data[pointer++] = (byte) ((value >> 56) & 0xff);
        data[pointer++] = (byte) ((value >> 48) & 0xff);
        data[pointer++] = (byte) ((value >> 40) & 0xff);
        data[pointer++] = (byte) ((value >> 32) & 0xff);
        data[pointer++] = (byte) ((value >> 24) & 0xff);
        data[pointer++] = (byte) ((value >> 16) & 0xff);
        data[pointer++] = (byte) ((value >> 8) & 0xff);
        data[pointer++] = (byte) ((value) & 0xff);
        return pointer;
    }

    //write a float at a specific point
    public static int writeFloat(int pointer, byte[] data, float value) {
        int i = Float.floatToIntBits(value);
        return writeBytes(pointer, data, i);
    }

    //write a double at a specific point
    public static int writeBytes(int pointer, byte[] data, double value) {
        long i = Double.doubleToLongBits(value);
        return writeBytes(pointer, data, i);
    }

    //write a string with the length given before that in a short
    public static int writeBytes(int pointer, byte[] data, String string) {
        pointer = writeBytes(pointer, data, (short) string.getBytes().length);
        return writeBytes(pointer, data, string.getBytes());
    }

    //write a boolean to a byte(maybe in the future boolean array?, 8 booleans in 1 byte)
    public static int writeBytes(int pointer, byte[] data, boolean value) {
        byte bool = (byte) (value ? 255 : 0);
        pointer = writeBytes(pointer, data, bool);
        return pointer;
    }
}
