package com.example.proyecto_fixit_final.Specialist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_fixit_final.R
import com.example.proyecto_fixit_final.Specialist.modelos.Specialist
import com.example.proyecto_fixit_final.adapters.SpecialistAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ViewSpecialistsByCategory : AppCompatActivity() {

    private lateinit var categoryName: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchFilter: EditText
    private lateinit var citySpinner: Spinner
    private val specialistsList = mutableListOf<Specialist>()
    private lateinit var adapter: SpecialistAdapter
    private val displayedList = mutableListOf<Specialist>()
    private var selectedCity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_specialist_client)

        categoryName = intent.getStringExtra("categoryName") ?: ""
        recyclerView = findViewById(R.id.recycler_view_specialists)
        searchFilter = findViewById(R.id.search_filter)
        citySpinner = findViewById(R.id.city_spinner)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SpecialistAdapter(displayedList)
        recyclerView.adapter = adapter

        setupCitySpinner()
        loadSpecialistsByCategory()

        searchFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedCity = if (position == 0) null else parent.getItemAtPosition(position) as String
                filter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCity = null
                filter()
            }
        }
    }

    private fun setupCitySpinner() {
        val cities = resources.getStringArray(R.array.simple_items)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter
    }

    private fun loadSpecialistsByCategory() {
        val db = FirebaseFirestore.getInstance()

        db.collection("especialistas")
            .get()
            .addOnSuccessListener { specialistDocuments ->
                specialistsList.clear()
                displayedList.clear()

                for (specialistDocument in specialistDocuments) {
                    val nombre = specialistDocument.getString("nombre") ?: ""
                    val imageUrl = specialistDocument.getString("imageUrl") ?: ""
                    val ciudad = specialistDocument.getString("ciudad") ?: ""
                    val specialistUid = specialistDocument.id

                    db.collection("especialistas")
                        .document(specialistUid)
                        .collection("servicios")
                        .whereEqualTo("categoria", categoryName)
                        .get()
                        .addOnSuccessListener { serviceDocuments ->
                            for (serviceDocument in serviceDocuments) {
                                val nombreServicio = serviceDocument.getString("nombreServicio") ?: ""
                                val categoria = serviceDocument.getString("categoria") ?: ""
                                val precio = serviceDocument.getString("precio") ?: ""

                                val specialist = Specialist(
                                    uid = specialistUid,
                                    nombre = nombre,
                                    imageUrl = imageUrl,
                                    nombreServicio = nombreServicio,
                                    categoria = categoria,
                                    ciudad = ciudad,
                                    precio = precio
                                )

                                if (!specialistsList.contains(specialist)) {
                                    specialistsList.add(specialist)
                                }
                            }

                            displayedList.clear()
                            displayedList.addAll(specialistsList)
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al cargar los servicios: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los especialistas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filter() {
        val searchText = searchFilter.text.toString().toLowerCase()
        displayedList.clear()

        for (specialist in specialistsList) {
            val matchesSearchText = specialist.nombre.contains(searchText, true) ||
                    specialist.nombreServicio.contains(searchText, true) ||
                    specialist.categoria.contains(searchText, true) ||
                    specialist.precio.contains(searchText, true)

            val matchesCity = selectedCity == null || specialist.ciudad.equals(selectedCity, true)

            if (matchesSearchText && matchesCity) {
                displayedList.add(specialist)
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun backMenu(view: View) {
        super.onBackPressed()
    }
}
