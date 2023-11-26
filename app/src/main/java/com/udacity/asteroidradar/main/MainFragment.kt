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
import androidx.recyclerview.widget.LinearLayoutManager
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
        val listData = listOf(
            Asteroid(
                id = 2426071,
                codename = "426071 (2012 CD29)",
                closeApproachDate = "2023-11-25",
                absoluteMagnitude = 19.94,
                estimatedDiameter = 0.6109982675,
                relativeVelocity = 21.0317768281,
                distanceFromEarth = 0.3242307374,
                isPotentiallyHazardous = false
            ),
            Asteroid(
                id = 3618494,
                codename = "(2012 WS10)",
                closeApproachDate = "2023-11-25",
                absoluteMagnitude = 24.9,
                estimatedDiameter = 0.0622357573,
                relativeVelocity = 17.2683641843,
                distanceFromEarth = 0.1736117899,
                isPotentiallyHazardous = false
            ),
            Asteroid(
                id = 3648769,
                codename = "(2013 TK4)",
                closeApproachDate = "2023-11-25",
                absoluteMagnitude = 23.86,
                estimatedDiameter = 0.1004708274,
                relativeVelocity = 6.6674845126,
                distanceFromEarth = 0.1875343468,
                isPotentiallyHazardous = false
            ),
            // ... (other Asteroid objects)
            Asteroid(
                id = 54403824,
                codename = "(2023 VH5)",
                closeApproachDate = "2023-11-25",
                absoluteMagnitude = 25.165,
                estimatedDiameter = 0.0550858403,
                relativeVelocity = 9.037805623,
                distanceFromEarth = 0.0458982973,
                isPotentiallyHazardous = false
            )
        )
        val newsAdapter = MainAdapter(listData) {
            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        }
        recyclerViewNews.adapter = newsAdapter

        viewModel.asteroidsdata.observe(this.viewLifecycleOwner) { asteroids ->
            Log.d("TAG", "Asteroids size:asteroids ")
            newsAdapter.setData(asteroids)

        }


        recyclerViewNews.layoutManager = LinearLayoutManager(this.context)
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
    fun setData(newList: List<Asteroid>) {
        mList = newList
        notifyDataSetChanged()
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