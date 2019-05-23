package br.pucminas.damd;

public class RequestClass {
    String sensorName;
    String sensorValue;

    public RequestClass(String sensorName, String sensorValue) {
        this.sensorName = sensorName;
        this.sensorValue = sensorValue;
    }

    public RequestClass() {
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(String sensorValue) {
        this.sensorValue = sensorValue;
    }
}
