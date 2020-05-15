import TemplatePackage.RowTemplate;
import com.google.gson.Gson;
import org.intellij.lang.annotations.Pattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class Client extends JFrame {

    private JTextArea inputArea = new JTextArea("2020-05-01&2020-05-03");
    private JTextField outputArea = new JTextField("");
    JButton button=new JButton("Report for latest values");
    JButton button2=new JButton("Report for latest values");

    String[] parts;

    public void ConfigureJFrame(int sizeX, int sizeY, int locationX, int locationY){
        setLayout(new GridLayout(10,2));
        setSize(sizeX, sizeY);
        setLocation(locationX, locationY);

        JButton button=new JButton("Report for latest values");
        JButton button2=new JButton("Detailed report of temperature(needs 2 dates seperated by coma)");
        JButton button3=new JButton("Detailed report of humidity(needs 2 dates seperated by coma)");
        JButton button4=new JButton("Detailed report of lumen(needs 2 dates seperated by coma)");
        JButton button5=new JButton("Electrical cost of (needs 2 dates seperated by coma)");
        JButton button6=new JButton("Add new value (exempel: 20.5&50&...)");
        JButton button7 =new JButton("Show latest values here");

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String string = inputArea.getText();
                    parts = string.split("&");
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    inputArea.setText(null);
                }
            }

        });

        button.addActionListener(e -> {
            try {
                String data = GetData("http://localhost:8080//GetLatestRow");
                System.out.println(data);
                CreateReport1(data);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        button2.addActionListener(e -> {
            try {
                String data = GetData("http://localhost:8080//GetDataBetweenDateTimeRange/"+parts[0]+","+parts[1]+"");
                System.out.println(data);
                CreateReport2Temp(data);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        button3.addActionListener(e -> {
            try {
                String data = GetData("http://localhost:8080/GetDataBetweenDateTimeRange/"+parts[0]+","+parts[1]+"");
                System.out.println(data);
                CreateReport2Humidity(data);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        button4.addActionListener(e -> {
            try {
                String data = GetData("http://localhost:8080/GetDataBetweenDateTimeRange/"+parts[0]+","+parts[1]+"");
                System.out.println(data);
                CreateReport2Lumen(data);


            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        button5.addActionListener(e -> {
            try {
                String data = GetData("http://localhost:8080/GetDataBetweenDateTimeRange/"+parts[0]+","+parts[1]+"");
                System.out.println(data);
                CreateReportElectricalCost(data);


            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        button6.addActionListener(e -> {
            try {
                AddRow("http://localhost:8080/AddRow/"+parts[0]+","+parts[1]+","+parts[2]+"");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        button7.addActionListener(e -> {
            try {
                String data = GetData("http://localhost:8080//GetLatestRow");
                System.out.println(data);
                Gson gson = new Gson();
                RowTemplate[] row = gson.fromJson(data, RowTemplate[].class);
                outputArea.setText("Temperature: "+row[0].getTemperature()+" Humidity: "+row[0].getHumidity()+"Lumen: "+row[0].getLumen()+" Created: "+row[0].getTimestamp());

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        add(button);
        add(inputArea);
        add(button2);
        add(button3);
        add(button4);
        add(button5);
        add(button6);
        add(button7);
        add(outputArea);


        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public Client (){
        ConfigureJFrame(600,800,300,300);
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        while(true){


        }

    }

    public static void WriteToFile(String report){
        String fileSuffix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        try {
            FileWriter myWriter = new FileWriter("Reports/Report "+fileSuffix+".txt",true);
            myWriter.write(report);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void CreateReport2Temp(String data){
        Gson gson = new Gson();
        RowTemplate[] templateList = gson.fromJson(data, RowTemplate[].class);

        StringBuilder stringBuilder = new StringBuilder("Report "+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"\r\n\n");
        stringBuilder.append( "- "+templateList[0].getTimestamp()+" -\r\n");

        int latestDay = templateList[0].getTimestamp().getDay();
        int counter = 0;
        float temperatures = 0.0f;
        for(RowTemplate template : templateList) {

            if(template.getTimestamp().getDay() == latestDay)
            stringBuilder.append("Temperature:"+template.getTemperature()+" | time: "+template.getTimestamp()+"\r\n");
            else{
                latestDay = template.getTimestamp().getDay();
                stringBuilder.append("\r\n - "+template.getTimestamp()+" -\r\n");
                stringBuilder.append("Temperature:"+template.getTemperature()+" | time: "+template.getTimestamp()+"\r\n");
            }
            temperatures += template.getTemperature();
            counter++;
        }
        float avg = temperatures / counter;

        stringBuilder.append("\r\n - Average temperature across these dates: "+avg+" -\r\n");

        WriteToFile(stringBuilder.toString());
    }
    public static void CreateReport2Humidity(String data){
        Gson gson = new Gson();
        RowTemplate[] templateList = gson.fromJson(data, RowTemplate[].class);

        StringBuilder stringBuilder = new StringBuilder("Report "+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"\r\n\n");
        stringBuilder.append( "- "+templateList[0].getTimestamp()+" -\r\n");

        int latestDay = templateList[0].getTimestamp().getDay();
        int counter = 0;
        float Humidity = 0.0f;
        for(RowTemplate template : templateList) {

            if(template.getTimestamp().getDay() == latestDay)
                stringBuilder.append("Humidity:"+template.getHumidity()+" | time: "+template.getTimestamp()+"\r\n");
            else{
                latestDay = template.getTimestamp().getDay();
                stringBuilder.append("\r\n - "+template.getTimestamp()+" -\r\n");
                stringBuilder.append("Humidity:"+template.getHumidity()+" | time: "+template.getTimestamp()+"\r\n");
            }
            Humidity += template.getHumidity();
            counter++;
        }
        float avg = Humidity / counter;

        stringBuilder.append("\r\n - Average humidity across these dates: "+avg+" -\r\n");

        WriteToFile(stringBuilder.toString());
    }
    public static void CreateReport2Lumen(String data){
        Gson gson = new Gson();
        RowTemplate[] templateList = gson.fromJson(data, RowTemplate[].class);

        StringBuilder stringBuilder = new StringBuilder("Report "+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"\r\n\n");
        stringBuilder.append( "- "+templateList[0].getTimestamp()+" -\r\n");

        int latestDay = templateList[0].getTimestamp().getDay();
        int counter = 0;
        float Lumen = 0.0f;
        for(RowTemplate template : templateList) {

            if(template.getTimestamp().getDay() == latestDay)
                stringBuilder.append("Lumen:"+template.getLumen()+" | time: "+template.getTimestamp()+"\r\n");
            else{
                latestDay = template.getTimestamp().getDay();
                stringBuilder.append("\r\n - "+template.getTimestamp()+" -\r\n");
                stringBuilder.append("Lumen:"+template.getLumen()+" | time: "+template.getTimestamp()+"\r\n");
            }
            Lumen += template.getLumen();
            counter++;
        }
        float avg = Lumen / counter;

        stringBuilder.append("\r\n - Average humidity across these dates: "+avg+" -\r\n");

        WriteToFile(stringBuilder.toString());
    }
    public static void CreateReport1(String data){

        StringBuilder stringBuilder = new StringBuilder("Report "+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"\r\n");

        Gson gson = new Gson();
        RowTemplate[] templateList = gson.fromJson(data, RowTemplate[].class);

        for(RowTemplate template : templateList) {
            stringBuilder.append("Temperature:"+template.getTemperature()+" | Humidity:"+template.getHumidity()+" | Lumen:"+template.getLumen()+" | time: "+template.getTimestamp()+"\r\n");
        }

        WriteToFile(stringBuilder.toString());
    }
    public static void CreateReportElectricalCost(String data){

        StringBuilder stringBuilder = new StringBuilder("Report "+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"\r\n");

        Gson gson = new Gson();
        RowTemplate[] templateList = gson.fromJson(data, RowTemplate[].class);

        int totalLumen = 0;
        int counter = 0;
        for(RowTemplate template : templateList) {
            totalLumen += template.getLumen();
            counter++;
        }
        float avg = (totalLumen / counter);
        float cost = (float) (avg * 1.5);

        stringBuilder.append("The avg cost for this period is "+cost+" kr/kWh.\r\nThe total cost for the period is "+(totalLumen*1.5));

        WriteToFile(stringBuilder.toString());
    }

    public static String GetData(String restUrl) throws IOException {

        URL url = new URL(restUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        String result = null;
        StringBuilder response = new StringBuilder();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((result = in.readLine()) != null) {
                response.append(result);
            } in .close();
            System.out.println("JSON String Result " + response.toString());

        }
        else {
            System.out.println("GET NOT WORKED");
        }
        return response.toString();
    }
    public static void AddRow(String restUrl) throws IOException {

        URL url = new URL(restUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Success!");
        }
        else {
            System.out.println("FAILURE");
        }
    }
}
