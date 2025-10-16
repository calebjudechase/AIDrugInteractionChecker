package com.example.aidruginteractionchecker
import android.annotation.SuppressLint
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class DrugInteractionRecyclerItemAdapter(factorList: MutableList<String>, severityList: MutableList<String>, sideEffectList: MutableList<MutableList<String>>, interactionTypeList: MutableList<Int>, frequencyList: MutableList<MutableList<String>>) : RecyclerView.Adapter<DrugInteractionRecyclerItemAdapter.ItemViewHolder>() { //initiates recycler adapter class

    private var factorSet = factorList
    private var severitySet = severityList
    private var sideEffectsSet = sideEffectList
    private var interactionTypeSet = interactionTypeList
    private var frequencySet = frequencyList

    private val expandedPositions = mutableSetOf<Int>()

    override fun getItemCount() = factorSet.size //sets count to list size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context) //ease of use var
        val view = inflater.inflate(R.layout.drug_interaction_recycler_item, parent, false) as CardView //inflates to parent as cardview
        return ItemViewHolder(view) //returns the view
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) { //sets the view holder on bind to the item in data position
        val factor = factorSet[position]
        val severity = severitySet[position]
        val sideEffects = sideEffectsSet[position]
        val interactionType = interactionTypeSet[position]
        val frequency = frequencySet[position]
        val position = position
        holder.bind(factor, severity, sideEffects, interactionType, position, frequency)
    }

    inner class ItemViewHolder(rootView: CardView) : RecyclerView.ViewHolder(rootView) { //initiates inner view holder class

        private val factorText = rootView.findViewById<TextView>(R.id.interactionRecyclerText)
        private val severityText = rootView.findViewById<TextView>(R.id.severityText)
        private val interactionCardView = rootView.findViewById<MaterialCardView>(R.id.interactionCardView)

        // Expandable section
        private val sideEffectsLayout = rootView.findViewById<View>(R.id.sideEffectsLayout)
        private val tvSideEffect1 = rootView.findViewById<TextView>(R.id.tvSideEffect1)
        private val tvSideEffect2 = rootView.findViewById<TextView>(R.id.tvSideEffect2)
        private val tvSideEffect3 = rootView.findViewById<TextView>(R.id.tvSideEffect3)

        fun bind(factor: String, severity: String, sideEffects: List<String>, interactionType: Int, position: Int, frequency: List<String>) {
            factorText.text = factor
            severityText.text = severity

            val context = interactionCardView.context
            val (cardColor, textColor) = when (severity) {
                "Low" -> R.color.green to R.color.black
                "Moderate" -> R.color.orange to R.color.black
                "Severe" -> R.color.red to R.color.black
                else -> R.color.white to R.color.black
            }
            interactionCardView.setCardBackgroundColor(ContextCompat.getColor(context, cardColor))
            factorText.setTextColor(ContextCompat.getColor(context, textColor))
            severityText.setTextColor(ContextCompat.getColor(context, textColor))

            // Only drug-to-drug rows can expand; conditions stay compact
            val canExpand = (interactionType == 1) && sideEffects.isNotEmpty()

            // Populate up to 3 side effects; hide labels if fewer are present
            if (canExpand) {
                val s1 = sideEffects.getOrNull(0)
                val s2 = sideEffects.getOrNull(1)
                val s3 = sideEffects.getOrNull(2)
                val f1 = frequency.getOrNull(0)
                val f2 = frequency.getOrNull(0)
                val f3 = frequency.getOrNull(0)

                if (s1 != null) {
                    tvSideEffect1.visibility = View.VISIBLE
                    tvSideEffect1.text = "- $s1 | $f1"
                } else tvSideEffect1.visibility = View.GONE

                if (s2 != null) {
                    tvSideEffect2.visibility = View.VISIBLE
                    tvSideEffect2.text = "- $s2 | $f2"
                } else tvSideEffect2.visibility = View.GONE

                if (s3 != null) {
                    tvSideEffect3.visibility = View.VISIBLE
                    tvSideEffect3.text = "- $s3 | $f3"
                } else tvSideEffect3.visibility = View.GONE
            }

            // Apply expansion state for this position
            val isExpanded = canExpand && expandedPositions.contains(position)
            sideEffectsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Toggle on click only if expandable
            itemView.setOnClickListener {
                if (!canExpand) return@setOnClickListener

                val adapterPos = adapterPosition
                if (adapterPos == RecyclerView.NO_POSITION) return@setOnClickListener

                val nowExpanded = !expandedPositions.contains(adapterPos)
                TransitionManager.beginDelayedTransition(
                    interactionCardView, AutoTransition().apply { duration = 120 }
                )

                if (nowExpanded) {
                    expandedPositions.add(adapterPos)
                    sideEffectsLayout.visibility = View.VISIBLE
                } else {
                    expandedPositions.remove(adapterPos)
                    sideEffectsLayout.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetItems(fList: MutableList<String>, sList: MutableList<String>, seList: MutableList<MutableList<String>>, iList: MutableList<Int>, frList: MutableList<MutableList<String>>) { //resets when delete button is used
        factorSet = fList
        severitySet = sList
        sideEffectsSet = seList
        interactionTypeSet = iList
        frequencySet = frList
        notifyDataSetChanged()
    }
}