package at.medunigraz.imi.bst.n2c2.rules;

public class Rules {


	Patterns p = new Patterns(); 
	
	final public String[] a_CriterionID_drug_abuse = {}; 
	
	final public String[] a_CriterionID_alcohol_abuse = {}; 
	
	final public String[] a_CriterionID_english = {}; 
	
	final public String[] a_CriterionID_makes_decision = {}; 
	
	final public String[] a_CriterionID_abdominal = {}; 
	
	final public String[] a_CriterionID_major_diabetes = {}; 
	
	final public String[] a_CriterionID_advanced_cad = {}; 
	
	final public String[] a_CriterionID_MI_6mos = {}; 
	
	final public String[] a_CriterionID_keto_1year = {}; 
	
	final public String[] a_CriterionID_dietsupp_2mos = {}; 
	
	final public String[] a_CriterionID_asp_for_mi = {}; 
	
	final public String[] a_CriterionID_hba1c = {"hba1c","HB Alc","HgAlC","HbA1c","HBA1c"}; 
	
	final public String[] a_CriterionID_creatinine = {}; 
	
	
	
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
