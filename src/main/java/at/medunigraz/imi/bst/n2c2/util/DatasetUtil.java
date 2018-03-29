package at.medunigraz.imi.bst.n2c2.util;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DatasetUtil {

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
}
