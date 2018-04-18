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
	
	private String r_criterionID = "(criterionID)";
	
	
	public String setR_criterionID(String criterionID) {
		r_criterionID = getR_criterionID().replace("criterionID", criterionID); 
		return r_criterionID; 
	}
	
	public String getR_criterionID() {
		return r_criterionID;
	} 
	
	

}
