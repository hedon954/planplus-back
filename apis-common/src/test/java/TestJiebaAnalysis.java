import common.util.jieba_analysis.huaban.analysis.jieba.JiebaSegmenter;
import common.util.jieba_analysis.huaban.analysis.jieba.SegToken;

import java.util.List;
import java.util.Locale;

/**
 * 测试 jieba 分词工具
 *
 * @author Hedon Wang
 * @create 2020-11-24 11:50
 */
public class TestJiebaAnalysis {

    static String[] sentences =
            new String[]{
                    "明天下午在教五开会",
                    "下下周三在宋卿打篮球哈哈哈哈哈哈哈哈哈哈",
                    "后天下午三点去网易杭研大厦参观采访"
            };

    public static void main(String[] args) {
        JiebaSegmenter segmenter = new JiebaSegmenter();

        /**
         * testCutForSearch
         */
        for (String sentence : sentences) {
            List<SegToken> tokens = segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH);
            for (SegToken segToken:tokens){
                System.out.println(segToken);
            }
            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
        }

        System.out.println();
        System.out.println("=======================分隔符=======================");
        System.out.println();

        /**
         * testCutForIndex
         */
        for (String sentence : sentences) {
            List<SegToken> tokens = segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX);
            for (SegToken segToken:tokens){
                System.out.println(segToken);
            }
            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
        }
    }
}
