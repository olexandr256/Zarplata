package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.models.FileList;
import sample.models.Person;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class OpenController implements Initializable {

    @FXML   private Button B_convert;
    @FXML    private Button B_export;
    @FXML    private Button B_delete;
    @FXML    private Button B_open;
    @FXML    private Hyperlink update;

    @FXML    private TableView<FileList> tableFiles;
    @FXML    private TableColumn<FileList, String> numCol;
    @FXML    private TableColumn<FileList, String> nameCol;
    @FXML    private TableColumn<FileList, String> dateCol;
    @FXML    private TableColumn<FileList, String> platizhCol;
    @FXML    private Label message;

    //список файлів у внітрішній базі
    private ObservableList<FileList> fileList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numCol.setCellValueFactory(cellData -> cellData.getValue().formatProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());
        platizhCol.setCellValueFactory(cell -> cell.getValue().getPlatizhProperty());

        try {
            readRows();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tableFiles.setItems(fileList);
        tableFiles.setEditable(true);
        platizhCol.setCellFactory(TextFieldTableCell.forTableColumn());
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
                if(list.length == 4)
                    fileList.add(new FileList(list[0],list[1],list[2],list[3]));
                else
                    fileList.add(new FileList(list[0],list[1],list[2],""));
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
    private void addRow(File selectedFile,String platizh) throws IOException {
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String oldFileName = selectedFile.getName();

        int point = oldFileName.lastIndexOf(".");
        //розподіл назви файла та його формат

        String newFileName = oldFileName.substring(0,point);
        String format = oldFileName.substring(point+1).toUpperCase();

        FileList row = new FileList(format,newFileName,date,platizh);
        fileList.add(row);

        saveRows();
    }
    private void addRow(File selectedFile) throws IOException {
        addRow(selectedFile,"");
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
//        alert.setContentText("Цей файл видаляється лише із внутрішньої бази програми.");
//        alert.set
        ButtonType buttonTypeOne = new ButtonType("Так", ButtonBar.ButtonData.APPLY);
        ButtonType buttonTypeCancel = new ButtonType("Ні", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeOne);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == buttonTypeOne;
    }
    /*
        методи для роботи із даними імпорт, експорт, відкрити dbf або xls файли
     */

    //перевірка одинакових файлів
    private File filterFile(File file) {
        String oldFileName = file.getName();
        int point = oldFileName.lastIndexOf(".");

        //розподіл назви файла та його формат
        String filename = oldFileName.substring(0,point);
        String format = oldFileName.substring(point+1);

        do {
            int beg = filename.indexOf("(");

            if (beg != -1) {
                int end = filename.indexOf(")");
                String digit = filename.substring(beg + 1, end);
                int index = Integer.parseInt(digit) + 1;
                filename = filename.substring(0, beg + 1) + index + ")." + format;
            } else {
                filename = filename + "(1)." + format;
            }
        } while (new File(filename).exists());

        return new File(filename);
    }

    //копіювати файл у корін програми
    private void importFile() throws IOException {
        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.setTitle("Імпортувати файл");
        fileChooser1.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Таблиця EXCEL", "*.xlsx"),
                new FileChooser.ExtensionFilter("Таблиця EXCEL (2003)", "*.xls"),
                new FileChooser.ExtensionFilter("Таблиця DBF", "*.dbf"),
                new FileChooser.ExtensionFilter("Всі файли", "*.*"));
        Stage stage = new Stage();
        File selectedFile = fileChooser1.showOpenDialog(stage);

        if (selectedFile != null) {
            File newFile = new File(selectedFile.getName());

            //Якщо файла не існує додати до каталогу, якщо існує змінити назву із індексом на один більше
            if(!newFile.exists()){
                Files.copy(selectedFile.toPath(),newFile.toPath());
                addRow(newFile);
            }
            else{
                File newF = filterFile(newFile);
                Files.copy(selectedFile.toPath(),newF.toPath());
                addRow(newF);
            }


            message.setText("файл \""+selectedFile.getName()+"\" імпортовано");
        }
    }

    private void exportFile(String filename) throws IOException {
        String format = filename.substring(filename.length()-3).toLowerCase();

        if(format.equals("xls")||format.equals("xlsx")){
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
                if(new File(String.valueOf(selectedFile)).exists()){
                    Files.delete(selectedFile.toPath());
                }
                Files.copy(oldFile.toPath(),selectedFile.toPath());
                message.setText("файл \""+filename+"\" експортовано");
            }else{

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

    //натискання кнопки "оновити програму"
    public void on_update() throws IOException {
        
    }

    //натискання кнопки "імпорт"
    public void on_import() throws IOException {
        importFile();
        readRows();
    }

    //натискання кнопки "експортувати"
    public void on_export() throws IOException {
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        exportFile(select.getName()+"."+select.getFormat());

    }

    //натискання кнопки "конвертувати"
    public void on_convert()throws IOException{
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        String selectFileName = select.getName()+"."+select.getFormat();

        String format = select.getFormat().toLowerCase();
//        System.out.println(format);
        ObservableList<Person> listP = FXCollections.observableArrayList();
        String filename  = selectFileName.substring(0,selectFileName.length()-4);
//            System.out.println(filterFile(new File (filename+".xls")));
        File newFileName = filterFile(new File (filename+".xls"));
        if (format.equals("dbf")){
            ArrayList<Person> list = DBFViewer.load(selectFileName);
            listP.clear();
            listP.addAll(list);
            ExcelReader.saveFile(listP,"convert-"+newFileName.getName(),select.getPlatizh());// RLKOD_Z - призначення платежу

            addRow(new File("convert-"+newFileName.getName()),select.getPlatizh());
        } else
        if (format.equals("xls")||format.equals("xlsx")){
            listP.clear();
            listP.addAll(ExcelReader.readFile(selectFileName,format));
            ExcelReader.saveFile(listP,"convert-"+newFileName.getName(),select.getPlatizh());// RLKOD_Z - призначення платежу
            addRow(new File("convert-"+newFileName.getName()),select.getPlatizh());
        }

    }

    //натискання кнопки "видалити"
    public void on_delete() throws IOException {
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        if(select != null){
            if(deleteDialog(select.getName()+"."+select.getFormat())){
                deleteRow(select);
                File file = new File(select.getName()+"."+select.getFormat());
                if(file.exists()){
                    Files.delete(file.toPath());
                }

                message.setText("файл \""+select.getName()+"."+select.getFormat()+"\" видалений");
            }
        }
    }

    //вибір файла із списку tableView
    public void on_selected(MouseEvent mouseEvent) throws IOException {

        if(mouseEvent.getClickCount() == 1){
            FileList select = tableFiles.getSelectionModel().getSelectedItem();
            if(select != null){
                message.setText(select.getName()+"."+select.getFormat());

                if (select.getFormat().equals("XLS") || select.getFormat().equals("XLSX")){
                    B_export.setDisable(false);
                }

                if(select.getFormat().equals("DBF")) {
                    B_export.setDisable(true);
                }
            }
        }

        B_delete.setDisable(false);
        B_open.setDisable(false);
    }
//    public void on_edit_name(TableColumn.CellEditEvent event) throws IOException {
//        FileList select = tableFiles.getSelectionModel().getSelectedItem();
//        //   стара назва файлу
//        Path oldname = new File(select.getName()+"."+select.getFormat()).toPath();
//
//        //   видалення старого запису із списку
//        fileList.remove(select);
//        //заміна назви файлу на нову
//        select.setName(event.getNewValue().toString());
//
//        //   нова назва файлу
//        Path newname = new File(select.getName()+"."+select.getFormat()).toPath();
//
//        //  копіювання файла
//        Files.copy(oldname,newname);
//        //  видалення старого файла
//        Files.delete(oldname);
//
//        //додати новий файл у таблицю
//        addRow(new File(select.getName()+"."+select.getFormat()),select.getPlatizh());
//    }
    public void on_edit_platizh(TableColumn.CellEditEvent event) throws IOException {
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        fileList.remove(select);
        select.setPlatizh(event.getNewValue().toString());
        File newfile = new File(select.getName()+"."+select.getFormat());
        addRow(newfile,select.getPlatizh());
        if(select.getFormat().equals("XLS")){
            ExcelReader.rewrite(newfile.getName(),select.getPlatizh());
        }

    }

    //натискання кнопки "відкрити"
    public void open() throws IOException {
        FileList select = tableFiles.getSelectionModel().getSelectedItem();
        String format = select.getFormat().toLowerCase();
        if(format.equals("xls")){
            open_xls(select.getName()+"."+select.getFormat());
        }
        if(format.equals("dbf")){
            open_dbf(select.getName()+"."+select.getFormat());
//            System.out.println(select.getName()+"."+select.getFormat());
        }
    }
}
