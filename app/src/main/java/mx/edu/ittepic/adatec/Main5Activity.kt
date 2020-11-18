package mx.edu.ittepic.adatec
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.activity_main5.*
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList


class Main5Activity : AppCompatActivity() {
    val nombreBaseDatos = "CONTROL3"
    var listID = ArrayList<String>()
    var conjuntoS=0 //conjunto de todos los segundos inactivos DE TODAS LAS MAQUINAS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)
        setTitle("Estadísticas Grupales")
        consultarMaquinasActivas()
        consultarMaquinasInactivas()
        consultarTodasMaquinas()
        consultarTiempoTotIna()
        consultarTiempoTotAct()
        //graficaBarrasTM()
        graf()
        graficaPastelActivoInactivos()
      //  graficaMotivos()

       //consultarDescripcion()


    }

    fun consultarMaquinasActivas() :Int{
       var maquinasActivas =0
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT COUNT(*) FROM MAQUINAS2 WHERE STATUS = 'Activo' "


            var cursor =select.rawQuery(SQL,null)
            if(cursor.moveToFirst()){
                maquinasActivas=cursor.getInt(0)
              //  dialogo("Maquinas activas: "+maquinasActivas)
                activastxt.setText("Maquinas activas: "+maquinasActivas)

            }else{
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()

        }catch (error: SQLException){
            mensaje(error.message.toString())
        }
        return maquinasActivas
    }
    fun consultarMaquinasInactivas():Int{
        var maquinasInactivas =0
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT COUNT(*) FROM MAQUINAS2 WHERE STATUS = 'Inactivo' "


            var cursor =select.rawQuery(SQL,null)
            if(cursor.moveToFirst()){
                maquinasInactivas=cursor.getInt(0)
                //dialogo("Maquinas Inactivas: "+maquinasInactivas)
                inactivastxt.setText("Maquinas Inactivas: "+maquinasInactivas)

            }else{
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()

        }catch (error: SQLException){
            mensaje(error.message.toString())
        }

        return maquinasInactivas
    }
    fun consultarTodasMaquinas(){
        var totalM =0
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT COUNT(*) FROM MAQUINAS2 "


            var cursor =select.rawQuery(SQL,null)
            if(cursor.moveToFirst()){
                totalM=cursor.getInt(0)
              //  dialogo("Total de Maquinas: "+totalM)
                totalMtxt.setText("Total de Maquinas: "+totalM)

            }else{
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()

        }catch (error: SQLException){
            mensaje(error.message.toString())
        }
    }
    fun mensaje(mensaje :String){
        AlertDialog.Builder(this).setMessage(mensaje).show()
    }
    fun dialogo(s:String){
        AlertDialog.Builder(this).setMessage(s)
            .setTitle("ATENCION")
            .setPositiveButton("OK"){d,i->}
            .show()
    }

    fun consultarTiempoTotIna() :Int{
        try {
                var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
                var select = baseDatos.readableDatabase
                var SQL ="SELECT TIEMPOA FROM REPORTES  "
                //var parametros= arrayOf(idMaquina)

                var cursor =select.rawQuery(SQL,null)
                //if(cursor.moveToFirst()){
                cursor.moveToFirst()
                var cantidad =cursor.count -1
                (0..cantidad).forEach {
                    //significa que almenos hay una coicidencia con la consulta qur hiciste , si hay resultado

                    conjuntoS=conjuntoS+textoHora_Segundos(cursor.getString(0))
                    cursor.moveToNext()

                }
                select.close()
                baseDatos.close()
                conjuntoS= Math.abs(conjuntoS)
                //dialogo(""+conjuntoS)
                //dialogo("Tiempo total inactivo de todas las maquinas :"+seg_Hora(conjuntoS))
                tiempoInactivo.setText("Tiempo total inactivo de todas las maquinas :"+seg_Hora(conjuntoS))


            }catch (error: SQLException){
                mensaje(error.message.toString())
            }
        return conjuntoS
    }

    fun consultarTiempoTotAct() :Int{

        var resultado=0
        var fechaConsulta :Long //almacena las horas desde que se registro dicha maquina sin impotrtar los tiempos inactivos
        var tiempoRegistro =0
        var tiempoActivo =0
        var tiempoInactivo =conjuntoS.toLong()
        var porcentajeActivo :Float

        //var tr=""//variable que almacena la fecha de registro en string de la BD
        try {

            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT FECHARE FROM MAQUINAS2 "

            var cursor =select.rawQuery(SQL,null)
            cursor.moveToFirst()
            var cantidad =cursor.count -1
            (0..cantidad).forEach {

                tiempoRegistro=textoFecha_Segundos(cursor.getString(0)).toInt()
                fechaConsulta=textoFecha_Segundos(obtenerFecha())
                tiempoActivo=fechaConsulta.toInt()-tiempoRegistro.toInt()
                resultado=resultado+tiempoActivo
                cursor.moveToNext()

            }
            resultado=resultado-tiempoInactivo.toInt()
            select.close()
            baseDatos.close()


          //  dialogo( "tiempoRegistrado seg: "+ (textoFecha_Segundos(tr)))


       //     fechaConsulta=textoFecha_Segundos(obtenerFecha())
           // dialogo("Tiempo de consulta"+fechaConsulta)



      //      tiempoActivo=fechaConsulta.toInt()-tiempoRegistro.toInt()-tiempoInactivo.toInt()
            // dialogo("Tiempo Total Activo de todas las maquinas "+seg_Hora(resultado))


            //porcentajeActivo=porcentajeActivo(tiempoActivo.toInt(),tiempoActivo.toInt()+tiempoInactivo.toInt())

        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
        return resultado
    }
    fun textoHora_Segundos(Hora:String) :Int{
        var segundos=0
        var A= Hora.split(':')
        segundos= (A[0].toInt()*3600 + A[1].toInt()*60 + A[2].toInt()).toInt()
        return segundos;

    }
    fun seg_Hora(s :Int) :String{

        var h=(s/3600)
        var m=((s-h*3600)/60)
        var s= s-(h*3600+m*60)

        return ""+h+":"+m+":"+s

    }
    fun obtenerFecha() : String{
        val c: Calendar = Calendar.getInstance()

        val fecha: String = (
                ""+c.get(Calendar.YEAR)
                        +"/"+c.get(Calendar.MONTH)
                        +"/"+c.get(Calendar.DAY_OF_MONTH)+"-"
                        +c.get(Calendar.HOUR_OF_DAY)
                        + ":" + c.get(Calendar.MINUTE)
                        + ":" + c.get(Calendar.SECOND))
        //  dialogo(fecha)
        return fecha
    }
    fun textoFecha_Segundos(fecha:String):Long{
        //fecha.replace(" ","")
        var fechaCorregida=""
        fechaCorregida=fecha.replace("-",":")
        fechaCorregida=fechaCorregida.replace("/",":")


        var segundos :Long

        var A= fechaCorregida.split(':')
        segundos= (A[0].toLong()*31536000+ A[1].toLong()*2592000+A[2].toLong()*86400+ A[3].toLong()*3600 + A[4].toLong()*60 + A[5].toLong()).toLong()
        return segundos;
    }

    fun graficaPastelActivoInactivos(){
        val NoOfEmp = ArrayList<PieEntry>()

        NoOfEmp.add(PieEntry(consultarTiempoTotAct().toFloat(), "% Activo"))
        NoOfEmp.add(PieEntry(consultarTiempoTotIna().toFloat(), "% Inactivo"))

        //  NoOfEmp.add(PieEntry(1501f, "2014"))
        // NoOfEmp.add(PieEntry(1645f, "2015"))
        // NoOfEmp.add(PieEntry(1578f, "2016"))
        // NoOfEmp.add(PieEntry(1695f, "2017"))

        val dataSet = PieDataSet(NoOfEmp, "% de tiempo activo de todas las maquinas")

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        pieChart2.data = data
        pieChart2.highlightValues(null)
        pieChart2.setUsePercentValues(true)
        pieChart2.invalidate()
        pieChart2.animateXY(1000, 1000)
    }
    fun graficaMotivos(){
        val NoOfEmp = ArrayList<PieEntry>()

        NoOfEmp.add(PieEntry(945f, "2008"))
        NoOfEmp.add(PieEntry(1040f, "2009"))
        NoOfEmp.add(PieEntry(1133f, "2010"))
        NoOfEmp.add(PieEntry(1240f, "2011"))
        NoOfEmp.add(PieEntry(1369f, "2012"))
        NoOfEmp.add(PieEntry(1487f, "2013"))
        //  NoOfEmp.add(PieEntry(1501f, "2014"))
        // NoOfEmp.add(PieEntry(1645f, "2015"))
        // NoOfEmp.add(PieEntry(1578f, "2016"))
        // NoOfEmp.add(PieEntry(1695f, "2017"))

        val dataSet = PieDataSet(NoOfEmp, "Numero de empleados")

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        pieChart3.data = data
        pieChart3.highlightValues(null)
        pieChart3.invalidate()
        pieChart3.animateXY(1000, 1000)
    }
//metodo que grafica el total de maquinas Activas e inactivas
fun graficaBarrasTM(){
    // Initialize bar chart
    // Create bars
    // Create bars
    val xAxisLabels = listOf("inactivos", "Activos","maquinas")
    barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
    barChart.setDrawGridBackground(false)
    barChart.axisLeft.isEnabled = false
    barChart.axisRight.isEnabled = false
    barChart.description.isEnabled = false

    val points: ArrayList<BarEntry> = ArrayList()
    points.add(BarEntry(0f, consultarMaquinasActivas().toFloat()))  //Maquinas Activos
    points.add(BarEntry(1f, consultarMaquinasInactivas().toFloat()))  //Maquinas Inactivas
    // Add bars to a bar set
    // Add bars to a bar set

    // TO ADD THE VALUES IN X-AXIS


    val barSet = BarDataSet(points, "Maquinas Activas e Inactivas")

    // Create a BarData object and assign it to the chart
    // Create a BarData object and assign it to the chart
   // val barData = BarData(barSet,year)
    val barData = BarData(barSet)
    // Display it as a percentage
    // Display it as a percentage
    barSet.setColors(*ColorTemplate.COLORFUL_COLORS)
  //  barData.setValueFormatter(PercentFormatter())
    barChart.data = barData
   //barSet.setDrawValues(true)

    barChart.invalidate()
    barChart.animateXY(2000, 2000)


}

    fun graf(){
        val labels = arrayListOf(
            "Activos", "Inactivos"
        )

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.setDrawGridBackground(false)
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false


        val entries = arrayListOf(
            BarEntry(0f, consultarMaquinasActivas().toFloat()),
            BarEntry(1f, consultarMaquinasInactivas().toFloat())

        )
        val set = BarDataSet(entries, "Maquinas")
        set.valueTextSize = 20f

        barChart.data = BarData(set)
        barChart.invalidate()
        barChart.animateXY(2000, 2000)
    }


fun consultarDescripcion(){
    var moto =""
    try {
        var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
        var select = baseDatos.readableDatabase
        var SQL ="SELECT COUNT(*) FROM MAQUINAS2 INNER JOIN  REPORTES ON MAQUINAS2.IDMAQUINA=REPORTES.IDMAQUINA WHERE IDMAQUINA='1' "


        var cursor =select.rawQuery(SQL,null)
        cursor.moveToFirst()
        var cantidad =cursor.count -1
        (0..cantidad).forEach {

        moto=moto+cursor.getString(0)+"\n"

            cursor.moveToNext()

        }
        dialogo(moto)
        select.close()
        baseDatos.close()
    }catch (error: SQLException){
        mensaje(error.message.toString())
    }

}

    override fun onBackPressed() {
        var intento = Intent(this,MainActivity::class.java)
        startActivityForResult(intento,0)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.estadisitcas-> {
                //abrimos el activitiy de estadisticas grupales
                var intento = Intent(this,Main5Activity::class.java)
                startActivityForResult(intento,0)
            }

            R.id.reportes ->{
                var intento = Intent(this,Main6Activity::class.java)
                startActivityForResult(intento,0)
            }
            R.id.salir ->{
                finishAffinity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
