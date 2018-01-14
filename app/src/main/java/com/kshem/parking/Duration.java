package com.kshem.parking;

/**
 * Created by kshem on 12/19/17.
 */

public class Duration {
    private int hours;
    private int minutes;
    private int total_minutes;

    public Duration(){
        this.hours = 0;
        this.minutes = 0;
        this.total_minutes = 0;
    }

    public void addMinutes(int minutes){
        total_minutes += minutes;
        this.hours = total_minutes / 60;
        this.minutes = total_minutes % 60;
    }

    public int getDurationInMinutes(){
        return total_minutes;
    }

    public String getDuration(){
        if(hours == 0){
            return String.valueOf(minutes) + "MIN";
        }else if(minutes !=0 ){
            return String.valueOf(hours) + "HR" + " " + String.valueOf(minutes) + "MIN";
        }else{
            return String.valueOf(hours) + "HR";
        }
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }
}
