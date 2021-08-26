package com.sun.network;

import com.sun.network.base.NetworkApi;
import com.sun.network.beans.AmapBaseResponse;
import com.sun.network.errorhandler.ExceptionHandler;

import java.io.IOException;

import io.reactivex.functions.Function;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description:
 */
public class AmapWeatherApi extends NetworkApi {
    private static volatile AmapWeatherApi sInstance;
    private static final String KEY = "ae6c53e2186f33bbf240a12d80672d1b";

    //單例構造方法私有化
    private AmapWeatherApi() {}

    public static AmapWeatherApi getInstance() {
        if (sInstance == null) {
            synchronized (AmapWeatherApi.class) {
                if (sInstance == null) {
                    sInstance = new AmapWeatherApi();
                }
            }
        }
        return sInstance;
    }

    public static <T> T getService(Class<T> service) {
        return getInstance().getRetrofit(service).create(service);
    }

    @Override
    protected Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //处理自己的拦截
                Request request = chain.request();
                if (request.method().equals("POST")) {
                    RequestBody body = request.body();
                    if (body instanceof FormBody) {
                        // 构造新的请求表单
                        FormBody.Builder builder = new FormBody.Builder();
                        FormBody formBody = (FormBody) body;
                        //将以前的参数添加
                        for (int i = 0; i < formBody.size(); i++) {
                            builder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                        }
                        //追加新的参数
                        builder.add("key", KEY);
                        request = request
                                .newBuilder()
                                .post(builder.build())
                                .build();
                    }
                } else {
                    HttpUrl url = request.url();
                    HttpUrl newUrl = url.newBuilder()
                            .addEncodedQueryParameter("key", KEY)
                            .build();
                    request = request.newBuilder()
                            .url(newUrl).build();
                }
                return chain.proceed(request);
            }
        };
    }

    @Override
    protected <T> Function<T, T> getAppErrorHandler() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                if (response instanceof AmapBaseResponse && ((AmapBaseResponse) response).status != 1) {
                    ExceptionHandler.ServerException exception = new ExceptionHandler.ServerException();
                    exception.code = ((AmapBaseResponse) response).status;
                    exception.message = ((AmapBaseResponse) response).info != null ? ((AmapBaseResponse) response).info : "";
                    throw exception;
                }
                return response;
            }
        };
    }

    @Override
    public String getFormal() {
        return "https://restapi.amap.com/";
    }

    @Override
    public String getTest() {
        return "https://restapi.amap.com/";
    }
}
