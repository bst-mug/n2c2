package at.medunigraz.imi.bst.n2c2.model.metrics;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.util.List;
import java.util.Map;

public interface Metrics {

    double getOfficialRankingMeasure();

    double getOfficialRankingMeasureByCriterion(Criterion c);

    void add(Metrics addend);

    void divideBy(double divisor);

    List<String> getMetricNames();

    Map<String, Double> getMetrics(Criterion c);
}
