package com.dell.yuvabul

import com.dell.yuvabul.Constants.Constants.Companion.CONTENT_TYPE
import com.dell.yuvabul.Constants.Constants.Companion.SERVER_KEY
import com.dell.yuvabul.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    //create notification interface for retrofit
    @Headers("Authorization: key = $SERVER_KEY","Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification:PushNotification
    ): Response<ResponseBody>
}