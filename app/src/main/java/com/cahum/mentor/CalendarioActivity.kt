package com.cahum.mentor

import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cahum.mentor.modelo.Cita
import com.cahum.mentor.modelo.Cliente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_calendario.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class CalendarioActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    @RequiresApi(Build.VERSION_CODES.N)
    private var fechaSeleccionada = Calendar.getInstance()
    private val opcionesDuracion = listOf<String>("30 Minutos", "1 Hora", "1 Hora y 30 Mn", "2 Horas", "Más de 2 Horas")
    private val uidMentor = FirebaseAuth.getInstance().uid
    private var duracion = 30
    private var nombreCliente = ""
    private var uidCliente = ""
    private val listaDeNombres = ArrayList<String>()
    private val listaDeUID = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)
        spinnerClientes!!.onItemSelectedListener = this
        spinnerDuracion!!.onItemSelectedListener = this
        textoHora.text = SimpleDateFormat("HH:mm").format(fechaSeleccionada.time)
        crearAdaptadorSpinnerDuracion()
        conseguirClientes()

        //Listeners
        calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            fechaSeleccionada.set(year, month, dayOfMonth)
        }
        botonCrearCita.setOnClickListener {
            crearCitaEnDB()
            crearCitaEnCalendario()
        }
        textoHora.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                textoHora.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }

    private fun crearCitaEnCalendario() {
        val intent = Intent(Intent.ACTION_EDIT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra("beginTime", fechaSeleccionada.timeInMillis)
        intent.putExtra("title", "Cita con $nombreCliente")
        intent.putExtra("rrule", "FREQ=DAILY")
        if (duracion == 0) //Más de 2 horas
            intent.putExtra("allDay", true)
        else
            intent.putExtra("endTime", fechaSeleccionada.timeInMillis + 60 * duracion * 1000)
        startActivity(intent)
    }

    private fun crearCitaEnDB() {
        val refMandar = FirebaseDatabase.getInstance().getReference("/citas/${uidMentor}/${uidCliente}").push()
        val cita = crearCita()
        refMandar.setValue(cita)
            .addOnSuccessListener {
                Toast.makeText(this, "Cita registrada con éxito", Toast.LENGTH_LONG)
            }
    }

    private fun crearCita(): Cita =
        Cita(uidMentor!!, uidCliente, SimpleDateFormat("dd-MM-yyyy").format(fechaSeleccionada.time), textoHora.text as String, duracion == 0, duracion)

    private fun crearAdaptadorSpinnerDuracion() {
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesDuracion)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDuracion!!.adapter = adaptador
        spinnerDuracion.setSelection(0)
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
                    if (cliente != null && cliente.uidMentor == FirebaseAuth.getInstance().currentUser!!.uid) {
                        listaDeNombres.add(cliente.nombre)
                        listaDeUID.add(cliente.uid)
                    }
                }
                crearAdaptadorSpinnerClientes()
            }
        }
        ref.addValueEventListener(postListener)
    }

    private fun crearAdaptadorSpinnerClientes() {
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaDeNombres)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClientes!!.adapter = adaptador
    }

    //Adaptadores de spinner
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            spinnerClientes.id -> {
                nombreCliente = listaDeNombres[position]
                uidCliente = listaDeUID[position]
            }
            spinnerDuracion.id -> duracion = swichDuracion(position)
        }
    }

    private fun swichDuracion(position: Int): Int {
        when (position) {
            0 -> return 30
            1 -> return 60
            2 -> return 90
            3 -> return 120
            else -> return 0
        }
    }


}
