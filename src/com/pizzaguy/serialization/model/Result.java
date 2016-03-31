package com.pizzaguy.serialization.model;
////model class for results
public class Result {

    //result object, kept as a object to convert later
    private Object result;
    //Position at which the result has been read and stopt reading
    private int length;

    //constructor with data
    public Result(Object result, int length) {
        this.result = result;
        this.length = length;
    }

    //result getter
    public Object getResult() {
        return result;
    }
    
    //length getter
    public int getLength(){
        return length;
    }

}
