package com.abc.luntan.utils.api;

/*
通用返回对象
单例设计模式：每种类型生成一个对象
 */
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;
    
    protected CommonResult() {
    }

    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //成功返回
    public static <T> CommonResult<T> success(T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }
    public static <T> CommonResult<T> success(T data,String message){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(),message,data);
    }

    //失败返回
    public static <T> CommonResult<T> fail(CodeInterface codeInterface){
        return new CommonResult<T>(codeInterface.getCode(),codeInterface.getMessage(),null);
    }
    public static <T> CommonResult<T> fail(String message){
        return new CommonResult<T>(ResultCode.FAILED.getCode(),message,null);
    }
    public static <T> CommonResult<T> fail(){
        return fail(ResultCode.SUCCESS);
    }

    //验证失败
    public static <T> CommonResult<T> validateFail(){
        return fail(ResultCode.VALIDATE_FAILED);
    }
    public static <T> CommonResult<T> validateFail(String message){
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(),message,null);
    }

    //用户未登录
    public static <T> CommonResult<T> unauthorized(T data){
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(),ResultCode.UNAUTHORIZED.getMessage(),data);
    }

    //禁止访问
    public static <T> CommonResult<T> forbidden(T data){
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(),ResultCode.FORBIDDEN.getMessage(),data);
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        return "CommonResult{code = " + code + ", message = " + message + ", data = " + data + "}";
    }
}
