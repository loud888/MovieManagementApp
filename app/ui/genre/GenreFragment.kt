package com.example.moviemanagement.ui.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviemanagement.R
import com.example.moviemanagement.data.database.AppDatabase
import com.example.moviemanagement.data.entity.Genre

class GenreFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GenreAdapter
    private lateinit var searchView: SearchView
    private lateinit var dao: AppDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_genre, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = GenreAdapter()
        recyclerView.adapter = adapter

        dao = AppDatabase.getInstance(requireContext()).appDao()
        var liveData = dao.getAllGenres()
        liveData.observe(viewLifecycleOwner, Observer { genres ->
            adapter.submitList(genres)
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                liveData.removeObservers(viewLifecycleOwner)
                liveData = if (newText.isNullOrEmpty()) {
                    dao.getAllGenres()
                } else {
                    dao.searchGenres(newText)
                }
                liveData.observe(viewLifecycleOwner, Observer { genres ->
                    adapter.submitList(genres)
                })
                return true
            }
        })

        return view
    }
}

class GenreAdapter : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    private var genres = listOf<Genre>()

    fun submitList(newGenres: List<Genre>) {
        genres = newGenres
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genres[position]
        holder.text1.text = genre.name
        holder.text2.text = "Danh mục: ${genre.categories.replace(",", ", ")}"
    }

    override fun getItemCount(): Int = genres.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text1: android.widget.TextView = view.findViewById(android.R.id.text1)
        val text2: android.widget.TextView = view.findViewById(android.R.id.text2)
    }
}
