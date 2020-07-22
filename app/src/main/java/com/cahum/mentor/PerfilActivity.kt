package com.cahum.mentor

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cahum.mentor.modelo.Mentor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_perfil.*

class PerfilActivity : AppCompatActivity() {
    private val usuario = FirebaseAuth.getInstance().currentUser
    private val ref = FirebaseDatabase.getInstance().getReference("/mentores/${usuario!!.uid}")
    private val tag = "PerfilMentor"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag, "ERROR")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mentor = dataSnapshot.getValue(Mentor::class.java)
                if (mentor != null) actualizarPantalla(mentor)
            }

        }
        ref.addListenerForSingleValueEvent(postListener)
    }

    private fun actualizarPantalla(mentor: Mentor) {
        texto_nombre_perfil_mentor.text = usuario!!.displayName
        texto_correo_perfil_mentor.text = usuario.email
        ratingBar.rating = mentor.calificacion.toFloat()
        supportActionBar?.title = "Perfil de ${mentor.nombre}"
    }


}
