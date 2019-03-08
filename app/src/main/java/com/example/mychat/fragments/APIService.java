package com.example.mychat.fragments;

import com.example.mychat.notification.MyResponse;
import com.example.mychat.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {

                    "Content-Type:application/json",
                    "Authorization:key=AAAA8ZA7QB0:APA91bH08Ce_LAsGn8FOX62y8yz7YGF6b3HnLalNd2KOVyGO3vfay9WJs5g9G2PPDcHPj2jeR50AKfylxb68jnwNZ0c4W74GGdcajfZTDhTkdv2j8jUUJkTbAIdJwlVvfVD0YCb38s0j"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
