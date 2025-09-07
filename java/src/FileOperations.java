import java.io.*;
import java.util.*;

public class FileOperations {
    private FileOperations() {}

    public static void write(String fileName, String content){
        try {
            PrintWriter out;
            out = new PrintWriter(new FileWriter(fileName));
            out.print(content);
            out.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot write to file " + fileName);
        }
    }

    public static void append(String fileName, String content) {
        try {
            PrintWriter out;
            out = new PrintWriter(new FileWriter(fileName, true));
            out.println(content);
            out.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot write to file " + fileName);
        }
    }

    public static double readDouble(String fileName){
        try {
            BufferedReader in;
            in = new BufferedReader(new FileReader(fileName));
            double output = Double.parseDouble(in.readLine());
            in.close();
            return output;
        } catch (IOException ex) {
            return 0;
        }
    }

    public static List<String> readMultiLineString(String fileName) {
        try {
            BufferedReader in;
            in = new BufferedReader(new FileReader(fileName));
            List<String> output = new ArrayList<>();
            String line = in.readLine();

            while (line != null) {
                output.add(line);
                line = in.readLine();
            }

            in.close();
            return output;
        } catch (IOException e) {
            return null;
        }
    }

    public static String readSingleLine(String fileName) {
        try {
            BufferedReader in;
            in = new BufferedReader(new FileReader(fileName));
            String output = in.readLine();
            in.close();
            return output;
        } catch (IOException e) {
            return null;
        }
    }
}
