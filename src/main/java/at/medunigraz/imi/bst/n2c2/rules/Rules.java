package at.medunigraz.imi.bst.n2c2.rules;

public class Rules {


	Patterns p = new Patterns(); 
	
	
	String[] regex_drug_abuse = {
			
			// drug abuse, current or past 
			
			
	}; 
	
	String[] regex_alcohol_abuse = {
			
			// current alcohol use over weekly recommended limits 
			
	}; 
	
	String[] regex_english = {
			
			// patient must speak English 
			
	}; 
	
	String[] regex_makes_decision = {
			
			// patient must make their own medical decisions 
			
	}; 
	
	String[] regex_abdominal = {
			
			// history of intra abdominal surgery, small or 
			// large intestine resection or small bowel obstruction 
			
			
			
	}; 
	
	
	String[] regex_major_diabetes = {
			
			// major diabetes related complication 
			
			
	}; 
	
	String[] regex_advanced_cad = {
			
			// advanced cardiovascular disease 
			
			
	}; 
	
	String[] regex_MI_6mos = {
			
			// myocardial infarction in the past 6 months 
			
			
			
	}; 
	
	String[] regex_keto_1year = {
			
			// diagnoses of ketoacidosis in the past year 
			
			
	}; 
		
	String[] regex_dietsupp_2mos = {
			
			// taken a dietary supplement (excluding vitamin D) in the past two months 
			
			
			
	};	
	
	String[] regex_asp_for_mi = {
			
			// use of aspirin to prevent myocardial infarction 
			
			
			
	};		
	
	String[] regex_hba1c = {
			
			// any hba1c value between 6.5 and 9.5 % 
			
			p.r_criterionID + p.r_whitespace + p.r_1_digit,
			
			p.r_criterionID + p.r_whitespace + p.r_1_digit + p.r_dot + p.r_1_digit,
			
			p.r_criterionID + p.r_whitespace + p.r_2_digit + p.r_dot + p.r_1_digit,
			
			p.r_criterionID + p.r_whitespace + p.r_2_digit + p.r_dot + p.r_2_digit,
			
			p.r_criterionID + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_1_digit, 
			
			p.r_criterionID + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_2_digit, 
			
			p.r_criterionID + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_1_digit + p.r_dot + p.r_2_digit,
			
			p.r_criterionID + p.r_whitespace + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_forwardslash + p.r_2_digit + p.r_2_digit + p.r_whitespace + p.r_2_digit + p.r_dot + p.r_1_digit,
			
			p.r_criterionID + p.r_white_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_2_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_word_white + p.r_2_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_2_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_word_white + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_word_white + p.r_1_digit + p.r_dot + p.r_1_digit, 
			
			p.r_criterionID + p.r_white_word_white + p.r_word_white + p.r_word_white + p.r_word_white + p.r_2_digit + p.r_dot + p.r_1_digit
			
			
	}; 
	
	
	String[] regex_creatinine = {
			
			// serum creatinine > upper limit of normal 
			
			
	
	}; 
	
	
	
	
	
	
	
	
}
