package gymManagement.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EquipmentModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty equipmentId;
    private final StringProperty status;

    public EquipmentModel() {
        id = new SimpleStringProperty(this, "Id");
        name = new SimpleStringProperty(this, "Name");
        equipmentId = new SimpleStringProperty(this, "EquipmentId");
        status = new SimpleStringProperty(this, "Status");
    }

    public StringProperty idProperty() { return id; }
    public String getId() { return id.get(); }
    public void setId(String newId) { id.set(newId); }

    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String newName) { name.set(newName); }

    public StringProperty equipmentIdProperty() { return equipmentId; }
    public String getEquipmentId() { return equipmentId.get(); }
    public void setEquipmentId(String newEquipmentId) { equipmentId.set(newEquipmentId); }

    public StringProperty statusProperty() { return status; }
    public String getStatus() { return status.get(); }
    public void setStatus(String newStatus) { status.set(newStatus); }

    @Override
    public String toString() {
        return "EquipmentModel{" + "id=" + getId() + ", name=" + getName() + ", equipmentId=" + getEquipmentId() + ", status=" + getStatus() + '}';
    }
}
