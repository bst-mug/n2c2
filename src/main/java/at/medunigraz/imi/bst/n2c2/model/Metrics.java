package at.medunigraz.imi.bst.n2c2.model;

public class Metrics {

    private int tp, fp, tn, fn;

    private double p, r;

    public Metrics(int tp, int fp, int tn, int fn) {
        this.tp = tp;
        this.fp = fp;
        this.tn = tn;
        this.fn = fn;
    }

    public Metrics(double p, double r) {
        this.p = p;
        this.r = r;
    }

    public boolean isValid() {
        return tp + fp + tn + fn > 0 || p + r > 0;
    }

    public double getF1() {
        double p = getPrecision();
        double r = getRecall();
        return 2 * p * r / (p + r);
    }

    public double getPrecision() {
        if (p == 0) {
            p = tp / (double) (tp + fp);
        }
        return p;
    }

    public double getRecall() {
        if (r == 0) {
            r = tp / (double) (tp + fn);
        }
        return r;
    }
}
