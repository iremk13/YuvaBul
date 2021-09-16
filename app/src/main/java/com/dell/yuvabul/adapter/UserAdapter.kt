package com.dell.yuvabul.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dell.yuvabul.ChatActivity
import com.dell.yuvabul.R
import com.dell.yuvabul.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context,private val userList:ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.UserName
        Glide.with(context).load(user.profileImage).placeholder(R.drawable.desse)
            .into(holder.imgUser)
        holder.layoutUser.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("UserId", user.UserId)
            intent.putExtra("UserName", user.UserName)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtUserName:TextView = view.findViewById(R.id.UserName)
        val txtTemp:TextView = view.findViewById(R.id.temp)
        val imgUser:CircleImageView = view.findViewById(R.id.userImage)
        val layoutUser:LinearLayout = view.findViewById(R.id.layoutUser)

    }
}