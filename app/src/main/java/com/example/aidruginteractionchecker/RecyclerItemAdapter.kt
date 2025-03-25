package com.example.aidruginteractionchecker
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemAdapter(data: MutableList<String>, val onClickDelete: (Int) -> Unit) : RecyclerView.Adapter<RecyclerItemAdapter.ItemViewHolder>() { //initiates recycler adapter class

    private var dataSet = data

    override fun getItemCount() = dataSet.size //sets count to list size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context) //ease of use var
        val view = inflater.inflate(R.layout.recycler_item, parent, false) as CardView //inflates to parent as cardview
        return ItemViewHolder(view) //returns the view
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) { //sets the view holder on bind to the item in data position
        val item = dataSet[position]
        holder.bind(item, position)
    }

    inner class ItemViewHolder(private val rootView: CardView) : RecyclerView.ViewHolder(rootView) { //initiates inner view holder class

        private val itemTitle = rootView.findViewById<TextView>(R.id.recyclerText) //creates var for easy access to recyclerText

        fun bind(item: String, index: Int) { //creates bind function which sets itemText to the string in list
            itemTitle.text = item

            val deleteBtn = rootView.findViewById<ImageButton>(R.id.deleteBtn)
            deleteBtn.setOnClickListener { //if button clicked
                onClickDelete(index) //sets on click delete to index
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetItems(items: MutableList<String>) { //resets when delete button is used
        dataSet = items
        notifyDataSetChanged()
    }
}