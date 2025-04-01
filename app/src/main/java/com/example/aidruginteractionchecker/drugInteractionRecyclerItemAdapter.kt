package com.example.aidruginteractionchecker
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class DrugInteractionRecyclerItemAdapter(factorList: MutableList<String>, severityList: MutableList<String>) : RecyclerView.Adapter<DrugInteractionRecyclerItemAdapter.ItemViewHolder>() { //initiates recycler adapter class

    private var factorSet = factorList
    private var severitySet = severityList

    override fun getItemCount() = factorSet.size //sets count to list size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context) //ease of use var
        val view = inflater.inflate(R.layout.drug_interaction_recycler_item, parent, false) as CardView //inflates to parent as cardview
        return ItemViewHolder(view) //returns the view
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) { //sets the view holder on bind to the item in data position
        val factor = factorSet[position]
        val severity = severitySet[position]
        holder.bind(factor, severity)
    }

    inner class ItemViewHolder(private val rootView: CardView) : RecyclerView.ViewHolder(rootView) { //initiates inner view holder class

        private val factorText = rootView.findViewById<TextView>(R.id.interactionRecyclerText) //creates var for easy access to recyclerText
        private val severityText = rootView.findViewById<TextView>(R.id.severityText)

        fun bind(factor: String, severity: String) { //creates bind function which sets itemText to the string in list
            factorText.text = factor
            severityText.text = severity
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetItems(fList: MutableList<String>, sList: MutableList<String>) { //resets when delete button is used
        factorSet = fList
        severitySet = sList
        notifyDataSetChanged()
    }
}