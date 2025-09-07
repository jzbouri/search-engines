import java.util.*;

public class Searcher {
    Getter g;
    public Searcher(){
        g = new Getter();
    }

    public List<SearchResult> search(String query, boolean boost, int X){
        Map<String, Integer> urlFileMap = g.getUrlFileMap();
        List<SearchResult> results = new ArrayList<>();
        double cosineSimilarity;
        PriorityQueue<SearchResult> resultsQueue = new PriorityQueue<>();

        List<String> words = List.of(query.toLowerCase().split(" "));
        Set<String> wordSet = new HashSet<>(words);
        String[] wordSetAsArray = wordSet.toArray(new String[0]);

        double[] queryVector = new double[wordSetAsArray.length];
        for(int i = 0; i < wordSetAsArray.length; i++){
            queryVector[i] = ((Math.log(1 + (double) Collections.frequency(words, wordSetAsArray[i]) / words.size()) / Math.log(2)) * g.getIDF(wordSetAsArray[i]));
        }

        for (Map.Entry<String, Integer> url : urlFileMap.entrySet()) {
            double[] urlVector = new double[wordSet.size()];
            for(int i = 0; i < wordSetAsArray.length; i++){
                urlVector[i] = g.getTFIDF(url.getKey(), wordSetAsArray[i]);
            }

            double numerator = 0;
            double leftDenominator = 0;
            double rightDenominator = 0;
            for(int i=0; i<queryVector.length; i++){
                numerator += queryVector[i] * urlVector[i];
                leftDenominator += Math.pow(queryVector[i], 2);
                rightDenominator += Math.pow(urlVector[i], 2);
            }
            if(numerator != 0) {
                leftDenominator = Math.sqrt(leftDenominator);
                rightDenominator = Math.sqrt(rightDenominator);
                cosineSimilarity = numerator / (leftDenominator * rightDenominator);
            } else {
                cosineSimilarity = 0;
            }

            if(boost){
                cosineSimilarity *= g.getPageRank(url.getKey());
            }

            resultsQueue.add(new Result(FileOperations.readSingleLine("./crawlFolder/urlFolder/" + url.getValue() + "/title"), cosineSimilarity));
        }

        for(int i=0; i<X; i++){
            results.add(resultsQueue.poll());
        }

        return results;
    }
}
