package com.example.test_exchangerate

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.test_exchangerate.retrofit.CurrencyApiInterface
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.components.XAxis
import android.graphics.DashPathEffect
import androidx.core.content.ContextCompat

import android.widget.TextView
import android.widget.Toast
import com.example.test_exchangerate.retrofit.ApiClient
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.Utils


private const val BASE = "USD"

class ChartActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        val currencySymbols = intent?.extras?.getString("currency")
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val textCurrency = findViewById<TextView>(R.id.chartCurrency)
        textCurrency.text = currencySymbols
        CoroutineScope(Dispatchers.IO).launch {
            val request = ApiClient.buildServiceChart(CurrencyApiInterface::class.java)
            //get the historical exchange rate for the last 7 days
            val response = request.getCurrencyTimeSeries(
                LocalDateTime.now().minusDays(6).format(ISO_LOCAL_DATE),
                LocalDateTime.now().format(ISO_LOCAL_DATE),
                BASE,
                currencySymbols.toString()
            )
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    var i: Int = 0
                    //add values for y-axis
                    val yAXIS: ArrayList<Entry> = ArrayList()
                    for ((key, value) in response.body()!!.rates) {
                        val map = value.getValue(currencySymbols.toString())
                        yAXIS.add(Entry(i.toFloat(), map.toFloat()))
                        Log.d("ChartActivity", "onCreate: $i  $map")
                        i++
                    }
                    //Set the data to plot in a line
                    val lineDataSet = LineDataSet(yAXIS, "Rate")
                    lineDataSet.setDrawIcons(false)
                    lineDataSet.enableDashedLine(10f, 5f, 0f)
                    lineDataSet.enableDashedHighlightLine(10f, 5f, 0f)
                    lineDataSet.color = Color.DKGRAY
                    lineDataSet.setCircleColor(Color.DKGRAY)
                    lineDataSet.lineWidth = 1f
                    lineDataSet.circleRadius = 3f
                    lineDataSet.setDrawCircleHole(false)
                    lineDataSet.valueTextSize = 9f
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.formLineWidth = 1f
                    lineDataSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    lineDataSet.formSize = 15f
                    //set gradient below the line if the API level 18 and above
                    if (Utils.getSDKInt() >= 18) {
                        val drawable = ContextCompat.getDrawable(
                            lineChart.context,
                            R.drawable.fill_gradient_green
                        )
                        lineDataSet.fillDrawable = drawable
                    } else {
                        // if API level is below 18
                        lineDataSet.fillColor = Color.GREEN
                    }
                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(lineDataSet)
                    val data = LineData(dataSets)
                    //set labels for x-axis
                    val axisData = response.body()!!.rates.keys.toTypedArray()
                    lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(axisData)
                    val xAxis: XAxis = lineChart.xAxis
                    xAxis.labelRotationAngle = -45f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM;
                    lineChart.data = data
                    lineChart.animateX(1000)

                }
            } else Toast.makeText(
                this@ChartActivity,
                "No exchange rate data is available for the selected currency.",
                Toast.LENGTH_LONG
            ).show()
        }


    }
}