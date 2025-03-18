package com.example.aidruginteractionchecker
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemAdapter(private val data: MutableList<String>) : RecyclerView.Adapter<RecyclerItemAdapter.ItemViewHolder>() { //initiates recycler adapter class

    override fun getItemCount() = data.size //sets count to list size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = ItemViewHolder.inflate(parent) //sets the view holder on create to custom view holder

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) { //sets the view holder on bind to the item in data position
        val item = data[position]

        holder.bind(item)


    }

    class ItemViewHolder(private val rootView: CardView) : RecyclerView.ViewHolder(rootView) { //initiates inner view holder class
        private val itemTitle = rootView.findViewById<TextView>(R.id.recyclerText) //creates var for easy access to recyclerText

        companion object {
            fun inflate(parent: ViewGroup) : ItemViewHolder { //inflates layout to parent as cardview
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.recycler_item, parent, false) as CardView
                return ItemViewHolder(view)
            }
        }

        fun bind(item: String) { //creates bind function which sets itemText to the string in list
            itemTitle.text = item

        }


    }
}