package com.cahum.mentor

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cahum.mentor.modelo.Cliente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_calendario.*


class CalendarioActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    @RequiresApi(Build.VERSION_CODES.N)
    private var fechaSeleccionada = Calendar.getInstance()
    private var nombreCliente = ""
    private val listaDeNombres = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)
        spinner!!.setOnItemSelectedListener(this)
        conseguirClientes()
        calendario.setOnDateChangeListener { view, year, month, dayOfMonth ->
            fechaSeleccionada.set(year, month, dayOfMonth)
        }
        botonCrearCita.setOnClickListener {
            val intent = Intent(Intent.ACTION_EDIT)
            intent.type = "vnd.android.cursor.item/event"
            intent.putExtra("beginTime", fechaSeleccionada.getTimeInMillis())
            intent.putExtra("allDay", false)
            intent.putExtra("rrule", "FREQ=DAILY")
            intent.putExtra("endTime", fechaSeleccionada.getTimeInMillis() + 60 * 60 * 1000)
            intent.putExtra("title", "Cita con ${nombreCliente}")
            startActivity(intent)
        }
    }
    private fun conseguirClientes() {
        val ref = FirebaseDatabase.getInstance().getReference("/clientes")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("CalendarioActivity", "ERROR")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val cliente = it.getValue(Cliente::class.java)
                    if (cliente != null && cliente.uidMentor == FirebaseAuth.getInstance().currentUser!!.uid)
                        listaDeNombres.add(cliente.nombre)
                }
                crearAdaptadorSpinner()
            }
        }
        ref.addValueEventListener(postListener)
    }

    private fun crearAdaptadorSpinner() {
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaDeNombres)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(adaptador)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
       nombreCliente = listaDeNombres[position]
    }


}
