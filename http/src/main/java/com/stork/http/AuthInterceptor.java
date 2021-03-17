package com.stork.http;

import com.stork.http.utils.MyJSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AuthInterceptor implements Interceptor {
    Response response;
    ResponseBody res;

    Request request;
    Headers.Builder builder;
    Headers headers;
    @Override
    public Response intercept(Chain chain) throws IOException {

        try{
            request = chain.request();
            builder = request.headers().newBuilder();
            builder.add("Content-Type", "application/json");

            headers = builder.build();
            request = request.newBuilder().headers(headers).build();

            response = chain.proceed(request);
            res = response.peekBody(Long.MAX_VALUE);

            JSONObject root = new JSONObject(res.string());
            String code = MyJSONObject.getString(root, "code");
            String message = MyJSONObject.getString(root, "message");
            return  response;
        }catch (Exception e){
            ResponseBody newResBody = ResponseBody.create(
                    MediaType.get("application/json"),
                    createBodyErr("").toString()
            );
            return response.newBuilder().body(newResBody).build();
        }
    }

    private JSONObject createBodyErr(String message){
        JSONObject bodyErr;
        try {
            bodyErr = new JSONObject()
                    .put("code", "3")
                    .put("message", message)
                    .put("data", null);

            return bodyErr;

        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }

    }
}