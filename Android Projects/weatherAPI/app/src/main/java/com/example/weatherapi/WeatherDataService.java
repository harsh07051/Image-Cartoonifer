package com.example.weatherapi;

import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";
    Context context;
    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }

    public void getcityID(String cityName,VolleyResponseListener volleyResponseListener){
        String url =QUERY_FOR_CITY_ID + cityName;
        JsonArrayRequest request= new JsonArrayRequest(Request.Method.GET,url, null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response){
                 cityID="";
                try {


                    JSONObject cityinfo = response.getJSONObject(0);
                    cityID =cityinfo.getString("woeid");

                } catch (JSONException e){
                    e.printStackTrace();
                }

                /* This Worked. but it didn't return the id number to Mainactivity. */
              //  Toast.makeText(context,"city ID="+cityID,Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(cityID);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(context,"Something wrong",Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something Wrong");
            }
        });

        //returned a NULL.
        //return cityID;
    }
    public interface ForeCastByIDResponse {


        void onResponse(List<WeatherReportModel>weatherReportModels);

        void onError(String message);
    }


  public  void getCityForecastByID(String cityID, final ForeCastByIDResponse foreCastByIDResponse){

       final List<WeatherReportModel>weatherReportModels=new ArrayList<>();
      String url=QUERY_FOR_CITY_WEATHER_BY_ID + cityID;
      //get the json object
      JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET,url, null,new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
               // Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
              try {
                  JSONArray consolidated_weather_list=response.getJSONArray("consolidated_weather");
                  //get the first item in the array.

                  for(int i=0; i<consolidated_weather_list.length();i++) {
                      WeatherReportModel One_day_weather =new WeatherReportModel();


                      JSONObject first_day_from_api = (JSONObject) consolidated_weather_list.get(i);
                      One_day_weather.setId(first_day_from_api.getInt("id"));
                      One_day_weather.setWeather_state_name(first_day_from_api.getString("weather_state_name"));
                      One_day_weather.setWind_direction_compass(first_day_from_api.getString("wind_direction_compass"));
                      One_day_weather.setCreated(first_day_from_api.getString("created"));
                      One_day_weather.setApplicable_date(first_day_from_api.getString("applicable_date"));
                      One_day_weather.setMin_temp(first_day_from_api.getLong("min_temp"));
                      One_day_weather.setMax_temp(first_day_from_api.getLong("max_temp"));
                      One_day_weather.setThe_temp(first_day_from_api.getLong("the_temp"));
                      One_day_weather.setWind_speed(first_day_from_api.getLong("wind_speed"));
                      One_day_weather.setWind_direction(first_day_from_api.getLong("wind_direction"));
                      One_day_weather.setAir_pressure(first_day_from_api.getInt("air_pressure"));
                      One_day_weather.setHumidity(first_day_from_api.getInt("humidity"));
                      One_day_weather.setVisibility(first_day_from_api.getLong("visibility"));
                      One_day_weather.setPredictability(first_day_from_api.getLong("predictability"));
                      weatherReportModels.add(One_day_weather);
                  }

                  foreCastByIDResponse.onResponse(weatherReportModels);


              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }
      },new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

          }
      });


              //get the property called "consolidated weather"which is an array
              //get each item in the array and assign it to a new weatherReportModel object.
      MySingleton.getInstance(context).addToRequestQueue(request);
  }
    public interface GetCityForecastByNameCallback {


        void onResponse(List<WeatherReportModel>weatherReportModels);

        void onError(String message);
    }


    public void getCityForecastByName(String cityName,GetCityForecastByNameCallback  getCityForecastByNameCallback ){
        //fetch cityId given the cityname.
       getcityID(cityName, new VolleyResponseListener() {
           @Override
           public void onError(String message) {

           }

           @Override
           public void onResponse(String cityID) {
               //now we have the cityId
               getCityForecastByID(cityID, new ForeCastByIDResponse() {
                   @Override
                   public void onResponse(List<WeatherReportModel> weatherReportModels) {
                       //we have the  weather  report.
                       getCityForecastByNameCallback.onResponse(weatherReportModels);

                   }

                   @Override
                   public void onError(String message) {

                   }
               });

           }
       });
       //fetch the forecast for the city given the  city ID.

    }

}
