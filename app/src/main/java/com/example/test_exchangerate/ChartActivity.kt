package com.example.test_exchangerate

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.test_exchangerate.ui.interfaces.CurrencyApiInterface
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
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
import com.github.mikephil.charting.animation.Easing
import androidx.core.content.ContextCompat

import android.graphics.drawable.Drawable
import android.widget.TextView
import android.widget.Toast
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
            val response = request.getCurrencyTimeSeries(
                LocalDateTime.now().minusDays(6).format(ISO_LOCAL_DATE),
                LocalDateTime.now().format(ISO_LOCAL_DATE),
                BASE,
                currencySymbols.toString()
            )
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    var i: Int =0
                    val yAXES: ArrayList<Entry> = ArrayList()
                    val axisData  = response.body()!!.rates.keys.toTypedArray()
                    for ((key,value) in response.body()!!.rates) {
                        val map = value.getValue(currencySymbols.toString())
                        yAXES.add(Entry(i.toFloat(), map.toFloat()))
                        Log.d("ChartActivity", "onCreate: $i  $map")
                        i++
                    }
                    val lineDataSet = LineDataSet(yAXES, "Rate")
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
                    if (Utils.getSDKInt() >= 18) {
                        val drawable = ContextCompat.getDrawable(lineChart.context, R.drawable.fill_gradient_green)
                        lineDataSet.fillDrawable = drawable
                    } else {
                        lineDataSet.fillColor = Color.GREEN
                    }
                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(lineDataSet)
                    val data = LineData(dataSets)
                    lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(axisData)
                    val xAxis: XAxis = lineChart.xAxis
                    xAxis.labelRotationAngle = -45f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM;
                    lineChart.data = data
                    lineChart.animateX(1000)

                }
            } else Toast.makeText(this@ChartActivity, "No exchange rate data is available for the selected currency.", Toast.LENGTH_LONG).show()
        }


    }
}