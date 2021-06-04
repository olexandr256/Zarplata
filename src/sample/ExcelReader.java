package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.models.Person;

import java.io.*;
import java.util.*;

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

    public static ObservableList<Person> readFile(String filename, String typeFile)
            throws IOException {
        FileInputStream is = new FileInputStream(filename);
        ObservableList<Person> list = FXCollections.observableArrayList();
        Map<String, Integer> sortedColumn;
        Iterator<Row> iterator;

        if(typeFile.equals("xlsx")) {
            XSSFWorkbook book = new XSSFWorkbook(is);
            XSSFSheet sheet = book.getSheetAt(0);

            iterator = sheet.iterator();
            sortedColumn = SortedColumn(iterator.next());
            while (iterator.hasNext()) {
                /**
                 * дізнатися які дані у яких стовпцях знаходяться
                 */
                Row row = iterator.next();
                String name = String.valueOf(row.getCell(sortedColumn.get("NAME")));
                String fam = String.valueOf(row.getCell(sortedColumn.get("FAM")));
                String ot = String.valueOf(row.getCell(sortedColumn.get("OT")));
                String INN = String.valueOf(row.getCell(sortedColumn.get("INN")));
                String LSTBL = String.valueOf(row.getCell(sortedColumn.get("LSTBL")));

                String CARD_NO = String.valueOf(row.getCell(sortedColumn.get("CARD_NO")));
                String RLSUM = String.valueOf(row.getCell(sortedColumn.get("RLSUM")));
//                String RLKOD = String.valueOf(row.getCell(sortedColumn.get("RLKOD"))); //неважливе поле

                list.add(new Person(name, fam, ot, INN, LSTBL, CARD_NO, RLSUM));
            }
        } else if(typeFile.equals("xls")) {
            HSSFWorkbook book = new HSSFWorkbook();
            HSSFSheet sheet = book.getSheetAt(0);

            iterator = sheet.iterator();
            sortedColumn = SortedColumn(iterator.next());
            while (iterator.hasNext()) {
                /**
                 * дізнатися які дані у яких стовпцях знаходяться
                 */
                Row row = iterator.next();
                String name = String.valueOf(row.getCell(sortedColumn.get("NAME")));
                String fam = String.valueOf(row.getCell(sortedColumn.get("FAM")));
                String ot = String.valueOf(row.getCell(sortedColumn.get("OT")));
                String INN = String.valueOf(row.getCell(sortedColumn.get("INN")));
                String LSTBL = String.valueOf(row.getCell(sortedColumn.get("LSTBL")));

                String CARD_NO = String.valueOf(row.getCell(sortedColumn.get("CARD_NO")));
                String RLSUM = String.valueOf(row.getCell(sortedColumn.get("RLSUM")));
//                String RLKOD = String.valueOf(row.getCell(sortedColumn.get("RLKOD"))); //неважливе поле

                list.add(new Person(name, fam, ot, INN, LSTBL, CARD_NO, RLSUM));
            }
        }

        is.close();
        return list;
//        saveFile(list,"новий "+filename,"");

    }

    private static Map<String,Integer> SortedColumn(Row next) {
        Map<String,Integer> map = new HashMap<>();
        int index = 0;
        do {
            String cell = String.valueOf(next.getCell(index));
            switch (cell) {
                case "CARD_NO":
                    map.put("CARD_NO", index);
                    break;
                case "LSTBL":
                    map.put("LSTBL", index);
                    break;
                case "FAM":
                    map.put("FAM", index);
                    break;
                case "NAME":
                    map.put("NAME", index);
                    break;
                case "OT":
                    map.put("OT", index);
                    break;
                case "RLSUM":
                    map.put("RLSUM", index);
                    break;
                case "INN":
                    map.put("INN", index);
                    break;
                case "RLCOD":
                    map.put("RLCOD", index);
                    break;
            }
            index++;
        } while (index <= 13);

        map.forEach((k,v) -> System.out.println(k+" - "+v));
        return map;
    }
}
