package com.shujiai.ka.util;

import cn.hutool.core.util.NumberUtil;
import com.shujiai.ka.constant.KAResultCodes;
import com.shujiai.base.exception.BizException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 数据处理
 *
 * @author jiuyi
 * @date 2023/11/4 14:28
 */
public class NumUtil {
    public static final int INT_ZERO = 0;
    public static final long LONG_ZERO = 0L;
    public static final Integer INTEGER_100 = 100;
    public static final BigDecimal DECIMAL_100 = new BigDecimal(100);

    public static int parseInt(Integer v) {
        return Objects.isNull(v) ? INT_ZERO : v;
    }

    public static int parseInt(Integer v, int defaultValue) {
        return Objects.isNull(v) ? defaultValue : v;
    }

    public static int parseInt(Object v, int defaultValue) {
        return Objects.isNull(v) ? defaultValue : parseInt(v);
    }

    public static int parseIntNullToZero(String v) {
        return isNumber(v) ? Integer.parseInt(v) : INT_ZERO;
    }

    public static boolean isNumber(String v) {
        return NumberUtil.isNumber(v);
    }

    public static BigDecimal parseBigDecimal(String txt) {
        if (StringUtils.hasText(txt)) {
            return new BigDecimal(txt);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal parseBigDecimal(Object obj) {
        if (Objects.nonNull(obj)) {
            if (obj instanceof String) {
                return new BigDecimal((String) obj);
            }
            if (obj instanceof Integer) {
                return new BigDecimal((Integer) obj);
            }
            if (obj instanceof Double) {
                return BigDecimal.valueOf((Double) obj);
            }
            if (obj instanceof Long) {
                return BigDecimal.valueOf((Long) obj);
            }
            throw new BizException(KAResultCodes.KA_ILLEGAL_OBJECT);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal parseBigDecimal(Integer v) {
        if (Objects.nonNull(v)) {
            return BigDecimal.valueOf(v);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 判断是否整数
     *
     * @param v
     * @return
     */
    public static boolean isLong(String v) {
        return NumberUtil.isLong(v);
    }

    public static boolean isDouble(String v) {
        return NumberUtil.isDouble(v);
    }

    public static boolean isNumber(Object v) {
        if (Objects.isNull(v)) {
            return false;
        }
        return NumberUtil.isNumber(String.valueOf(v));
    }

    public static int parseInt(String v) {
        return StringUtils.hasText(v) ? Integer.parseInt(v) : INT_ZERO;
    }

    public static int parseInt(Object v) {
        if (Objects.isNull(v)) {
            return INT_ZERO;
        }
        return parseInt(String.valueOf(v));
    }

    public static long parseLong(Long v) {
        return Objects.isNull(v) ? LONG_ZERO : v;
    }

    public static long parseLong(String v) {
        return NumberUtil.parseLong(v);
    }

    public static double parseDouble(String v) {
        return NumberUtil.parseDouble(v);
    }

    public static boolean equal(Long x, Long y) {
        long xl = parseLong(x);
        long yl = parseLong(y);
        return xl == yl;
    }

    public static boolean equal(Integer x, Integer y) {
        long xl = parseInt(x);
        long yl = parseInt(y);
        return xl == yl;
    }

    public static int compare(Integer x, Integer y) {
        int xl = parseInt(x);
        int yl = parseInt(y);
        return Integer.compare(xl, yl);
    }

    public static boolean isGreat(Integer x, Integer y) {
        int xl = parseInt(x);
        int yl = parseInt(y);
        return xl > yl;
    }
}
