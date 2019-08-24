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
        ArrayList<PatientVisits> patientVisits = new ArrayList<>();
        String[] visits = docFulltext.split("Record date:");

        for (int i = 0; i < visits.length; i++) {
            PatientVisits pv = new PatientVisits();

            pv.setVisitNumber(i);
            String line = visits[i].trim();

            String date = getFirstToken(line);
            if (date != null) {
                pv.setVisitDate(convertFormatDate(date));
                pv.setVisitText(trimVisitText(line));
                patientVisits.add(pv);
            }
        }

        return patientVisits;
    }

    private String getFirstToken(String line) {
        int lineLength = line.length();
        String firstToken = null;
        if (lineLength > 2) {
            firstToken = line.substring(0, 12).trim();
        }
        return firstToken;
    }

    private Date convertFormatDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            if (dateStr != null) {
                date = sdf.parse(dateStr);
            }
        } catch (ParseException | NullPointerException ne) {
            date = null;
            ne.printStackTrace();
        }
        return date;
    }

    private String trimVisitText(String visitText) {
        final String end = "*******************************************"
            + "**************************************************"
            + "*******";
        String trimmedVisitText = visitText;
        if (visitText.contains(end)) {
            trimmedVisitText = visitText.replace(end, "");
        }

        return trimmedVisitText;
    }

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
    }

    public PatientVisits getLastVisit() {
        PatientVisits visit = new PatientVisits();
        int size = getAllVisits().size();
        if (size > 0) {
            visit = getAllVisits().get(size - 1);
        }
        return visit;
    }

    public ArrayList<PatientVisits> getMultipleVisits(int months) {
        ArrayList<PatientVisits> afterDate = new ArrayList<>();
        ArrayList<PatientVisits> visits = getAllVisits();
        Date lastVisitDate = getLastVisit().getVisitDate();
        Date pastDate = getPastTimestamp(lastVisitDate, months);

        for (int i = 0; i < visits.size(); i++) {
            if (visits.get(i).getVisitDate().after(pastDate)) {
                afterDate.add(visits.get(i));
            }
        }

        return afterDate;
    }

    /**
     * Get the text corresponding to the visits in the last months.
     *
     * @param months
     * @return
     */
    public String getMultipleVisitsText(int months) {
        StringBuilder sb = new StringBuilder();
        for (PatientVisits visits : getMultipleVisits(months)) {
            sb.append(visits.getVisitText());
            sb.append(" ");
        }
        return sb.toString();
    }

    public Period getTimeIntervalBetweenVisits(PatientVisits visit1, PatientVisits visit2) {
        Period p = null;
        LocalDate firstVisit = convertDateToLocalDate(visit1.getVisitDate());
        LocalDate secondVisit = convertDateToLocalDate(visit2.getVisitDate());
        p = Period.between(firstVisit, secondVisit);
        return p;
    }

    private Date getPastTimestamp(Date currentDate, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.MONTH, -months);
        Date pastTimestamp = c.getTime();
        return pastTimestamp;
    }

    private LocalDate convertDateToLocalDate(Date d) {
        Instant instant = d.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate localDate = zdt.toLocalDate();
        return localDate;
    }

    @Override
    public int compareTo(@NotNull Patient o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Patient patient = (Patient) o;
        return id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}