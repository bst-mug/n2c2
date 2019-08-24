package at.medunigraz.imi.bst.n2c2.model;

import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;

import java.util.Date;

public class PatientVisits {
    private Integer visitNumber = null;
    private Date visitDate = null;
    private String visitText = null;

    public Integer getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitText() {
        return visitText;
    }

    public void setVisitText(String visitText) {
        this.visitText = visitText;
    }

    public String getCleanedVisitText() {
        return DataUtilities.removeWhitespaces(visitText);
    }
}
