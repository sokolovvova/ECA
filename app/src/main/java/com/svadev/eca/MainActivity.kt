package com.svadev.eca

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.observe
import com.svadev.eca.fragments.ContractItemsFragment
import com.svadev.eca.fragments.ContractListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var regionMap: Map<String,Long> = mapOf("PR-01" to 13000001,"ADR05" to 12000005,"ADR04" to 12000004,"ADR03" to 12000003,"ADR02" to 12000002,"ADR01" to 12000001,"K-R00033" to 11000033,"H-R00032" to 11000032,"G-R00031" to 11000031,"F-R00030" to 11000030,"E-R00029" to 11000029,"E-R00028" to 11000028,"E-R00027" to 11000027,"E-R00026" to 11000026,"E-R00025" to 11000025,"E-R00024" to 11000024,"D-R00023" to 11000023,"D-R00022" to 11000022,"D-R00021" to 11000021,"D-R00020" to 11000020,"D-R00019" to 11000019,"D-R00018" to 11000018,"D-R00017" to 11000017,"D-R00016" to 11000016,"C-R00015" to 11000015,"C-R00014" to 11000014,"C-R00013" to 11000013,"C-R00012" to 11000012,"C-R00011" to 11000011,"C-R00010" to 11000010,"C-R00009" to 11000009,"B-R00008" to 11000008,"B-R00007" to 11000007,"B-R00006" to 11000006,"B-R00005" to 11000005,"B-R00004" to 11000004,"A-R00003" to 11000003,"A-R00002" to 11000002,"A-R00001" to 11000001,"Black Rise" to 10000069,"Verge Vendor" to 10000068,"Genesis" to 10000067,"Perrigen Falls" to 10000066,"Kor-Azor" to 10000065,"Essence" to 10000064,"Period Basis" to 10000063,"Omist" to 10000062,"Tenerifis" to 10000061,"Delve" to 10000060,"Paragon Soul" to 10000059,"Fountain" to 10000058,"Outer Ring" to 10000057,"Feythabolis" to 10000056,"Branch" to 10000055,"Aridia" to 10000054,"Cobalt Edge" to 10000053,"Kador" to 10000052,"Cloud Ring" to 10000051,"Querious" to 10000050,"Khanid" to 10000049,"Placid" to 10000048,"Providence" to 10000047,"Fade" to 10000046,"Tenal" to 10000045,"Solitude" to 10000044,"Domain" to 10000043,"Metropolis" to 10000042,"Syndicate" to 10000041,"Oasa" to 10000040,"Esoteria" to 10000039,"The Bleak Lands" to 10000038,"Everyshore" to 10000037,"Devoid" to 10000036,"Deklein" to 10000035,"The Kalevala Expanse" to 10000034,"The Citadel" to 10000033,"Sinq Laison" to 10000032,"Impass" to 10000031,"Heimatar" to 10000030,"Geminate" to 10000029,"Molden Heath" to 10000028,"Etherium Reach" to 10000027,"Immensea" to 10000025,"Pure Blind" to 10000023,"Stain" to 10000022,"Outer Passage" to 10000021,"Tash-Murkon" to 10000020,"A821-A" to 10000019,"The Spire" to 10000018,"J7HZ-F" to 10000017,"Lonetrek" to 10000016,"Venal" to 10000015,"Catch" to 10000014,"Malpais" to 10000013,"Curse" to 10000012,"Great Wildlands" to 10000011,"Tribute" to 10000010,"Insmother" to 10000009,"Scalding Pass" to 10000008,"Cache" to 10000007,"Wicked Creek" to 10000006,"Detorid" to 10000005,"UUA-F4" to 10000004,"Vale of the Silent" to 10000003,"The Forge" to 10000002,"Derelik" to 10000001)
    private var sortList = listOf<String>("Volume \u02C4","Volume \u02C5","Price \u02C4","Price \u02C5","Date \u02C4","Date \u02C5")
    private val model: MainViewModel by viewModels()
    private var firstStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(viewToolbar)
        viewToolbar.title="Public contracts"
        supportFragmentManager.beginTransaction().add(R.id.mainFragmentContainer,
            ContractListFragment()
        ).commit()
        configureNavigationDrawer()
        viewOkButtonRegion.setOnClickListener {
            model.setNewRegion(sendRegion(viewRegionTextView.text.toString()))
            closeKeyboard()
        }

        viewRegionTextView.setOnEditorActionListener { _, actionId, _ ->
            if(actionId==EditorInfo.IME_ACTION_SEND) model.setNewRegion(sendRegion(viewRegionTextView.text.toString()))
            closeKeyboard()
            true
        }
        viewRegionTextView.setOnClickListener { viewRegionTextView.selectAll() }
        viewRefreshButton.setOnClickListener {
            model.changeDataSource(1)
            showProgressBar(1)
            model.vmFragmentChanger(1)
            model.updateContractList()
            viewRefreshButton.isEnabled=false
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,sortList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewSpinner.adapter = adapter
        viewSpinner.setSelection(4)

        viewSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                model.setNewSortOrder(position+1)
                if(!firstStart) showProgressBar(1)
                firstStart = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        model.selectedRegion.observe(this){
            n-> viewRegionName.text = regionMap.filterValues { it==n }.keys.toString()
        }
        model.currentFragment.observe(this){
            changeFragment(it)
        }
        model.contractListLD.observe(this){
            if (it.isNotEmpty()) {
                showProgressBar(0)
                viewRefreshButton.isEnabled=true
                viewContractSize.text="[${it.size}]"
            }
        }
        viewRegionTextView?.setAdapter(ArrayAdapter(applicationContext,android.R.layout.simple_dropdown_item_1line,regionMap.keys.toList()))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }

    fun showProgressBar(n: Int){
        if (n==0)  viewToolbar.menu.getItem(0).actionView = null
        else{
            val pb = ProgressBar(this)
            pb.setBackgroundColor(applicationContext.getColor(R.color.colorPrimary))
            pb.setPadding(20,20,20,20)
            pb.layoutParams = LinearLayout.LayoutParams(140,140)
            val ll = LinearLayout(applicationContext)
            ll.addView(pb)
            viewToolbar.menu.getItem(0).actionView = ll
        }
    }
    private fun closeKeyboard(){
        val view = this.currentFocus
        if(view!=null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
    private fun changeFragment(fragmentId: Int){
        when(fragmentId){
            1-> {
                supportFragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, ContractListFragment()).commit()
            }
            2-> supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, ContractItemsFragment()).addToBackStack(fragmentId.toString()).commit()
        }
    }
    override fun onDestroy() {
        model.destroyThread()
        super.onDestroy()
    }
    private fun sendRegion(region: String): Long{
        return if(regionMap.containsKey(region)) regionMap[region]?.toLong()!!
        else{
            val toast = Toast.makeText(applicationContext, "Region not found!", Toast.LENGTH_SHORT)
            toast.show()
            10000043
        }
    }
    private fun configureNavigationDrawer(){
        navigation.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.itemPublicContracts-> {
                    showBottomElements(true)
                    viewToolbar.title ="Public contracts"
                    drawerLayout.closeDrawer(GravityCompat.START)
                    model.changeDataSource(1)
                    showProgressBar(1)
                    model.vmFragmentChanger(1)
                    model.updateContractList()
                true}
                R.id.itemSavedContracts ->{
                    showBottomElements(false)
                    viewToolbar.title ="Saved contracts"
                    drawerLayout.closeDrawer(GravityCompat.START)
                    model.changeDataSource(2)
                    model.vmFragmentChanger(1)
                    model.updateSavedDatabase()
                    true}

                else -> true
            }
        }
    }

    private fun showBottomElements(it: Boolean){
        if(it){
            viewRefreshButton.visibility = View.VISIBLE
            viewRegionName.visibility = View.VISIBLE
            viewOkButtonRegion.visibility = View.VISIBLE
            viewRegionTextView.visibility = View.VISIBLE
            viewContractSize.visibility = View.VISIBLE
        }
        else{
            viewRefreshButton.visibility = View.INVISIBLE
            viewRegionName.visibility = View.INVISIBLE
            viewOkButtonRegion.visibility = View.INVISIBLE
            viewRegionTextView.visibility = View.INVISIBLE
            viewContractSize.visibility = View.INVISIBLE
        }
    }
}