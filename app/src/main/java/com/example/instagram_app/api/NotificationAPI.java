package com.example.instagram_app.api;

import static com.example.instagram_app.utils.Constants.CONTENT_TYPE;
import static com.example.instagram_app.utils.Constants.SERVER_KEY;

import com.example.instagram_app.model.PushNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationAPI {

    @Headers({"Authorization: key=" + SERVER_KEY, "Content-Type:" + CONTENT_TYPE})
    @POST("fcm/send")
    Call<Void> postNotification(
            @Body PushNotification pushNotification
    );
}
