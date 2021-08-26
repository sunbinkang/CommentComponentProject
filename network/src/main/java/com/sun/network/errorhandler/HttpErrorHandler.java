package com.sun.network.errorhandler;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description:
 */
public class HttpErrorHandler<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        return Observable.error(ExceptionHandler.handleException(throwable));
    }
}
