package common.util;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.extension.api.R;

import java.util.regex.PatternSyntaxException;

/**
 * 邮箱格式判断器
 *
 * @author Hedon Wang
 * @create 2020-11-30 10:05
 */
public class EmailFormatCheckUtils {

    private static final String EMAIL_PATTERN = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isEmailLegal(String str) throws PatternSyntaxException {
        return ReUtil.isMatch(EMAIL_PATTERN,str);
    }
}
