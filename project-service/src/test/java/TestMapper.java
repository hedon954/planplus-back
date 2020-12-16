import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hedon.ProjectApplication;
import common.entity.DidaTask;
import common.mapper.DidaTaskMapper;
import common.util.CompulateStringSimilarity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Hedon Wang
 * @create 2020-12-16 11:06
 */

@SpringBootTest(classes = {ProjectApplication.class})
public class TestMapper {

    @Autowired
    DidaTaskMapper didaTaskMapper;

    @Test
    public void testSelectByTime(){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime start = LocalDateTime.now().minusHours(2);

        LocalDateTime finish = LocalDateTime.now().plusHours(10);

        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectTasksByTimeRegion(12, dtf.format(start), dtf.format(finish));

        for (DidaTask didaTask : didaTasks){
            System.out.println(didaTask);
        }
    }

    @Test
    public void testGetByStatus(){

        Date start = new Date();

        String taskContent = "吃饭";

        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectByStatus(13, 2);
        System.out.println(didaTasks);
        System.out.println(didaTasks.size());

        //去掉瞬时任务
        ArrayList<DidaTask> needToRemove = new ArrayList<>();
        for (DidaTask didaTask: didaTasks){
            if (didaTask.getTaskStartTime().toEpochSecond(ZoneOffset.UTC) == didaTask.getTaskPredictedFinishTime().toEpochSecond(ZoneOffset.UTC)){
                needToRemove.add(didaTask);
            }
        }
        didaTasks.removeAll(needToRemove);


        //根据相似度进行排序
        didaTasks.sort(new Comparator<DidaTask>() {
            @Override
            public int compare(DidaTask o1, DidaTask o2) {
                Float similarity1 = CompulateStringSimilarity.levenshtein(o1.getTaskContent(), taskContent);
                Float similarity2 = CompulateStringSimilarity.levenshtein(o2.getTaskContent(), taskContent);
                return similarity1.compareTo(similarity2);
            }
        });

        //去掉相似度小于10%
        needToRemove = new ArrayList<>();
        for (DidaTask didaTask:didaTasks){
            if (CompulateStringSimilarity.levenshtein(didaTask.getTaskContent(), taskContent) < 0.1){
                needToRemove.add(didaTask);
            }
        }
        didaTasks.removeAll(needToRemove);


        //计算耗时
        float weights = (1+didaTasks.size())*didaTasks.size()/2;
        float weight =0.0f;
        LocalDateTime startTime;
        LocalDateTime finishTime;
        long consumedTimeSum = 0;
        for (int i = 0; i < didaTasks.size(); i++) {
            weight = (didaTasks.size() - i)/weights;
            startTime = didaTasks.get(i).getTaskRealStartTime();
            finishTime = didaTasks.get(i).getTaskRealFinishTime();
            consumedTimeSum += (finishTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) * weight;
        }

        //将耗时秒数转为：天/时/分
        long day = consumedTimeSum / (60 * 60 * 24);
        long hour = (consumedTimeSum %(60 * 60 * 24))/(60 * 60);
        long minutes = (consumedTimeSum %(60 * 60))/ 60;

        String consumedTime = (day==0? "": day + " 天 ") + (hour==0? "": hour + " 小时 ") + minutes + " 分钟";

        System.out.println(consumedTime);

        Date end = new Date();
        System.out.println("执行时间："+ (end.getTime() - start.getTime()));

    }

}
