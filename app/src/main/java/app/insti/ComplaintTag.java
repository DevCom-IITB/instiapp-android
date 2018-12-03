package app.insti;

import app.insti.api.model.Venter; /**
 * Created by Shivam Sharma on 13-08-2018.
 */
public class ComplaintTag {

    private String name;

    public ComplaintTag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
