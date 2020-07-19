package com.cahum.mentor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cahum.mentor.modelo.Cliente
import com.cahum.mentor.modelo.UserItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_nuevos_clientes.*

class NuevosClientesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevos_clientes)
        supportActionBar?.title = "Nuevos Clientes"
        conseguirClientes(recycle_view_nuevos_clientes)

    }

}

private fun conseguirClientes(vista: RecyclerView) {
    val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    val ref = FirebaseDatabase.getInstance().getReference("/clientes")
    val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("NuevosClientes", "ERROR")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children.forEach {
                val cliente = it.getValue(Cliente::class.java)
                if (cliente != null && cliente.uidMentor.isEmpty()) adapter.add(UserItem(cliente))
            }
            vista.adapter = adapter
            agregarListener(adapter)
        }

    }
    ref.addListenerForSingleValueEvent(postListener)
}

private fun agregarListener(adapter: GroupAdapter<GroupieViewHolder>) {
    adapter.setOnItemClickListener { item, view ->
        val intent = Intent(view.context, ChatLogActivity::class.java)
        val userItem = item as UserItem
        intent.putExtra("USER_KEY", userItem.cliente)
        view.context.startActivity(intent)
    }
}

