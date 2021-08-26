package com.sun.network.observer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description:对观察者进行一层封装：只关心成功和失败的两个情况
 */
public abstract class BaseObserver<T> implements Observer<T> {


    @Override
    public void onSubscribe(Disposable d) {
        onSubscription(d);
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {

    }

    /**
     * 已订阅，建立连接
     * @param d
     */
    public abstract void onSubscription(Disposable d);

    /**
     * 请求成功
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * 请求失败
     * @param t
     */
    public abstract void onFailure(Throwable t);
}
