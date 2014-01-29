package au.csiro.magsurvey;

public class SurveyPoint {
    private Integer pointNumber;
    private Double pointLat;
    private Double pointLon;
    private Double totalMag;

    public SurveyPoint(Integer pointNumber, Double pointLat, Double pointLon, Double totalMag) {
        this.pointNumber = pointNumber;
        this.pointLat = pointLat;
        this.pointLon = pointLon;
        this.totalMag = totalMag;
    }

    public Integer getpointNumber() {
        return this.pointNumber;
    }

    public Double getpointLat() {
        return this.pointLat;
    }

    public Double getpointLon() {
        return this.pointLon;
    }

    public Double getTotalMag() {
        return this.totalMag;
    }


}
