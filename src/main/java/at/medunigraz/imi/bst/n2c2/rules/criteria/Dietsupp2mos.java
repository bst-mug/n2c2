package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Dietsupp2mos extends BaseClassifiable {

    private static final int PAST_MONTHS = 2;

    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

    static {
        // Vitamin D is excluded
        POSITIVE_MARKERS.add(Pattern.compile("folate", Pattern.CASE_INSENSITIVE));

        // 364.xml (NOT_MET): calcium algenate
        // 209.xml (NOT_MET): calcium channel blocker
        // 209.xml (NOT_MET): Calcium                8.6
        // 161.xml (NOT_MET): Calcium                          8.6
        // 302.xml (MET): 12.  Calcium 1200 mg q.d.
        // 365.xml (MET): CALCIUM 600mg
        // 337.xml (MET): Calcium
        POSITIVE_MARKERS.add(Pattern.compile("calcium(?! +[0-9]\\.)", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("calcium carbonate", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("supplement", Pattern.CASE_INSENSITIVE));

        POSITIVE_MARKERS.add(Pattern.compile("multivitamin", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("vit(?:amin)? [abce-z]", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("folic", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("ascorbic", Pattern.CASE_INSENSITIVE));   // 113.xml: Ascorbic Acid (vit C)
        POSITIVE_MARKERS.add(Pattern.compile("niferex", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("vitamins", Pattern.CASE_INSENSITIVE));    // 382.xml:  continue current regimen of vitamins and enzymes
        POSITIVE_MARKERS.add(Pattern.compile("nephro-vit", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("renax", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("fish oil", Pattern.CASE_INSENSITIVE));   // 121.xml: Fish OIL CAPSULE (OMEGA-3-FATTY ACIDS)
        POSITIVE_MARKERS.add(Pattern.compile("omega-3", Pattern.CASE_INSENSITIVE));

        // 272.xml (NOT_MET): Magnesium                1.4
        POSITIVE_MARKERS.add(Pattern.compile("magnesium (oxide|gluconate)", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("ferrous", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("mvi", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("calcitriol", Pattern.CASE_INSENSITIVE));

        // TODO check for fp
        POSITIVE_MARKERS.add(Pattern.compile("kcl", Pattern.CASE_INSENSITIVE));     // 279.xml: Kcl SUSTAINED RELEASE

        POSITIVE_MARKERS.add(Pattern.compile("gluconate", Pattern.CASE_INSENSITIVE));

        // 258.xml: check iron
        //POSITIVE_MARKERS.add(Pattern.compile("iron", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("iron sulfate", Pattern.CASE_INSENSITIVE));

        POSITIVE_MARKERS.add(Pattern.compile("nephrocaps", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("potassium chloride", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPatternInRecentPast(p, POSITIVE_MARKERS, PAST_MONTHS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}

/* following can occur in any part of a document.

alpha, beta, etc. can be in greek as well
categories separated by underlines
if in parentheses, it is an acronym of the previous
if there is an 's' in the end, it can be removed
_______________________________
Fructo-oligosaccharides
Galacto-oligosaccharides 
Human milk oligosaccharides 
Isomalto-oligosaccharides 
Maltotriose
Mannan oligosaccharides 
Raffinose 
stachyose 
verbascose
________________________________
Chondroitin sulfate
Beta-glucan
Beta mannan
psylium
chitin
________________________________
Alanine
Arginine
Asparagine
Aspartic acid
Cysteine
Glutamic acid
Glutamine
Glycine
Histidine
Isoleucine
Leucine
Lysine
Methionine
Phenylalanine
Proline
Selenocysteine
Serine
Threonine
Tryptophan
Tyrosine
Valine
________________________________
Citrulline
Cystine
Gama aminobutyric acid (GABA)
Ornithine
Theanine
________________________________
Betaine
Carnitine
Carnosine
Creatine
Hydroxyproline
Hydroxytryptophan
N-acetyl cysteine
S-Adenosyl methionine (SAM-e)
Taurine
Tyramine
_________________________________
Omega-3
Alpha-linolenic acid 
(ALA)
Eicosapentaenoic 
(EPA) 
Docosahexaenoic acid 
(DHA)
Omega-6
Arachidonic acid 
(AA)
Linoleic acid
Conjugated linoleic acid 
(CLA)
_________________________________
Lecithin 
Phosphatidylcholine
phytosterols
_________________________________
Astaxanthin
Lutein
zeaxanthin
Lycopene
Vitamin A
carotenoids
________________________________
Vitamin A 
Retinol and retinal
Vitamin B1 
Thiamine
Vitamin B2
Riboflavin
Vitamin B3 
Niacin
Vitamin B5 
Pantothenic acid
Vitamin B6 
Pyridoxine
Vitamin B7 
Biotin
Vitamin B9 
Folic acid
Vitamin B12 
Cobalamin
Choline
Vitamin C 
Ascorbic acid
Vitamin D 
Ergocalciferol and cholecalciferol
Vitamin E 
Tocopherol
Vitamin K 
Phylloquinone
___________________________________
Curcumin
FLAVONOIDS
Anthocyanidins
Flavanols
flavonones
Isothiocyanates
Lignin
Phytic acid (inositol hexaphosphate)
Piperine
Proanthocyanidins
anthocyanes
anthocianine
hesperdin
quercetin
diosmin
luteolin
Isoflavones: daidzein, genistein
Caffeic acid
Chlorogenic acid
Lignans
Resveratrol
Tannins
Tannic acid
Allicin
Chlorophyll and chlorophyllin
Indole-3-carbinol
*/
