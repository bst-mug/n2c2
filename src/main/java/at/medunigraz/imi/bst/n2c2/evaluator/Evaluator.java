package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public interface Evaluator {

    void evaluate(List<Patient> gold, List<Patient> results);

    double getF1();

    double getF1ByCriterion(Criterion c);

}
