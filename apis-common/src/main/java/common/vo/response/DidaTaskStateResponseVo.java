package common.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Ruolin
 * @create 2020-12-05 12:00
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DidaTaskStateResponseVo {
    private int[] numOfTasks;
    private int[] numOfFinishedTasks;
    private String[] dateOfWeek;
    private float[]  completePercentage;
    private int[] numOfDelay;
}
