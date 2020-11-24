import common.util.timenlp.nlp.TimeNormalizer;
import common.util.timenlp.nlp.TimeUnit;
import common.util.timenlp.util.DateUtil;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Hedon Wang
 * @create 2020-11-24 10:51
 */
public class TestTimeNlp {

    public static void main(String[] args) throws URISyntaxException {
        URL url = TimeNormalizer.class.getResource("/TimeExp.m");
        TimeNormalizer normalizer = new TimeNormalizer(url.toURI().toString());
        normalizer.setPreferFuture(true);

        normalizer.parse("Hi，all.下周一下午开会");// 抽取时间
        TimeUnit[] unit = normalizer.getTimeUnit();
        System.out.println("Hi，all.下周一下午开会");
        System.out.println(DateUtil.formatDateDefault(unit[0].getTime()) + "-" + unit[0].getIsAllDayTime());


        normalizer.parse("Hi，all.下周一开会");// 抽取时间
        unit = normalizer.getTimeUnit();
        System.out.println("Hi，all.下周一开会");
        System.out.println(DateUtil.formatDateDefault(unit[0].getTime()) + "-" + unit[0].getIsAllDayTime());


        normalizer.parse("周四下午三点到五点开会");// 多时间识别，注意第二个时间点用了第一个时间的上文
        unit = normalizer.getTimeUnit();
        System.out.println("周四下午三点到五点开会");
        System.out.println(DateUtil.formatDateDefault(unit[0].getTime()) + "-" + unit[0].getIsAllDayTime());
        System.out.println(DateUtil.formatDateDefault(unit[1].getTime()) + "-" + unit[1].getIsAllDayTime());

    }
}
