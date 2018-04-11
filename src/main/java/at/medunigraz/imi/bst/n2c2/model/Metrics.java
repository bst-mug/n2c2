package at.medunigraz.imi.bst.n2c2.model;

@Deprecated
public class Metrics {

    private int tp, fp, tn, fn;

    private double p, r, a;

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

    public Metrics(double a) {
        this.a = a;
    }

    public double getF1() {
        double p = getPrecision();
        double r = getRecall();
        if (p + r == 0) {
            return 0;
        }

        return 2 * p * r / (p + r);
    }

    public double getPrecision() {
        if (p == 0) {
            if (tp == 0) {
                return p;
            }
            p = tp / (double) (tp + fp);
        }
        return p;
    }

    public double getRecall() {
        if (r == 0) {
            if (tp == 0) {
                return p;
            }
            r = tp / (double) (tp + fn);
        }
        return r;
    }

    public double getAccuracy() {
        if (a == 0) {
            if (tp + tn == 0) {
                return a;
            }
            a = (tp + tn) / (double) (tp + tn + fp + fn);
        }
        return a;
    }

    public double getSensitivity() {
        return getRecall();
    }

    public double getSpecificity() {
        if (tn == 0) {
            return 0;
        }
        return tn / (double) (fp + tn);
    }

    public double getFalsePositives() {
        return fp;
    }

    public double getFalseNegatives() {
        return fn;
    }
}
