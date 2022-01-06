package com.sw.semweb.model;

public class Observation {
    
String observationId;
String observationDate;
String observationSensor;
Float observationResult;

public Observation(String observationId, String observationDate, String observationSensor, Float observationResult) {
    this.observationId = observationId;
    this.observationDate = observationDate;
    this.observationSensor = observationSensor;
    this.observationResult = observationResult;
}

public String getObservationId() {
    return observationId;
}

public void setObservationId(String observationId) {
    this.observationId = observationId;
}

public String getObservationDate() {
    return observationDate;
}

public void setObservationDate(String observationDate) {
    this.observationDate = observationDate;
}

public String getObservationSensor() {
    return observationSensor;
}

public void setObservationSensor(String observationSensor) {
    this.observationSensor = observationSensor;
}

public Float getObservationResult() {
    return observationResult;
}

public void setObservationResult(Float observationResult) {
    this.observationResult = observationResult;
}




}



