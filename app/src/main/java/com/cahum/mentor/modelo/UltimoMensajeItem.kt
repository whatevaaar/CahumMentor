package com.cahum.mentor.modelo

import com.cahum.mentor.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.ultimos_mensajes.view.*

class UltimoMensajeItem(private val mensaje: Mensaje) : Item<GroupieViewHolder>() {
    var usuarioChatPartner: Cliente? = null

    override fun getLayout(): Int {
        return R.layout.ultimos_mensajes
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartner = if (mensaje.fromId == FirebaseAuth.getInstance().uid) mensaje.toId else mensaje.fromId
        val ref = FirebaseDatabase.getInstance().getReference("/clientes/${chatPartner}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                usuarioChatPartner = p0.getValue(Cliente::class.java)
                viewHolder.itemView.text_nombre_usuario.text = usuarioChatPartner?.nombre
            }

        })
        viewHolder.itemView.texto_ultimo_mensaje.text = mensaje.texto
    }
}