package com.example.what_should_i_order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.what_should_i_order.Calendar.Calendar
import com.example.what_should_i_order.Setting.Setting
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var main = findViewById<BottomNavigationView>(R.id.bottonavi)



        main.run {
            setOnNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.first -> {
                        val MainFragment1 = Main_Screen()
                        var haha = BuildConfig.GOOGLE_API_KEY
                        supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment1).commit()
                    }
                    R.id.second -> {
                        val MainFragment2 = Calendar()
                        supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment2).commit()
                    }
                    R.id.third -> {
                        val MainFragment3 = Setting()
                        supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment3).commit()
                    }
                }
                true
            }
            selectedItemId = R.id.first
        }

    }
}