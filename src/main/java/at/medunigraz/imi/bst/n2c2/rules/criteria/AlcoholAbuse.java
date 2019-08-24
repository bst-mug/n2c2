package at.medunigraz.imi.bst.n2c2.rules.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class AlcoholAbuse extends BaseClassifiable {
    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

    static {
        // 176.xml: have resulted in failure because of his Hx of alcoholism
        POSITIVE_MARKERS.add(Pattern.compile("(Hx|History).{1,5}alcoholism", Pattern.CASE_INSENSITIVE));

        // TODO grouping of drinks or beers per day

        // TODO parse the amount of beers/drinks per day
        // 148.xml (NOT_MET): 1-2 drinks per day
        // 159.xml (MET): she drinks about one beer per day
        //POSITIVE_MARKERS.add(Pattern.compile("(beer|beers|drinks) per day", Pattern.CASE_INSENSITIVE));

        // TODO parse the amount of beers/night
        // 325.xml: 5-6 beers/night on wkends
        //POSITIVE_MARKERS.add(Pattern.compile("beers/night", Pattern.CASE_INSENSITIVE));

        // 159.xml: intermittently binge drinking
        // 187.xml: depressed he becomes a binge drinker
        POSITIVE_MARKERS.add(Pattern.compile("binge drink", Pattern.CASE_INSENSITIVE));

        // 258.xml: wife is concerned about the amount of alcohol he was drinking
        POSITIVE_MARKERS.add(Pattern.compile("concerned.{1,25}alcohol", Pattern.CASE_INSENSITIVE));

        // 344.xml:  He does admit to heavy drinking
//        POSITIVE_MARKERS.add(Pattern.compile("heavy drinking", Pattern.CASE_INSENSITIVE));

        // TODO parse amount of whiskey, e.g. one drink per week is not alcohol abuse
        // 291.xml (NOT_MET): remote history of alcohol abuse
        // 126.xml (NOT_MET): heavy drinking history, stopped 10 years ago
        // 212.xml (MET): Alcohol use status: abuse  Heavy drinker. Whiskey.
        // 212.xml (MET): Four whiskies per night
        POSITIVE_MARKERS.add(Pattern.compile("whisk", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPattern(p.getCleanedText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}