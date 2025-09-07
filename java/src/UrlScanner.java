import java.io.*;
import java.util.*;

public class UrlScanner {
    private String[] urlStringArray;

    private String buildURL(String parentUrl, String childLine){

        if(childLine.contains("http")){
            return childLine.split("href=\"./")[1].split("\">")[0];
        }

        return parentUrl.substring(0, parentUrl.lastIndexOf("/")) + childLine.split("href=\".")[1].split("\"")[0];
    }

    public Set<String> scan(String seed, int urlCount, Map<String, Integer> wordDocFreqs){int totalWordCount = 0;
        boolean isWords = false;
        Set<String> outgoingUrls = new HashSet<>();
        Map<String, Double> tfs = new HashMap<>();

        try {
            urlStringArray = WebRequester.readURL(seed).split("\\n");
        } catch (IOException e){
            System.out.println("Error: Cannot read URL " + seed);
        }

        String urlFolderString = "./crawlFolder/urlFolder/" + urlCount;
        new File(urlFolderString).mkdir();
        new File(urlFolderString + "/words").mkdir();
        FileOperations.write("./crawlFolder/urlFolder/" + urlCount + "/urlAddress", seed);

        for(String line : urlStringArray){
            if(line.contains("<title>")){
                String title = line.split("title>")[1].substring(0, line.split("title>")[1].length() - 2);
                FileOperations.write("./crawlFolder/urlFolder/" + urlCount + "/title", title);
            }

            if(line.contains("</p>")){
                isWords = false;

                for (Map.Entry<String, Double> word : tfs.entrySet()) {
                    word.setValue(word.getValue() / totalWordCount);
                    new File(urlFolderString + "/words/" + word.getKey()).mkdir();
                    FileOperations.write("./crawlFolder/urlFolder/" + urlCount + "/words/" + word.getKey() + "/tf", String.valueOf(word.getValue()));
                    wordDocFreqs.put(word.getKey(), wordDocFreqs.getOrDefault(word.getKey(), 0) + 1);
                }
            }
            if(isWords){
                for(String word : line.split(" ")){
                    tfs.put(word, tfs.getOrDefault(word, 0.0) + 1);
                    totalWordCount++;
                }
            }
            if(line.contains("<p")){
                isWords = true;
            }

            if(line.contains("href")){
                String childUrl = buildURL(seed, line);
                if(outgoingUrls.add(childUrl)){
                    FileOperations.append("./crawlFolder/urlFolder/" + urlCount + "/outgoingUrls", childUrl);
                }
            }
        }

        return outgoingUrls;
    }
}
