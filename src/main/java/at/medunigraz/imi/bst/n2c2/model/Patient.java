package at.medunigraz.imi.bst.n2c2.model;


import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;


public class Patient implements Comparable<Patient> {

    private String id;
    private String text;
    private Map<Criterion, Eligibility> criteria = new HashMap<>();

    public Patient withID(String id) {
        this.id = id;
        return this;
    }

    public Patient withText(String text) {
        this.text = text;
        return this;
    }

    public Patient withCriterion(Criterion criterion, Eligibility eligibility) {
        criteria.put(criterion, eligibility);
        return this;
    }

    public String getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCleanedText() {
        return DataUtilities.removeWhitespaces(text);
    }

    public Eligibility getEligibility(Criterion criterion) {
        return criteria.get(criterion);
    }

    public boolean hasEligibility(Criterion criterion) {
        return criteria.containsKey(criterion);
    }

    /**
     * getAllVisits() returns all the visits of one patient as
     * an ArrayList, which includes the parsed date of the visit,
     * the number of the visits (depending on their order in the
     * original patient file, and the text of the visit itself.
     *
     * @return ArrayList of the PatientVisits
     */
    public ArrayList<PatientVisits> getAllVisits() {
        String docFulltext = getText();
        ArrayList<PatientVisits> a_pv = new ArrayList<>();
        String[] visits = docFulltext.split("Record date:");

        for (int i = 0; i < visits.length; i++) {
            PatientVisits pv = new PatientVisits();

            pv.setVisit_number(i);
            String line = visits[i].trim();

            String s_date = getFirstToken(line);
            if (s_date != null) {
                pv.setVisit_date(convertFormatDate(s_date));
                pv.setVisit_text(trimVisitText(line));
                a_pv.add(pv);
            }

        } // End of for loop

        return a_pv;
    } // End of getAllVisits() 

    private String getFirstToken(String line) {
        int lineLength = line.length();
        String dLine = null;
        if (lineLength > 2) {
            dLine = line.substring(0, 12).trim();
        }
        return dLine;
    } // End of getFirstToken() 

    private Date convertFormatDate(String str_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            if (str_date != null) {
                date = sdf.parse(str_date);
            }
        } catch (ParseException | NullPointerException ne) {
            date = null;
            ne.printStackTrace();
        }
        return date;
    } // End of convertFormatDate() 

    private String trimVisitText(String visit_text) {

        String end_id = "*******************************************"
            + "**************************************************"
            + "*******";
        if (visit_text.contains(end_id)) {
            visit_text = visit_text.replace(end_id, "");
        }

        return visit_text;
    } // End of trimVisitText() 

    /**
     * getFirstVisit() returns the first PatientVisits object, according
     * to the patient text file, which was parsed. This includes the parsed
     * date of the visit, the number of the visits (depending on their order
     * in the original patient file, and the text of the visit itself.
     *
     * @return PatientVisits
     */
    public PatientVisits getFirstVisit() {
        PatientVisits pv = new PatientVisits();
        if (getAllVisits().size() > 0) {
            pv = getAllVisits().get(0);
        }
        return pv;
    } // End of getFirstVisit() 

    public PatientVisits getLastVisit() {
        PatientVisits pv = new PatientVisits();
        int pv_size = getAllVisits().size();
        if (pv_size > 0) {
            pv = getAllVisits().get(pv_size - 1);
        }
        return pv;
    } // End of getLastVisit()

    public ArrayList<PatientVisits> getMultipleVisits(int months) {
        ArrayList<PatientVisits> a_pv_afterDate = new ArrayList<>();
        ArrayList<PatientVisits> a_pv = getAllVisits();
        Date date_ofLastVisit = getLastVisit().getVisit_date();
        Date date_inthepast = getPastTimestamp(date_ofLastVisit, months);

        for (int i = 0; i < a_pv.size(); i++) {
            if (a_pv.get(i).getVisit_date().after(date_inthepast)) {
                a_pv_afterDate.add(a_pv.get(i));
            }
        }

        return a_pv_afterDate;
    } // End of getMultipleVisits()

    /**
     * Get the text corresponding to the visits in the last months.
     *
     * @param months
     * @return
     */
    public String getMultipleVisitsText(int months) {
        StringBuilder sb = new StringBuilder();
        for (PatientVisits visits : getMultipleVisits(months)) {
            sb.append(visits.getVisit_text());
            sb.append(" ");
        }
        return sb.toString();
    }


    public Period getTimeIntervalBetweenVisits(PatientVisits visit1, PatientVisits visit2) {
        Period p = null;
        LocalDate d_visit1 = convertDateToLocalDate(visit1.getVisit_date());
        LocalDate d_visit2 = convertDateToLocalDate(visit2.getVisit_date());
        p = Period.between(d_visit1, d_visit2);
        return p;
    } // End of getTimeIntervalBetweenVisits()

    private Date getPastTimestamp(Date current_date, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(current_date);
        c.add(Calendar.MONTH, -months);
        Date past_timestamp = c.getTime();
        return past_timestamp;
    } // End of getPastTimestamp()

    private LocalDate convertDateToLocalDate(Date d) {
        Instant instant = d.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate locDate = zdt.toLocalDate();
        return locDate;
    } // End of convertDateToLocalDate()

    @Override
    public int compareTo(@NotNull Patient o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}