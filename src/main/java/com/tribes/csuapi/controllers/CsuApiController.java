package com.tribes.csuapi.controllers;

import com.tribes.csuapi.exceptions.CsuApiException;
import com.tribes.csuapi.integration.CsuApiService;
import com.tribes.csuapi.integration.model.Data;
import com.tribes.csuapi.integration.model.Municipality;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RestController
public class CsuApiController {

  @GetMapping("/municipality")
  public Municipality getMunicipality() throws CsuApiException {

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.apitalks.store/czso.cz/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    CsuApiService service = retrofit.create(CsuApiService.class);
    try {
      Municipality result = service.getMunicipality("557467264").execute().body();
      return result;
    } catch (IOException e) {
      throw new CsuApiException(e);
    }
  }

  @GetMapping("/municipalities")
  public Data listMunicipality() throws CsuApiException {

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.apitalks.store/czso.cz/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    CsuApiService service = retrofit.create(CsuApiService.class);
    try {
      Data data =
          service.listMunicipalities("{\"where\": {\"vuzemi_kod\":592528}}").execute().body();
      return data;
    } catch (IOException e) {
      throw new CsuApiException(e);
    }
  }
}
