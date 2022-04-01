package co.wangming.nsb.javafx;

import com.codahale.metrics.*;

public class Metrics {

    private static final MetricRegistry metricRegistry;

    static {
        metricRegistry = new MetricRegistry();
    }

    public static Histogram getHistogram(String key) {
        return metricRegistry.histogram(key);
    }

    public static String getHistogramString(String type) {
        Histogram his = metricRegistry.getHistograms().get(type);
        StringBuffer buffer = new StringBuffer();

        buffer.append("msgID:" + type);
        buffer.append(" 计数:" + his.getCount());
        buffer.append(". 耗时统计{ 最大值:" + his.getSnapshot().getMax());
        buffer.append(". 最小值:" + format(his.getSnapshot().getMin()));
        buffer.append(". 平均数:" + format(his.getSnapshot().getMean()));
        buffer.append(". 中位数:" + format(his.getSnapshot().getMedian()));
        buffer.append(". 标准差:" + format(his.getSnapshot().getStdDev()));
        buffer.append(". [耗时分布: 50%<" + format(his.getSnapshot().getValue(0.5)));
        buffer.append("毫秒,  75%<" + format(his.getSnapshot().get75thPercentile()));
        buffer.append("毫秒,  90%<" + format(his.getSnapshot().getValue(0.9)));
        buffer.append("毫秒,  99%<" + format(his.getSnapshot().get99thPercentile()));
        buffer.append("毫秒]}");

        return buffer.toString();
    }

    private static long format(double his) {
        return Double.valueOf(his).intValue();
    }

}
