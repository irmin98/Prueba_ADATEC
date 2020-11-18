package mx.edu.ittepic.adatec

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.lista
import kotlinx.android.synthetic.main.activity_main4.*
import kotlinx.android.synthetic.main.activity_main6.*
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList

class Main4Activity : AppCompatActivity() {
    val nombreBaseDatos = "CONTROL3"
    var listID = ArrayList<String>()
    var id=0
    var idMaquina=""
    var nombre =""
    var noParte=""
    var idMolde=0
    var lote=""
    var cliente=""
    var horaI="00:00:00"
    var horaF="00:00:00"
    var motivo=""
    var hI=0
    var hF=0
    var status=""
    var fechar="" // variable que almacena decha de registro de maquina directa de la bd
    var pa="" //variable que almacena porcentaje activo de maquina directa de la bd
    var ta="" //variable que almacena tiempo activo de maquina directa de la bd
    var ti="" //variable que almacena tiempo inactivo de maquina directa de la bd
    var spin="" //almacena el valor del spinner

    var listaReportesC= emptyList<ReporteCompleto>()
    var reporteC=ReporteCompleto("Mantenimiento", "0:00:00", "0:00:01","")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        setTitle("Generar Reporte")

            var fallas = arrayOf("Factores ambientales","Mantenimiento","Falla desconocida")

        if (spinner != null) {
            val adapter = ArrayAdapter(
                this, R.layout.spinner_item, fallas )

            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    spin=fallas[position]

                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

        }






            var extra = intent.extras
       // dialogo(extra!!.getString("id").toString())
        id=extra!!.getString("id").toString().toInt()
        cargarLista()

        iniciar.setOnClickListener {
            insertarR()
            cargarLista()

        }
        verMas.setOnClickListener{
            var intento = Intent(this,Main6Activity::class.java)
            startActivityForResult(intento,0)

        }

    }

    fun dialogo(s: String) {
            AlertDialog.Builder(this).setMessage(s)
                .setTitle("ATENCION")
                .setPositiveButton("OK") { d, i -> }
                .show()
        }
    fun mensaje(mensaje :String){
        AlertDialog.Builder(this).setMessage(mensaje).show()
    }
    fun cargarLista(){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
         //   var SQL ="SELECT * FROM REPORTES WHERE IDMAQUINA = '${this.id}' "
            var SQL ="SELECT * FROM REPORTES WHERE IDMAQUINA = ${id} "


            var cursor =select.rawQuery(SQL,null)
            if(cursor.count>0){
                var arreglo= ArrayList<String>()
                this.listID =ArrayList<String>()
                cursor.moveToFirst()
                var cantidad =cursor.count -1

                (0..cantidad).forEach {
                    /*var data ="MOTIVO: ${cursor.getString(2)}  \nHora inicio: ${cursor.getString(3)} \n" +
                            "Hora Finalizada: ${cursor.getString(4)} \n" +
                            "Tempo inactivo: ${cursor.getString(5)}"

                    arreglo.add(data)
                    */
                    reporteC = ReporteCompleto(cursor.getString(2), cursor.getString(3), cursor.getString(4),"Tiempo Inactivo Acumulado: "+cursor.getString(5))
                    listaReportesC=listaReportesC+listOf(reporteC)

                    listID.add(cursor.getString(0))
                    cursor.moveToNext()
                }
              /*  lista.adapter =ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arreglo)
                lista.setOnItemClickListener { parent, view, position, id ->
                    AlertDialog.Builder(this).setTitle("ATENCION")
                        .setMessage("¿QUE DESEA HACER CON ESTE REPORTE?")
                        .setPositiveButton("DETENER "){d,i->
                            actualizarHoraF(listID[position])
                            iniciar!!.setEnabled(true)



                        }
                        .setNegativeButton("CANCELAR"){d,i->

                        }

                        .show()
                }*/

            }

            val adapter = ReporteAdapterCompleto(this, listaReportesC)
            lista.adapter = adapter
            listaReportesC= emptyList()




            select.close()
            baseDatos.close()

        }catch (error: SQLException){
            mensaje(error.message.toString())
        }

        lista.setOnItemClickListener { parent, view, position, id ->
            AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("¿QUE DESEA HACER CON ESTE REPORTE?")
                .setPositiveButton("DETENER "){d,i->
                    actualizarHoraF(listID[position])
                    iniciar!!.setEnabled(true)



                }
                .setNegativeButton("CANCELAR"){d,i->

                }

                .show()
        }
    }
    fun insertarR(){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)

            var insertar =baseDatos.writableDatabase
            var SQL ="INSERT INTO REPORTES VALUES(NULL,'${id}','${spin}','${obtenerHora()}','00:00:00','00:00:00')"

            insertar.execSQL(SQL)
            insertar.close()
            baseDatos.close()

            mensaje("se inserto reporte correctamente")
            iniciar!!.setEnabled(false)

            statusFalse()



        }catch (error: SQLException){
            mensaje(error.message.toString())
        }
    }
    fun obtenerHora() : String{
        val c: Calendar = Calendar.getInstance()

        val fecha: String = (
                ""+c.get(Calendar.HOUR_OF_DAY)
                        + ":" + c.get(Calendar.MINUTE)
                        + ":" + c.get(Calendar.SECOND))
        //  dialogo(fecha)
        return fecha
    }
    fun actualizarHoraF(id:String){
        consultarID(id)
        if(!horaF.equals("00:00:00")){
            dialogo("Este reporte ya se solucionó")
            return
        }

        //dialogo("Tiempo ini:"+horaI+"\n"+"TiempoF"+obtenerHora())
        tiempoInactivo()
        try {
            var baseDatos1 =BaseDatos(this,nombreBaseDatos,null,1)

            var actualizar =baseDatos1.writableDatabase
            var SQL ="UPDATE REPORTES  SET DESCRIPCION='${motivo.toString()}',HI='${horaI}',HF='${obtenerHora()}',TIEMPOA='${tiempoInactivo()}' WHERE IDREPORTE= ? "
            var parametros= arrayOf(id)
            actualizar.execSQL(SQL,parametros)

            // mensaje("se Actualizo correctamente")

            actualizar.close()
            baseDatos1.close()


            cargarLista()
            statusTrue()
        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
    }
    fun consultarID(id:String){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT * FROM REPORTES WHERE IDREPORTE =? "
            var parametros= arrayOf(id)

            var cursor =select.rawQuery(SQL,parametros)
            if(cursor.moveToFirst()){
                //significa que almenos hay una coicidencia con la consulta qur hiciste , si hay resultado


                motivo=cursor.getString(2)
                horaI=cursor.getString(3)
                horaF=cursor.getString(4)

                // dialogo("INFORMACION DE MAQUINA \n"+nombre+"\n"+noParte+"\n"+idMolde)

            }else{
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()

        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
    }
    fun tiempoInactivo():String{

        var tiempoIn=""
        var A= horaI.split(':')
        var B= obtenerHora().split(':')
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
         //dialogo("Tiempo inactivo" +h+":"+m+":"+s)
       // inactivotxt.setText("Tiempo Inactivo :"+tiempoIn)
        return tiempoIn

    }
    fun statusTrue(){
        consultarIDMaquinas(""+id)
        try {
            var baseDatos1 =BaseDatos(this,nombreBaseDatos,null,1)

            var actualizar =baseDatos1.writableDatabase
            var SQL ="UPDATE MAQUINAS2  SET STATUS='Activo' WHERE IDMAQUINA= ? "
            var parametros= arrayOf(id)
            actualizar.execSQL(SQL,parametros)

            // mensaje("se Actualizo correctamente")

            actualizar.close()
            baseDatos1.close()


        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
    }
    fun statusFalse(){
        consultarIDMaquinas(""+id)
        try {
            var baseDatos1 =BaseDatos(this,nombreBaseDatos,null,1)

            var actualizar =baseDatos1.writableDatabase
            var SQL ="UPDATE MAQUINAS2  SET STATUS='Inactivo' WHERE IDMAQUINA= ? "
            var parametros= arrayOf(id)
            actualizar.execSQL(SQL,parametros)

            // mensaje("se Actualizo correctamente")

            actualizar.close()
            baseDatos1.close()


        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
    }
    fun consultarIDMaquinas(id:String){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT * FROM MAQUINAS2 WHERE IDMAQUINA =? "
            var parametros= arrayOf(id)

            var cursor =select.rawQuery(SQL,parametros)
            if(cursor.moveToFirst()){
                //significa que almenos hay una coicidencia con la consulta qur hiciste , si hay resultado

                //idMaquina=cursor.getString(0)
                nombre=cursor.getString(1)
                noParte=cursor.getString(2)
                idMolde=cursor.getInt(3)
                lote=cursor.getString(4)
                cliente=cursor.getString(5)
                horaI=cursor.getString(6)
                horaF=cursor.getString(7)
                fechar=cursor.getString(8)
                status=cursor.getString(9)
                pa=cursor.getString(10)
                ta=cursor.getString(11)
                ti=cursor.getString(12)
                // dialogo("INFORMACION DE MAQUINA \n"+nombre+"\n"+noParte+"\n"+idMolde)

            }else{
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()

        }catch (error:SQLException){
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

