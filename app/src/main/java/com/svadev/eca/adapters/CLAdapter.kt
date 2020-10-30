package com.svadev.eca.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.svadev.eca.R
import com.svadev.eca.models.ContractModel
import com.svadev.eca.priceToString
import com.svadev.eca.stationIdToName
import com.svadev.eca.volumeToString
import kotlinx.android.synthetic.main.contract_list_item.view.*

class CLAdapter(private val myContractOnClickListener: ContractOnClickListener) :
    RecyclerView.Adapter<CLAdapter.CLViewHolder>() {
    private var contracts = emptyList<ContractModel>()

    fun updateContractList(newContracts: List<ContractModel>, sortBy: Int) {
        var finalContracts = emptyList<ContractModel>()
        when (sortBy) {
            1 -> finalContracts = newContracts.sortedWith(compareBy { it.volume })
            2 -> finalContracts = newContracts.sortedWith(compareByDescending { it.volume })
            3 -> finalContracts = newContracts.sortedWith(compareBy { it.price })
            4 -> finalContracts = newContracts.sortedWith(compareByDescending { it.price })
            5 -> finalContracts = newContracts.sortedWith(compareBy { it.dateIssued })
            6 -> finalContracts = newContracts.sortedWith(compareByDescending { it.dateIssued })
        }
        contracts = finalContracts.toMutableList()
    }

    fun getContractId(position: Int): Int = contracts[position].contractId ?: 0

    class CLViewHolder(
        inflater: LayoutInflater,
        var parent: ViewGroup,
        var contractOnClickListener: ContractOnClickListener
    ) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.contract_list_item, parent, false
        )
    ), View.OnClickListener {
        private var titleTextView: TextView? = null
        private var locationTextView: TextView? = null
        private var priceTextView: TextView? = null
        private var volumeTextView: TextView? = null

        override fun onClick(v: View?) {
            contractOnClickListener.onItemClick(adapterPosition)
        }

        init {
            titleTextView = itemView.viewTextTitle
            locationTextView = itemView.viewTextLocation
            priceTextView = itemView.viewTextPrice
            volumeTextView = itemView.viewTextVolume
            itemView.setOnClickListener(this)
        }

        fun bind(item: ContractModel) {
            when (item.contractId) {
                0, 1 -> {
                    titleTextView?.text = item.title
                }
                else -> {
                    titleTextView?.text = item.title
                    locationTextView?.text = stationIdToName(item.startLocationId, parent.context)
                    priceTextView?.text = priceToString(item.price)
                    volumeTextView?.text = volumeToString(item.volume)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CLViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CLViewHolder(inflater, parent, myContractOnClickListener)
    }

    override fun onBindViewHolder(holder: CLViewHolder, position: Int) {
        holder.bind(contracts[position])
    }

    override fun getItemCount() = contracts.size

    interface ContractOnClickListener {
        fun onItemClick(position: Int)
    }
}