package com.bikesystem.dto;

public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;

    public ApiResponse() {}

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> error(int code, String msg) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
