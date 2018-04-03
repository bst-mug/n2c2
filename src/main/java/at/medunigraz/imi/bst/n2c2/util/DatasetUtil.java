package at.medunigraz.imi.bst.n2c2.util;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DatasetUtil {

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
        List<Patient> ret = new ArrayList<>();
        for (int i : indices) {
            ret.add(patients.get(i));
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
}
