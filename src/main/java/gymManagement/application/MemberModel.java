package gymManagement.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MemberModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty mobile;
    private final StringProperty status;

    public MemberModel() {
        id = new SimpleStringProperty(this, "Id");
        name = new SimpleStringProperty(this, "Name");
        mobile = new SimpleStringProperty(this, "Mobile");
        status = new SimpleStringProperty(this, "Status");
    }

    public StringProperty idProperty() { return id; }
    public String getId() { return id.get(); }
    public void setId(String newId) { id.set(newId); }

    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String newName) { name.set(newName); }

    public StringProperty mobileProperty() { return mobile; }
    public String getMobile() { return mobile.get(); }
    public void setMobile(String newMobile) { mobile.set(newMobile); }

    public StringProperty statusProperty() { return status; }
    public String getStatus() { return status.get(); }
    public void setStatus(String newStatus) { status.set(newStatus); }

    @Override
    public String toString() {
        return "Member[ID: " + getId() + ", Name: " + getName() + ", Mobile: " + getMobile() +
                ", Status: " + getStatus() + "]";
    }
}
