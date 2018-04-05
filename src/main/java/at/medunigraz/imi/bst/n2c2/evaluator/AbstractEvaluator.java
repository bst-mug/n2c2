package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

public abstract class AbstractEvaluator implements Evaluator {

    @Override
    public double getF1() {
        return getF1ByCriterion(Criterion.OVERALL);
    }
}
