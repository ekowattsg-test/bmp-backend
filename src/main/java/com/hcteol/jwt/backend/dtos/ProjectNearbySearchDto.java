package com.hcteol.jwt.backend.dtos;

public class ProjectNearbySearchDto {

    private Double latitude;
    private Double longitude;

    public ProjectNearbySearchDto() {
    }

    public ProjectNearbySearchDto(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
