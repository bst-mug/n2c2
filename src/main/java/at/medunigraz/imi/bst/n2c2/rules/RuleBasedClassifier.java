package at.medunigraz.imi.bst.n2c2.rules;

import at.medunigraz.imi.bst.n2c2.model.Patient;

public class RuleBasedClassifier {
	
	Rules r = new Rules(); 
	
	Patterns pattern = new Patterns(); 
	
	
	private String[] is_snippet_found(Patient p, String[] valid_snippets){
		
		String[] snippet_annotation = new String[2]; 
		
		String fulltext = p.getText(); 
		
		for(int i = 0; i<valid_snippets.length; i++){
			
			if(fulltext.contains(valid_snippets[i])){
				
				snippet_annotation[0] = valid_snippets[i]; 
				
				snippet_annotation[1] = getWantedLineOfData(fulltext, valid_snippets); 
				
			}
			
		} // End of for loop 
		
		return snippet_annotation;
		
	} // End of is_snippet_found() 
	
	
	public Boolean is_criterion_met(Patient patient, String[] criterion_snippets){
	
		String[] crit_data = is_snippet_found(patient, criterion_snippets);
		
		String criterionID = crit_data[0]; 
		
		String criterion_annotation = crit_data[1]; 
		
		System.out.println("CriterionID: ... " + criterionID);
		
		pattern.setR_criterionID(criterionID); 
		
		
		String[] regex_CriterionID = r.getRegex_hba1c(); 
		
				
		Boolean is_criterion_met = null; 
		
		
		if(criterion_annotation != null){ 
			
			for(int i = 0; i<regex_CriterionID.length; i++){
				
				System.out.println("regex criterion ID -- " + regex_CriterionID[i]);
				
				if(criterion_annotation.matches(regex_CriterionID[i])){ 
					
					CriterionData cd = getCriterionData(criterion_annotation, regex_CriterionID[i], criterionID); 
					
					double cd_value = cd.getCriterion_value(); 
					
					System.out.println("criterion value: " + cd_value);
					
					if(cd_value >= 6.5 && cd_value <= 9.5){
						
						is_criterion_met = true; 
						
					}else{
						
						is_criterion_met = false; 
						
					}
					
					break; 
					
				}else{
					
					is_criterion_met = false; 
					
				}
				
			} // End of for loop 
			
		} // End of if statement 
		
		return is_criterion_met; 
		
	
	} // End of is_criterion_met() 
	
	
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
