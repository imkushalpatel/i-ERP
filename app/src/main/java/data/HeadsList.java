package data;

/**
 * Created by kushal on 5/4/15.
 */
public class HeadsList {
    String HeadId, HeadName;

    public HeadsList(String headId, String headName) {

        HeadId = headId;
        HeadName = headName;
    }

    public String getHeadId() {
        return HeadId;
    }

    public void setHeadId(String headId) {
        HeadId = headId;
    }

    public String getHeadName() {
        return HeadName;
    }

    public void setHeadName(String headName) {
        HeadName = headName;
    }
}
