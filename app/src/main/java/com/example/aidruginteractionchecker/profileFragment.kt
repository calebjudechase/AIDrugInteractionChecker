package com.example.aidruginteractionchecker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class ProfileFragment : Fragment() {

    lateinit var drugList: MutableList<String> //late init vars needed in functions
    lateinit var conditionList: MutableList<String>
    lateinit var drugAdapter: RecyclerItemAdapter
    lateinit var conditionAdapter: RecyclerItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) { //first event
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView( //second event
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //third event

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance() //initializes firebase authentication
        val db = Firebase.firestore

        drugList = mutableListOf() //initializes so app doesn't crash
        conditionList = mutableListOf()

        val editAge = view.findViewById<EditText>(R.id.editAge) //age edit reference

        //spinner functionality
        val sexSpinner = view.findViewById<Spinner>(R.id.sexSpinner) //initializes spinner
        ArrayAdapter.createFromResource(requireContext(), R.array.choose_sex,android.R.layout.simple_spinner_item).also{ //adapts string array
                adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sexSpinner.adapter = adapter
        }

        db.collection("users").document(firebaseAuth.uid.toString()).get().addOnSuccessListener { documentSnapshot -> //gets data from firestore
            if(documentSnapshot.getString("Age") != null) { //if age str isn't null
                editAge.setText(documentSnapshot.getString("Age")) //sets edittext for age to the saved age
            }
            if(documentSnapshot.getString("Sex") != null) { //if sex string is not null
                var spinStr = documentSnapshot.getString("Sex") //sets var to sex string
                if (spinStr == "Female") { //if it was female
                    sexSpinner.setSelection(1) //changes spinner to female
                }
            }
            if(documentSnapshot.getString("Meds") != null && documentSnapshot.getString("Meds") != "") { //if meds string isn't null or empty
                drugList = documentSnapshot.getString("Meds").toString().split("~").toMutableList() //splits string into list
                drugAdapter.resetItems(drugList) //resets adapter with saved list
            }
            if(documentSnapshot.getString("Conditions") != null && documentSnapshot.getString("Conditions") != "") { //same as above
                conditionList = documentSnapshot.getString("Conditions").toString().split("~").toMutableList()
                conditionAdapter.resetItems(conditionList)
            }
        }

        super.onViewCreated(view, savedInstanceState)

        //Logout Button Functionality
        val logoutBtn = view.findViewById<Button>(R.id.logoutBtn) //initializes logout button
        logoutBtn.setOnClickListener{ //when clicked
            firebaseAuth.signOut() //logs out
            val sendToLogin = Intent(requireContext(), LoginPage::class.java) //sends to login page
            startActivity(sendToLogin)
        }

        //drug recycler functionality
        val drugRecycler = view.findViewById<RecyclerView>(R.id.drugRecycler)
        drugAdapter = RecyclerItemAdapter(drugList){index -> drugRecyclerDeleteItem(index)}
        drugRecycler.layoutManager = LinearLayoutManager(context)
        drugRecycler.adapter = drugAdapter
        drugRecycler.recycledViewPool.setMaxRecycledViews(0, 0)

        //condition recycler functionality
        val conditionRecycler = view.findViewById<RecyclerView>(R.id.conditionRecycler)
        conditionAdapter = RecyclerItemAdapter(conditionList){index -> conditionRecyclerDeleteItem(index)}
        conditionRecycler.layoutManager = LinearLayoutManager(context)
        conditionRecycler.adapter = conditionAdapter
        conditionRecycler.recycledViewPool.setMaxRecycledViews(0, 0)

        //drug add functionality
        val addDrugBtn = view.findViewById<Button>(R.id.addDrug) //initializes add drug button and entry
        val drugEntry = view.findViewById<EditText>(R.id.drugEntry)
        addDrugBtn.setOnClickListener{ //when clicked
            if ((drugEntry.text.toString() != "") && !drugList.contains(drugEntry.text.toString())) { //if text not empty or already in list
                drugList.add(0, drugEntry.text.toString()) //adds drug to list at beginning
                drugAdapter.resetItems(drugList) //resets adapter
            }
        }

        //condition add functionality
        val addConditionBtn = view.findViewById<Button>(R.id.addCondition) //initializes add condition button and entry
        val conditionEntry = view.findViewById<EditText>(R.id.conditionEntry)
        addConditionBtn.setOnClickListener{ //when clicked
            if ((conditionEntry.text.toString() != "") && !conditionList.contains(conditionEntry.text.toString())){ //if text not empty or already in list
                conditionList.add(0, conditionEntry.text.toString()) //adds drug to list at beginning
                conditionAdapter.resetItems(conditionList) //resets adapter
            }
        }

        //save button functionality
        val saveBtn = view.findViewById<Button>(R.id.saveBtn) //save button variable
        saveBtn.setOnClickListener { //when save clicked
            var drugString = "" //initializes drug string
            if(drugList.isNotEmpty()) {//if drug list isn't empty
                for (i in 0 until drugList.size-1) { //for every item except last in drug list
                    drugString += drugList[i] //adds drug to string
                    drugString += "~" //followed by ~ as separation char
                }
                drugString += drugList[drugList.size-1] //adds last item from list
            }
            var conditionString = "" //same as above but with conditions
            if(conditionList.isNotEmpty()) {
                for (i in 0 until conditionList.size-1) {
                    conditionString += conditionList[i]
                    conditionString += "~"
                }
                conditionString += conditionList[conditionList.size-1]
            }

            var newVals = hashMapOf( //makes hash map with the save data
                "Age" to editAge.text.toString(),
                "Sex" to sexSpinner.selectedItem.toString(),
                "Meds" to drugString,
                "Conditions" to conditionString
            )

            db.collection("users").document(firebaseAuth.uid.toString()).set(newVals) //updates firestore vals to save data
        }
    }

    fun drugRecyclerDeleteItem(index: Int) { //deletes drug item from list when button clicks by communicating with adapter
        drugList.removeAt(index)
        drugAdapter.resetItems(drugList)
    }

    fun conditionRecyclerDeleteItem(index: Int) { //deletes condition item from list when button clicks by communicating with adapter
        conditionList.removeAt(index)
        conditionAdapter.resetItems(conditionList)
    }
}