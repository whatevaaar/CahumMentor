package com.cahum.mentor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cahum.mentor.modelo.ChatItemFrom
import com.cahum.mentor.modelo.ChatItemTo
import com.cahum.mentor.modelo.Cliente
import com.cahum.mentor.modelo.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chatlog.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    var usuarioDestino: Cliente? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        usuarioDestino = intent.getParcelableExtra<Cliente>("USER_KEY")
        supportActionBar?.title = usuarioDestino!!.nombre
        escucharMensajes()
        recyclerview_chat.adapter = adapter
        boton_enviar.setOnClickListener {
            mandarMensaje()
        }
    }

    private fun escucharMensajes() {
        val usuario = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/mensajes-usuario/${usuario}/${usuarioDestino!!.uid}")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val mensaje = p0.getValue(Mensaje::class.java) ?: return
                if (mensaje.fromId == usuario)
                    adapter.add(ChatItemFrom(mensaje.texto))
                else
                    adapter.add(ChatItemTo(mensaje.texto))
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
        recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
    }

    private fun mandarMensaje() {
        val texto = texto_mensaje.text.toString()
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = usuarioDestino!!.uid
        val refMandar = FirebaseDatabase.getInstance().getReference("/mensajes-usuario/${fromId}/${toId}").push()
        val refRecibir = FirebaseDatabase.getInstance().getReference("/mensajes-usuario/${toId}/${fromId}").push()
        val refUltimoMensaje = FirebaseDatabase.getInstance().getReference("/ultimos-mensajes/${fromId}/${toId}")
        val refUltimoMensajeMandar = FirebaseDatabase.getInstance().getReference("/ultimos-mensajes/${toId}/${fromId}")
        val mensaje = Mensaje(refMandar.key!!, texto, fromId, toId, System.currentTimeMillis() / 1000)
        refRecibir.setValue(mensaje)
        refUltimoMensaje.setValue(mensaje)
        refUltimoMensajeMandar.setValue(mensaje)
        refMandar.setValue(mensaje)
            .addOnSuccessListener {
                texto_mensaje.text.clear()
                recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
                registrarClienteConMentor(fromId)
            }
    }

    private fun registrarClienteConMentor(idMentor: String) {
        if (usuarioDestino == null) return
        val idCliente: String = usuarioDestino!!.uid
        usuarioDestino!!.uidMentor = idMentor
        val ref= FirebaseDatabase.getInstance().getReference("/clientes")
        ref.child(idCliente).setValue(usuarioDestino)
    }

}
