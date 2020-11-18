package mx.edu.ittepic.adatec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.activity_main3.*
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Main3Activity : AppCompatActivity() {
    val nombreBaseDatos="CONTROL3"
    var listID =ArrayList<String>()
    var horaI=""
    var horaF=""
    var hI=0
    var hF=0
    var status=""
    var conjuntoS=0 //conjunto de todos los segundos inactivos
    var idMaquina=""
    var HoraCronometro="00:00:00"

    var graficaPorA=0f
    var graficaPorI=0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        setTitle("Reporte individual")

        //crono.base = SystemClock.elapsedRealtime()
       // crono.start()



        var extra = intent.extras
       // dialogo(extra!!.getString("nombre").toString())
        nombretxt.setText("Nombre : "+extra!!.getString("nombre"))
        noPartetxt.setText("No. Parte : "+extra!!.getString("noParte"))
        idMoldetxt.setText("Id Molde : "+extra!!.getInt("idMolde").toString())
        lotetxt.setText("Lote : "+extra!!.getString("lote"))
        clientetxt.setText("Cliente : "+extra!!.getString("cliente"))
        statustxt.setText("Status :"+extra!!.getString("status"))
        horaI=extra!!.getString("horaI").toString()
        horaF=extra!!.getString("horaF").toString()
        idMaquina=extra!!.getString("idMaquina").toString()
        obtenerTiemposI()
        obtenerTiempoA()
        cronometro()

        //dialogo(consultarStatus())


    }
    fun cronometro(){
        val format = SimpleDateFormat("HH:mm:ss")
        val startDate: Date = format.parse(HoraCronometro)

        val calendar = Calendar.getInstance()
        calendar.time = startDate

        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]

        crono.setBase(SystemClock.elapsedRealtime() - (hour * 60000 * 60 + minute * 60000 + second * 1000))

       if(!HoraCronometro.equals("00:00:00")){
           crono.start()
       }

    }
    fun tiempoInactivo(){
        var tiempoIn=""
        var A= horaI.split(':')
        var B= horaF.split(':')
      //  dialogo(A[0]+"\n"+A[1]+"\n"+A[2])
        hI= (A[0].toInt()*3600 + A[1].toInt()*60 + A[2].toInt()).toInt()
        hF= (B[0].toInt()*3600 + B[1].toInt()*60 + B[2].toInt()).toInt()
        var totalS=hF-hI

        //dialogo(""+hI+" segundos de inicio \n"+hF+" segundos al finalizar")
       // dialogo("Total Segundos:"+totalS)

        var h=(totalS/3600)
        var m=((totalS-h*3600)/60)
        var s= totalS-(h*3600+m*60)
        tiempoIn= ""+h+":"+m+":"+s
       // dialogo("Tiempo inactivo" +h+":"+m+":"+s)
        inactivotxt.setText("Tiempo Inactivo :"+tiempoIn)

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
    fun obtenerTiemposI(){

        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT TIEMPOA FROM REPORTES WHERE IDMAQUINA = '${idMaquina}'  "
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
           // dialogo(""+conjuntoS)
          //  dialogo(seg_Hora(conjuntoS))
            inactivotxt.setText("Tiempo Inactivo Acumulado :"+seg_Hora(Math.abs(conjuntoS)))

        }catch (error: SQLException){
            mensaje(error.message.toString())
        }
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
    fun obtenerTiempoA() {
        var fechaConsulta :Long //almacena las horas desde que se registro dicha maquina sin impotrtar los tiempos inactivos
        var tiempoRegistro :Long
        var tiempoActivo :Long
        var tiempoInactivo =conjuntoS.toLong()
        var porcentajeActivo :Float

        if(consultarStatus().equals("Inactivo")){
            dialogo("Maquina averiada, favor de dar mantenimiento antes de ver estadisticas")

            return
        }



        var tr=""//variable que almacena la fecha de registro en string de la BD
        try {

            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT FECHARE FROM MAQUINAS2 WHERE IDMAQUINA =? "
            var parametros= arrayOf(idMaquina)

            var cursor =select.rawQuery(SQL,parametros)
            if(cursor.moveToFirst()){
               tr=cursor.getString(0)


            }else{
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()


            //dialogo( "tiempoRegistrado seg: "+ (textoFecha_Segundos(tr)))
            tiempoRegistro=textoFecha_Segundos(tr)

            fechaConsulta=textoFecha_Segundos(obtenerFecha())
          //  dialogo("Tiempo de consulta"+fechaConsulta)


           // dialogo("Tiempo de inactivo"+tiempoInactivo)




            tiempoActivo=fechaConsulta-tiempoRegistro-tiempoInactivo
            //dialogo("Tiempo Activo "+tiempoActivo)




            txtTiempoA.setText("Tiempo Activo: "+seg_Hora(tiempoActivo.toInt()))

            porcentajeActivo=porcentajeActivo(tiempoActivo.toInt(),tiempoActivo.toInt()+tiempoInactivo.toInt())
            porcentajetxt.setText("Porcentaje Activo: "+porcentajeActivo+"%")
           // graficaPorA=porcentajeActivo
            graficaPorA=tiempoActivo.toFloat()
            graficaPorI=tiempoInactivo.toFloat()
            GraficaP()
            HoraCronometro= seg_Hora(tiempoActivo.toInt())
        }catch (error:SQLException){
            mensaje(error.message.toString())
        }

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
    fun porcentajeActivo(ta:Int, tr:Int) :Float{
        return  (ta.toFloat()*100/tr.toFloat())


    }
    fun consultarStatus() :String{
        try {

            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT STATUS FROM MAQUINAS2 WHERE IDMAQUINA =? "
            var parametros= arrayOf(idMaquina)

            var cursor =select.rawQuery(SQL,parametros)
            if(cursor.moveToFirst()){

            status=cursor.getString(0)

            }else{
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()
        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
        return status
    }

    fun GraficaP(){
        val NoOfEmp = ArrayList<PieEntry>()

     //   NoOfEmp.add(PieEntry(graficaPorA, "% Activo"))
      //  NoOfEmp.add(PieEntry(100-graficaPorA, "% Inactivo"))
        NoOfEmp.add(PieEntry(graficaPorA, "% Activo"))
        NoOfEmp.add(PieEntry(graficaPorI, "% Inactivo"))
        //  NoOfEmp.add(PieEntry(1501f, "2014"))
        // NoOfEmp.add(PieEntry(1645f, "2015"))
        // NoOfEmp.add(PieEntry(1578f, "2016"))
        // NoOfEmp.add(PieEntry(1695f, "2017"))

        val dataSet = PieDataSet(NoOfEmp, "Tiempo Activo e inactivo")

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.highlightValues(null)
        pieChart.setUsePercentValues(true)
       // pieChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.invalidate()
        pieChart.animateXY(1000, 1000)
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
