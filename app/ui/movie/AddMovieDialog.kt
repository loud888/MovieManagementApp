package com.example.moviemanagement.ui.movie

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.moviemanagement.R
import com.example.moviemanagement.data.MovieWithInfo
import com.example.moviemanagement.data.database.AppDatabase
import com.example.moviemanagement.data.entity.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddMovieDialog(private val editingMovie: MovieWithInfo? = null) : DialogFragment() {
    private var selectedDate: Long = System.currentTimeMillis()
    private lateinit var spGenre: Spinner
    private lateinit var spCinema: Spinner
    private lateinit var etTitle: EditText
    private lateinit var etPrice: EditText
    private lateinit var btnDate: Button
    private lateinit var btnSave: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_movie, null)
        initViews(view)

        val dao = AppDatabase.getInstance(requireContext()).appDao()

        // Load genres
        dao.getAllGenres().observe(this, Observer { genres ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spGenre.adapter = adapter
            editingMovie?.let { spGenre.setSelection(genres.indexOfFirst { it.id == it.movie.genreId }) }
        })

        // Load cinemas
        dao.getAllCinemas().observe(this, Observer { cinemas ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cinemas)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spCinema.adapter = adapter
            editingMovie?.let { spCinema.setSelection(cinemas.indexOfFirst { it.id == it.movie.cinemaId }) }
        })

        if (editingMovie != null) {
            etTitle.setText(editingMovie.movie.title)
            etPrice.setText(editingMovie.movie.ticketPrice.toString())
            selectedDate = editingMovie.movie.releaseDate
        }

        btnDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
        btnDate.setOnClickListener { showDatePicker() }
        btnSave.setOnClickListener { saveMovie(dao) }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(if (editingMovie == null) "Thêm phim mới" else "Sửa phim")
            .setNegativeButton("Hủy") { _, _ -> dismiss() }
            .create()
    }

    private fun initViews(view: View) {
        spGenre = view.findViewById(R.id.spGenre)
        spCinema = view.findViewById(R.id.spCinema)
        etTitle = view.findViewById(R.id.etTitle)
        etPrice = view.findViewById(R.id.etPrice)
        btnDate = view.findViewById(R.id.btnDate)
        btnSave = view.findViewById(R.id.btnSave)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                btnDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveMovie(dao: AppDao) {
        val title = etTitle.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        if (title.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        if (price <= 0) {
            Toast.makeText(requireContext(), "Giá vé phải > 0", Toast.LENGTH_SHORT).show()
            return
        }

        val genre = spGenre.selectedItem as? com.example.moviemanagement.data.entity.Genre
        val cinema = spCinema.selectedItem as? com.example.moviemanagement.data.entity.Cinema
        if (genre == null || cinema == null) {
            Toast.makeText(requireContext(), "Chọn thể loại và rạp", Toast.LENGTH_SHORT).show()
            return
        }

        val movie = Movie(
            id = editingMovie?.movie?.id ?: 0,
            genreId = genre.id,
            cinemaId = cinema.id,
            title = title,
            releaseDate = selectedDate,
            ticketPrice = price
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (editingMovie == null) {
                dao.insertMovie(movie)
            } else {
                dao.updateMovie(movie)
            }
            withContext(Dispatchers.Main) {
                dismiss()
            }
        }
    }
}
