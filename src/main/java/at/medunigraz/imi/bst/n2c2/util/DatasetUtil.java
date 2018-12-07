package at.medunigraz.imi.bst.n2c2.util;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class DatasetUtil {

    private static final Logger LOG = LogManager.getLogger();

    public static void saveToFolder(List<Patient> patientList, File folder) {
        PatientDAO dao = new PatientDAO();
        for (Patient p : patientList) {
            try {
                dao.toXML(p, new File(folder, p.getID()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<Patient> stripTags(List<Patient> patients) {
        return patients.stream().map(p -> new Patient().withID(p.getID()).withText(p.getText())).collect(Collectors.toList());
    }

    public static List<Patient> slice(List<Patient> patients, int[] indices) {
        return DatasetUtil.slice(patients, indices, 0, patients.size());
    }

    /**
     * Slice the given list of patients using the provided indices array and starting/end index.
     *
     * @param patients
     * @param indices
     * @param startingIndex
     * @param length
     * @return
     */
    public static List<Patient> slice(List<Patient> patients, int[] indices, int startingIndex, int length) {
        List<Patient> ret = new ArrayList<>();
        for (int i = startingIndex; i < startingIndex + length && i < indices.length; i++) {
            ret.add(patients.get(indices[i]));
        }
        return ret;
    }

    public static List<Patient> loadFromFolder(File folder) {
        List<File> files = (List<File>) FileUtils.listFiles(folder, new String[]{"xml"}, false);

        LOG.info("Loading {} files from {} ...", files.size(), folder.getAbsolutePath());

        PatientDAO dao = new PatientDAO();

        List<Patient> patients = new ArrayList<>();
        for (File file : files) {
            LOG.debug("Reading " + file);
            try {
                patients.add(dao.fromXML(file));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return patients;
    }

    /**
     * Checks whether at least one patient in the given list has no prediction for a given criterion.
     *
     * @param test
     * @param c
     * @return
     */
    public static boolean isFullyPredicted(List<Patient> test, Criterion c) {
        for (Patient patient : test) {
            if (!patient.hasEligibility(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generate 'size' random integers of 'max' value each.
     *
     * @param size
     * @param max
     * @return
     */
    public static int[] getRandomIndices(int size, int max) {
        Random rng = new Random(42);

        int[] ret = new int[size];

        // Keep a control set for fast search of added integers
        HashSet<Integer> controlSet = new HashSet<>();

        for (int i = 0; i < ret.length; ) {
            int chosen = (int) (rng.nextFloat() * max);
            if (controlSet.contains(chosen)) {
                continue;
            }

            ret[i++] = chosen;
            controlSet.add(chosen);
        }

        return ret;
    }

    /**
     * Generates n empty patients. Mainly used for testing.
     *
     * @param n
     * @return
     */
    public static List<Patient> generateEmptyPatients(int n) {
        List<Patient> patients = new ArrayList<>(n);

        // The official evaluation script does not like XMLs without all tags
        for (int i = 0; i < n; i++) {
            // XML Transformer doesn't like empty text in some Java versions
            Patient p = new Patient().withID(String.format("%d.xml", i)).withText("abc");
            for (Criterion c : Criterion.classifiableValues()) {
                p.withCriterion(c, Eligibility.NOT_MET);
            }
            patients.add(p);
        }

        return patients;
    }

    /**
     * Find a patient from a given list using its id.
     *
     * @param id
     * @param patients
     * @return the patient found or null if it's not found.
     */
    public static Patient findById(String id, List<Patient> patients) {
        for (Patient p : patients) {
            if (p.getID().equals(id))
                return p;
        }
        return null;
    }

    /**
     * Generates an unique representation (checksum) out of a list of patients.
     *
     * @param patients
     * @return
     */
    public static String getChecksum(List<Patient> patients) {
        // We need a TreeSet to ensure order and avoid duplicates, e.g. "1_2" == "2_1"
        TreeSet<String> patientIds = new TreeSet<>();
        patients.forEach(p -> patientIds.add(p.getID()));

        StringBuilder sb = new StringBuilder();
        for (String id : patientIds) {
            sb.append(id);
            sb.append("_"); // We need a separator to ensure no collisions, e.g. "1_2" != "12"
        }

        return DigestUtils.md5Hex(sb.toString());
    }
}
