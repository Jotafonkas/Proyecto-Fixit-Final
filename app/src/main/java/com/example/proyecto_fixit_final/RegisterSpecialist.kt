package com.example.proyecto_fixit_final

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterSpecialist : AppCompatActivity() {

    //se declaran las variables del layout
    private lateinit var auth: FirebaseAuth
    private lateinit var btnregistro: Button
    private lateinit var nombreEsp: EditText
    private lateinit var rutEsp: EditText
    private lateinit var correoEsp: EditText
    private lateinit var telefonoEsp: EditText
    private lateinit var profesionEsp: EditText
    private lateinit var passEsp: EditText
    private lateinit var pass2Esp: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_especialista)
        auth= Firebase.auth

        //set variables por id
        btnregistro = findViewById(R.id.btnRegistrarse_especialista)
        nombreEsp = findViewById(R.id.edNombre_especialista)
        rutEsp = findViewById(R.id.edRut_especialista)
        correoEsp = findViewById(R.id.edCorreo_especialista)
        telefonoEsp = findViewById(R.id.edTelefono_especialista)
        profesionEsp = findViewById(R.id.edEspecialidad)
        passEsp = findViewById(R.id.edContraseña_especialista)
        pass2Esp = findViewById(R.id.edConfirmaContraseña_especialista)


        btnregistro.setOnClickListener {
            //obtener datos registro
            val nombre: String = nombreEsp.text.toString()
            val rut: String = rutEsp.text.toString()
            val correo: String = correoEsp.text.toString()
            val telefono: String = telefonoEsp.text.toString()
            val profesion: String = profesionEsp.text.toString()
            val pass: String = passEsp.text.toString()
            val pass2: String = pass2Esp.text.toString()

            //validacion

            if(nombre.isEmpty()){
                nombreEsp.error = "Ingrese su nombre"
                return@setOnClickListener
            }
            if(rut.isEmpty()){
                rutEsp.error = "Ingrese su rut"
                return@setOnClickListener
            }
            if(correo.isEmpty()){
                correoEsp.error = "Ingrese su correo"
                return@setOnClickListener
            }
            if(telefono.isEmpty()){
                telefonoEsp.error = "Ingrese su telefono"
                return@setOnClickListener
            }
            if(profesion.isEmpty()){
                profesionEsp.error = "Ingrese su profesion/especialidad"
                return@setOnClickListener
            }
            if(pass.isEmpty()){
                passEsp.error = "Ingrese su contraseña"
                return@setOnClickListener
            }
            if(pass2.isEmpty()){
                pass2Esp.error = "Repita su contraseña"
                return@setOnClickListener
            }
            if (pass==pass2){
                registrarNuevoUsuario(correo,pass)

            }else{
                Toast.makeText(this,"las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            }

        }

    }

    fun openHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }
    private fun registrarNuevoUsuario(nombre: String,pass: String){
        auth.createUserWithEmailAndPassword(nombre,pass)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"usuario creado completamente", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"error", Toast.LENGTH_LONG).show()
                }
            }
    }
}