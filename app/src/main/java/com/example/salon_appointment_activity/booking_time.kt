package com.example.salon_appointment_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class booking_time : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var timeslotArrayList: ArrayList<timeModel> // arrayof timelist that send to adapter to process
    lateinit var time : ArrayList<String>// init of the time slot
    lateinit var firebaseBookingResultArrayList: ArrayList<String> // timeslot that match with condition
    lateinit var extras : Bundle
    lateinit var bitmap: Bitmap

    private lateinit var db : FirebaseFirestore

    private val sharedViewModel: sharedViewModel by viewModels()

    lateinit var adapter : TimeslotAdapter

    lateinit var hairdresserNameBuffer : String //buffer
    lateinit var dateBuffer : String // buffer
    var count : Int = 0
    var jNumberNeedToRemove : Int = -1 // the nested loop below have a logic of found which time slot has been booked, but then cannot remove at that loop process due to will cause OutOfBoundIndex exception

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_time)

        dateBuffer = ""
        hairdresserNameBuffer = ""

        time =  arrayListOf(
            "8AM-9AM",
            "9AM-10AM",
            "10AM-11AM",
            "11AM-12PM",
            "1PM-2PM",
            "2PM-3PM",
            "3PM-4PM",
            "4PM-5PM",
            "5PM-6PM"
        )
        firebaseBookingResultArrayList = ArrayList()

        val hairdresserName = findViewById<TextView>(R.id.txtHairdresserName)
        val hairdresserImg = findViewById<ImageView>(R.id.imgHairdresserProfile)
        val txtDate = findViewById<TextView>(R.id.txtDate)
        val btnDatePicker = findViewById<Button>(R.id.btnDatePicker)

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        txtDate.text = currentDate
        dateBuffer = currentDate

        btnDatePicker.setOnClickListener{
            // create new instance of DatePickerFragment
            val datePickerFragment = DatePickerFragment()
            val supportFragmentManager = this.supportFragmentManager

            // we have to implement setFragmentResultListener
            supportFragmentManager.setFragmentResultListener(
                "REQUEST_KEY",
                this
            ) { resultKey, bundle ->
                if (resultKey == "REQUEST_KEY") {
                    val date = bundle.getString("SELECTED_DATE")
                    txtDate.text = date
                    if (date != null) {
                        dateBuffer = date
                    }
                    EventChangeListener() // when onchange also retrieve chosen date and process with array that passed by firebase again
                    Log.i("EventChangeListener","line 89")
                }
            }

            // show
            datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
        }

        //hairdresserName.text = getIntent()
        bitmap = extras.getParcelable("imageBitMap")!!
            extras = getIntent().getExtras()!!

            hairdresserNameBuffer
        sharedViewModel.imgHairdresser.observe(this,{ image ->
            hairdresserImg.setImageDrawable(image)
        })

        EventChangeListener() //init

        recyclerView = findViewById(R.id.bookingTimeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this,3,
            GridLayoutManager.VERTICAL,false)
        recyclerView.setHasFixedSize(true)

        timeslotArrayList = arrayListOf<timeModel>()

        getTimeslotData()
    }

    private fun getTimeslotData(){
        Log.i("code123 getTimeslotData","line 117")

        for (i in time.indices) {
            val times = timeModel(time[i])
            timeslotArrayList.add(times)
        }

        //Log.i("getTimeslotData()","The arrayList have been send to adapter")
        adapter = TimeslotAdapter(timeslotArrayList)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object : chooseServiceAdapter.onItemClickListener{
            override fun onItemClick(view: View) {
                val name: TextView = view.findViewById<TextView>(R.id.txtTime)
                Toast.makeText(getApplicationContext() , "time slot choosed is ${name.text}", Toast.LENGTH_SHORT).show()
                sharedViewModel.setTimeSlotBooking(name.text.toString())
                val intent = Intent(applicationContext,choose_service::class.java)
                startActivity(intent)
            }

        })
    }

    @SuppressLint("LongLogTag")
    fun EventChangeListener(){
        Log.i("EventChangeListener","line 141")

        db = FirebaseFirestore.getInstance()
        db.collection("booking").whereEqualTo("hairdresser",hairdresserNameBuffer).whereEqualTo("bookdate",dateBuffer).get()
            .addOnSuccessListener { documents ->
                Log.i("code123 data dateBuffer",dateBuffer)
                Log.i("code123 hairdresserNameBuffer",hairdresserNameBuffer)

                for (document in documents) {
                    firebaseBookingResultArrayList.add(document.get("booktime").toString())
                    Log.i("code123 The retrieved data","The database retrieved data is ${document.get("booktime").toString()}")
                }
                if(firebaseBookingResultArrayList.size > 0){
                    //Log.i("database result found","The result has found")
                    for( i in 0 .. firebaseBookingResultArrayList.size-1){
                        for(j in 0 ..time.size-1){
                            if(firebaseBookingResultArrayList.get(i).equals(time.get(j))){
                                timeslotArrayList.removeAt(j)
                                jNumberNeedToRemove = j
                                Log.i("code123 The row of time have removed","The row of time have removed${j.toString()}")
                            }
                        }
                    }
                    if(jNumberNeedToRemove != -1){
                        time.removeAt(jNumberNeedToRemove)
                        jNumberNeedToRemove = -1
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error getting documents: ", exception.toString())
            }
    }
}