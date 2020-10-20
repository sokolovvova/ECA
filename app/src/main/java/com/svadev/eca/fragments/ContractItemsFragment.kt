package com.svadev.eca.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.svadev.eca.*
import com.svadev.eca.adapters.ItemsAdapter
import com.svadev.eca.db.ContractsDatabase
import com.svadev.eca.db.SavedContractsDatabase
import com.svadev.eca.models.ContractModel
import kotlinx.android.synthetic.main.contract_items_fragment.*
import kotlinx.android.synthetic.main.contract_items_fragment.view.*

class ContractItemsFragment: Fragment() {
    private lateinit var model: MainViewModel
    private lateinit var contract: ContractModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: ItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.contract_items_fragment,container,false)
        model = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        var fabOpen = false
        var isSaved = false

        itemsAdapter= ItemsAdapter()
        recyclerView = view.itemsRV.apply {
            adapter=itemsAdapter
            layoutManager=LinearLayoutManager(context)
        }

        model.selectedId.observe(viewLifecycleOwner){
            if(it!=0){
                contract =
                    if(model.datasource==1) ContractsDatabase.getInstance(context!!).contractsDao().getContractById(it)
                    else SavedContractsDatabase.getInstance(context!!).contractsDao().getContractById(it)
                viewTitle.text=contract.title
                viewContractId.text="id: "+contract.contractId.toString()
                viewDateIssued.text=contract.dateIssued?.replace("T"," ",true)?.replace("Z"," ",true)
                viewDateExpired.text=contract.dateExpired?.replace("T"," ",true)?.replace("Z"," ",true)
                viewLocation.text= stationIdToName(contract.startLocationId, context!!)
                viewPrice.text= priceToString(contract.price)
                viewVolume.text= volumeToString(contract.volume)
                model.getContractData()
            }
            else {contract= ContractModel(null,null,null,null,null,null,null,null,null,null,null,null,null)
            fab.visibility = View.INVISIBLE}
        }
        model.currentContractItems.observe(viewLifecycleOwner){
            itemsAdapter.updateItemList(it)
            itemsAdapter.notifyDataSetChanged()
            if(it.isNotEmpty())(activity as MainActivity).showProgressBar(0)
        }

        view.fab.setOnClickListener {
            if(fabOpen){
                fabSave.visibility = View.INVISIBLE
                fabShare.visibility = View.INVISIBLE
                DrawableCompat.setTintList(DrawableCompat.wrap(fab.background),context?.getColorStateList(R.color.colorPrimary))
                fabOpen = false
            }
            else{
                fabSave.visibility = View.VISIBLE
                fabShare.visibility = View.VISIBLE
                DrawableCompat.setTintList(DrawableCompat.wrap(fab.background),context?.getColorStateList(R.color.red))
                fabOpen = true
            }
        }
        view.fabShare.setOnClickListener {
            if(this::contract.isInitialized){
                val sendIntent: Intent = Intent().apply{
                    val contractString ="<url=contract:30003576//${contract.contractId.toString()}>${contract.title}</url>"
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,contractString)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent,null)
                startActivity(shareIntent)
            }
            fabSave.visibility = View.INVISIBLE
            fabShare.visibility = View.INVISIBLE
            DrawableCompat.setTintList(DrawableCompat.wrap(fab.background),context?.getColorStateList(R.color.colorPrimary))
            fabOpen = false
        }
        model.savedContractListLD.observe(viewLifecycleOwner){
            isSaved = false
            for(item in it){
                if (item.contractId == contract.contractId) isSaved = true
            }
            if(isSaved) fabSave.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_star_24))
            else fabSave.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_star_border_24))
        }
        view.fabSave.setOnClickListener {
            if (!isSaved)
                model.saveContract(contract)
            else
                contract.contractId?.let { it1 -> model.removeSavedContract(it1) }
        }

        return view
    }

    override fun onDestroy() {
        model.clearCurrentContractItems()
        super.onDestroy()
    }
}