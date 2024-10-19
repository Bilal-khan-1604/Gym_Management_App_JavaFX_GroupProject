package gymManagement.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TrainerModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty mobile;
    private final StringProperty service;
    private final StringProperty charges;

    // Default constructor
    public TrainerModel() {
        this("", "", "", "", "");
    }

    // Constructor with parameters for initialization
    public TrainerModel(String id, String name, String mobile, String service, String charges) {
        this.id = new SimpleStringProperty(this, "ID", id);
        this.name = new SimpleStringProperty(this, "Name", name);
        this.mobile = new SimpleStringProperty(this, "Mobile", mobile);
        this.service = new SimpleStringProperty(this, "Service", service);
        this.charges = new SimpleStringProperty(this, "Charges", charges);
    }

    // Getters and Setters with properties
    public StringProperty idProperty() { return id; }
    public String getId() { return id.get(); }
    public void setId(String newId) {
        if (newId != null && !newId.trim().isEmpty()) {
            id.set(newId);
        }
    }

    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String newName) {
        if (newName != null && !newName.trim().isEmpty()) {
            name.set(newName);
        }
    }

    public StringProperty mobileProperty() { return mobile; }
    public String getMobile() { return mobile.get(); }
    public void setMobile(String newMobile) {
        if (newMobile != null && !newMobile.trim().isEmpty()) {
            mobile.set(newMobile);
        }
    }

    public StringProperty serviceProperty() { return service; }
    public String getService() { return service.get(); }
    public void setService(String newService) {
        if (newService != null && !newService.trim().isEmpty()) {
            service.set(newService);
        }
    }

    public StringProperty chargesProperty() { return charges; }
    public String getCharges() { return charges.get(); }
    public void setCharges(String newCharges) {
        if (newCharges != null && !newCharges.trim().isEmpty()) {
            charges.set(newCharges);
        }
    }

    // Override toString() for easier debugging
    @Override
    public String toString() {
        return "Trainer[ID: " + getId() + ", Name: " + getName() + ", Mobile: " + getMobile() +
                ", Service: " + getService() + ", Charges: " + getCharges() + "]";
    }
}
