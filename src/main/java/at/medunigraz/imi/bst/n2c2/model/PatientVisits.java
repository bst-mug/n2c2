package at.medunigraz.imi.bst.n2c2.model;

import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;

import java.util.Date;

public class PatientVisits {


    private Integer visit_number = null;

    private Date visit_date = null;

    private String visit_text = null;


    public Integer getVisit_number() {
        return visit_number;
    }

    public void setVisit_number(Integer visit_number) {
        this.visit_number = visit_number;
    }

    public Date getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(Date visit_date) {
        this.visit_date = visit_date;
    }

    public String getVisit_text() {
        return visit_text;
    }

    public String getCleanedVisitText() {
        return DataUtilities.removeWhitespaces(visit_text);
    }

    public void setVisit_text(String visit_text) {
        this.visit_text = visit_text;
    }


}
