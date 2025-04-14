package com.example.aidruginteractionchecker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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

        var interactionAdapter : DrugInteractionRecyclerItemAdapter
        var factorList = mutableListOf<String>()
        var severityList = mutableListOf<String>()

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
        var drugList : MutableList<String>
        var conditionList : MutableList<String>
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

        //comparison button functionality
        val comparisonEntry = view.findViewById<EditText>(R.id.comparisonEntry)
        val compareBtn = view.findViewById<Button>(R.id.compareBtn) //compare button variable
        compareBtn.setOnClickListener { //when compare clicked
            if (comparisonEntry.text.toString().isNotEmpty() && factorList.isNotEmpty() && factorList.contains(comparisonEntry.text.toString()) != true) {
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
                interactionAdapter.resetItems(factorList, severityList)//reset adapter
            } else if (comparisonEntry.text.toString().isNotEmpty() != true){
                Toast.makeText(requireContext(), "Enter comparison drug!", Toast.LENGTH_SHORT).show()//gives error
            } else if (factorList.isNotEmpty() != true) {
                Toast.makeText(requireContext(), "Fill out and save health profile!", Toast.LENGTH_SHORT).show()//gives error
            } else {
                Toast.makeText(requireContext(), "You are already taking comparison drug!", Toast.LENGTH_SHORT).show()//gives error
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