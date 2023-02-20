package com.k3wd.concurrent.practicaljavahighconcurrencyprogramming.chapter5.section8;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 并行排序
 *
 * @author k3wd
 * @date 2023/2/20
 */
public class PSortDemo {

    public static void main(String[] args) throws InterruptedException {
        int[] array = {1, 312, 441, 12, 1, 5};
//        bubbleSort(array);
//        parityChangeSort(array);
//        OddEventSort(array);
//        PSort.pOddEventSort(array);
        System.out.println(Arrays.toString(array));
    }


    /**
     * 冒泡排序
     */
    public static void bubbleSort(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 奇偶交换排序
     */
    public static void parityChangeSort(int[] array) {
        boolean isExchange = false;
        int start = 0;
        // 结束条件：上一轮是偶校验且没发生变化
        while (start == 0 || isExchange) {
            isExchange = false;
            for (int i = start; i < array.length - 1; i = i + 2) {
                if (array[i] > array[i + 1]) {
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                    isExchange = true;
                }
            }
            if (start == 1) {
                start = 0;
            } else {
                start = 1;
            }
        }
    }

    /**
     * 并行奇偶排序
     */
    private static void OddEventSort(int[] array) {
        boolean isExchange = false;

        int start = 0;
        // 结束条件：上一轮是偶校验且没发生变化
        while (start == 0 || isExchange) {
            isExchange = false;
            for (int i = start; i < array.length - 1; i = i + 2) {
                if (array[i] > array[i + 1]) {
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                    isExchange = true;
                }
            }
            if (start == 1) {
                start = 0;
            } else {
                start = 1;
            }
        }
    }

}
