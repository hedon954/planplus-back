package common.vo.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 分页查询专用封装类
 *
 * @author Hedon Wang
 * @create 2020-10-15 22:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageVo<T> {

    /**
     * 当前页数
     */
    private Integer currentPage = 0;

    /**
     * 总页数
     */
    private Integer totalPage = 0;

    /**
     * 总记录数
     */
    private Integer totalCount = 0;

    /**
     * 一页有的记录数，不是data的大小
     */
    private Integer pageSize = 0;

    /**
     * 分页数据
     */
    private List<T> data = null;

    /**
     * 设置和页有关的数据，data不会设置
     */
    public void setPageMsg(IPage<?> page) {
        this.currentPage = (int) page.getCurrent();
        this.totalPage = (int) page.getPages();
        this.totalCount = (int) page.getTotal();
        this.pageSize = (int) page.getSize();
    }
}
