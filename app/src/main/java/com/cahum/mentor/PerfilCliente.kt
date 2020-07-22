package com.cahum.mentor

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cahum.mentor.modelo.Cliente
import com.cahum.mentor.modelo.Mentor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_perfil_cliente.*

class PerfilCliente : AppCompatActivity() {
    private val tag = "PerfilCliente"
    private var cliente: Cliente? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_cliente)
        supportActionBar?.title = "Perfil"
        var clienteTemp = intent.getParcelableExtra<Cliente>("USER_KEY")
        var ref = FirebaseDatabase.getInstance().getReference("/clientes/${clienteTemp!!.uid}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag, "ERROR")
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cliente = dataSnapshot.getValue(Cliente::class.java)
                actualizarPantalla()
                crearListeners()
            }

        }
        ref.addListenerForSingleValueEvent(postListener)
    }

    private fun crearListeners() {
        checkBoxCv.setOnCheckedChangeListener { _, isChecked ->
            cliente!!.tieneCv = isChecked
            actualizarCliente()
        }
        checkBoxSimulador.setOnCheckedChangeListener { _, isChecked ->
            cliente!!.simulador = isChecked
            actualizarCliente()
        }
        checkBoxPsicometria.setOnCheckedChangeListener { _, isChecked ->
            cliente!!.psicometria = isChecked
            actualizarCliente()
        }
        checkBoxLinkedin.setOnCheckedChangeListener { _, isChecked ->
            cliente!!.perfilLinked = isChecked
            actualizarCliente()
        }
        checkBoxEstrategia.setOnCheckedChangeListener { _, isChecked ->
            cliente!!.estrategia = isChecked
            actualizarCliente()
        }
    }

    private fun actualizarCliente() {
        val ref = FirebaseDatabase.getInstance().getReference("/clientes")
        ref.child(cliente!!.uid).setValue(cliente)
    }

    private fun actualizarPantalla() {
        texto_nombre_perfil_cliente.text = cliente!!.nombre
        supportActionBar?.title = "Perfil de ${cliente!!.nombre}"
        establecerCheckbox()
    }

    private fun establecerCheckbox() {
        checkBoxCv.isChecked = cliente!!.tieneCv
        checkBoxSimulador.isChecked = cliente!!.simulador
        checkBoxPsicometria.isChecked = cliente!!.psicometria
        checkBoxLinkedin.isChecked = cliente!!.perfilLinked
        checkBoxEstrategia.isChecked = cliente!!.estrategia
    }
}
