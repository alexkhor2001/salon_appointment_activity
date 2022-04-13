package com.example.salon_appointment_activity

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class booking_hairdresser : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hairdresserArrayList: ArrayList<hairdresserModel>
    private lateinit var imageId : ArrayList<Int>
    private lateinit var heading : ArrayList<String>
    private lateinit var img : Bitmap
    private lateinit var extras : Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_hairdresser)

        imageId = arrayListOf (
            R.drawable.img_2328596phmodi,
            R.drawable.img_20200719159511,
            R.drawable.img_15408877569017,
            R.drawable.img_hairdresser4,
            R.drawable.img_hairdresser5
        )

        heading = arrayListOf(
            "Sim Pei Wen",
            "Lily Tan Xian Yan",
            "Kim Hao Yu",
            "Lucas",
            "Davil"
        )
        recyclerView = findViewById(R.id.bookingHairdresserRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)
        hairdresserArrayList = arrayListOf<hairdresserModel>()
        getServicesData()
    }

    private fun getServicesData(){
        for(i in imageId.indices){
            val times = hairdresserModel(heading[i],imageId[i])
            hairdresserArrayList.add(times)
        }
        var adapter = hairdresserChooseAdapter(hairdresserArrayList)
        recyclerView.adapter = adapter
        adapter.setOnClickListener(object : hairdresserChooseAdapter.onItemClickListener{
            override fun onItemClick(view: View) {
                val name = view.findViewById<TextView>(R.id.txtHairdresserName)
                val image = view.findViewById<ImageView>(R.id.imgHairdresserProfile)
                Toast.makeText(applicationContext , "hairdresser choosed is ${name.text.toString()}", Toast.LENGTH_SHORT).show()
                img = image.getDrawingCache()
                extras.putParcelable("imagebitmap",img)
                val intent = Intent(applicationContext,booking_time::class.java).also {
                    it.putExtra("ExtraHairdresserName",name.text.toString())
                    it.putExtra("ExtraHairdresserImg",img)
                }
                startActivity(intent)
            }

        })
    }
}