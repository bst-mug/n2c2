package at.medunigraz.imi.bst.n2c2.rules;

import java.util.List;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class RuleBasedClassifier implements Classifier {
	
	Rules r = new Rules(); 
	
	Patterns pattern = new Patterns(); 
	
	
	private String[] is_snippet_found(Patient p, String[] valid_snippets){
		
		String[] snippet_annotation = new String[2]; 
		
		String fulltext = p.getText(); 
		
		for(int i = 0; i<valid_snippets.length; i++){
			
			System.out.println("is_snippet_found() --> snippet --> " + valid_snippets[i] + " ... found (true/false) -->  " + fulltext.contains(valid_snippets[i]));
			
			if(fulltext.contains(valid_snippets[i])){
				
				snippet_annotation[0] = valid_snippets[i]; 
				
				snippet_annotation[1] = getWantedLineOfData(fulltext, valid_snippets); 
				
				System.out.println("is_snippet_found() --> yes --> " + snippet_annotation[0] + snippet_annotation[1]);
				
			}
			
		} // End of for loop 
		
		return snippet_annotation;
		
	} // End of is_snippet_found() 
	
	public Boolean is_criterion_met(Patient patient, String[] criterion_snippets, String[] regex_CriterionID){
//	public Boolean is_criterion_met(Patient patient, Criterion c){
	
		Boolean is_criterion_met = null; 
		
		String[] crit_data = is_snippet_found(patient, criterion_snippets);
		
		if(crit_data[0] == null) {
			
			return null; 
			
		}else{
			
			String criterionID = crit_data[0]; 
			
			String criterion_annotation = crit_data[1]; 
			
			System.out.println("CriterionID: ... " + criterionID);
			
			pattern.setR_criterionID(criterionID); 
			
			if(criterion_annotation != null){ 
				
				for(int i = 0; i<regex_CriterionID.length; i++){
					
					System.out.println("regex criterion ID -- " + regex_CriterionID[i]);
//					
//					if(criterion_annotation.contains(criterion_annotation)){
//						
//						System.out.println();
//						
//					}
					
					
//					if(criterion_annotation.matches(regex_CriterionID[i])){ 
//						
//						CriterionValues cd = getCriterionData(criterion_annotation, regex_CriterionID[i], criterionID); 
//						
//						double cd_value = cd.getCriterion_value(); 
//						
//						System.out.println("criterion value: " + cd_value);
//						
//						if(cd_value >= 6.5 && cd_value <= 9.5){
//							
//							is_criterion_met = true; 
//							
//						}else{
//							
//							is_criterion_met = false; 
//							
//						}
//						
//						break; 
//						
//					}else{
//						
//						is_criterion_met = false; 
//						
//					}
					
					
					//TODO 
					
					Criterion[] aCrits = Criterion.classifiableValues(); 
					
					Rules r = new Rules(); 
					
					Criterion c = null; 
					
					for(int j = 0; j<aCrits.length; j++){
						
						if(aCrits[j] == c.ABDOMINAL){
							
							
							
							
						}
						
						if(aCrits[j] == c.ADVANCED_CAD){
							
							
							
						}
						
						if(aCrits[j] == c.ALCOHOL_ABUSE){
							
							
							
						}
						
						if(aCrits[j] == c.ASP_FOR_MI){
							
							
							
						}
						
						if(aCrits[j] == c.CREATININE){
							
							
							
						}
						
						if(aCrits[j] == c.DIETSUPP_2MOS){
							
							
							
						}
						
						if(aCrits[j] == c.DRUG_ABUSE){
							
							
							
						}
						
						
						if(aCrits[j] == c.ENGLISH){
							
							
							
						}
						
						if(aCrits[j] == c.ENGLISH){
							
							
							
						}
						
						if(aCrits[j] == c.HBA1C){
							
							
							
						}
						
						if(aCrits[j] == c.KETO_1YR){
							
							
							
						}
						
						if(aCrits[j] == c.MAJOR_DIABETES){
							
							
							
						}
						
						if(aCrits[j] == c.MAJOR_DIABETES){
							
							
							
						}
						
						if(aCrits[j] == c.MAKES_DECISIONS){
							
							
							
						}
						
						if(aCrits[j] == c.MI_6MOS){
							
							
							
						}
						
						
					}
					
					
				} // End of for loop 
				
			} // End of if statement 
			
		}
		
		
		
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
	
	
	private CriterionValues getCriterionData(String line, String valid_regex, String critID){
		
		CriterionValues cv = new CriterionValues(); 
		
		String wanted_snippet = null; 
		
		String[] aLine = line.split(","); 
		
		for(int i = 0; i<aLine.length; i++){
			
			if(aLine[i].matches(valid_regex)){
				
				wanted_snippet = aLine[i]; 
				
				cv.setCriterionSnippet(wanted_snippet);
				
				break; 
				
			}
			
		}
		
		if(wanted_snippet != null){
			
			String[] aSnippet = wanted_snippet.split(" "); 
			
			for(int j = 0; j<aSnippet.length; j++){
				
				if(aSnippet[j].contains(".")){
					
					cv.setCriterion_value(Double.parseDouble(aSnippet[j]));
					
					break; 
					
				}
				
				if(aSnippet[j].contains("0") || aSnippet[j].contains("2") || 
						aSnippet[j].contains("3")  || aSnippet[j].contains("4") || 
						aSnippet[j].contains("5")  || aSnippet[j].contains("6") || 
						aSnippet[j].contains("7")  || aSnippet[j].contains("8") || 
						aSnippet[j].contains("9")){
					
					cv.setCriterion_value(Double.parseDouble(aSnippet[j]));
					
					break; 
					
				}
				
			}
			
			
		}
		
		return cv; 
		
	} // End of getCriterionData() 

	
	protected Criterion criterion;
	
	
	@Deprecated
	public void train(List<Patient> examples) {
		
	}


	@Override
	public Eligibility predict(Patient p, Criterion c) {
		
		Eligibility eli = null; 
		RuleBasedClassifier rbc = new RuleBasedClassifier(); 
		
		Rules r = new Rules(); 
		
			
		
//		Criterion.classifiableValues() // overall iterate over all of them 
		
		// map for ID to regex 
			
		
//		
//		String[] criterion_drug_abuse = r.a_CriterionID_drug_abuse; 
//		
//		Boolean is_met = rbc.is_criterion_met(p, criterion_drug_abuse, r.getRegex_drug_abuse());
		
		
		return eli;
	
	}

	public Eligibility getEligibilityRules(Boolean is_met){
		
		Eligibility eli; 
		
		if(is_met == true){
			eli = Eligibility.MET; 	
		}else{
			eli = Eligibility.NOT_MET; 
		}
		return eli;
		
	}

	@Override
	public List<Patient> predict(List<Patient> patientList) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
} // End of class RuleBasedClassifier 
