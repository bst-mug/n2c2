package at.medunigraz.imi.bst.n2c2.evaluator;

public abstract class AbstractEvaluator implements Evaluator {

    private double f1 = 0;

    @Override
    public double getF1() {
        if (f1 == 0) {
            evaluate();
        }

        return f1;
    }
}
