package com.cahum.mentor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cahum.mentor.control.MyFirebaseMessagingService
import com.cahum.mentor.modelo.Mentor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

const val RC_SIGN_IN = 200

class RegistrarActivity : AppCompatActivity() {
    private lateinit var botonSignIn: SignInButton
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val usuario = firebaseAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Registro"
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setOnClickListener {
            if (checkBoxTerminos.isChecked)
                signIn()
            else Toast.makeText(
                this,
                "Primero tienes que estar de acuerdo con los términos y condiciones.",
                Toast.LENGTH_LONG
            ).show()
        }
        boton_registrar.setOnClickListener {
            if (checkBoxTerminos.isChecked)
                registrarConCorreo()
            else Toast.makeText(
                this,
                "Primero tienes que estar de acuerdo con los términos y condiciones.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun signIn() {
        if (usuarioLogeado())
            return
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onStart() {
        super.onStart()
        if (usuarioLogeado()) {
            registrarUsuarioSiNoExiste()
            redirigirAMenu()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task!!)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Toast.makeText(this, "Error al autenticarse", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("LOGIN", "firebaseAuthWithGoogle:" + account.id!!)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication Completed.", Toast.LENGTH_LONG).show()
                    redirigirAMenu()
                } else Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
    }

    private fun registrarConCorreo() {
        val nombre = texto_nombre.editText?.text.toString()
        val correo = texto_correo.editText?.text.toString()
        val pass = texto_password.editText?.text.toString()
        if (nombre.isEmpty() or correo.isEmpty() or pass.isEmpty()) {
            Toast.makeText(this, "Error al autenticarse", Toast.LENGTH_LONG).show()
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Authentication Completed.", Toast.LENGTH_LONG).show()
                    registrarEnFirebase(nombre)
                    redirigirAMenu()
                }
            }
    }

    private fun registrarEnFirebase(nombre: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(nombre).build()
        val usuario = FirebaseAuth.getInstance().currentUser
        usuario!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    registrarUsuarioSiNoExiste()
                    Log.d("LOGIN", "User password updated.")
                }
            }
    }

    private fun redirigirAMenu() {
        MyFirebaseMessagingService().conseguirToken()
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun usuarioLogeado(): Boolean = usuario != null
    private fun registrarUsuarioSiNoExiste() {
        val usuario = FirebaseAuth.getInstance().currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("/mentores/${usuario.uid}")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) ref.setValue(Mentor(usuario.uid, usuario.displayName!!))
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
    }

}


