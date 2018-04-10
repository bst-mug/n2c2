package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

public abstract class AbstractEvaluator implements Evaluator {

    @Override
    public double getAccuracy() {
        return getAccuracyByCriterion(Criterion.OVERALL);
    }
}
