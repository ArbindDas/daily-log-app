
package com.JSR.DailyLog.Api.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class WeatherResponse {

    private Request request;
    private Location location;
    private Current current;

    @Getter
    @Setter
    public static class AirQuality {
        public String co;
        public String no2;
        public String o3;
        public String so2;

        @JsonProperty("pm2_5")
        public String pm2;

        public String pm10;

        @JsonProperty("us-epa-index")
        public String index;

        @JsonProperty("gb-defra-index")
        public String index2;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Astro {
        public String sunrise;
        public String sunset;
        public String moonrise;
        public String moonset;

        @JsonProperty("moon_phase")
        public String moonPhase;

        @JsonProperty("moon_illumination")
        public int moonIllumination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Current {

        @JsonProperty("observation_time")
        public String observationTime;

        public int temperature;

        @JsonProperty("weather_code")
        public int weatherCode;

        @JsonProperty("weather_icons")
        public List<String> weatherIcons;

        @JsonProperty("weather_descriptions")
        public List<String> weatherDescriptions;

        public Astro astro;

        @JsonProperty("air_quality")
        public AirQuality airQuality;

        @JsonProperty("windSpeed")
        public int wind_speed;

        @JsonProperty("wind_degree")
        public int windDegree;

        @JsonProperty("windDir")
        public String wind_dir;

        public int pressure;
        public double precip;
        public int humidity;
        public int cloudcover;
        public int feelslike;

        @JsonProperty("uv_index")
        public int uvIndex;

        public int visibility;

        @JsonProperty("is_day")
        public String isDay;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        public String name;
        public String country;
        public String region;
        public String lat;
        public String lon;

        @JsonProperty("timezone_id")
        public String timezoneId;

        public String localtime;

        @JsonProperty("localtime_epoch")
        public int localtimeEpoch;

        @JsonProperty("utc_offset")
        public String utcOffset;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        public String type;
        public String query;
        public String language;
        public String unit;
    }
}
