package com.example.aidruginteractionchecker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) { //first event
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView( //second event
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //third event

        lateinit var firebaseAuth: FirebaseAuth //initializes firebase authentication

        super.onViewCreated(view, savedInstanceState)

        //Logout Button Functionality
        val logoutBtn = view.findViewById<Button>(R.id.logoutBtn) //initializes logout button
        logoutBtn.setOnClickListener{ //when clicked
            firebaseAuth.signOut() //logs out
            val sendToLogin = Intent(requireContext(), LoginPage::class.java) //sends to login page
            startActivity(sendToLogin)
        }

        //spinner functionality
        val sexSpinner = view.findViewById<Spinner>(R.id.sexSpinner) //initializes spinner
        ArrayAdapter.createFromResource(requireContext(), R.array.choose_sex,android.R.layout.simple_spinner_item).also{ //adapts string array
         adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sexSpinner.adapter = adapter
        }

        //drug recycler functionality
        val drugList = mutableListOf<String>("item1", "item2", "item3", "item4")
        val drugRecycler = view.findViewById<RecyclerView>(R.id.drugRecycler)
        var drugAdapter = RecyclerItemAdapter(drugList)
        drugRecycler.layoutManager = LinearLayoutManager(context)
        drugRecycler.adapter = drugAdapter

        //condition recycler functionality
        val conditionList = mutableListOf<String>("item11", "item12", "item13")
        val conditionRecycler = view.findViewById<RecyclerView>(R.id.conditionRecycler)
        var conditionAdapter = RecyclerItemAdapter(conditionList)
        conditionRecycler.layoutManager = LinearLayoutManager(context)
        conditionRecycler.adapter = conditionAdapter

        //drug add functionality
        val addDrugBtn = view.findViewById<Button>(R.id.addDrug) //initializes add drug button
        val drugEntry = view.findViewById<EditText>(R.id.drugEntry)
        addDrugBtn.setOnClickListener{ //when clicked
            if ((drugEntry.text.toString() != "") && !drugList.contains(drugEntry.text.toString())) {
                drugList.add(0, drugEntry.text.toString())
                drugAdapter = RecyclerItemAdapter(drugList)
                drugRecycler.adapter = drugAdapter
            }
        }

        //condition add functionality
        val addConditionBtn = view.findViewById<Button>(R.id.addCondition) //initializes add condition button
        val conditionEntry = view.findViewById<EditText>(R.id.conditionEntry)
        addConditionBtn.setOnClickListener{ //when clicked
            if ((conditionEntry.text.toString() != "") && !conditionList.contains(conditionEntry.text.toString())){
                conditionList.add(0, conditionEntry.text.toString())
                conditionAdapter = RecyclerItemAdapter(conditionList)
                conditionRecycler.adapter = conditionAdapter
            }
        }

    }
}