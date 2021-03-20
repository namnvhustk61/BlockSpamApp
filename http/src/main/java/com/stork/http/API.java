package com.stork.http;

import com.stork.http.model.AddPhoneCloud;
import com.stork.http.model.BlockPhone;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class API{
    public interface ApiItf<T>{
         void onSuccess(ServiceResult<T> response);
         void onError(String message);
    }
    private static final ApiServiceITF _call = ApiClient.getClient().create(ApiServiceITF.class);
    public static void getAllBLockPhone(ApiItf<List<BlockPhone>> itf){
        _call.getAllPhone()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ServiceResult<List<BlockPhone>>>() {
                    @Override
                    public void accept(ServiceResult<List<BlockPhone>> res) throws Exception {
                        itf.onSuccess(res);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        itf.onError(throwable.getMessage());
                    }
                }).isDisposed();
    }

    public static void addPhoneCloud(AddPhoneCloud body, ApiItf<Objects> itf){
        _call.addPhoneCloud(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ServiceResult<Objects>>() {
                    @Override
                    public void accept(ServiceResult res) throws Exception {
                        itf.onSuccess(res);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        itf.onError(throwable.getMessage());
                    }
                }).isDisposed();
    }
}
