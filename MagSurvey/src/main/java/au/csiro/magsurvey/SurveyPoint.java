package au.csiro.magsurvey;

public class SurveyPoint {
    private Integer pointNumber;
    private Double pointLat;
    private Double pointLon;
    private Double totalMag;
    private Float xMag;
    private Float yMag;
    private Float zMag;

    public SurveyPoint(Integer pointNumber, Double pointLat, Double pointLon, Float xMag, Float yMag, Float zMag) {
        this.pointNumber = pointNumber;
        this.pointLat = pointLat;
        this.pointLon = pointLon;
        this.xMag = xMag;
        this.yMag = yMag;
        this.zMag = zMag;
        totalMag = Math.sqrt((Math.pow(xMag,2) + Math.pow(yMag,2) + Math.pow(zMag,2)));
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
    public Float getxMag() {
        return this.xMag;
    }
    public Float getyMag() {
        return this.yMag;
    }
    public Float getzMag() {
        return this.zMag;
    }

}
