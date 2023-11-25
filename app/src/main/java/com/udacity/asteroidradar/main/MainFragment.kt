package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        val recyclerViewNews = binding.asteroidRecycler
        viewModel.asteroids.observe(viewLifecycleOwner, Observer { it ->
            val newsAdapter = MainAdapter(it){
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))

                (it.codename)
            }
            recyclerViewNews.adapter = newsAdapter
        })
        Log.e("TAG111",  viewModel.picOfDay.toString());

        // recyclerViewNews.layoutManager = LinearLayoutManager(this)
        recyclerViewNews.setHasFixedSize(true)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.setFilter(
            when (item.itemId) {
                R.id.show_all_menu -> AsteroidApiFilter.SHOW_WEEK
                R.id.show_rent_menu -> AsteroidApiFilter.SHOW_TODAY
                else -> AsteroidApiFilter.SHOW_SAVED
            }
        )
        return true
    }
}

class MainAdapter(private var mList: List<Asteroid>,    private val listener: (Asteroid) -> Unit
) : RecyclerView.Adapter<LargeNewsViewHolder>(){
    private lateinit var binding: AsteroidItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LargeNewsViewHolder {
        binding = AsteroidItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LargeNewsViewHolder(binding)
    }

    override fun getItemCount():Int{
        return mList.size
    }

    override fun onBindViewHolder(holder: LargeNewsViewHolder, position: Int) {
        val largeNews = mList[position]
        holder.bind(largeNews)
        holder.itemView.setOnClickListener { listener(largeNews) }
    }

}


class LargeNewsViewHolder(
    private val binding: AsteroidItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(asteroid: Asteroid) {
        binding.asteroid = asteroid
    }
}