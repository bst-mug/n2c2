package at.medunigraz.imi.bst.n2c2.model.metrics;

public class BasicMetrics {

    private double tp;
    private double fp;
    private double tn;
    private double fn;

    private double precision;
    private double recall;
    private double accuracy;

    public BasicMetrics(int tp, int fp, int tn, int fn) {
        this.tp = tp;
        this.fp = fp;
        this.tn = tn;
        this.fn = fn;
    }

    public BasicMetrics(double precision, double recall) {
        this.precision = precision;
        this.recall = recall;
    }

    public BasicMetrics(double accuracy) {
        this.accuracy = accuracy;
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
        if (precision == 0) {
            if (tp == 0) {
                return precision;
            }
            precision = tp / (tp + fp);
        }
        return precision;
    }

    public double getRecall() {
        if (recall == 0) {
            if (tp == 0) {
                return precision;
            }
            recall = tp / (tp + fn);
        }
        return recall;
    }

    public double getAccuracy() {
        if (accuracy == 0) {
            accuracy = calculateAccuracy();
        }
        return accuracy;
    }

    public double calculateAccuracy() {
        if (tp + tn == 0) {
            return 0;
        }
        return (tp + tn) / (tp + tn + fp + fn);
    }

    public double getSensitivity() {
        return getRecall();
    }

    public double getSpecificity() {
        if (tn == 0) {
            return 0;
        }
        return tn / (fp + tn);
    }

    public double getFalsePositives() {
        return fp;
    }

    public double getFalseNegatives() {
        return fn;
    }

    public double getTruePositives() {
        return tp;
    }

    public double getTrueNegatives() {
        return tn;
    }

    public void add(BasicMetrics addend) {
        this.tp += addend.tp;
        this.fp += addend.fp;
        this.tn += addend.tn;
        this.fn += addend.fn;

        this.precision += addend.precision;
        this.recall += addend.recall;
        this.accuracy += addend.accuracy;
    }

    public void divideBy(double divisor) {
        this.tp /= divisor;
        this.fp /= divisor;
        this.tn /= divisor;
        this.fn /= divisor;

        this.precision /= divisor;
        this.recall /= divisor;
        this.accuracy /= divisor;
    }
}
