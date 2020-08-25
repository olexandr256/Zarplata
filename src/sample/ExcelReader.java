package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.models.Person;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ExcelReader {
    public static void saveFile(ObservableList<Person> listPerson, String filename, String platizh) throws IOException {
        Workbook book = new HSSFWorkbook(); //створення книги
        Sheet sheet = book.createSheet("zarplata");  //створення листа

        // Нумерация починається з нуля
        Row row = sheet.createRow(0);

        //створення комірок у рядку почерзі починаючи із 0,1,2,...
        Cell RLSUM = row.createCell(0);
        RLSUM.setCellValue("RLSUM");
        Cell LSTBL = row.createCell(1);
        LSTBL.setCellValue("LSTBL");
        Cell INN = row.createCell(2);
        INN.setCellValue("INN");
        //призначення платежу
        Cell RLKOD = row.createCell(3);
        RLKOD.setCellValue("RLKOD");

        Cell CARD_NO = row.createCell(4);
        CARD_NO.setCellValue("CARD_NO");
        int num = 0;
        for(Person elem:listPerson)
        {
            Double res = Double.parseDouble(elem.getRlsum());
            if(res > 0){
                num++;
                Row rowElem = sheet.createRow(num);

                rowElem.createCell(0).setCellValue(String.valueOf(res).replace(".",","));
                rowElem.createCell(1).setCellValue(elem.getLstbl());
                rowElem.createCell(2).setCellValue(elem.getInn());
                rowElem.createCell(3).setCellValue(platizh);
                rowElem.createCell(4).setCellValue(elem.getCard_no());

            }
        };

        // Меняем размер столбца
        sheet.autoSizeColumn(1);
        // Записываем всё в файл
        FileOutputStream fs = new FileOutputStream(filename);
        book.write(fs);
        book.close();
        fs.close();
    }

    public static void  rewrite(String filename, String platizh) throws IOException {

        FileInputStream is = new FileInputStream(filename);
        Workbook book = new HSSFWorkbook(is);

        Sheet sheet = book.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        iterator.next();
        while(iterator.hasNext()){
            Row row = iterator.next();
            row.getCell(3).setCellValue(platizh);
        }

        is.close();
        FileOutputStream os = new FileOutputStream(filename);
        book.write(os);
        os.close();
    }

    public static ObservableList<Person> readFile(String filename) throws IOException {
        FileInputStream is = new FileInputStream(filename);
        XSSFWorkbook book = new XSSFWorkbook(is);
        XSSFSheet sheet = book.getSheetAt(0);
        ObservableList<Person> list = FXCollections.observableArrayList();

        Iterator<Row> iterator = sheet.iterator();
        iterator.next();
        while(iterator.hasNext()){
            /**
             * дізнатися які дані у яких стовпцях знаходяться
             */
            Row row = iterator.next();
            String name = String.valueOf(row.getCell(6));
            String fam = String.valueOf(row.getCell(5));
            String ot = String.valueOf(row.getCell(7));
            String INN = String.valueOf(row.getCell(9));
            String LSTBL = String.valueOf(row.getCell(4));

            String CARD_NO = String.valueOf(row.getCell(3));
            String RLSUM = String.valueOf(row.getCell(8));
            String RLKOD = String.valueOf(row.getCell(10)); //неважливе поле

            list.add(new Person(name,fam,ot,INN,LSTBL,CARD_NO,RLSUM));
        }

        is.close();

        return list;
//        saveFile(list,"новий "+filename,"");

    }
}
