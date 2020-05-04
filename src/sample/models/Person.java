package sample.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
    private StringProperty name;
    private StringProperty fam;
    private StringProperty ot;
    private StringProperty inn;
    private StringProperty lstbl;
    private StringProperty card_no;
    private StringProperty rlsum;

    @Override
    public String toString() {
        return getFam()+","+getName()+","+getOt()+","+getInn()+","+getLstbl()+","+getCard_no()+","+getRlsum();
    }

    public Person(String name, String fam, String ot, String inn, String lstbl, String card_no, String rlsum) {
        this.name = new SimpleStringProperty(name) ;
        this.fam = new SimpleStringProperty(fam);
        this.ot = new SimpleStringProperty(ot);
        this.inn = new SimpleStringProperty(inn);
        this.lstbl = new SimpleStringProperty(lstbl);
        this.card_no = new SimpleStringProperty(card_no);
        this.rlsum = new SimpleStringProperty(rlsum);
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

    public String getFam() {
        return fam.get();
    }

    public StringProperty famProperty() {
        return fam;
    }

    public void setFam(String fam) {
        this.fam.set(fam);
    }

    public String getOt() {
        return ot.get();
    }

    public StringProperty otProperty() {
        return ot;
    }

    public void setOt(String ot) {
        this.ot.set(ot);
    }

    public String getInn() {
        return inn.get();
    }

    public StringProperty innProperty() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn.set(inn);
    }

    public String getLstbl() {
        return lstbl.get();
    }

    public StringProperty lstblProperty() {
        return lstbl;
    }

    public void setLstbl(String lstbl) {
        this.lstbl.set(lstbl);
    }

    public String getCard_no() {
        return card_no.get();
    }

    public StringProperty card_noProperty() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no.set(card_no);
    }

    public String getRlsum() {
        return rlsum.get();
    }

    public StringProperty rlsumProperty() {
        return rlsum;
    }

    public void setRlsum(String rlsum) {
        this.rlsum.set(rlsum);
    }
}
