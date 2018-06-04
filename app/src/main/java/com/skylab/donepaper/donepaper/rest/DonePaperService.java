package com.skylab.donepaper.donepaper.rest;

import com.skylab.donepaper.donepaper.rest.model.ArticlesResponse;
import com.skylab.donepaper.donepaper.rest.model.Coupon;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.DeviceTokenReponse;
import com.skylab.donepaper.donepaper.rest.model.FileLibraryResponse;
import com.skylab.donepaper.donepaper.rest.model.FormInfoData;
import com.skylab.donepaper.donepaper.rest.model.OrderData;
import com.skylab.donepaper.donepaper.rest.model.ResponseDownload;
import com.skylab.donepaper.donepaper.rest.model.ResponseOrder;
import com.skylab.donepaper.donepaper.rest.model.SearchResults;
import com.skylab.donepaper.donepaper.rest.model.SubjectsLibraryResponse;
import com.skylab.donepaper.donepaper.rest.model.TokenData;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface DonePaperService {
    @GET("Order/FormInfo")
    Call<DPResponse<FormInfoData>> getFormInfo();

    @FormUrlEncoded
    @POST("User/Login")
    Call<DPResponse<TokenData>> login(@Field("email") String email,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("User/Create")
    Call<DPResponse<TokenData>> signup(@Field("email") String email,
                                       @Field("name") String name,
                                       @Field("password") String password);

    @GET("/OrderDetails/{id}")
    Call<DPResponse<OrderData>> getOrder(@Path("id") int orderId,
                                         @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("/Order/Search")
    Call<DPResponse<SearchResults>> getListOrderByKeyword(@Header("Authorization") String authorization,
                                                          @Field("keyword") String keyword,
                                                          @Field("status") String status);

    @FormUrlEncoded
    @POST("/Order/Search")
    Call<DPResponse<SearchResults>> getListOrderByTime(
            @Header("Authorization") String authorization,
            @Field("from_date") int from_date,
            @Field("to_date") int to_date,
            @Field("status") String status);

    @FormUrlEncoded
    @POST("/Order/Search")
    Call<DPResponse<List<OrderData>>> getListOrderByAll(@Header("Authorization") String authorization,
                                                        @Field("keyword") String keyword,
                                                        @Field("from_date") int from_date,
                                                        @Field("to_date") int to_date,
                                                        @Field("status") String status);

    @FormUrlEncoded
    @POST("/Coupon/Info")
    Call<DPResponse<Coupon>> getCouponInfo(@Field("coupon_code") String coupon);

    @Multipart
    @POST("Order/Create")
    Call<DPResponse<ResponseOrder>> createOrder(@Header("Authorization") String authorization,
                                                @PartMap() Map<String, RequestBody> map,
                                                @Part MultipartBody.Part file);

    @GET("OrderDetails/{id}")
    Call<DPResponse<OrderData>> getOrderDetailByOrderId(@Path("id") int orderId,
                                                        @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("/Order/SendMessage")
    Call<DPResponse<OrderData.PostsBean>> sendMessage(@Header("Authorization") String authorization, @Field("order_id") int orderId, @Field("content") String message);

    @Multipart
    @POST("/Order/SendMessage")
    Call<DPResponse<OrderData.PostsBean>> sendMessageWithAttachment(
            @Header("Authorization") String authorization,
            @Part("order_id") RequestBody orderid,
            @Part("content") RequestBody message,
            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("/User/UpdateProfile")
    Call<DPResponse<TokenData>> updateProfile(@Header("Authorization") String authorization,
                                              @Field("email") String email,
                                              @Field("name") String name,
                                              @Field("old_password") String oldPassword,
                                              @Field("new_password") String newPassword);

    @GET("/Post/{postId}/AttachmentDetails")
    Call<DPResponse<ResponseDownload>> getDownloadLink(@Path("postId") int postId,
                                                       @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("https://api.donepaper.com/DeviceToken/Register")
    Call<DPResponse<DeviceTokenReponse>> registerDeviceTokenWithoutId(@Field("device_token") String deviceToken,
                                                                      @Field("platform") int platform,
                                                                      @Field("environment") int environment);

    @FormUrlEncoded
    @POST("https://api.donepaper.com/DeviceToken/Register")
    Call<DPResponse<DeviceTokenReponse>> registerDeviceTokenWithId(@Header("Authorization") String userToken,
                                                                   @Field("device_token") String deviceToken,
                                                                   @Field("platform") int platform,
                                                                   @Field("environment") int environment);
    @FormUrlEncoded
    @POST("https://api.donepaper.com/Article/List")
    Call<DPResponse<ArticlesResponse>> getArticlesList(@Field("limit") int limit,
                                                       @Field("page") int page,
                                                       @Field("excerpt_characters") int excerptCharacters);

    @GET("https://api.donepaper.com/Library/Subjects")
    Call<DPResponse<SubjectsLibraryResponse>> getSubjectListLibrary ();

    @FormUrlEncoded
    @POST("https://api.donepaper.com/Library/Search")
    Call<DPResponse<FileLibraryResponse>> getFileLibrary(@Field("limit") int limit,
                                                         @Field("page") int page,
                                                         @Field("subject_id") int subjectId);

    @FormUrlEncoded
    @POST("https://api.donepaper.com/DeviceToken/Deregister")
    Call<DPResponse<DeviceTokenReponse>> removeDeviceToken(@Field("device_token") String deviceToken,
                                                           @Field("platform") int platform);

    @GET("https://api.donepaper.com/User/Profile")
    Call<DPResponse<TokenData>> getProfile(@Header("Authorization") String token);
}
