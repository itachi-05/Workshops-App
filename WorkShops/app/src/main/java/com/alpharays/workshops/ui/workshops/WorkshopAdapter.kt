package com.alpharays.workshops.ui.workshops

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.alpharays.workshops.R
import com.alpharays.workshops.data.entities.Workshop
import com.google.android.material.button.MaterialButton


class WorkshopAdapter(
    private var applyButtonStatus: Boolean,
    private var deleteButtonStatus: Boolean,
    private var onApplyClickListener: OnApplyClickListener? = null,
    private var onDeleteClickListener: OnDeleteClickListener? = null,
    private val view: View,
    private val context: Context,
    private val listOfImages: HashMap<Long, Drawable>
) :
    RecyclerView.Adapter<WorkshopAdapter.MyViewHolder>() {
    private val workshopList = ArrayList<Workshop>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: CheckedTextView = itemView.findViewById(R.id.workshopName)
        val description: CheckedTextView = itemView.findViewById(R.id.workshopDescription)
        val applyBtn: MaterialButton = itemView.findViewById(R.id.applyButton)
        val workShopImage: ImageView = itemView.findViewById(R.id.workShopImage)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewHolder =
            MyViewHolder(LayoutInflater.from(context).inflate(R.layout.workshopitem, parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentWorkShop = workshopList[position]
        holder.name.text = Html.fromHtml(
            "<strong>" + "Workshop Name" + "</strong><br>" + currentWorkShop.name,
            Html.FROM_HTML_MODE_LEGACY
        )
        holder.description.text = Html.fromHtml(
            "<strong>" + "Description" + "</strong><br>" + currentWorkShop.description,
            Html.FROM_HTML_MODE_LEGACY
        )

        if (!applyButtonStatus) {
            holder.applyBtn.visibility = View.GONE
        } else if (applyButtonStatus) {
            holder.applyBtn.setOnClickListener {
                onApplyClickListener?.onApplyClicked(currentWorkShop.id)
            }
        }

        if (!deleteButtonStatus) {
            holder.deleteButton.visibility = View.GONE
        } else if (deleteButtonStatus) {
            holder.deleteButton.setOnClickListener {
                onDeleteClickListener?.onDeleteClicked(currentWorkShop.id)
            }
        }


        if (listOfImages.containsKey(currentWorkShop.id)) {
            holder.workShopImage.setImageDrawable(listOfImages[currentWorkShop.id])
        }

    }

    override fun getItemCount(): Int {
        return workshopList.size
    }


    fun updateList(newList: ArrayList<Workshop>) {
        workshopList.clear()
        workshopList.addAll(newList)
        notifyDataSetChanged()
    }

    interface OnApplyClickListener {
        fun onApplyClicked(id: Long)
    }

    interface OnDeleteClickListener {
        fun onDeleteClicked(id: Long)
    }

}