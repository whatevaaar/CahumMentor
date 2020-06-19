package com.cahum.mentor.control

import android.util.Log
import com.cahum.mentor.modelo.Mentor
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class DBManager {
    private lateinit var db: DatabaseReference
    private val tag = "DBManager"

    fun registrarUsuarioSiNoExiste(usuario: FirebaseUser) {

        val ref = FirebaseDatabase.getInstance().getReference("/mentores/${usuario.uid}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(tag, "ERROR")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) ref.setValue(Mentor(usuario.uid,usuario.displayName!!))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
    }


    }