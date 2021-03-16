package com.stork.http;

import com.stork.http.model.BlockPhone;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiServiceITF {
    @GET("/blockphone/all")
    Observable<ServiceResult<List<BlockPhone>>> getAllPhone();
}
