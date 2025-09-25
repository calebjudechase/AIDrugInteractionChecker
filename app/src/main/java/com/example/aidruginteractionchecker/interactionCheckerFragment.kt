package com.example.aidruginteractionchecker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.processing.SurfaceProcessorNode
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import java.io.DataInputStream
import java.io.EOFException
import kotlin.random.Random

class InteractionCheckerFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interaction_checker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //third event

        //var interpreter: Interpreter

        FirebaseModelDownloader.getInstance().getModel(
            "Drug_Interaction_Model",
            DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
            CustomModelDownloadConditions.Builder().requireWifi().build()
        ).addOnSuccessListener {
            //Toast.makeText(requireContext(), "ML Model Successfully Downloaded!", Toast.LENGTH_SHORT).show()
//            model: CustomModel? ->
//            val modelFile = model?.file
//            if (modelFile != null) {
//                interpreter = Interpreter(modelFile) //not sure if this works come back to this l8r
//            }
        }

        var interactionAdapter : DrugInteractionRecyclerItemAdapter
        var factorList = mutableListOf<String>()
        var severityList = mutableListOf<String>()

        val abbKey = mapOf(
            "aaa" to listOf("abdominal", "aortic", "aneurysm"),
            "aas" to listOf("aarskog", "scott", "syndrome"),
            "abcd" to listOf("albinism", "black", "lock", "cell", "migration", "disorder"),
            "abpa" to listOf("allergic", "bronchopulmonary", "aspergillosis"),
            "acc" to listOf("agenesis", "of", "the", "corpus", "callosum"),
            "acs" to listOf("acute", "coronary", "syndrome"),
            "acth" to listOf("adrenocorticotropic", "hormone"),
            "acug" to listOf("arthrocutaneouveal", "granulomatosis"),
            "acvd" to listOf("atherosclerotic", "cardiovascular", "disease"),
            "ad" to listOf("alzheimers", "disease", "attachment", "disorder"),
            "add" to listOf("attention", "deficit", "disorder"),
            "add-rt" to listOf("attention", "deficit", "disorder", "residual", "type"),
            "adem" to listOf("acute", "disseminated", "encephalomyelitis"),
            "adhd" to listOf("attention", "deficit", "hyperactivity", "disorder"),
            "aerd" to listOf("aspirin", "exacerbated", "respiratory", "disease"),
            "af" to listOf("atrial", "fibrillation"),
            "fib" to listOf("fibrillation"),
            "ags" to listOf("aicardi", "goutieres", "syndrome"),
            "ah" to listOf("acquired", "hemophilia"),
            "aha" to listOf("acquired", "hemophilia", "a"),
            "ahb" to listOf("acquired", "hemophilia", "b"),
            "ahc" to listOf("alternating", "hemiplegia", "of", "childhood"),
            "ahf" to listOf("alkhurma", "hemorrhagic", "fever"),
            "aki" to listOf("acute", "kidney", "injury"),
            "aids" to listOf("acquired", "immune", "deficiency", "syndrome"),
            "aip" to listOf("acute", "intermittent", "porphyria"),
            "ala " to listOf("doss", "porphyria", "ala", "dehydratase", "deficiency", "plumboporphyria"),
            "ald" to listOf("alcoholic", "liver", "disease"),
            "ali" to listOf("acute", "lung", "injury"),
            "all" to listOf("acute", "lymphoblastic", "lymphoma", "acute", "lymphocytic", "leukemia"),
            "als" to listOf("amyotrophic", "lateral", "sclerosis"),
            "amd" to listOf("age", "related", "macular", "degeneration"),
            "aml" to listOf("acute", "myelogenous", "leukemia"),
            "an" to listOf("anorexia", "nervosa"),
            "aocd" to listOf("anemia", "of", "chronic", "disease"),
            "aodm" to listOf("adult", "onset", "diabetes", "mellitus"),
            "aos" to listOf("apraxia", "of", "speech"),
            "apa" to listOf("aldosterone-producing", "adenoma"),
            "aps" to listOf("antiphospholipid", "syndrome"),
            "arbd" to listOf("alcohol", "related", "birth", "defects"),
            "ard" to listOf("adult", "refsum", "disease"),
            "ards" to listOf("acute", "respiratory", "distress", "syndrome"),
            "arnd" to listOf("alcohol", "related", "neurodevelopmental", "disorder"),
            "arm" to listOf("anorectal", "malformation"),
            "as" to listOf("ankylosing", "spondylitis", "asperger", "syndrome"),
            "ascvd" to listOf("atherosclerotic", "cardiovascular", "disease"),
            "asd" to listOf("atrial", "septal", "defect"),
            "asds" to listOf("autism", "spectrum", "disorders"),
            "ashf" to listOf("acute", "systolic", "heart", "failure"),
            "ass" to listOf("african", "sleeping", "sickness"),
            "a-t" to listOf("ataxia-telangiectasia"),
            "avms" to listOf("arteriovenous", "malformations"),
            "avm" to listOf("arteriovenous", "malformation"),
            "ba" to listOf("bronchial", "asthma"),
            "bbs" to listOf("bardet", "biedl", "syndrome", "bashful", "bladder", "syndrome", "(see", "paruresis)"),
            "beb" to listOf("benign", "essential", "blepharospasm"),
            "bd" to listOf("behcet", "disease", "binswangers", "disease"),
            "beh" to listOf("behaviorally", "emotionally", "handicapped"),
            "bh" to listOf("behaviorally", "handicapped"),
            "bl" to listOf("burkitt", "lymphoma"),
            "bmd" to listOf("beckers", "muscular", "dystrophy"),
            "bpad" to listOf("bipolar", "affective", "disorder"),
            "bpd" to listOf("borderline", "personality", "disorder"),
            "bph" to listOf("benign", "prostatic", "hyperplasia"),
            "brbns" to listOf("blue", "rubber", "bleb", "nevus", "syndrome"),
            "bric1" to listOf("benign", "recurrent", "intrahepatic", "cholestasis"),
            "brrs" to listOf("bannayan", "riley", "ruvalcaba", "syndrome"),
            "brs" to listOf("brugada", "syndrome"),
            "bs" to listOf("bloom", "syndrome"),
            "bse" to listOf("bovine", "spongiform", "encephalopathy"),
            "bss" to listOf("brown", "sequard", "syndrome"),
            "bv" to listOf("bacterial", "vaginosis"),
            "ca" to listOf("cancer"),
            "cach" to listOf("childhood", "ataxia", "with", "central", "nervous", "system", "hypomyelination"),
            "cad" to listOf("coronary", "artery", "disease"),
            "cadasil" to listOf("cerebral", "autosomal", "dominant", "arteriopathy", "with", "subcortical", "infarcts", "and", "leukoencephalopathy"),
            "cap" to listOf("community", "acquired", "pneumonia"),
            "capa" to listOf("covid", "associated", "pulmonary", "aspergillosis"),
            "capd" to listOf("central", "auditory", "processing", "disorder"),
            "ccd" to listOf("considerable", "conduct", "disorder"),
            "cchf" to listOf("crimean-congo", "haemorrhagic", "fever"),
            "cchs" to listOf("congenital", "central", "hypoventilation", "syndrome"),
            "ccm" to listOf("cerebral", "cavernous", "malformation"),
            "cdg" to listOf("congenital", "disorder", "of", "glycosylation"),
            "cdgs" to listOf("carbohydrate", "deficient", "glycoprotein", "syndrome"),
            "cdhf" to listOf("chronic", "diastolic", "heart", "failure"),
            "cep" to listOf("congenital", "erythropoietic", "porphyria"),
            "cesd" to listOf("cholesteryl", "ester", "storage", "disease"),
            "cf" to listOf("cystic", "fibrosis"),
            "cfids" to listOf("chronic", "fatigue", "immune", "dysfunction", "syndrome"),
            "cfs" to listOf("chronic", "fatigue", "syndrome"),
            "cgbd" to listOf("corticobasal", "ganglionic", "degeneration"),
            "ch" to listOf("cluster", "headache"),
            "chd" to listOf("congenital", "heart", "disease", "congenital", "hip", "dysplasia", "coronary", "heart", "disease"),
            "chf" to listOf("congestive", "heart", "failure"),
            "cidp" to listOf("chronic", "inflammatory", "demyelinating", "polyneuropathy"),
            "cipa" to listOf("congenital", "insensitivity", "to", "pain", "with", "anhidrosis"),
            "cip" to listOf("congenital", "insensitivity", "to", "pain"),
            "cjd" to listOf("creutzfeldt", "jakob", "disease"),
            "ckd" to listOf("chronic", "kidney", "disease"),
            "cml" to listOf("chronic", "myelogenous", "leukemia"),
            "cms" to listOf("chiari", "malformations"),
            "cmt" to listOf("charcot", "marie", "tooth"),
            "cmt1a" to listOf("charcot", "marie", "tooth", "disease", "type", "1a"),
            "cmt1b" to listOf("charcot", "marie", "tooth", "disease", "type", "1b"),
            "cmt1c" to listOf("charcot", "marie", "tooth", "disease", "type", "1c"),
            "cmt1d" to listOf("charcot", "marie", "tooth", "disease", "type", "1d"),
            "cmt1e" to listOf("charcot", "marie", "tooth", "disease", "type", "1e"),
            "cmt1f" to listOf("charcot", "marie", "tooth", "disease", "type", "1f"),
            "cmt1x" to listOf("charcot", "marie", "tooth", "disease", "type", "1x"),
            "cmt2" to listOf("charcot", "marie", "tooth", "disease", "type", "2"),
            "cofs" to listOf("cerebro", "oculo", "facio", "skeletal", "syndrome"),
            "copd" to listOf("chronic", "obstructive", "pulmonary", "disease"),
            "covid" to listOf("coronavirus", "disease"),
            "cp" to listOf("cerebral", "palsy"),
            "cp" to listOf("chronic", "prostatitis", "chronic", "pelvic", "pain", "syndrome"),
            "cpps" to listOf("chronic", "prostatitis", "chronic", "pelvic", "pain", "syndrome"),
            "cpdd" to listOf("calcium", "pyrophosphate", "deposition", "disease"),
            "cpm" to listOf("central", "pontine", "myelinolysis"),
            "cpps" to listOf("chronic", "pelvic", "pain", "syndrome"),
            "cre" to listOf("carbapenem", "resistant", "enterobacteriaceae"),
            "crf" to listOf("chronic", "renal", "failure"),
            "crkp" to listOf("carbapenem", "resistant", "klebsiella", "pneumoniae"),
            "crps" to listOf("complex", "regional", "pain", "syndrome"),
            "csa" to listOf("central", "sleep", "apnea"),
            "csd" to listOf("cat", "scratch", "disease"),
            "cte" to listOf("chronic", "traumatic", "encephalopathy"),
            "ctf" to listOf("colorado", "tick", "fever"),
            "cvd" to listOf("cardiovascular", "disease"),
            "cwd" to listOf("chronic", "wasting", "disease"),
            "das" to listOf("developmental", "apraxia", "of", "speech"),
            "dba" to listOf("diamond–blackfan", "anemia"),
            "dbmd" to listOf("duchenne–becker", "muscular", "dystrophy"),
            "dd" to listOf("developmental", "disability"),
            "def" to listOf("deaf"),
            "df" to listOf("dengue", "fever"),
            "dh" to listOf("developmentally", "handicapped"),
            "dhf" to listOf("dengue", "hemorrhagic", "fever", "diastolic", "heart", "failure"),
            "dhpr" to listOf("dihydropteridine", "reductase", "deficiency"),
            "di" to listOf("diabetes", "insipidus"),
            "dic" to listOf("disseminated", "intravascular", "coagulation"),
            "did" to listOf("dissociative", "identity", "disorder"),
            "dig" to listOf("digeorge", "syndrome"),
            "dish" to listOf("diffuse", "idiopathic", "skeletal", "hyperostosis"),
            "djd" to listOf("degenerative", "joint", "disease"),
            "dlb" to listOf("dementia", "with", "lewy", "bodies"),
            "dm" to listOf("diabetes", "mellitus"),
            "dmd" to listOf("duchenne", "muscular", "dystrophy"),
            "dp" to listOf("doss", "porphyria/ala", "dehydratase", "deficiency/plumboporphyria", "(the", "disease", "is", "known", "by", "multiple", "names)"),
            "dpt" to listOf("diphtheria,", "pertussis,", "tetanus"),
            "drsp" to listOf("drug-resistant", "streptococcus", "pneumoniae"),
            "ds" to listOf("down", "syndrome"),
            "dsps" to listOf("delayed", "sleep", "phase", "syndrome"),
            "dts" to listOf("delirium", "tremens"),
            "dvd" to listOf("developmental", "verbal", "dyspraxia"),
            "dvt" to listOf("deep", "vein", "thrombosis"),
            "ed" to listOf("emotionally", "disturbed", "erectile", "dysfunction"),
            "eds" to listOf("ehlers", "danlos", "syndrome", "excessive", "daytime", "sleepiness"),
            "eee" to listOf("eastern", "equine", "encephalitis"),
            "ehk" to listOf("epidermolytic", "hyperkeratosis"),
            "emh" to listOf("educable", "mentally", "handicapped"),
            "emr" to listOf("educable", "mentally", "retarded"),
            "ens" to listOf("epidermal", "nevus", "syndrome"),
            "epm" to listOf("extrapontine", "myelinolysis"),
            "epp" to listOf("erythropoietic", "protoporphyria"),
            "esrd" to listOf("end", "stage", "renal", "disease"),
            "ess" to listOf("empty", "sella", "syndrome"),
            "evd" to listOf("ebola", "virus", "disease"),
            "fae" to listOf("fetal", "alcohol", "effects"),
            "fas" to listOf("fetal", "alcohol", "syndrome"),
            "fasds" to listOf("fetal", "alcohol", "spectrum", "disorders"),
            "ffi" to listOf("fatal", "familial", "insomnia"),
            "fma" to listOf("focal", "muscular", "atrophies"),
            "fmd" to listOf("fibromuscular", "dysplasia"),
            "fms" to listOf("fibromyalgia", "syndrome"),
            "fsp" to listOf("familial", "spastic", "paraparesis"),
            "ftd" to listOf("frontotemporal", "dementia"),
            "fuo" to listOf("fever", "of", "unknown", "origin"),
            "fvs" to listOf("fetal", "valproate", "syndrome"),
            "fx" to listOf("fracture"),
            "fxs" to listOf("fragile", "x", "syndrome"),
            "gad" to listOf("generalized", "anxiety", "disorder"),
            "gan" to listOf("giant", "axonal", "neuropathy"),
            "gave" to listOf("gastric", "antral", "vascular", "ectasia"),
            "gbs" to listOf("guillain", "barre", "syndrome", "group", "b", "streptococcal"),
            "gce" to listOf("glycine", "encephalopathy"),
            "gd" to listOf("gestational", "diabetes"),
            "gerd" to listOf("gastroesophageal", "reflux", "disease"),
            "gi" to listOf("gastrointestinal"),
            "gib" to listOf("gastrointestinal", "bleeding"),
            "gn" to listOf("glossopharyngeal", "neuralgia"),
            "gord" to listOf("gastro", "oesophageal", "reflux", "disease"),
            "gss" to listOf("gerstmann", "straussler", "scheinker"),
            "gt" to listOf("gifted", "and", "learning", "disabled"),
            "ld" to listOf("gifted", "and", "learning", "disabled"),
            "gvhd" to listOf("graft", "versus", "host", "disease"),
            "gwd" to listOf("guinea", "worm", "disease"),
            "h1n1" to listOf("hemagglutinin", "type", "1", "and", "neuraminidase", "type", "1", "influenza"),
            "hcp" to listOf("hereditary", "coproporphyria"),
            "hd" to listOf("huntingtons", "disease"),
            "hdl2" to listOf("huntingtons", "disease", "like", "2"),
            "hellp" to listOf("hemolytic", "anemia", "elevated", "liver", "enzymes", "and", "low", "platelet", "count"),
            "hev" to listOf("hendra", "virus"),
            "hf" to listOf("heart", "failure"),
            "hfa" to listOf("high", "functioning", "autism"),
            "hfmd" to listOf("hand", "foot", "and", "mouth", "disease"),
            "hfrs" to listOf("hemorrhagic", "fever", "with", "renal", "syndrome"),
            "hib" to listOf("haemophilus", "influenzae", "type", "b"),
            "hibm" to listOf("hereditary", "inclusion", "body", "myopathy"),
            "hmsn" to listOf("hereditary", "motor", "and", "sensory", "polyneuropathy"),
            "hoh" to listOf("hard", "of", "hearing"),
            "htn" to listOf("hypertension"),
            "hprt" to listOf("hypoxanthine", "guanine", "phosphoribosyl", "transferase"),
            "hpd" to listOf("histrionic", "personality", "disorder"),
            "hps" to listOf("hantavirus", "pulmonary", "syndrome"),
            "hpv" to listOf("human", "papillomavirus"),
            "hsp" to listOf("hereditary", "spastic", "paraplegia"),
            "iapa" to listOf("influenza", "associated", "pulmonary", "aspergillosis"),
            "ibd" to listOf("inflammatory", "bowel", "disease"),
            "ibids" to listOf("ichthyosis,", "brittle", "hair", "intellectual", "impairment", "decreased", "fertility", "and", "short", "stature"),
            "ibm" to listOf("inclusion", "body", "myositis"),
            "ibs" to listOf("ichthyosis", "bullosa", "of", "siemens", "irritable", "bowel", "syndrome"),
            "ic" to listOf("interstitial", "cystitis"),
            "pbs" to listOf("painful", "bladder", "syndrome"),
            "icf" to listOf("immunodeficiency", "centromere", "instability", "and", "facial", "anomalies"),
            "id" to listOf("infectious", "disease"),
            "ied" to listOf("intermittent", "explosive", "disorder"),
            "ifap" to listOf("ichthyosis", "follicularis,", "alopecia,", "and", "photophobia"),
            "iha" to listOf("idiopathic", "hyperaldosteronism"),
            "inad" to listOf("infantile", "neuroaxonal", "dystrophy"),
            "ip" to listOf("incontinentia", "pigmenti"),
            "ird" to listOf("infantile", "refsum", "disease"),
            "is" to listOf("infantile", "spasm"),
            "itp" to listOf("idiopathic", "thrombocytopenic", "purpura"),
            "jas" to listOf("juvenile", "ankylosing", "spondylitis"),
            "jbs" to listOf("johanson", "blizzard", "syndrome"),
            "je" to listOf("japanese", "encephalitis"),
            "jhd" to listOf("juvenile", "huntingtons", "disease"),
            "jmml" to listOf("juvenile", "myelomonocytic", "leukemia"),
            "jodm" to listOf("juvenile", "onset", "diabetes", "mellitus"),
            "jpa" to listOf("juvenile", "pilocytic", "astrocytoma"),
            "jra" to listOf("juvenile", "rheumatoid", "arthritis"),
            "jws" to listOf("jackson", "weiss", "syndrome"),
            "kc" to listOf("keratoconus"),
            "kfd" to listOf("kyasanur", "forest", "disease"),
            "ks" to listOf("kawasaki", "syndrome"),
            "kss" to listOf("kearns", "sayre", "syndrome"),
            "kts" to listOf("klippel", "trenaunay", "syndrome"),
            "ktw" to listOf("klippel", "trenaunay", "weber"),
            "lcm" to listOf("lymphocytic", "choriomeningitis"),
            "lds" to listOf("learning", "disabilities", "difficulties", "differences", "disorder"),
            "ld" to listOf("legionnaires", "disease"),
            "lems" to listOf("lambert", "eaton", "myasthenic", "syndrome"),
            "lfa" to listOf("low", "functioning", "autism"),
            "lgv" to listOf("lymphogranuloma", "venereum"),
            "lks" to listOf("landau", "kleffner", "syndrome"),
            "lns" to listOf("lesch", "nyhan", "syndrome"),
            "lp" to listOf("lipoid", "proteinosis", "little", "person"),
            "lpr" to listOf("laryngopharyngeal", "reflux"),
            "luhf" to listOf("lujo", "hemorrhagic", "fever"),
            "mac" to listOf("mycobacterium", "avium", "complex"),
            "mbd" to listOf("minimal", "brain", "dysfunction"),
            "mcs" to listOf("multiple", "chemical", "sensitivities"),
            "md" to listOf("muscular", "dystrophy"),
            "mds" to listOf("myoclonic", "dystonia"),
            "mdd" to listOf("major", "depressive", "disorder"),
            "mdr " to listOf("multi", "drug", "resistant"),
            "tb" to listOf("tuberculosis"),
            "me" to listOf("myalgic", "encephalomyelitis"),
            "cfs" to listOf("chronic", "fatigue", "syndrome"),
            "mers" to listOf("middle", "east", "respiratory", "syndrome"),
            "mfs" to listOf("marfan", "syndrome"),
            "mi" to listOf("myocardial", "infarction"),
            "mid" to listOf("multi", "infarct", "dementia"),
            "mis" to listOf("multisystem", "inflammatory", "syndrome"),
            "mjd" to listOf("machado", "joseph", "disease"),
            "ml" to listOf("mucolipidoses"),
            "mld" to listOf("metachromatic", "leukodystrophy"),
            "mma" to listOf("monomelic", "amyotrophy"),
            "mmr" to listOf("measles", "mumps", "rubella"),
            "mmrv" to listOf("measles", "mumps", "rubella", "varicella"),
            "mnd" to listOf("motor", "neuron", "disease"),
            "mody" to listOf("maturity", "onset", "diabetes", "of", "the", "young"),
            "moh" to listOf("medication", "overuse", "headaches"),
            "mpd" to listOf("myeloproliferative", "disorders"),
            "mps" to listOf("mucopolysaccharoidosis"),
            "mpx" to listOf("monkeypox"),
            "mr" to listOf("mentally", "retarded"),
            "ms" to listOf("multiple", "sclerosis"),
            "msa" to listOf("multiple", "system", "atrophy"),
            "msdd" to listOf("multi", "sensory", "developmental", "delays"),
            "nas" to listOf("neonatal", "abstinence", "syndrome"),
            "nbia" to listOf("neurodegeneration", "with", "brain", "iron", "accumulation"),
            "ncip" to listOf("novel", "coronavirus", "infected", "pneumonia"),
            "ncl" to listOf("neuronal", "ceroid", "lipofuscinosis"),
            "nf1" to listOf("neurofibromatosis", "type", "1"),
            "nf2" to listOf("neurofibromatosis", "type", "2"),
            "nkh" to listOf("nonketotic", "hyperglycinemia"),
            "nld" to listOf("nonverbal", "learning", "disability"),
            "nmds" to listOf("neuronal", "migration", "disorders"),
            "nmo" to listOf("neuromyelitis", "optica"),
            "nms" to listOf("neuroleptic", "malignant", "syndrome"),
            "np" to listOf("niemann", "pick", "disease"),
            "npc1" to listOf("niemann", "pick", "disease,", "type", "c1"),
            "nph" to listOf("normal", "pressure", "hydrocephalus"),
            "ntd" to listOf("neural", "tube", "defect"),
            "ntds" to listOf("neural", "tube", "defects", "neglected", "tropical", "diseases"),
            "oa" to listOf("osteoarthritis"),
            "ocd" to listOf("obsessive", "compulsive", "disorder"),
            "odd" to listOf("oppositional", "defiant", "disorder"),
            "ohf" to listOf("omsk", "hemorrhagic", "fever"),
            "oma" to listOf("oculomotor", "apraxia"),
            "opc" to listOf("oropharyngeal", "candidiasis"),
            "opca" to listOf("olivopontocerebellar", "atrophy"),
            "osa" to listOf("obstructive", "sleep", "apnea"),
            "osdd" to listOf("other", "specified", "dissociative", "disorder"),
            "pbc" to listOf("primary", "biliary", "cirrhosis"),
            "pbd" to listOf("peroxisome", "biogenesis", "disorders"),
            "pcos" to listOf("polycystic", "ovarian", "syndrome"),
            "pct" to listOf("porphyria", "cutanea", "tarda"),
            "pd" to listOf("parkinsons", "disease"),
            "pdd" to listOf("pervasive", "developmental", "disorder"),
            "pdd" to listOf("pervasive", "developmental", "disorder"),
            "pds" to listOf("pokkuri", "death", "syndrome"),
            "pe" to listOf("pulmonary", "embolism"),
            "pkan" to listOf("panthothenate", "kinase", "associated", "neurodegeneration"),
            "plmd" to listOf("periodic", "limb", "movement", "disorder"),
            "pls" to listOf("primary", "lateral", "sclerosis"),
            "pmd" to listOf("pelizaeus", "merzbacher", "disease"),
            "pml" to listOf("progressive", "multifocal", "leukoencephalopathy"),
            "pms" to listOf("premenstrual", "syndrome"),
            "pots" to listOf("postural", "orthostatic", "tachycardia", "syndrome"),
            "ppma" to listOf("post", "polio", "progressive", "muscular", "atrophy"),
            "pps" to listOf("post", "polio", "syndrome"),
            "psc" to listOf("primary", "sclerosing", "cholangitis"),
            "psp" to listOf("progressive", "supranuclear", "palsy"),
            "ptsd" to listOf("post", "traumatic", "stress", "disorder"),
            "pvl" to listOf("periventricular", "leukomalacia"),
            "pw" to listOf("port", "wine", "stain"),
            "qms" to listOf("qazi", "markouizos", "syndrome"),
            "qpd" to listOf("qualitative", "platelet", "defect", "quebec", "platelet", "disorder"),
            "qps" to listOf("quebec", "platelet", "syndrome"),
            "qtt" to listOf("queensland", "tick", "typhus"),
            "ra" to listOf("rheumatoid", "arthritis"),
            "rad" to listOf("reactive", "airway", "disease"),
            "rind" to listOf("reversible", "ischemic", "neurologic", "deficit"),
            "rlf" to listOf("retrolental", "fibroplasia"),
            "rls" to listOf("restless", "legs", "syndrome"),
            "rmds" to listOf("repetitive", "motion", "disorders"),
            "rop" to listOf("retinopathy", "of", "prematurity"),
            "rs" to listOf("reyes", "syndrome"),
            "rsd" to listOf("reflex", "sympathetic", "dystrophy"),
            "rti" to listOf("respiratory", "tract", "infection"),
            "rvf" to listOf("rift", "valley", "fever"),
            "sads" to listOf("sudden", "arrhythmic", "death", "syndrome"),
            "sars" to listOf("severe", "acute", "respiratory", "syndrome"),
            "sb" to listOf("spina", "bifida"),
            "sbmd" to listOf("sensory", "based", "motor", "disorder"),
            "sbs" to listOf("shaken", "baby", "syndrome"),
            "sc" to listOf("sydenham", "chorea"),
            "sd" to listOf("saint", "vituss", "dance"),
            "sdd" to listOf("sensory", "discrimination", "disorder"),
            "sds" to listOf("sudden", "death", "syndrome"),
            "shf" to listOf("systolic", "heart", "failure"),
            "sids" to listOf("sudden", "infant", "death", "syndrome"),
            "sirs" to listOf("systemic", "inflammatory", "response", "syndrome"),
            "sis" to listOf("shaken", "infant", "syndrome"),
            "sle" to listOf("systemic", "lupus", "erythematosus"),
            "sm" to listOf("selective", "mutism"),
            "sjs" to listOf("stevens", "johnson", "syndrome"),
            "sma" to listOf("spinal", "muscular", "atrophy"),
            "smd" to listOf("sensory", "modulation", "disorder"),
            "smei" to listOf("severe", "myoclonic", "epilepsy", "of", "infancy"),
            "sms" to listOf("smith", "magenis", "syndrome"),
            "sod" to listOf("septo", "optic", "dysplasia"),
            "spd" to listOf("sensory", "processing", "disorder"),
            "sps" to listOf("stiff", "person", "syndrome"),
            "sspe" to listOf("subacute", "sclerosing", "panencephalitis"),
            "stemi" to listOf("st", "elevation", "myocardial", "infarction"),
            "std" to listOf("sexually", "transmitted", "disease"),
            "sti" to listOf("sexually", "transmitted", "infection"),
            "sunct" to listOf("short", "lasting", "unilateral", "neuralgiform", "headache", "with", "conjunctival", "injection", "and", "tearing"),
            "sunds" to listOf("sudden", "unexplained", "nocturnal", "death", "syndrome"),
            "sws" to listOf("sturge", "weber", "syndrome"),
            "slos" to listOf("smith", "lemli", "opitz", "syndrome"),
            "tac" to listOf("trigeminal", "autonomic", "cephalalgia"),
            "tao" to listOf("thromboangiitis", "obliterans"),
            "tb" to listOf("tuberculosis"),
            "tbi" to listOf("traumatic", "brain", "injury"),
            "tcs" to listOf("tethered", "cord", "syndrome"),
            "tef" to listOf("tracheoesophageal", "fistula"),
            "tia" to listOf("transient", "ischemic", "attack"),
            "tmh" to listOf("trainable", "mentally", "handicapped"),
            "tmj" to listOf("temporomandibular", "joint", "disorder"),
            "tmd" to listOf("temporomandibular", "joint", "disorder"),
            "tmr" to listOf("trainable", "mentally", "retarded"),
            "tn" to listOf("trigeminal", "neuralgia"),
            "tos" to listOf("thoracic", "outlet", "syndrome"),
            "ts" to listOf("tourette", "syndrome", "tuberous", "sclerosis"),
            "tsc" to listOf("tuberous", "sclerosis"),
            "tses" to listOf("transmissible", "spongiform", "encephalopathies"),
            "tsp" to listOf("tropical", "spastic", "paraparesis"),
            "tth" to listOf("tension", "type", "headache"),
            "ttp" to listOf("thrombotic", "thrombocytopenic", "purpura"),
            "ucpps" to listOf("urologic", "chronic", "pelvic", "pain", "syndrome"),
            "ucd" to listOf("unicentric", "castleman", "disease"),
            "uda" to listOf("urticaria", "deafness", "amyloidosis"),
            "ufs" to listOf("urofacial", "syndrome"),
            "usp" to listOf("ubiquitin", "specific", "protease"),
            "uti" to listOf("urinary", "tract", "infection"),
            "uc" to listOf("ulcerative", "colitis"),
            "uri" to listOf("upper", "respiratory", "infection"),
            "vacterl" to listOf("vertebral", "abnormalities", "anal", "atresia,", "cardiac", "defects", "tracheoesophageal", "fistula", "esophageal", "atresia", "renal", "and", "radial", "abnormalities", "limb", "abnormalities", "with", "hydrocephalus"),
            "vaed" to listOf("vaccine", "associated", "enhanced", "disease"),
            "vcfs" to listOf("velo", "cardio", "facial", "syndrome"),
            "vcjd" to listOf("variant", "creutzfeldt", "jakob", "disease"),
            "vd" to listOf("venereal", "disease"),
            "vhf" to listOf("viral", "hemorrhagic", "fever"),
            "vhl" to listOf("von", "hippel", "lindau", "disease"),
            "vkc" to listOf("vernal", "keratoconjunctivitis"),
            "vkh" to listOf("vogt", "koyanagi", "harada", "disease"),
            "vod" to listOf("veno", "occlusive", "disease"),
            "vp" to listOf("variegate", "porphyria"),
            "vsd" to listOf("ventricular", "septal", "defect"),
            "vvc" to listOf("vulvovaginal", "candidiasis"),
            "vwd" to listOf("von", "willebrand", "disease"),
            "vwm" to listOf("vanishing", "white", "matter"),
            "wagr" to listOf("wilms", "tumor,", "aniridia", "genitourinary", "anomalies", "and", "mental", "retardation"),
            "wd" to listOf("wilsons", "disease"),
            "wee" to listOf("western", "equine", "encephalitis"),
            "ws" to listOf("williams", "syndrome"),
            "ws4" to listOf("waardenburg", "syndrome", "type", "4"),
            "cald" to listOf("cerebral", "adrenoleukodystrophy"),
            "xdh" to listOf("xanthine", "dehydrogenase"),
            "xdp" to listOf("x", "linked", "dystonia", "parkinsonism"),
            "xdr tb" to listOf("extensively", "drug", "resistant", "tuberculosis"),
            "xhed" to listOf("x", "linked", "hypohidrotic", "ectodermal", "dysplasia"),
            "xlmtm" to listOf("x", "linked", "myotubular", "myopathy"),
            "xlos" to listOf("x", "linked", "opitz", "syndrome"),
            "xlp syndrome" to listOf("x", "linked", "lymphoproliferative", "syndrome"),
            "xlsa" to listOf("x", "linked", "sideroblastic", "anemia"),
            "xmea" to listOf("x", "linked", "myopathy", "with", "excessive", "autophagy"),
            "xmen" to listOf("x", "linked", "immunodeficiency", "with", "magnesium", "defect", "epstein", "barr", "virus", "infection", "and", "neoplasia"),
            "xp" to listOf("xeroderma", "pigmentosa"),
            "xscid" to listOf("x", "linked", "severe", "combined", "immunodeficiency"),
            "xxx" to listOf("triple", "x"),
            "yf" to listOf("yellow", "fever"),
            "yns" to listOf("yellow", "nail", "syndrome"),
            "yss" to listOf("young", "simpson", "syndrome"),
            "yvs" to listOf("yunis", "varon", "syndrome"),
            "yy" to listOf("double", "y"),
            "zap" to listOf("zeta", "associated", "protein"),
            "zbls" to listOf("zadik", "barak", "levin", "syndrome"),
            "zes" to listOf("zollinger", "ellison", "syndrome"),
            "zls" to listOf("zimmermann", "laband", "syndrome"),
            "zs" to listOf("zellweger", "syndrome"),
            "zsd" to listOf("zellweger", "spectrum", "disorders"),
            "zss" to listOf("zellweger", "syndrome", "spectrum"),
            "zttk" to listOf("zhu", "tokita", "takenouchi", "kim")
            ) //medical abbreviation key

        val word2vecKey = (requireActivity().application as MyApp).word2vecKey //gets word2vecKey from myapp
        val maxIndex = word2vecKey.values.maxOrNull() ?: -1
        val unknownIndex = maxIndex + 1

        val word2vecEmbeddings = (requireActivity().application as MyApp).word2vecEmbeddings //gets word2vecEmbeddings from myapp

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance() //initializes firebase authentication
        val db = Firebase.firestore

        super.onViewCreated(view, savedInstanceState)

        //sets up results recycler
        val interactionRecycler = view.findViewById<RecyclerView>(R.id.resultsRecycler)
        interactionAdapter = DrugInteractionRecyclerItemAdapter(factorList, severityList)
        interactionRecycler.layoutManager = LinearLayoutManager(context)
        interactionRecycler.adapter = interactionAdapter
        interactionRecycler.recycledViewPool.setMaxRecycledViews(0, 0)

        //imports data and initializes
        var sexAge : String?
        var drugList = mutableListOf<String>()
        var conditionList = mutableListOf<String>()
        db.collection("users").document(firebaseAuth.uid.toString()).get().addOnSuccessListener { documentSnapshot -> //gets data from firestore
            if(documentSnapshot.getString("Age") != null && documentSnapshot.getString("Sex") != null && documentSnapshot.getString("Age") != "" && documentSnapshot.getString("Sex") != "") { //if age and sex str isn't null or empty string
                sexAge = documentSnapshot.getString("Sex") + ", " + documentSnapshot.getString("Age") //sets up sex and age string
                factorList += sexAge //adds string to list
            }
            if(documentSnapshot.getString("Meds") != null && documentSnapshot.getString("Meds") != "") { //if meds string isn't null or empty
                drugList = documentSnapshot.getString("Meds").toString().split("~").toMutableList() //splits string into list
                factorList += drugList //adds the drug list to the factor list
            }
            if(documentSnapshot.getString("Conditions") != null && documentSnapshot.getString("Conditions") != "") { //same as above
                conditionList = documentSnapshot.getString("Conditions").toString().split("~").toMutableList()
                factorList += conditionList //adds the condition list to the factor list
            }
            print(factorList)
            for (i in 0 until factorList.size) { //makes an empty entry for severity list for every item in factor list
                severityList += ""
            }
            interactionAdapter.resetItems(factorList, severityList)
        }

        var cleanDrugList = mutableListOf<MutableList<String>>()
        var cleanConditionList = mutableListOf<MutableList<String>>()

        var indexDrugList = mutableListOf<MutableList<Int>>()
        var indexConditionList = mutableListOf<MutableList<Int>>()

        //comparison button functionality
        val comparisonEntry = view.findViewById<EditText>(R.id.comparisonEntry)
        val compareBtn = view.findViewById<Button>(R.id.compareBtn) //compare button variable
        compareBtn.setOnClickListener { //when compare clicked
            if(comparisonEntry.text.toString() == "StressTest") { //if stress test
                val dbStressTest = db.collection("stresstest").document("letters") //assigns doc to variable
                var storedVal = ":)" //creates initial stored val
                for (i in 0 until Random.nextInt(100, 200)) { //for a number of times between 100 and 199
                    var readWriteType = Random.nextInt(0, 2) //picks read or write at random
                    var fieldNumber = Random.nextInt(1, 27).toString() //picks random spot to read/write from
                    var saveString = Random.nextInt(1, 27).toString() //picks random number string
                    if (readWriteType == 0) { //if read
                        dbStressTest.get().addOnSuccessListener { documentSnapshot -> //gets doc snapshot
                            storedVal = documentSnapshot.getString(fieldNumber).toString() //reads from spot to storedVal
                        }
                    } else { //else if write
                        dbStressTest.update(fieldNumber, saveString) //updates random spot to the stored val
                    }
                }
            } else {
                if (comparisonEntry.text.toString().isNotEmpty() && factorList.isNotEmpty() && factorList.contains(comparisonEntry.text.toString()) != true) {

                    //rand result for severity level (temp)
                    for (i in 0 until factorList.size) { //for size of factor list
                        var riskVal = Random.nextInt(0, 3) //simulating output of MLM with random number gen of 0, 1, or 2
                        if (riskVal == 0) { //if val is 0 severity is low
                            severityList[i] = "Low"
                        } else if (riskVal == 1) { //if val is 1 severity is moderate
                            severityList[i] = "Moderate"
                        } else { //otherwise val is 2 and severity is severe
                            severityList[i] = "Severe"
                        }
                    }

                    //preprocess drug list for w2vec index matching
                    for (i in 0 until drugList.size) {
                        cleanDrugList += drugList[i]
                            .lowercase()
                            .replace(Regex("[^a-z0-9 -]"), "")
                            .replace("-", " ")
                            .split("\\s+".toRegex())
                            .filter{it.isNotEmpty()}
                            .toMutableList()
                        cleanDrugList[i] = cleanDrugList[i].flatMap{ item -> abbKey[item] ?: listOf(item)}.toMutableList()
                        Log.d("CleanList", "Index $i: ${cleanDrugList[i]}")
                    }

                    //w2vec index matching
                    for (i in 0 until cleanDrugList.size) {
                        indexDrugList += cleanDrugList[i].map {word -> word2vecKey[word] ?: unknownIndex }.toMutableList()
                        Log.d("IndexList", "Index $i: ${indexDrugList[i]}")
                    }

                    //preprocess condition list for w2vec index matching
                    for (i in 0 until conditionList.size) {
                        cleanConditionList += conditionList[i]
                            .lowercase()
                            .replace(Regex("[^a-z0-9 -]"), "")
                            .replace("-", " ")
                            .split("\\s+".toRegex())
                            .filter{it.isNotEmpty()}
                            .toMutableList()
                        cleanConditionList[i] = cleanConditionList[i].flatMap{ item -> abbKey[item] ?: listOf(item)}.toMutableList()
                        Log.d("CleanList", "Index $i: ${cleanConditionList[i]}")
                    }

                    //w2vec index matching
                    for (i in 0 until cleanConditionList.size) {
                        indexConditionList += cleanConditionList[i].map {word -> word2vecKey[word] ?: unknownIndex }.toMutableList()
                        Log.d("IndexList", "Index $i: ${indexConditionList[i]}")
                    }

                    interactionAdapter.resetItems(factorList, severityList)//reset adapter
                } else if (comparisonEntry.text.toString().isNotEmpty() != true){
                    Toast.makeText(requireContext(), "Enter comparison drug!", Toast.LENGTH_SHORT).show()//gives error
                } else if (factorList.isNotEmpty() != true) {
                    Toast.makeText(requireContext(), "Fill out and save health profile!", Toast.LENGTH_SHORT).show()//gives error
                } else {
                    Toast.makeText(requireContext(), "You are already taking comparison drug!", Toast.LENGTH_SHORT).show()//gives error
                }
            }
        }

        val barcodeBtn = view.findViewById<ImageButton>(R.id.barcodeBtn) //barcode button variable
        barcodeBtn.setOnClickListener { //when barcode button clicked
            BarcodeScannerPage.goToScanner(requireContext()) { barcodes -> //runs go to scanner process returning barcodes
                barcodes.forEach { barcode -> //for 1, since theres only ever one barcode
                    db.collection("drugs").document("labels").get().addOnSuccessListener { documentSnapshot -> //gets drug labels
                        if (documentSnapshot.getString(barcode.rawValue.toString()) != null) { //if drug label exists
                            comparisonEntry.setText(documentSnapshot.getString(barcode.rawValue.toString())) //enters drug name
                        } else { //if not
                            Toast.makeText(requireContext(), "Drug not in Database!", Toast.LENGTH_SHORT).show() //tells em
                        }
                    }

                }
            }
        }
    }
}