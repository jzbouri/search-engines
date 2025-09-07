import java.io.*;
import java.util.*;

public class Getter {
    Map<String, Integer> urlFileMap;

    public Getter(){
        urlFileMap = getUrlFileMap();
    }

    public void update(){
        urlFileMap = getUrlFileMap();
    }

    public List<String> getOutgoingLinks(String url){
        return FileOperations.readMultiLineString("./crawlFolder/urlFolder/" + urlFileMap.get(url) + "/outgoingUrls");
    }

    public List<String> getIncomingLinks(String url){
        return FileOperations.readMultiLineString("./crawlFolder/urlFolder/" + urlFileMap.get(url) + "/incomingUrls");
    }

    public double getPageRank(String url){
        double pageRank = FileOperations.readDouble("./crawlFolder/urlFolder/" + urlFileMap.get(url) + "/pageRank");

        if(pageRank > 0){
            return pageRank;
        }

        return -1;
    }

    public double getIDF(String word){
        return FileOperations.readDouble("./crawlFolder/wordFolder/" + word);
    }

    public double getTF(String url, String word){
        return FileOperations.readDouble("./crawlFolder/urlFolder/" + urlFileMap.get(url) + "/words/" + word + "/tf");
    }

    public double getTFIDF(String url, String word){
        return FileOperations.readDouble("./crawlFolder/urlFolder/" + urlFileMap.get(url) + "/words/" + word + "/tfIdf");
    }

    public Map<String, Integer> getUrlFileMap(){
        try {
            HashMap<String, Integer> urlFileMap;
            ObjectInputStream in;

            in = new ObjectInputStream(new FileInputStream("./crawlFolder/urlFileMap.ser"));
            urlFileMap = (HashMap<String, Integer>)in.readObject();
            in.close();
            return urlFileMap;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
