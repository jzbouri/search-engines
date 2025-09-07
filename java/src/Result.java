public class Result implements SearchResult, Comparable<Result>{
    String title;
    double score;
    public Result(String newTitle, double newScore){
        title = newTitle;
        score = newScore;
    }

    public String getTitle() {
        return title;
    }

    public double getScore() {
        return score;
    }

    public int compareTo(Result o) {
        if(Double.compare((Math.floor(o.getScore() * 1000) / 1000), Math.floor(getScore() * 1000) / 1000) == 0){
            return getTitle().compareTo(o.getTitle());
        }
        return Double.compare(o.getScore(), getScore());
    }
}
