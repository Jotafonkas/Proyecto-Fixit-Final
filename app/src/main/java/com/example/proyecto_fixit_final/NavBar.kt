package com.example.proyecto_fixit_final

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.databinding.NavBarBinding
import com.example.proyecto_fixit_final.fragments.HelpFragment
import com.example.proyecto_fixit_final.fragments.HomeFragment
import com.example.proyecto_fixit_final.fragments.MenuFragment
import com.example.proyecto_fixit_final.fragments.ServicesFragment

class NavBar : AppCompatActivity() {
    //COnfigurar binding
    private lateinit var binding: NavBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=NavBarBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Cargar un fragment cuando incie la app
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, HomeFragment()).commit()
        }

        //Configurar el bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.nav_home -> {
                    //Mostrar el fragment de home
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, HomeFragment()).commit()
                    true
                }

                R.id.nav_services -> {
                    //Mostrar el fragment de home
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, ServicesFragment()).commit()
                    true
                }

                R.id.nav_menu -> {
                    //Mostrar el fragment de home
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, MenuFragment()).commit()
                    true
                }

                R.id.nav_help -> {
                    //Mostrar el fragment de home
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, HelpFragment()).commit()
                    true
                }

                else -> false
            }
        }

        //Configurar el evento de reseleccionar un item
        binding.bottomNavigationView.setOnItemReselectedListener {
            when(it.itemId){
                R.id.nav_home -> Toast.makeText(this,"Ya estas en el home", Toast.LENGTH_SHORT).show()
                R.id.nav_services -> Toast.makeText(this,"Ya estas en Servicios", Toast.LENGTH_SHORT).show()
                R.id.nav_menu -> Toast.makeText(this,"Ya estas en el menu", Toast.LENGTH_SHORT).show()
                R.id.nav_help -> Toast.makeText(this,"Ya estas en ayuda", Toast.LENGTH_SHORT).show()
                else -> false
            }
        }
    }
}