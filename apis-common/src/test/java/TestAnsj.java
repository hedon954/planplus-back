import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Hedon Wang
 * @create 2020-11-25 09:18
 */
public class TestAnsj {

    public static void main(String[] args) {

//        String str = "明天下午3点到5点去计算机学院进行计算机组成原理考试" ;
        String str = "明晚前往老街烧烤吃烧烤";
        Result parse = ToAnalysis.parse(str);
        List<Term> terms = parse.getTerms();
        for (Term term : terms){
            System.out.println(term);
            String[] split = term.toString().split("/");
            System.out.println(split[0] + ": " + split[1]);
            System.out.println("getNatureStr:" + term.getNatureStr());
            System.out.println("getOffe:" +term.getOffe());
        }
        System.out.println(ToAnalysis.parse(str));
    }
}
