package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;

import java.util.List;

public interface Evaluator {

    void evaluate(List<Patient> gold, List<Patient> results);

    Metrics getMetrics();
}
