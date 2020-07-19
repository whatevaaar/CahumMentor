package com.cahum.mentor.modelo

import com.cahum.mentor.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_row.view.*

class UserItem(internal val cliente: Cliente) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username.text = cliente.nombre
    }
}

