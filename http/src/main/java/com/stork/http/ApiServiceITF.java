package com.stork.http;

import com.stork.http.model.AddPhoneCloud;
import com.stork.http.model.BlockPhone;
import com.stork.http.model.BlockPhoneRes;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiServiceITF {
    @GET("/blockphone/all")
    Observable<ServiceResult<List<BlockPhone>>> getAllPhone();

    @POST("/blockphone/add")
    Observable<ServiceResult<BlockPhoneRes>> addPhoneCloud(@Body AddPhoneCloud body);
}
