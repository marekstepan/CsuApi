package com.tribes.csuapi.integration;

import com.tribes.csuapi.integration.model.Data;
import com.tribes.csuapi.integration.model.Municipality;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CsuApiService {

  @Headers("x-api-key: IQQdwQRQjE8bqKQcRSkaQ9da9R3Y40fP8uNhJp3l")
  @GET("obyvatelstvo-domy/{idhod}")
  Call<Municipality> getMunicipality(@Path("idhod") String idhod);

  @Headers("x-api-key: IQQdwQRQjE8bqKQcRSkaQ9da9R3Y40fP8uNhJp3l")
  @GET("obyvatelstvo-domy/")
  Call<Data> listMunicipalities(@Query("filter") String filter);
}
