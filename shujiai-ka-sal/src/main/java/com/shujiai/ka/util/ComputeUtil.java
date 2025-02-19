package com.shujiai.ka.util;

import com.shujiai.ka.constants.SymbolConstant;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author guowei
 */
public class ComputeUtil {

    /**
     * 判断是否满足运算规则
     * 当前提供：大于，大于等于，等于，小于，小于等于，介于，介于（包含前者），介于（包含后者），介于（包含前者包含后者）
     *
     * @param operator 运算符
     * @param param    参照值
     * @param value    实际值
     * @return 是/否
     */
    public static boolean computeOperator(Object operator, Object param, Object value) {
        if (value == null) {
            return false;
        }
        if (param == null) {
            return false;
        }
        BigDecimal val = new BigDecimal(value.toString());
        BigDecimal par;
        BigDecimal par1;

        if (">".equals(operator)) {
            par = new BigDecimal(param.toString());
            return val.compareTo(par) > 0;
        } else if (">=".equals(operator)) {
            par = new BigDecimal(param.toString());
            return val.compareTo(par) >= 0;
        } else if ("=".equals(operator)) {
            par = new BigDecimal(param.toString());
            return val.compareTo(par) == 0;
        } else if ("<".equals(operator)) {
            par = new BigDecimal(param.toString());
            return val.compareTo(par) < 0;
        } else if ("<=".equals(operator)) {
            par = new BigDecimal(param.toString());
            return val.compareTo(par) <= 0;
        } else {
            String[] split = param.toString().split(SymbolConstant.COMMA);
            if (split.length == 2) {
                par = new BigDecimal(split[0]);
                par1 = new BigDecimal(split[1]);
                List<BigDecimal> collect = Stream.of(par, par1).sorted().collect(Collectors.toList());
                BigDecimal smaller = collect.get(0);
                BigDecimal bigger = collect.get(1);

                if ("<>".equals(operator)) {
                    return val.compareTo(smaller) > 0 && val.compareTo(bigger) < 0;
                } else if ("=<>".equals(operator)) {
                    return val.compareTo(smaller) >= 0 && val.compareTo(bigger) < 0;
                } else if ("<>=".equals(operator)) {
                    return val.compareTo(smaller) > 0 && val.compareTo(bigger) <= 0;
                } else if ("=<>=".equals(operator)) {
                    return val.compareTo(smaller) >= 0 && val.compareTo(bigger) <= 0;
                }
            }
        }
        return false;
    }
}
