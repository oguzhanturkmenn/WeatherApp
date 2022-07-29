package com.oguzhanturkmen.kotlinweatherapp

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.oguzhanturkmen.kotlinweatherapp.model.WeatherModel
import com.oguzhanturkmen.kotlinweatherapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var GET : SharedPreferences
    private lateinit var SET  : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        GET = this.requireActivity().getSharedPreferences("com.oguzhanturkmen.kotlinweatherapp",AppCompatActivity.MODE_PRIVATE)
        //GET = getSharedPreferences(packageName, AppCompatActivity.MODE_PRIVATE)
        SET = GET.edit()


        val cName = GET.getString("cityNAme","antalya")
        edt_city_name.setText(cName)

        viewModel.refreshData(cName!!)

        swipe_refresh_layout.setOnRefreshListener {
            ll_data.visibility = View.GONE
            tv_error.visibility = View.GONE
            pb_loading.visibility = View.GONE

            var cityName = GET.getString("cityName",cName)
            edt_city_name.setText(cityName)
            viewModel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }
        img_search_city.setOnClickListener {
            val cityName = edt_city_name.text.toString()
            SET.putString("cityName",cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
        }



    }


    @SuppressLint("SetTextI18n")
    private fun getLiveData() {
        viewModel.weather_data.observe(viewLifecycleOwner, Observer {
            it?.let {
                ll_data.visibility = View.VISIBLE
                pb_loading.visibility = View.generateViewId()

                tv_degree.text = it.main.temp.toString() + "°C"
                tv_city_code.text = it.sys.country.toString()
                tv_city_name.text = it.name.toString()
                tv_humidity.text = ":" + it.main.humidity.toString()
                tv_wind_speed.text = ":" + it.wind.speed.toString() + "%"
                tv_lat.text = ":" + it.coord.lat.toString()
                tv_lon.text = ":" + it.main.pressure

                Glide.with(this)
                    .load("http://openweathermap.org/img/wn")
                    .into(img_weather_pictures)
            }
        })

        viewModel.weather_lodaing.observe(viewLifecycleOwner, Observer {
            if (it){
                pb_loading.visibility = View.VISIBLE
                tv_error.visibility = View.GONE
                ll_data.visibility = View.GONE
            }else{
                pb_loading.visibility = View.GONE
            }
        })

        viewModel.weather_error.observe(viewLifecycleOwner, Observer {
            if (it){
                tv_error.visibility = View.VISIBLE
                pb_loading.visibility = View.GONE
                ll_data.visibility = View.GONE

            }else{
                tv_error.visibility = View.GONE
            }
        })
    }

}