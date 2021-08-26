package com.sun.network.errorhandler;

import android.net.ParseException;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description: 异常
 */
public class ExceptionHandler {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ResponseThrowable handleException(Throwable throwable) {
        ResponseThrowable ex;
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            ex = new ResponseThrowable(throwable, ERROR.HTTP_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (throwable instanceof ServerException) {
            ServerException resultException = (ServerException) throwable;
            ex = new ResponseThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (throwable instanceof JsonParseException
                || throwable instanceof JSONException
                || throwable instanceof ParseException) {
            ex = new ResponseThrowable(throwable, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        } else if (throwable instanceof ConnectException) {
            ex = new ResponseThrowable(throwable, ERROR.NETWORD_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (throwable instanceof javax.net.ssl.SSLException) {
            ex = new ResponseThrowable(throwable, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (throwable instanceof ConnectTimeoutException) {
            ex = new ResponseThrowable(throwable, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (throwable instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(throwable, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else {
            ex = new ResponseThrowable(throwable, ERROR.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }
    }

    public static class ResponseThrowable extends Exception {
        public int code;
        public String message;

        public ResponseThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }
    }

    public static class ServerException extends RuntimeException {
        public int code;
        public String message;
    }

    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }

}
