package com.example.mobileprogrammingproject;
import java.util.List;
public class WeatherResponse {

    private List<Weather> weather;
    private Main main;

    public List<Weather> getWeather() {
        return weather;
    }
    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
    public Main getMain() {
        return main;
    }
    public void setMain(Main main) {
        this.main = main;
    }

    public static class Weather {
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Main {
        private float temp;

        public float getTemp() {
            return temp;
        }

        public void setTemp(float temp) {
            this.temp = temp;
        }
    }
}
