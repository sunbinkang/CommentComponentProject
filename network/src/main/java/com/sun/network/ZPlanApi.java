package com.sun.network;


import com.sun.network.base.NetworkApi;
import com.sun.network.beans.BaseResponse;
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
 * Created by BinKang on 2021/8/19.
 * Des : ZPlan的api
 */
public class ZPlanApi extends NetworkApi {

    private static volatile ZPlanApi sInstance;

    //构造方法私有化
    private ZPlanApi() {
    }

    /**
     * 单例模式（双重判空：高效+安全）
     *
     * @return
     */
    public static ZPlanApi getInstance() {
        if (sInstance == null) {
            synchronized (ZPlanApi.class) {
                if (sInstance == null) {
                    sInstance = new ZPlanApi();
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
                        builder.add("USERKEY", "4");
                        request = request
                                .newBuilder()
                                .post(builder.build())
                                .build();
                    }
                } else {
                    HttpUrl url = request.url();
                    HttpUrl newUrl = url.newBuilder()
                            .addEncodedQueryParameter("USERKEY", "4")
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
                //code不为"0"时：请求异常
                if (response instanceof BaseResponse && !((BaseResponse) response).getCode().equals("0")) {
                    ExceptionHandler.ServerException exception = new ExceptionHandler.ServerException();
                    exception.code = Integer.parseInt(((BaseResponse) response).getCode());
                    exception.message = ((BaseResponse) response).getMsg() != null ? ((BaseResponse) response).getMsg() : "";
                    throw exception;
                }
                return response;
            }
        };
    }

    @Override
    public String getFormal() {
        return "http://192.101.11.70:19333";
    }

    @Override
    public String getTest() {
        return "http://192.101.11.70:19333";
    }
}
