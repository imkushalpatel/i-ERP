package data;

/**
 * Created by kushal on 5/4/15.
 */
public class LocationList {
    String LocationId,LocationName;

    public LocationList(String locationId, String locationName) {
        LocationId = locationId;
        LocationName = locationName;
    }

    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }
}
