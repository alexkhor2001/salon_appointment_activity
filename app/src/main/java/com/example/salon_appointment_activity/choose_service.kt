package com.example.salon_appointment_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class choose_service : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceArrayList: ArrayList<serviceModel>
    private lateinit var imageId : ArrayList<Int>
    private lateinit var heading : ArrayList<String>

    private val sharedViewModel: sharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_service)

        imageId = arrayListOf (
            R.drawable.img_image3,
            R.drawable.img_image4,
            R.drawable.img_image5,
            R.drawable.img_image6,
            R.drawable.img_image7,
            R.drawable.img_image8
        )

        heading = arrayListOf(
            "Hair Dry",
            "Hair Stream",
            "Hair Cut",
            "Perm",
            "Hair Wash",
            "Hair Dye"
        )

        recyclerView = findViewById(R.id.chooseServiceRecyclerview)
        recyclerView.layoutManager = GridLayoutManager(getApplicationContext(),2,
            GridLayoutManager.VERTICAL,false)
        recyclerView.setHasFixedSize(true)

        serviceArrayList = arrayListOf<serviceModel>()
        getServicesData()
    }

    private fun getServicesData(){
        for(i in imageId.indices){
            val times = serviceModel(imageId[i], heading[i])
            serviceArrayList.add(times)
        }
        var adapter = chooseServiceAdapter(serviceArrayList)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object : chooseServiceAdapter.onItemClickListener{
            override fun onItemClick(view: View) {
                val name = view.findViewById<TextView>(R.id.serviceTextView)
                Toast.makeText(applicationContext , "service choosed is ${name.text.toString()}", Toast.LENGTH_SHORT).show()
                sharedViewModel.setServiceChoose(name.text.toString())
                val intent = Intent(applicationContext,booking_hairdresser::class.java).also {
                    it.putExtra("ExtraService",name.text.toString())
                }
                startActivity(intent)
            }

        })
    }
}