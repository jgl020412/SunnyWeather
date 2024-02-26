package com.sunnyweather.android.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.FragmentPlaceBinding
import com.sunnyweather.android.ui.weather.WeatherActivity

class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }

    private lateinit var adapter: PlaceAdapter
    private lateinit var binging: FragmentPlaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binging = FragmentPlaceBinding.inflate(layoutInflater, container, false)
        return binging.root
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityCreated(savedInstanceState)",
        "androidx.fragment.app.Fragment"
    )
    )
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            Log.d(TAG, "onActivityCreated: MainActivity")
            val place = viewModel.getSavePlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        val recyclerView = binging.recyclerView
        recyclerView.layoutManager = layoutManager
        val searchPlaceEdit = binging.searchPlaceEdit
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                binging.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            Log.d(Companion.TAG, "onActivityCreated: $result")
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                binging.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    companion object {
        private const val TAG = "PlaceFragment"
    }
}