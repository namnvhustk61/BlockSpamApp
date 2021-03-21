package com.stork.http.thread;

import com.stork.http.ServiceResult;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
