package at.medunigraz.imi.bst.n2c2.util;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
}
