package com.haonan.service;

import com.haonan.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class AlgorithmUtilsTest {
    @Test
    void testEditDistance() {
        List<String> tagList1 = Arrays.asList("Java", "大一", "篮球");
        List<String> tagList2 = Arrays.asList("Java", "大二", "篮球");
        List<String> tagList3 = Arrays.asList("Python", "大一", "羽毛球");
        int i = AlgorithmUtils.minDistance(tagList1, tagList2);
        int i2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.println(i);
        System.out.println(i2);
    }
}
