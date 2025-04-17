package com.JSR.DailyLog.Services;

import com.JSR.DailyLog.Api.response.WeatherResponse;
import com.JSR.DailyLog.Cache.AppCache;
import com.JSR.DailyLog.Constant.Placeholders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WeatherService {

    @Value ("${weather.api.key}")
    private  String apiKey;


    @Autowired
    private AppCache appCache;

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getWeather(String city){


//        String finalApi = appCache.APP_CACHE.get ( AppCache.keys.weather_api.toString() ).replace ( Placeholders.CITY , city ).replace ( Placeholders.API_KEY, apiKey );

        String finalApi = UriComponentsBuilder.fromUriString ( appCache.APP_CACHE.get (AppCache.keys.weather_api.toString ()) )
                .queryParam(Placeholders.CITY, city)
                .queryParam(Placeholders.API_KEY, apiKey)
                .toUriString();

        ResponseEntity< WeatherResponse > response = restTemplate.exchange ( finalApi , HttpMethod.GET , null , WeatherResponse.class );
        WeatherResponse body = response.getBody ( );
        return body;
    }
}
