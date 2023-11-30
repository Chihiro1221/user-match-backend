package com.haonan.utils;

import com.haonan.exception.BusinessException;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 算法工具类
 *
 * @author haonan
 */
public class AlgorithmUtils {
    /**
     * 编辑距离算法，（用于计算距离相近的字符串）
     *
     * @param tagList1
     * @param tagList2
     * @return
     */
    public static Integer minDistance(List<String> tagList1, List<String> tagList2) {
        if (CollectionUtils.isEmpty(tagList1) || CollectionUtils.isEmpty(tagList2)) {
            return Integer.MAX_VALUE;
        }
        int n = tagList1.size();
        int m = tagList2.size();

        if (n * m == 0)
            return n + m;

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!tagList1.get(i - 1).equals(tagList2.get(j - 1)))
                    left_down += 1;
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}
