package com.svadev.eca.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.svadev.eca.R
import com.svadev.eca.db.EveSdaDatabase
import com.svadev.eca.models.ContractItemModel
import com.svadev.eca.volumeToString
import kotlinx.android.synthetic.main.item_list_item.view.*
import java.io.FileNotFoundException
import java.io.IOException

class ItemsAdapter: RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {
    private var items = emptyList<ContractItemModel>()


    fun updateItemList(newIL: List<ContractItemModel>){
        items=newIL.toMutableList()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemsViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val item: ContractItemModel = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int =items.size

    class ItemsViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_list_item,parent,false)){
        private var mItemName: TextView?=null
        private var mStatus: ImageView?=null
        private var mNumber: TextView?=null
        private var mVolume: TextView?=null
        private var mME: TextView?=null
        private var mTE: TextView?=null
        private var mRuns: TextView?=null
        private var mImage: ImageView?=null

        init{
            mItemName = itemView.viewTitle
            mStatus = itemView.viewStatus
            mNumber = itemView.viewNumber
            mVolume = itemView.viewVolume
            mME = itemView.viewME
            mTE = itemView.viewTE
            mRuns = itemView.viewCopyNumber
            mImage= itemView.viewImage
        }

        fun bind(item: ContractItemModel){
            when(item.type_id){
                1090000->{
                    mItemName?.text = "some error"
                }
                1090001->{
                    mItemName?.text = "error[400] Contract not found!"
                }
                1090003->{
                    mItemName?.text = "error[403] Forbidden"
                }
                else->{
                    var name = EveSdaDatabase.getInstance(mItemName?.context!!).eveSdaDao().getItemNameByTypeId(item.type_id!!)
                    if(item.is_included==true) mStatus?.setBackgroundColor(mStatus?.context?.getColor(R.color.green)!!) else mStatus?.setBackgroundColor(mStatus?.context?.getColor(R.color.red)!!)
                    mNumber?.text = "x${item.quantity.toString()}"
                    mVolume?.text = volumeToString(EveSdaDatabase.getInstance(mItemName?.context!!).eveSdaDao().getItemVolumeByTypeId(item.type_id)*item.quantity!!)
                    if(item.is_blueprint_copy!=null&&item.is_blueprint_copy==true) name = "[BPC]$name"
                    mItemName?.text= name
                    if(item.material_efficiency!=null) mME?.text = "ME: "+item.material_efficiency.toString()
                    if(item.time_efficiency!=null) mTE?.text = "TE: "+item.time_efficiency.toString()
                    if(item.runs!=null)mRuns?.text = "Runs:"+item.runs.toString()

                    try{
                        val ims = mItemName?.context!!.assets.open("img/item_icons/${item.type_id}_32.png")
                        val d = Drawable.createFromStream(ims,null)
                        mImage?.setImageDrawable(d)
                    }
                    catch (ex:FileNotFoundException){
                        val ims = mItemName?.context!!.assets.open("img/item_icons/0_32.png")
                        val d = Drawable.createFromStream(ims,null)
                        mImage?.setImageDrawable(d)
                    }
                    catch (ex: IOException){}
                }
            }
        }
    }
}