package com.k3wd.concurrent.study;

import com.k3wd.concurrent.theartofjavaconcurrentprogramming.chapter4.section3.Profiler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 测试一下并行流汇总
 *
 * @author k3wd
 * @date 2023/2/14
 */
public class ParallelStreamSummation {

    public static void main(String[] args) {
        Wallet wallet = new Wallet();
        wallet.init();
        System.out.println("收入为：\t" + wallet.getSum());

        // 测试普通的流求和
        final BigDecimal[] sum1 = {BigDecimal.ZERO};
        List<BigDecimal> historyIncome = wallet.getHistoryIncome();
        Profiler.begin();
        historyIncome.stream().forEach(e -> {
            sum1[0] = sum1[0].add(e);
        });
        System.out.println("求出收入为：\t" + sum1[0] + "\t耗时：\t" + Profiler.end() + "ms");


        // 并行流求和，线程不安全，通过加锁保证线程安全
        // 经测试，在十万内，耗时从普通流的1/10到2/5，后续甚至耗时超过普通流！
        final BigDecimal[] sum2 = {BigDecimal.ZERO};
        Profiler.begin();
        historyIncome.parallelStream().forEach(e -> {
            synchronized (ParallelStreamSummation.class) {
                sum2[0] = sum2[0].add(e);
            }
        });
        System.out.println("求出收入为：\t" + sum2[0] + "\t耗时：\t" + Profiler.end() + "ms");


        // 并行流求和，线程不安全，通过加锁保证线程安全
        // 怎么提高并行流保证在大量数据下仍然有较好的性能？
    }


    public static class Wallet {
        private List<BigDecimal> historyIncome = new ArrayList<>();
        private BigDecimal sum = BigDecimal.ZERO;

        public void init() {
            for (int i = 0; i < 100000; i++) {
                Random random = new Random();
                BigDecimal income = new BigDecimal(random.nextInt(10000) + 5000);
                historyIncome.add(income);
                sum = sum.add(income);
            }
        }

        public List<BigDecimal> getHistoryIncome() {
            return historyIncome;
        }

        public void setHistoryIncome(List<BigDecimal> historyIncome) {
            this.historyIncome = historyIncome;
        }

        public BigDecimal getSum() {
            return sum;
        }

        public void setSum(BigDecimal sum) {
            this.sum = sum;
        }

    }
}
