package com.sw.semweb.model;

public class Observation {
    String room;   
String observationId;
String observationDate;
String observationSensor;
float observationResult;
float observationResultExt;
String statut;
public Observation(String room,String observationId, String observationDate, String observationSensor, Float observationResult,Float observationResultExt) {
    this.room = room;
    this.observationId = observationId;
    this.observationDate = observationDate;
    this.observationSensor = observationSensor;
    this.observationResult = observationResult;
    this.observationResultExt = observationResultExt;
    if(getDifferences(observationResult, observationResultExt)>10){
        this.statut="Concerning";
    }else{
        this.statut="OK";
    }

}
public float getDifferences(float f1,float f2){
    if (Float.compare(f1, f2) == 0) {
 
        System.out.println("f1=f2");
        return (float) 0.0;
    }
    else if (Float.compare(f1, f2) < 0) {

        return f2-f1;
    }
    else {

        return f1-f2;
    }
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

public Float getObservationResultExt() {
    return observationResultExt;
}

public void setObservationResultExt(Float observationResultExt) {
    this.observationResultExt = observationResultExt;
}

public String getStatut() {
    return statut;
}

public void setStatut(String statut) {
    this.statut = statut;
}
public String getRoom() {
    return room;
}
public void setRoom(String room) {
    this.room = room;
}
public void setObservationResult(float observationResult) {
    this.observationResult = observationResult;
}
public void setObservationResultExt(float observationResultExt) {
    this.observationResultExt = observationResultExt;
}




}



