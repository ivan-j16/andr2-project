package com.example.andr2app.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAoWuvyQg:APA91bGXb5VLjc8-mqUe1UVMKcRPLOdi7FEK_fiHwFjg3M7y_XLXIXFWFUe1qgVk4fjDKEszCO8Wd3PaeiDJnKY1-B_peGp3YjPiZN7EEmCMdypKKDP1l8DWZKNbATtCKH0fulbWWiFq"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);

}
