package at.medunigraz.imi.bst.n2c2.rules;

public class Patterns {
	
	final String r_1_digit = "[0-9]"; 
	
	final String r_2_digit = "[0-9][0-9]"; 
	
	final String r_whitespace = "\\s"; 
	
	final String r_forwardslash = "\\/"; 
	
	final String r_word = "(\\w+)"; 
	
	final String r_white_word_white = r_whitespace + r_word + r_whitespace;
	
	final String r_word_white = r_word + r_whitespace; 
	
	final String r_dot = "."; 
	
	final String r_criterionID = "(criterionID)";

	
	public String getR_1_digit() {
		return r_1_digit;
	}

	public String getR_2_digit() {
		return r_2_digit;
	}

	public String getR_whitespace() {
		return r_whitespace;
	}

	public String getR_forwardslash() {
		return r_forwardslash;
	}

	public String getR_word() {
		return r_word;
	}

	public String getR_white_word_white() {
		return r_white_word_white;
	}

	public String getR_word_white() {
		return r_word_white;
	}

	public String getR_dot() {
		return r_dot;
	}

	public String getR_criterionID() {
		return r_criterionID;
	} 
	
	public String setR_criterionID(String criterionID) {
		return getR_criterionID().replace("criterionID", criterionID); 
	}
	
	
	
	

}
