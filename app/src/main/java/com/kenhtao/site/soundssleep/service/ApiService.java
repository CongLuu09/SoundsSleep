package com.kenhtao.site.soundssleep.service;

import com.kenhtao.site.soundssleep.models.MixDto;
import com.kenhtao.site.soundssleep.models.SoundDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("/api/v1/music")
    Call<SoundResponse> getAllSounds();


    @GET("/api/v1/categories")
    Call<CategoryResponse> getAllCategories();

    @GET("/api/v1/music")
    Call<ApiResponse<PagedResponse<SoundDto>>> getAllMusics();


    @GET("/api/v1/sounds/by-category")
    Call<ApiResponse<List<SoundDto>>> getSoundsByCategoryId(@Query("categoryId") int categoryId);


    @GET("/api/sounds/by-ids")
    Call<List<SoundDto>> getSoundsByIds(@Query("ids") String commaSeparatedIds);


    @GET("/api/sounds/by-name")
    Call<SoundDto> getSoundByName(@Query("name") String name);


    @POST("/api/mix")
    Call<MixDto> createMix(@Body MixCreateRequest request);


    @PUT("/api/mix/{id}")
    Call<MixDto> updateMix(@Path("id") int id, @Body MixUpdateRequest request);


    @DELETE("/api/mix/{id}")
    Call<Void> deleteMix(@Path("id") int id);

    @FormUrlEncoded
    @POST("/api/login")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );


    @Multipart
    @POST("/api/upload")
    Call<UploadResponse> uploadSound(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part fileImage,
            @Part MultipartBody.Part fileSound
    );


    @Multipart
    @POST("/api/upload/audio")
    Call<UploadResponse> uploadAudio(@Part MultipartBody.Part file);
}
