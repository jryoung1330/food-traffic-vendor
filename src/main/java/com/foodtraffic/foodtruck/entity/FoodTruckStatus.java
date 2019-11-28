package com.foodtraffic.foodtruck.entity;

public enum FoodTruckStatus {
    ACTIVE(0),
    INACTIVE(1),
    HOLD(2);

    private int statusNum;

    FoodTruckStatus(int statusNum) {
        this.statusNum = statusNum;
    }

    public int getStatusNum() {
        return statusNum;
    }
}
