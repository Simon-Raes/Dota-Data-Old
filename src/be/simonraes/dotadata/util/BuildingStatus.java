package be.simonraes.dotadata.util;

/**
 * Object holding the type, side and location of a live building on the map.
 * Created by Simon Raes on 31/08/2014.
 */
public class BuildingStatus {

    private Side teamSide;
    public enum Side{
        RADIANT, DIRE
    }

    private BuildingType type;
    public enum BuildingType{
        TOWER, BARRACKS;
    }
    double x, y;

    public BuildingStatus(Side teamSide, BuildingType type, double x, double y) {
        this.teamSide = teamSide;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public Side getTeamSide() {
        return teamSide;
    }

    public BuildingType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
