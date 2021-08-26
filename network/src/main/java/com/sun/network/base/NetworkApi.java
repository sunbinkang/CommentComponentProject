package com.sun.network.base;

import android.content.Context;
import android.content.Intent;

import com.sun.network.environment.EnvironmentActivity;
import com.sun.network.environment.IEnvironment;
import com.sun.network.errorhandler.HttpErrorHandler;
//import com.sun.network.httpdns.AlibabaHttpDns;
import com.sun.network.interceptors.CommonRequestInterceptor;
import com.sun.network.interceptors.CommonResponseInterceptor;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CertificatePinner;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description:
 */
public abstract class NetworkApi implements IEnvironment {

    private static INetworkRequiredInfo iNetworkRequiredInfo;
    private static Map<String, Retrofit> retrofitMap = new HashMap<>();
    private String baseUrl;
    private static boolean mIsFormal = true;
    private OkHttpClient okHttpClient;

    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        iNetworkRequiredInfo = networkRequiredInfo;
        mIsFormal = EnvironmentActivity.isOfficialEnvironment(iNetworkRequiredInfo.getApplicationContext());
    }

    public static void chooseEnvironment(Context context) {
        if (iNetworkRequiredInfo != null && iNetworkRequiredInfo.isDebug()) {
            Intent intent = new Intent(context, EnvironmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public NetworkApi() {
        if (!mIsFormal) {
            baseUrl = getTest();
        }
        baseUrl = getFormal();
    }

//    public static <T> T getService(Class<T> service) {
//        return getRetrofit(service).create(service);
//    }

    public Retrofit getRetrofit(Class service) {
        String key = baseUrl + service.getName();
        if (retrofitMap.get(key) != null) {
            return retrofitMap.get(key);
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        //构建者模式配置retrofit
        builder.baseUrl(baseUrl);
        //给Retrofit配置OKHttpClient
        builder.client(getOkHttpClient());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        //得到retrofit
        Retrofit retrofit = builder.build();
        retrofitMap.put(key, retrofit);
        return retrofit;
    }

    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder oKHttpClientBuilder = new OkHttpClient.Builder();
            if (getInterceptor() != null) {
                oKHttpClientBuilder.addInterceptor(getInterceptor());
            }
//            oKHttpClientBuilder.certificatePinner(new CertificatePinner.Builder()
//                    .add("restapi.amap.com", "/6WD2EQVJUtFa3zd+7JlFZtf5NYejXZI/qmPKSZsY5I=").build());
            oKHttpClientBuilder.addInterceptor(new CommonRequestInterceptor(iNetworkRequiredInfo));
            oKHttpClientBuilder.addInterceptor(new CommonResponseInterceptor());
            //debug 模式下开启oKHttp日志拦截器
            if (iNetworkRequiredInfo != null && (iNetworkRequiredInfo.isDebug())) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                oKHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
            }
//            oKHttpClientBuilder.dns(new AlibabaHttpDns(iNetworkRequiredInfo.getApplicationContext()));
            okHttpClient = oKHttpClientBuilder.build();
        }
        return okHttpClient;
    }

    //线程切换的封装
    public <T> ObservableTransformer<T, T> applySchedulers(final Observer<T> observer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<T> observable = upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                if (getAppErrorHandler() != null) {
                    observable = observable.map(NetworkApi.this.<T>getAppErrorHandler());
                }
                observable.onErrorResumeNext(new HttpErrorHandler<T>())
                        .subscribe(observer);
                return observable;
            }
        };
    }

    protected abstract Interceptor getInterceptor();

    protected abstract <T> Function<T, T> getAppErrorHandler();
}
