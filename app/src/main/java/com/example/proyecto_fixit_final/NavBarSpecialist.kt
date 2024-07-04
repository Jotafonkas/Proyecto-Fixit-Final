package com.example.proyecto_fixit_final

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_fixit_final.databinding.EspecialistaNavbarBinding
import com.example.proyecto_fixit_final.fragments.UsersHelpFragment
import com.example.proyecto_fixit_final.fragments.SpecialistHomeFragment
import com.example.proyecto_fixit_final.fragments.SpecialistMenuFragment
import com.example.proyecto_fixit_final.fragments.SpecialistServicesFragment

class NavBarSpecialist : AppCompatActivity() {
    //COnfigurar binding
    private lateinit var binding: EspecialistaNavbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=EspecialistaNavbarBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Cargar un fragment cuando incie la app
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, SpecialistHomeFragment()).commit()
        }

        //Configurar el bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.nav_home -> {
                    //Mostrar el fragment de home
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, SpecialistHomeFragment()).commit()
                    true
                }

                R.id.nav_services -> {
                    //Mostrar el fragment de services
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, SpecialistServicesFragment()).commit()
                    true
                }

                R.id.nav_menu -> {
                    //Mostrar el fragment de menu
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, SpecialistMenuFragment()).commit()
                    true
                }

                R.id.nav_help -> {
                    //Mostrar el fragment de help
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, UsersHelpFragment()).commit()
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