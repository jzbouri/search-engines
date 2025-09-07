import java.util.*;
import java.io.*;

public class Crawler {
    private final Queue<String> urlsToScan;
    private final List<String> scannedUrls;
    private final Map<String, Integer> urlFileMap;
    private final Map<String, Set<String>> incomingUrlsMap;
    private final Map<String, Integer> wordDocFreqs;
    private int urlCount;
    public Crawler(){
        urlsToScan = new LinkedList<>();
        scannedUrls = new ArrayList<>();
        urlFileMap = new HashMap<>();
        incomingUrlsMap = new HashMap<>();
        wordDocFreqs = new HashMap<>();
        urlCount = 0;
    }

    private void serializeUrlFileMap(){
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("./crawlFolder/urlFileMap.ser"))) {
            outputStream.writeObject(urlFileMap);
        } catch (IOException e) {
            System.out.println("Error: Cannot serialize urlFileMap");
        }
    }

    private void uploadIncomingLinks(){
        for(Map.Entry<String, Integer> currentUrl : urlFileMap.entrySet()){
            for(String incomingUrl : incomingUrlsMap.get(currentUrl.getKey())){
                FileOperations.append("./crawlFolder/urlFolder/" + currentUrl.getValue() + "/incomingUrls", incomingUrl);
            }
        }
    }

    private void uploadPageRanks(){
        double[][] adjacencyMatrix = new double[urlCount][urlCount];

        for(int i = 0; i < urlCount; i++){
            List<String> outgoing;
            int outgoingCount = 0;

            outgoing = FileOperations.readMultiLineString("./crawlFolder/urlFolder/" + i + "/outgoingUrls");

            for(String url : outgoing){
                adjacencyMatrix[i][urlFileMap.get(url)] = 1;
                outgoingCount++;
            }

            for(int j=0; j<urlCount; j++){
                adjacencyMatrix[i][j] = (adjacencyMatrix[i][j] / outgoingCount * 0.9) + (0.1 / urlCount);
            }
        }

        double[] t1 = new double[urlCount];
        Arrays.fill(t1, (double) 1 / urlCount);
        double[] t2 = createT2(t1, adjacencyMatrix);
        double distance = getEuclidianDistance(t1, t2);

        while(distance > 0.0001){
            double[] temp = t2;
            t2 = createT2(t2, adjacencyMatrix);
            t1 = temp;

            distance = getEuclidianDistance(t1, t2);
        }

        for(int i = 0; i < urlCount; i++){
            FileOperations.write("./crawlFolder/urlFolder/" + i + "/pageRank", Double.toString(t2[i]));
        }
    }

    private void uploadIdfs(){
        new File("./crawlFolder/", "wordFolder").mkdir();
        for (Map.Entry<String, Integer> word : wordDocFreqs.entrySet()) {
            FileOperations.write("./crawlFolder/wordFolder/" + word.getKey(), String.valueOf(Math.log((double)urlCount / (1 + word.getValue())) / Math.log(2)));
        }
    }

    private void uploadTfIdfs(){
        for(Map.Entry<String, Integer> currentUrl : urlFileMap.entrySet()){
            String url = currentUrl.getKey();
            File words = new File("./crawlFolder/urlFolder/" + currentUrl.getValue() + "/words");
            words.mkdir();
            for(File wordFolder : words.listFiles()){
                FileOperations.write("./crawlFolder/urlFolder/" + currentUrl.getValue() + "/words/" + wordFolder.getName() + "/tfIdf", String.valueOf((Math.log(1 + getTf(url, wordFolder.getName())) / Math.log(2)) * getIdf(wordFolder.getName())));
            }
        }
    }

    private double getIdf(String word){
        return FileOperations.readDouble("./crawlFolder/wordFolder/" + word);
    }
    private double getTf(String url, String word){
        return FileOperations.readDouble("./crawlFolder/urlFolder/" + urlFileMap.get(url) + "/words/" + word + "/tf");
    }

    private double[] createT2(double[] t1, double[][] adjacencyMatrix){
        double[] result = new double[urlCount];

        for(int i=0; i<urlCount; i++){
            double newValue = 0;
            for(int j=0; j<urlCount; j++){
                newValue += t1[j] * adjacencyMatrix[j][i];
            }
            result[i] = newValue;
        }

        return result;
    }

    private double getEuclidianDistance(double[] a, double[] b){
        double distance = 0;

        for(int i=0; i<urlCount; i++){
            distance += Math.pow((a[i] - b[i]), 2);
        }

        return Math.sqrt(distance);
    }

    public void crawl(String seed){
        new File(System.getProperty("user.dir") + "/crawlFolder").mkdir();
        new File("./crawlFolder/urlFolder").mkdir();

        UrlScanner scan = new UrlScanner();
        urlsToScan.add(seed);

        while(!urlsToScan.isEmpty()){
            String currentUrl = urlsToScan.poll();
            Set<String> newBatch = scan.scan(currentUrl, urlCount, wordDocFreqs);
            urlFileMap.put(currentUrl, urlCount);
            scannedUrls.add(currentUrl);
            urlCount++;

            for(String url : newBatch){

                if(incomingUrlsMap.containsKey(url)){
                    Set<String> existingIncoming = incomingUrlsMap.get(url);
                    existingIncoming.add(currentUrl);
                } else {
                    Set<String> newIncoming = new HashSet<>();
                    newIncoming.add(currentUrl);
                    incomingUrlsMap.put(url, newIncoming);
                }

                if(!scannedUrls.contains(url) && !urlsToScan.contains(url)){
                    urlsToScan.offer(url);
                }
            }
            newBatch.clear();
        }

        serializeUrlFileMap();
        uploadIncomingLinks();
        uploadPageRanks();
        uploadIdfs();
        uploadTfIdfs();
    }
}
