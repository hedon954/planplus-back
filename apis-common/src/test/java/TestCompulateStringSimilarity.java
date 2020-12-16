import common.util.CompulateStringSimilarity;

/**
 * @author Hedon Wang
 * @create 2020-12-16 12:48
 */
public class TestCompulateStringSimilarity {

    public static void main(String[] args) {
        float levenshtein = CompulateStringSimilarity.levenshtein("吃烧烤", "打爸爸");
        System.out.println(levenshtein);
    }
}
