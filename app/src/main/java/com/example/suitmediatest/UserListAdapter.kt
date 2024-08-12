package com.example.suitmediatest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserListAdapter(
    private val onUserClicked: (User) -> Unit
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private val users = mutableListOf<User>()

    fun submitList(userList: List<User>) {
        users.clear()
        users.addAll(userList)
        notifyDataSetChanged()
    }

    fun appendList(userList: List<User>) {
        val startIndex = users.size
        users.addAll(userList)
        notifyItemRangeInserted(startIndex, userList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val userEmail: TextView = itemView.findViewById(R.id.userEmail)

        fun bind(user: User) {
            userName.text = "${user.firstName} ${user.lastName}"
            userEmail.text = user.email
            Glide.with(itemView.context).load(user.avatar).into(userAvatar)
            itemView.setOnClickListener { onUserClicked(user) }
        }
    }
}
