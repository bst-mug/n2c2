package at.medunigraz.imi.bst.n2c2.rules;

import at.medunigraz.imi.bst.n2c2.model.Patient;

public class RuleBasedClassifier {
	
	
	private String[] is_HbA1c_found(Patient p){
		
		String[] criterion_annotation = new String[2]; 
		
		String fulltext = p.getText(); 
		
		String[] hba1c_criterionIDs = {"hba1c","HB Alc","HgAlC","HbA1c","HBA1c"}; 
		
		for(int i = 0; i<hba1c_criterionIDs.length; i++){
			
			if(fulltext.contains(hba1c_criterionIDs[i])){
				
				criterion_annotation[0] = hba1c_criterionIDs[i]; 
				
				criterion_annotation[1] = getWantedLineOfData(fulltext, hba1c_criterionIDs); 
				
			}
			
		} // End of for loop 
		
		return criterion_annotation; 
		
	} // End of is_HbA1c_found() 
	
	public Boolean is_HbA1c_met(Patient p){ 
		
		String HbA1c_criterionID = is_HbA1c_found(p)[0];
		
		System.out.println("HbA1c_criterionID: ... " + HbA1c_criterionID);
		
		String HbA1c_annotation = is_HbA1c_found(p)[1]; 
		
		String r_1_digit = "[0-9]"; 
		
		String r_2_digit = "[0-9][0-9]"; 
		
		String r_whitespace = "\\s"; 
		
		String r_forwardslash = "\\/"; 
		
		String r_word = "(\\w+)"; 
		
		String r_white_word_white = r_whitespace + r_word + r_whitespace;
		
		String r_word_white = r_word + r_whitespace; 
		
		String r_dot = "."; 
		
		String r_criterionID = "(criterionID)"; 
		
		r_criterionID = r_criterionID.replace("criterionID", "" + HbA1c_criterionID); 
		
		
		
		String[] regex_CriterionID = {
				
				r_criterionID + r_whitespace + r_1_digit,
				
				r_criterionID + r_whitespace + r_1_digit + r_dot + r_1_digit,
				
				r_criterionID + r_whitespace + r_2_digit + r_dot + r_1_digit,
				
				r_criterionID + r_whitespace + r_2_digit + r_dot + r_2_digit,
				
				r_criterionID + r_whitespace + r_2_digit + r_forwardslash + r_2_digit + r_forwardslash + r_2_digit + r_2_digit + r_whitespace + r_1_digit, 
				
				r_criterionID + r_whitespace + r_2_digit + r_forwardslash + r_2_digit + r_forwardslash + r_2_digit + r_2_digit + r_whitespace + r_2_digit, 
				
				r_criterionID + r_whitespace + r_2_digit + r_forwardslash + r_2_digit + r_forwardslash + r_2_digit + r_2_digit + r_whitespace + r_1_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_whitespace + r_2_digit + r_forwardslash + r_2_digit + r_forwardslash + r_2_digit + r_2_digit + r_whitespace + r_1_digit + r_dot + r_2_digit,
				
				r_criterionID + r_whitespace + r_2_digit + r_forwardslash + r_2_digit + r_forwardslash + r_2_digit + r_2_digit + r_whitespace + r_2_digit + r_dot + r_1_digit,
				
				r_criterionID + r_white_word_white + r_1_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_2_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_word_white + r_1_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_word_white + r_2_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_word_white + r_word_white + r_1_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_word_white + r_word_white + r_2_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_word_white + r_word_white + r_word_white + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_word_white + r_word_white + r_word_white + r_1_digit + r_dot + r_1_digit, 
				
				r_criterionID + r_white_word_white + r_word_white + r_word_white + r_word_white + r_2_digit + r_dot + r_1_digit
				
				
		}; 
		
		Boolean is_HbA1c_met = null; 
		
		if(HbA1c_annotation != null){ 
			
			for(int i = 0; i<regex_CriterionID.length; i++){
				
				System.out.println("regex criterion ID -- " + regex_CriterionID[i]);
				
				if(HbA1c_annotation.matches(regex_CriterionID[i])){ 
					
					CriterionData cd = getCriterionData(HbA1c_annotation, regex_CriterionID[i], r_criterionID); 
					
					double cd_value = cd.getCriterion_value(); 
					
					System.out.println("criterion value: " + cd_value);
					
					if(cd_value >= 6.5 && cd_value <= 9.5){
						
						is_HbA1c_met = true; 
						
					}else{
						
						is_HbA1c_met = false; 
						
					}
					
					break; 
					
				}else{
					
					is_HbA1c_met = false; 
					
				}
				
			} // End of for loop 
			
		} // End of if statement 
		
		return is_HbA1c_met; 
		
	} // End of is_HbA1c_met() 
	
	private String getWantedLineOfData(String text, String[] identifiers){
		
		String wanted_line = null; 
		
		String[] text_in_lines = text.split("\n"); 
		
		for(int i = 0; i<text_in_lines.length; i++){
			
			for(int j = 0; j<identifiers.length; j++){
				
				if(text_in_lines[i].contains(identifiers[j])){
					
					wanted_line = text_in_lines[i-1] + text_in_lines[i] + text_in_lines[i+1]; 
					
					wanted_line = wanted_line.replace("\n", "").replace("\r", "").replace("\t", ""); 
					
					System.out.println("wanted line from getWantedLineOfData() ... "+ wanted_line);
					
					break; 
				
				}
				
			}
			
		}
		return wanted_line; 
		
	} // End of getWantedLineOfData() 
	
	private CriterionData getCriterionData(String line, String valid_regex, String critID){
		
		CriterionData cd = new CriterionData(); 
		
		String wanted_snippet = null; 
		
		String[] aLine = line.split(","); 
		
		for(int i = 0; i<aLine.length; i++){
			
			if(aLine[i].matches(valid_regex)){
				
				wanted_snippet = aLine[i]; 
				
				cd.setCriterionSnippet(wanted_snippet);
				
				break; 
				
			}
			
		}
		
		if(wanted_snippet != null){
			
			String[] aSnippet = wanted_snippet.split(" "); 
			
			for(int j = 0; j<aSnippet.length; j++){
				
				if(aSnippet[j].contains(".")){
					
					cd.setCriterion_value(Double.parseDouble(aSnippet[j]));
					
					break; 
					
				}
				
				if(aSnippet[j].contains("0") || aSnippet[j].contains("2") || 
						aSnippet[j].contains("3")  || aSnippet[j].contains("4") || 
						aSnippet[j].contains("5")  || aSnippet[j].contains("6") || 
						aSnippet[j].contains("7")  || aSnippet[j].contains("8") || 
						aSnippet[j].contains("9")){
					
					cd.setCriterion_value(Double.parseDouble(aSnippet[j]));
					
					break; 
					
				}
				
			}
			
			
		}
		
		
		
		
		return cd; 
		
	} // End of getCriterionData() 
	
	
	
} // End of class RuleBasedClassifier 
