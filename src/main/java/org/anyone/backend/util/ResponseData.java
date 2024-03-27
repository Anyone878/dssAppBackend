package org.anyone.backend.util;

public class ResponseData<T> {
    private Integer code;
    private String msg;
    private T data;

    public ResponseData(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseData(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public static ResponseData<?> badRequestBodyResponse() {
        return new ResponseData<>(400, "bad request body");
    }

    public static ResponseData<?> serverFailureResponse() {
        return new ResponseData<>(500, "server failure");
    }

    public static ResponseData<?> userNotFoundResponse() {
        return new ResponseData<>(401, "user not found");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
