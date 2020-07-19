package com.cahum.mentor

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.cahum.mentor.modelo.Mensaje
import com.cahum.mentor.modelo.UltimoMensajeItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val mapaDeMensajes = HashMap<String, Mensaje>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        recyclerview_ultimos_mensajes.adapter = adapter
        recyclerview_ultimos_mensajes.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener{ item, view ->
            val ultimoMensaje = item as UltimoMensajeItem
            val intent = Intent(this, ChatLogActivity::class.java)
            intent.putExtra("USER_KEY",ultimoMensaje.usuarioChatPartner)
            startActivity(intent)

        }
        conseguirUltimosMensajes()
    }

    private fun conseguirUltimosMensajes() {
        val fromId = FirebaseAuth.getInstance().uid
        val refUltimoMensaje = FirebaseDatabase.getInstance().getReference("/ultimos-mensajes/${fromId}")
        refUltimoMensaje.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val mensaje = p0.getValue(Mensaje::class.java) ?: return
                mapaDeMensajes[p0.key!!] = mensaje
                actualizarRecycler()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val mensaje = p0.getValue(Mensaje::class.java) ?: return
                mapaDeMensajes[p0.key!!] = mensaje
                actualizarRecycler()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun actualizarRecycler() {
        adapter.clear()
        mapaDeMensajes.values.forEach {
            adapter.add(UltimoMensajeItem(it))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, InicioActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.menu_perfil -> {
                startActivity(Intent(this, PerfilActivity::class.java))
            }

            R.id.menu_nuevos_clientes -> {
                startActivity(Intent(this, NuevosClientesActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


}

