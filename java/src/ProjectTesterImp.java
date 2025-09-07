import java.io.*;
import java.util.*;

public class ProjectTesterImp implements ProjectTester{
    private final Crawler c;
    private final Getter g;

    ProjectTesterImp(){
        c = new Crawler();
        g = new Getter();
    }

    public void initialize(){
        File crawlFolder = new File("./crawlFolder");
        deleteDirectory(crawlFolder);
    }

    public void crawl(String seed){
        c.crawl(seed);
        g.update();
    }

    public List<String> getOutgoingLinks(String url){
        return g.getOutgoingLinks(url);
    }

    public List<String> getIncomingLinks(String url){
        return g.getIncomingLinks(url);
    }

    public double getPageRank(String url){
        return g.getPageRank(url);
    }

    public double getIDF(String word){
        return g.getIDF(word);
    }

    public double getTF(String url, String word){
        return g.getTF(url, word);
    }

    public double getTFIDF(String url, String word){
        return g.getTFIDF(url, word);
    }

    public List<SearchResult> search(String query, boolean boost, int X){
        Searcher s = new Searcher();
        return s.search(query, boost, X);
    }

    private void deleteDirectory(File file) {
        File[] contents = file.listFiles();

        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }

        file.delete();
    }
}
