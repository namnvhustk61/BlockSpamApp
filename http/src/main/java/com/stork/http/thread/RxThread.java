package com.stork.http.thread;

import io.reactivex.Observable;

import io.reactivex.schedulers.Schedulers;

public class RxThread {
    public interface RxThreadItf{
        void onDo();
    }
    
    public static void onDoInIO(RxThreadItf itf){
        Observable.fromCallable(() ->{
            itf.onDo();
            return 0;
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }
}
