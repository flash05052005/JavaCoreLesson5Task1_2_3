import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<Employee> parseCsv(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static List<Employee> parseXML(String fileName) throws Exception {
        long id = 0;
        String firstName = "", lastName = "", country = "";
        int age = 0;
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("employee")) {
                NodeList nodeList1 = nodeList.item(i).getChildNodes();
                for (int a = 1; a < nodeList1.getLength(); a++) {
                    if (nodeList1.item(a).getNodeName().equals("id")) id = Long.valueOf(nodeList1.item(a).getTextContent());
                    if (nodeList1.item(a).getNodeName().equals("firstName")) firstName = nodeList1.item(a).getTextContent();
                    if (nodeList1.item(a).getNodeName().equals("lastName")) lastName = nodeList1.item(a).getTextContent();
                    if (nodeList1.item(a).getNodeName().equals("country")) country = nodeList1.item(a).getTextContent();
                    if (nodeList1.item(a).getNodeName().equals("age")) age = Integer.valueOf(nodeList1.item(a).getTextContent());
                }
                list.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return list;
    }

    public static String readString(String file) {
        String buildString = "", tempString;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (((tempString = br.readLine())) != null) {
                buildString = buildString + tempString;
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return  buildString;
    }

    public static List<Employee> jsonToList (String json) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try {
            Object obj = parser.parse(json);
            jsonArray = (JSONArray) obj;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        for (Object jsonAr: jsonArray) {
           Employee employee = gson.fromJson(jsonAr.toString(), Employee.class);
           list.add(employee);
        }

        return list;
    }

    public static String listToJson(List list) {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        return gson.toJson(list, listType);
    }

    public static void writeString(String stringJson, String fileName) {

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(stringJson);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCsv(columnMapping, fileName);
        writeString(listToJson(list), "data.json");

        fileName = "data.xml";
        try {
            list = parseXML(fileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        writeString(listToJson(list), "data2.json");

        String json = readString("data.json");
        list = jsonToList(json);
        for (Employee element: list) {
            System.out.println(element);
        }
    }
}
