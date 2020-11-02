package com.svadev.eca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.svadev.eca.MainActivity
import com.svadev.eca.MainViewModel
import com.svadev.eca.adapters.CLAdapter
import com.svadev.eca.R
import com.svadev.eca.models.ContractModel
import kotlinx.android.synthetic.main.contract_list_fragment.view.*

class ContractListFragment: Fragment(),CLAdapter.ContractOnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cLAdapter: CLAdapter
    private lateinit var model: MainViewModel
    private var sortOrder = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.contract_list_fragment,container,false)

        cLAdapter = CLAdapter(this,context!!)

        recyclerView = view.contractsRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cLAdapter
        }
        model = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        model.contractListLD.observe(viewLifecycleOwner){
            cLAdapter.updateContractList(it,sortOrder)
            cLAdapter.notifyDataSetChanged()
        }

        model.sortOrder.observe(viewLifecycleOwner){
            if(sortOrder!= it){
                sortOrder=it
                model.updateContractList()
                model.updateSavedDatabase()
                if(model.datasource.value==2) (activity as MainActivity).showProgressBar(0)
            }
        }

        model.datasource.observe(viewLifecycleOwner){
            when(it){
                1->model.changeTitle("Public contracts")
                2->model.changeTitle("Saved contracts")
                3->model.changeTitle("Char. Contracts")
                4->model.changeTitle("Corp. contracts")
            }
        }

        return view
    }

    override fun onItemClick(position: Int) {
        val id = cLAdapter.getContractId(position)
        model.setNewCurrentContractId(id)
        model.vmFragmentChanger(2)
        if(id!=0)(activity as MainActivity).showProgressBar(1)
    }
}