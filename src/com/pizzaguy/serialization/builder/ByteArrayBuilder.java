
package com.pizzaguy.serialization.builder;

import java.util.ArrayList;
import java.util.List;

import com.pizzaguy.serialization.SerializationReader;

public class ByteArrayBuilder {

    // the byte array
    private byte[] data;

    // constructor with starting array
    public ByteArrayBuilder(byte[] data) {
        this.data = data;
    }

    // add bytes to the array
    public ByteArrayBuilder add(byte[] data) {
        // if not null add data
        if (data != null)
            this.data = combine(this.data, data);
        // return this so this combining can be done inline
        return this;
    }

    // return final byte array
    public byte[] build() {
        return data;
    }

    // combine 2 byte arrays
    public static byte[] combine(byte[] data1, byte[] data2) {
        // calculate length
        int length = data1.length + data2.length;
        // create new byte array with the calculated space
        byte[] data = new byte[length];
        // add array 1 first
        for (int i = 0; i < data1.length; i++) {
            data[i] = data1[i];
        }
        // add array 2 after
        for (int i = 0; i < data2.length; i++) {
            data[data1.length + i] = data2[i];
        }
        // return the data
        return data;
    }

    // split an byte array with an delimiter
    public static byte[][] split(byte[] data, byte[] delimiter) {
        // create the 2d array
        byte[][] result = null;
        // list for pointer where the array should be split
        List<Integer> pointers = new ArrayList<Integer>();
        // go through the data
        for (int i = 0; i < data.length - (delimiter.length - 1); i++) {
            byte[] d = SerializationReader.readBytes(i, data, delimiter.length);
            // if dthe delimiter is found add the pointer
            if (equals(d, delimiter)) {
                pointers.add(i);
            }
        }
        // set up the array with the first demension the size of the amount of
        // pointers
        result = new byte[pointers.size()][];
        // go throught the pointers
        for (int i = 0; i < pointers.size(); i++) {
            // read the subbyterray and add set it to the correct array
            if (i == pointers.size() - 1) {
                result[i] = subByteArray(data, pointers.get(i), data.length);
            } else {
                result[i] = subByteArray(data, pointers.get(i), pointers.get(i + 1));
            }
        }
        // return the 2d array
        return result;
    }

    public static byte[][] split(byte[] data, int pos) {
        byte[][] result = new byte[2][];

        result[0] = new byte[pos];
        for (int i = 0; i < pos; i++) {
            result[0][i] = data[i];
        }
        result[1] = new byte[data.length - pos];
        for(int i = pos; i < data.length;i++){
            result[1][i-pos] = data[i];
        }
        return result;
    }

    // check if 2 byte arrays are equal
    public static boolean equals(byte[] data1, byte[] data2) {
        // check if lengths are equal
        if (data1.length != data2.length)
            return false;
        // check if every byte in the arrays are equal
        for (int i = 0; i < data1.length; i++) {
            if (data1[i] != data2[i])
                return false;
        }
        return true;
    }

    // read a sub byte array
    public static byte[] subByteArray(byte[] src, int p1, int p2) {
        // check if the points are a correct positioned, else return null
        if (p2 <= p1)
            return null;
        // calculate length
        int length = p2 - p1;
        // create array with length
        byte[] result = new byte[length];
        // read the data from the src and write it to the array
        for (int i = 0; i < length; i++) {
            result[i] = src[i + p1];
        }
        // return the result
        return result;
    }
}