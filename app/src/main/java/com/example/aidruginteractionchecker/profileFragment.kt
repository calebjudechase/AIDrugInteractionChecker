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

    lateinit var drugList: MutableList<String>
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

        db.collection("users").document(firebaseAuth.uid.toString()).get().addOnSuccessListener { documentSnapshot ->
            editAge.setText(documentSnapshot.getString("Age"))
            var spinStr = documentSnapshot.getString("Sex")
            if (spinStr == "Female") {
                sexSpinner.setSelection(1)
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
        val addDrugBtn = view.findViewById<Button>(R.id.addDrug) //initializes add drug button
        val drugEntry = view.findViewById<EditText>(R.id.drugEntry)
        addDrugBtn.setOnClickListener{ //when clicked
            if ((drugEntry.text.toString() != "") && !drugList.contains(drugEntry.text.toString())) {
                drugList.add(0, drugEntry.text.toString())
                drugAdapter.resetItems(drugList)
            }
        }

        //condition add functionality
        val addConditionBtn = view.findViewById<Button>(R.id.addCondition) //initializes add condition button
        val conditionEntry = view.findViewById<EditText>(R.id.conditionEntry)
        addConditionBtn.setOnClickListener{ //when clicked
            if ((conditionEntry.text.toString() != "") && !conditionList.contains(conditionEntry.text.toString())){
                conditionList.add(0, conditionEntry.text.toString())
                conditionAdapter.resetItems(conditionList)
            }
        }

        //save button functionality
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            var drugString = ""
            for (i in 0 until drugList.size-1) {
                drugString += drugList[i]
                drugString += "~"
            }
            drugString += drugList[drugList.size-1]

            var conditionString = ""
            for (i in 0 until conditionList.size-1) {
                conditionString += conditionList[i]
                conditionString += "~"
            }
            conditionString += conditionList[conditionList.size-1]

            var newvals = hashMapOf(
                "Age" to editAge.text.toString(),
                "Sex" to sexSpinner.selectedItem.toString(),
                "Meds" to drugString,
                "Conditions" to conditionString
            )

            db.collection("users").document(firebaseAuth.uid.toString()).set(newvals)
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