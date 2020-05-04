package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.models.FileList;
import sample.models.Person;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class OpenController implements Initializable {

    @FXML    private TableView<FileList> tableFiles;
    @FXML    private TableColumn<FileList, String> numCol;
    @FXML    private TableColumn<FileList, String> nameCol;
    @FXML    private TableColumn<FileList, String> dateCol;
    @FXML    private Label message;

    //список файлів у внітрішній базі
    private ObservableList<FileList> fileList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numCol.setCellValueFactory(cellData -> cellData.getValue().formatProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());

        try {
            readRows();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tableFiles.setItems(fileList);
    }

    /*
        робота із файлом "list.ini"
     */

    //зчитати рядки із файла "list.ini"
    private void readRows() throws IOException {
        fileList.clear();

        File file = new File("list.ini");
        if(file.exists()){
            FileReader fileReader = new FileReader(file);
            Scanner scan = new Scanner(fileReader);
            while(scan.hasNext()){
                String line = scan.nextLine();

                //друк даних із конфігурації
//                System.out.println(line);
                String[] list = line.split(",");

                fileList.add(new FileList(list[0],list[1],list[2]));

            }
            scan.close();
            fileReader.close();
        }
//        count = fileList.size();
    }
    //додати запис у файл "list.ini"
    private void saveRows() throws IOException {
        File file = new File("list.ini");

           FileWriter writer = new FileWriter(file);
           fileList.forEach(e -> {
               try {
                   writer.write(e+"\n");
               } catch (IOException ex) {
                   ex.printStackTrace();
               }
           });
           writer.close();
    }
    //додати рядок у список файлів
    private void addRow(File selectedFile) throws IOException {
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String filename = selectedFile.getName();
        String format = filename.substring(filename.length()-3).toUpperCase();
        FileList row = new FileList(format,filename,date);
        fileList.add(row);

        saveRows();
    }
    //видалити запис із файла "list.ini"
    private void deleteRow(FileList selectRow) throws IOException {
        fileList.remove(selectRow);
        saveRows();
    }

    /*
        спливаючі вікна діалогу із користувачем
     */

    //вікно де запитується чи видаляти файл із бази
    private boolean deleteDialog(String filename){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Виберіть дію");
        alert.setHeaderText("Видалити файл \""+filename+"\" із бази?");
        alert.setContentText("Цей файл видаляється лише із внутрішньої бази програми.");


        ButtonType buttonTypeOne = new ButtonType("Видалити");
        ButtonType buttonTypeCancel = new ButtonType("Відмінити", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == buttonTypeOne;
    }


    /*
        методи для роботи із даними імпорт, експорт, відкрити dbf або xls файли
     */
    //копіювати файл у корін програми
    private void importFile() throws IOException {
        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.setTitle("Імпортувати файл");
        fileChooser1.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Таблиця DBF", "*.dbf"),
                new FileChooser.ExtensionFilter("всі файли", "*.*"));
        Stage stage = new Stage();
        File selectedFile = fileChooser1.showOpenDialog(stage);

        if (selectedFile != null) {
            File newFile = new File(selectedFile.getName());
            if(!newFile.exists())
            Files.copy(selectedFile.toPath(),newFile.toPath());

            addRow(selectedFile);
            message.setText("файл \""+selectedFile.getName()+"\" імпортовано");
        }
    }

    private void exportFile(String filename) throws IOException {
        String format = filename.substring(filename.length()-3).toLowerCase();

        if(format.equals("xls")){

            File oldFile = new File(filename);

            FileChooser fileChooser2 = new FileChooser();
            fileChooser2.setTitle("Експортувати файл");
            fileChooser2.setInitialFileName(filename);
            fileChooser2.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Таблиця xls", "*.xls"),
                    new FileChooser.ExtensionFilter("всі файли", "*.*"));
            Stage stage = new Stage();

            File selectedFile = fileChooser2.showSaveDialog(stage);

            if (selectedFile != null) {
                Files.copy(oldFile.toPath(),selectedFile.toPath());
                message.setText("файл \""+selectedFile.getName()+"\" імпортовано");
            }
        }
    }
    //відкривання файлів із внутнішньої бази
    public void open_dbf(String filename) throws IOException {
        URL path = getClass().getResource("view_DBF.fxml");
        message.setText(path.toString());
        FXMLLoader loader = new FXMLLoader(path);
        Parent root = loader.load();

        ViewDBFController controller = loader.getController();
        controller.transferMessege(filename);
//        loader.setController(controller);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Зарплатна відомість");
        stage.show();

    }
    public void open_xls(String filename) throws IOException {
        File file = new File (filename);
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);
    }



    /*
        публічні методи для роботи із кнопками
     */

    //натискання кнопки "оновити"
    public void on_update() throws IOException {
        readRows();
        tableFiles.setItems(fileList);
        message.setText(" ");
    }

    //натискання кнопки "імпорт"
    public void on_import() throws IOException {
        importFile();
        readRows();
    }

    //натискання кнопки "експортувати"
    public void on_export() throws IOException {
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        String filename = select.getName();

        exportFile(filename);

    }

    //натискання кнопки "конвертувати"
    public void on_convert()throws IOException{
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        String selectFileName = select.getName();

        String format = selectFileName.substring(selectFileName.length()-3).toLowerCase();
//        System.out.println(format);
        if (format.equals("dbf")){
            ObservableList<Person> listP = FXCollections.observableArrayList();
            ArrayList<Person> list = DBFViewer.load(selectFileName);

            String filename  = selectFileName.substring(0,selectFileName.length()-4);
            String newFileName = filename+".xls";

            listP.clear();
            listP.addAll(list);
            ExcelReader.saveFile(listP,newFileName);

            addRow(new File(newFileName));
        }

    }

    //натискання кнопки "видалити"
    public void on_delete() throws IOException {
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        if(select != null){
            if(deleteDialog(select.getName())){
                deleteRow(select);
                File file = new File(select.getName());
                if(file.exists()){
                    Files.delete(file.toPath());
                }

                message.setText("файл \""+select.getName()+"\" видалений");
            }
        }
    }

    //вибір файла із списку tableView
    public void on_selected(MouseEvent mouseEvent) throws IOException {
        if(mouseEvent.getClickCount() == 2){
            open();
        }
        if(mouseEvent.getClickCount() == 1){
            FileList select = tableFiles.getSelectionModel().getSelectedItem();
            if(select != null){
                message.setText(select.getName());
            }
        }

    }

    //натискання кнопки "відкрити"
    public void open() throws IOException {
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        String format = select.getName().substring(select.getName().length()-3).toLowerCase();
        if(format.equals("xls")){
            open_xls(select.getName());
        }
        if(format.equals("dbf")){
            open_dbf(select.getName());
        }
    }
}
