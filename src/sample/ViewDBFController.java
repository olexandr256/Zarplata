package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.DBFViewer;
import sample.ExcelReader;
import sample.models.Person;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewDBFController implements Initializable {
    @FXML private Label filename;
    @FXML private Label massage;
    @FXML private TableView<Person> tablePerson;
    @FXML private TableColumn<Person,String> name;
    @FXML private TableColumn<Person,String> fam;
    @FXML private TableColumn<Person,String> ot;
    @FXML private TableColumn<Person,String> inn;
    @FXML private TableColumn<Person,String> lstbl;
    @FXML private TableColumn<Person,String> card_no;
    @FXML private TableColumn<Person,String> rlsum;
    private double sum;

    private ObservableList<Person> listPerson = FXCollections.observableArrayList();

    private void init() throws UnsupportedEncodingException {
        name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        fam.setCellValueFactory(cellData -> cellData.getValue().famProperty());
        ot.setCellValueFactory(cell -> cell.getValue().otProperty());
        inn.setCellValueFactory(cell -> cell.getValue().innProperty());
        lstbl.setCellValueFactory(cell -> cell.getValue().lstblProperty());
        card_no.setCellValueFactory(cell -> cell.getValue().card_noProperty());
        rlsum.setCellValueFactory(cell -> cell.getValue().rlsumProperty());
        loadDBF();
        massage.setText("кількість: "+listPerson.size()+"; сума: "+sum);
//        filename.setText("BANK0300.dbf");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void transferMessege(String text) throws UnsupportedEncodingException {
        filename.setText(text);
        init();
    }

    //загрузка dbf файла
    private void loadDBF() throws UnsupportedEncodingException {
        ArrayList<Person> list = DBFViewer.load(filename.getText());
        sum = list.stream().mapToDouble(e -> new Double(e.getRlsum())).sum();


        listPerson.addAll(list);
        tablePerson.setItems(listPerson);
    }
//
//    public void on_save() throws IOException {
//        String file = filename.getText()+".xls";
//        ExcelReader.saveFile(listPerson,file,);
//    }

}
