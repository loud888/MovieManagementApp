package com.example.moviemanagement.ui.movie

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviemanagement.R
import com.example.moviemanagement.data.MovieWithInfo
import com.example.moviemanagement.data.database.AppDatabase
import com.example.moviemanagement.data.entity.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MovieFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var tvStats: TextView
    private lateinit var etFromYear: EditText
    private lateinit var etToYear: EditText
    private lateinit var btnFilter: Button
    private lateinit var dao: AppDao
    private var currentLiveData = AppDatabase.getInstance(requireContext()).appDao().getAllMoviesWithInfo()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        tvStats = view.findViewById(R.id.tvStats)
        etFromYear = view.findViewById(R.id.etFromYear)
        etToYear = view.findViewById(R.id.etToYear)
        btnFilter = view.findViewById(R.id.btnFilter)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MovieAdapter(
            onEdit = { movie -> AddMovieDialog(movie).show(parentFragmentManager, "edit_movie") },
            onDelete = { movie ->
                AlertDialog.Builder(requireContext())
                    .setMessage("Xóa phim '${movie.movie.title}'?")
                    .setPositiveButton("Có") { _, _ ->
                        lifecycleScope.launch {
                            dao.deleteMovie(movie.movie)
                        }
                    }
                    .setNegativeButton("Không", null)
                    .show()
            }
        )
        recyclerView.adapter = adapter

        dao = AppDatabase.getInstance(requireContext()).appDao()
        currentLiveData.observe(viewLifecycleOwner, Observer { movies ->
            adapter.submitList(movies)
            updateStats(movies)
        })

        btnFilter.setOnClickListener {
            val fromYear = etFromYear.text.toString().toIntOrNull() ?: 1900
            val toYear = etToYear.text.toString().toIntOrNull() ?: 2100
            val fromDate = getYearStartTimestamp(fromYear)
            val toDate = getYearEndTimestamp(toYear)

            currentLiveData.removeObservers(viewLifecycleOwner)
            currentLiveData = dao.getMoviesByDateRangeWithInfo(fromDate, toDate)
            currentLiveData.observe(viewLifecycleOwner, Observer { movies ->
                adapter.submitList(movies)
                updateStats(movies)
            })
        }

        return view
    }

    private fun getYearStartTimestamp(year: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(year, 0, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getYearEndTimestamp(year: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(year, 11, 31, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    private fun updateStats(movies: List<MovieWithInfo>) {
        val stats = mutableMapOf<String, Int>()
        movies.forEach { movie ->
            movie.genre.categories.split(",").forEach { category ->
                val trimmed = category.trim()
                stats[trimmed] = stats.getOrDefault(trimmed, 0) + 1
            }
        }
        tvStats.text = "Thống kê: ${stats.map { "${it.key}: ${it.value}" }.joinToString(" | ")}"
    }
}

class MovieAdapter(
    private val onEdit: (MovieWithInfo) -> Unit,
    private val onDelete: (MovieWithInfo) -> Unit
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    private var movies = listOf<MovieWithInfo>()

    fun submitList(newMovies: List<MovieWithInfo>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.tvTitle.text = movie.movie.title
        holder.tvInfo.text = "Rạp: ${movie.cinema.name} | Ngày: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(movie.movie.releaseDate))} | Giá: ${String.format("%.0f", movie.movie.ticketPrice)}đ"
        holder.btnEdit.setOnClickListener { onEdit(movie) }
        holder.btnDelete.setOnClickListener { onDelete(movie) }
    }

    override fun getItemCount(): Int = movies.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvInfo: TextView = view.findViewById(R.id.tvInfo)
        val btnEdit: android.widget.ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: android.widget.ImageButton = view.findViewById(R.id.btnDelete)
    }
}
