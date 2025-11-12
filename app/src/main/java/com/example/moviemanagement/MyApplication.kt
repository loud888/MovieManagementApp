package com.example.moviemanagement

import android.app.Application
import com.example.moviemanagement.data.database.AppDatabase
import com.example.moviemanagement.data.entity.Cinema
import com.example.moviemanagement.data.entity.Genre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        insertSampleData()
    }

    private fun insertSampleData() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(this@MyApplication)
            val dao = db.appDao()

            dao.insertGenre(Genre(name = "Khoa học viễn tưởng", categories = "Hành động"))
            dao.insertGenre(Genre(name = "Lãng mạn", categories = "Tình cảm"))
            dao.insertGenre(Genre(name = "Hài", categories = "Hài hước"))
            dao.insertGenre(Genre(name = "Siêu anh hùng", categories = "Hành động,Hài hước"))

            dao.insertCinema(Cinema(name = "CGV Vincom", email = "cgv@vincom.com", address = "Hà Nội"))
            dao.insertCinema(Cinema(name = "Lotte Cinema", email = "lotte@hcm.com", address = "TP.HCM"))
            dao.insertCinema(Cinema(name = "Galaxy Nguyễn Du", email = "galaxy@dn.com", address = "Đà Nẵng"))
        }
    }
}
