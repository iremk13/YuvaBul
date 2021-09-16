package com.dell.yuvabul

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class feedRecyclerAdapter(val ilanlistesi: ArrayList<ilan>) :
    RecyclerView.Adapter<feedRecyclerAdapter.ilanHolder>() {

    class ilanHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    //xml ile kodu birbirine bağlama (recycler_row ile)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ilanHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row, parent, false)
        return ilanHolder(view)
    }

    override fun onBindViewHolder(holder: ilanHolder, position: Int) {

        holder.itemView.recycler_row_kullaniciadi.text = ilanlistesi[position].kullaniciadi
        holder.itemView.recycler_row_kullanici_email.text = ilanlistesi[position].kullaniciemail
        holder.itemView.recycler_row_kediköpek.text = ilanlistesi[position].kediköpek
        holder.itemView.recycler_row_yas.text = ilanlistesi[position].yas
        holder.itemView.recycler_row_irk.text = ilanlistesi[position].irk
        holder.itemView.recycler_row_cinsiyet.text = ilanlistesi[position].cinsiyet
        holder.itemView.recycler_row_il.text = ilanlistesi[position].il
        holder.itemView.recycler_row_ilce.text = ilanlistesi[position].ilce
        holder.itemView.recycler_row_genelbilgi.text = ilanlistesi[position].genelbilgi
        holder.itemView.recycler_row_barinak.text = ilanlistesi[position].barinak
        Picasso.get().load(ilanlistesi[position].gorselurl)
            .into(holder.itemView.recycler_row_imageview)


    }

    override fun getItemCount(): Int {

        return ilanlistesi.size
    }


}