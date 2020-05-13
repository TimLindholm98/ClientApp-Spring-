package TemplatePackage;

import java.io.Serializable;
import java.sql.Timestamp;

public class RowTemplate implements Serializable {
     float temperature;
     float humidity;
     float lumen;
     Timestamp timestamp;

    public RowTemplate(){

    }
    public float getHumidity() {
        return humidity;
    }
    public float getLumen() {
        return lumen;
    }
    public float getTemperature() {
        return temperature;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
    public void setLumen(float lumen) {
        this.lumen = lumen;
    }
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
