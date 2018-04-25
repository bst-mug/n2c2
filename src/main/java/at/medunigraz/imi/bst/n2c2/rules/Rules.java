package at.medunigraz.imi.bst.n2c2.rules;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.util.HashMap;
import java.util.Map;

public class Rules {


	Patterns p = new Patterns(); 

	@Deprecated // Moved to DrugAbuse class
	final public String[] a_CriterionID_drug_abuse = {"drug", "drugs", "cocaine", "beer", "6 pack"}; 
	// TODO - nothing in sample file --- see how it is stated in the other files 
	
	final public String[] a_CriterionID_alcohol_abuse = {"alcohol", "ETOH", "Etoh", "etoh", "etOH"}; 
	
	final public String[] a_CriterionID_english = {"Spanish", "German", "French", "Italian", "Chinese", "Arab", "Austrian", 
			"credit manager", "set designer"}; 
	
	final public String[] a_CriterionID_makes_decision = {"alert",  "aware", "Lives alone", "lives alone"}; 

	@Deprecated //Moved to Abdominal class
	final static public String[] a_CriterionID_abdominal = {"bowel surgery", "Polypectomy", "POLYPECTOMY", "Resection"};

	@Deprecated // Moved to MajorDiabetes class
	final public String[] a_CriterionID_major_diabetes = {"retinopathy", "amputation", "kidney damage", "skin condition", 
			"nephropathy", "neuropathy", "polyneuropathy", "macular degeneration"}; 
	
	final public String[] a_CriterionID_advanced_cad = {"cad", "advanced cad", "CAD", "captopril", "lipitor", "Lopressor", 
			"Lipitor", "ischemic equivalent", "CAD, s/p MI", "nitropatch", "chest  pain", "chest pain", "EKG changes", 
			"Metoprolol", "Cardia", "Ischemia", "ischemia", "Cozaar", "MI", "anginal  symptoms", "anginal", "Zocor", 
			"Plavix", "unstable angina", "LISINOPRIL", "nferior STEMI", "Atenolol", "Diltiazem", "Zestril", "Mevacor",
			"lovastatin", "Lovastatin", "nitropaste", "Nitropaste", "amlodipine", "IMI", "isinorpill", "Toprol-XL", "Toprol",
			"enalapril", "Lasix", "HCTZ", "Hctz", "HYDROCHLOROTHIAZIDE", "ATORVASTATIN", "ATENOLOL", "Enalapril Maleate", 
			"inferior ischemia"}; 

	@Deprecated // Moved to MI6Mos class
	final public String[] a_CriterionID_MI_6mos = {"NSTEMI", "inferior STEMI"}; 
	
	final public String[] a_CriterionID_keto_1year = {"keto", "ketoacidosis"};

	@Deprecated // Moved to Dietsupp2mos class
	final public String[] a_CriterionID_dietsupp_2mos = {"Folate", "calcium carbonate", "vitamin d", "vit d", "Vit.D", 
			"vitamine D", "iron supplements", "multivitamins", "supplement"}; 

	@Deprecated // Moved to AspForMi class
	final public String[] a_CriterionID_asp_for_mi = {"heparin", "ASA", "aspirin", "Pt on asa"}; 
	
	final public String[] a_CriterionID_hba1c = {"hba1c","HB Alc","HgAlC","HbA1c","HBA1c", "Hemoglobin A1C", "Hgb A1c", "hemoglobin A1c"}; 
	
	final public String[] a_CriterionID_creatinine = {"Creatinine", "crea", "cre", "Cr", "CRE", "CREA"}; 
	
	private static Map<Criterion, String[]> markersPerCriterion = new HashMap<>();
	static {
		markersPerCriterion.put(Criterion.ABDOMINAL, a_CriterionID_abdominal);
		// FIXME add the others
	}
	
	// TODO for all of them 
	// TODO 
	
	public String[] getMarkers(Criterion c) {
		return markersPerCriterion.get(c);
	}
	
	
	
	private String[] regex_drug_abuse = {
			
			// drug abuse, current or past 
			
			
			
	}; 
	
	private String[] regex_alcohol_abuse = {
			
			// current alcohol use over weekly recommended limits 
			
			
			
	}; 
	
	private String[] regex_english = {
			
			// patient must speak English 
			
			
			
			
	}; 
	
	private String[] regex_makes_decision = {
			
			// patient must make their own medical decisions 
			
	}; 
	
	private String[] regex_abdominal = {
			
			// history of intra abdominal surgery, small or 
			// large intestine resection or small bowel obstruction 
			
			
			
	}; 
	
	
	private String[] regex_major_diabetes = {
			
			// major diabetes related complication 
			
			
	}; 
	
	private String[] regex_advanced_cad = {
			
			// advanced cardiovascular disease 
			
			
	}; 
	
	private String[] regex_MI_6mos = {
			
			// myocardial infarction in the past 6 months 
			
			
			
	}; 
	
	private String[] regex_keto_1year = {
			
			// diagnoses of ketoacidosis in the past year 
			
			
	}; 
		
	private String[] regex_dietsupp_2mos = {
			
			// taken a dietary supplement (excluding vitamin D) in the past two months 
			
			
			
	};	
	
	private String[] regex_asp_for_mi = {
			
			// use of aspirin to prevent myocardial infarction 
			
			
			
		
			
	};		
	
	private String[] regex_hba1c = {
			
			// any hba1c value between 6.5 and 9.5 % 
			
			p.getR_criterionID() + p.r_whitespace + p.r_1_digit,
			
			p.getR_criterionID() + p.r_whitespace + p.r_1_digit + p.r_dot + p.r_1_digit,
			
			p.getR_criterionID() + p.r_whitespace + p.r_2_digit + p.r_dot + p.r_1_digit,
			
			p.getR_criterionID() + p.r_whitespace + p.r_2_digit + p.r_dot + p.r_2_digit,
			
			p.getR_criterionID() + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_2_digit, 
			
			p.getR_criterionID() + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_1_digit + p.r_dot + p.r_2_digit,
			
			p.getR_criterionID() + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_2_digit + p.r_dot + p.r_1_digit,
			
			p.getR_criterionID() + p.r_white_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_2_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_word_white + p.r_2_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_2_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_word_white + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.getR_criterionID() + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_word_white + p.r_2_digit + p.r_dot + p.r_1_digit
			
			
	}; 
	
	
	private String[] regex_creatinine = {
			
			// serum creatinine > upper limit of normal 
			
			
	
	};


	public String[] getRegex_drug_abuse() {
		return regex_drug_abuse;
	}


	public String[] getRegex_alcohol_abuse() {
		return regex_alcohol_abuse;
	}


	public String[] getRegex_english() {
		return regex_english;
	}


	public String[] getRegex_makes_decision() {
		return regex_makes_decision;
	}


	public String[] getRegex_abdominal() {
		return regex_abdominal;
	}


	public String[] getRegex_major_diabetes() {
		return regex_major_diabetes;
	}


	public String[] getRegex_advanced_cad() {
		return regex_advanced_cad;
	}


	public String[] getRegex_MI_6mos() {
		return regex_MI_6mos;
	}


	public String[] getRegex_keto_1year() {
		return regex_keto_1year;
	}


	public String[] getRegex_dietsupp_2mos() {
		return regex_dietsupp_2mos;
	}


	public String[] getRegex_asp_for_mi() {
		return regex_asp_for_mi;
	}


	public String[] getRegex_hba1c() {
		return regex_hba1c;
	}


	public String[] getRegex_creatinine() {
		return regex_creatinine;
	} 
	
	
	
	
	
	
	
	
}
