package at.medunigraz.imi.bst.n2c2.model.metrics;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

public interface Metrics {

    double getOfficialRankingMeasure();

    double getOfficialRankingMeasureByCriterion(Criterion c);

    void add(Metrics addend);

    void divideBy(double divisor);
}
