package sample.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileList {
    private StringProperty format;
    private StringProperty name;
    private StringProperty date;

    public FileList(String format, String name, String date) {
        this.format = new SimpleStringProperty(format);
        this.name = new SimpleStringProperty(name);
        this.date = new SimpleStringProperty(date);
    }

    @Override
    public String toString() {
        return getFormat()+","+getName()+","+getDate();
    }

    public String getFormat() {
        return format.get();
    }

    public StringProperty formatProperty() {
        return format;
    }

    public void setFormat(String format) {
        this.format.set(format);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }
}
