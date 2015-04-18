package data;

/**
 * Created by kushal on 16/4/15.
 */
public class HeadWiseList {
    String ProjectName, WorkDone, Value;

    public HeadWiseList(String projectName, String workDone, String value) {
        ProjectName = projectName;
        WorkDone = workDone;
        Value = value;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getWorkDone() {
        return WorkDone;
    }

    public void setWorkDone(String workDone) {
        WorkDone = workDone;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
