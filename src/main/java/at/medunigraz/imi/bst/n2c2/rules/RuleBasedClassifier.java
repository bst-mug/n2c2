package at.medunigraz.imi.bst.n2c2.rules;

import at.medunigraz.imi.bst.n2c2.classifier.CriterionBasedClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.rules.criteria.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleBasedClassifier extends CriterionBasedClassifier {
	
	

	
	private static final Map<Criterion, Classifiable> name = new HashMap<>();
	static {
        name.put(Criterion.ABDOMINAL, new Abdominal());
        name.put(Criterion.ADVANCED_CAD, new AdvancedCAD());
        name.put(Criterion.ALCOHOL_ABUSE, new AlcoholAbuse());
        name.put(Criterion.ASP_FOR_MI, new AspForMi());
        name.put(Criterion.CREATININE, new Creatinine());
        name.put(Criterion.DIETSUPP_2MOS, new Dietsupp2mos());
        name.put(Criterion.DRUG_ABUSE, new DrugAbuse());
        name.put(Criterion.ENGLISH, new English());
        name.put(Criterion.HBA1C, new HbA1c());
        name.put(Criterion.KETO_1YR, new Keto1Yr());
        name.put(Criterion.MAJOR_DIABETES, new MajorDiabetes());
        name.put(Criterion.MAKES_DECISIONS, new MakesDecisions());
        name.put(Criterion.MI_6MOS, new MI6Mos());
	}
	
	Rules r = new Rules(); 
	
	Patterns pattern = new Patterns();

	public RuleBasedClassifier(Criterion c) {
		super(c);
	}
	
	
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
	
	
	
	public Boolean is_makes_decision_met(Patient patient){
		
		Boolean is_keto1yr_met = null; 
		
		String[] criterion_snippets = r.a_CriterionID_makes_decision; 
		
		String[] regex_CriterionID = r.getRegex_makes_decision(); 
		
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
					
					
					
				} // End of for loop 
				
			} // End of if statement 
			
		}
		
		return is_keto1yr_met; 
		
	
	} // End of is_keto1yr_met() 
	
	
	
	
	public Boolean is_keto1yr_met(Patient patient){
	
		Boolean is_keto1yr_met = null; 
		
		String[] criterion_snippets = r.a_CriterionID_keto_1year; 
		
		String[] regex_CriterionID = r.getRegex_keto_1year(); 
		
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
					
					
					
				} // End of for loop 
				
			} // End of if statement 
			
		}
		
		return is_keto1yr_met; 
		
	
	} // End of is_keto1yr_met() 
	
	
	
	public Boolean is_english_met(Patient patient){
		
		Boolean is_english_met = null; 
		
		String[] criterion_snippets = r.a_CriterionID_english; 
		
		String[] regex_CriterionID = r.getRegex_english(); 
		
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
					
					
					
				} // End of for loop 
				
			} // End of if statement 
			
		}
		
		return is_english_met; 
		
	
	} // End of is_english_met() 
	
	
	
	public Boolean is_hba1c_met(Patient patient){
	
		Boolean is_hba1c_met = null; 
		
		Rules r = new Rules(); 
		
		String[] criterion_snippets = r.a_CriterionID_hba1c; 
		
		String[] regex_CriterionID = r.getRegex_hba1c(); 
		
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
					
					if(criterion_annotation.matches(regex_CriterionID[i])){ 
						
						CriterionValues cv = getCriterionData(criterion_annotation, regex_CriterionID[i], criterionID); 
						
						double cv_value = cv.getCriterion_value(); 
						
						System.out.println("criterion value: " + cv_value);

                        is_hba1c_met = cv_value >= 6.5 && cv_value <= 9.5;
						
						break; 
						
					}else{
						
						is_hba1c_met = false; 
						
					}
					
				} // End of for loop 
				
			} // End of if statement 
			
		}
		
		return is_hba1c_met; 
		
	
	} // End of is_hba1c_met() 
	
	
	
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
	
	
	@Deprecated
	public void train(List<Patient> examples) {
		
	}


	@Override
	public Eligibility predict(Patient p) {
		
//		Eligibility eli = null;
		
		Rules r = new Rules(); 
		
		String[] markers = r.getMarkers(criterion); // -- move to their own class
		
		
		return name.get(criterion).isMet(p);
		
		
		
//		RuleBasedClassifier rbc = new RuleBasedClassifier(); 
//		
//		
//		
//		Criterion.classifiableValues() // overall iterate over all of them // map for ID to regex 
//		
//		String[] criterion_drug_abuse = r.a_CriterionID_drug_abuse; 
//		
//		Boolean is_met = rbc.is_criterion_met(p, criterion_drug_abuse, r.getRegex_drug_abuse());
		
	
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

	
	
	
} // End of class RuleBasedClassifier 
