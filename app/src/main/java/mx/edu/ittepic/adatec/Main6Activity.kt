package mx.edu.ittepic.adatec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.lista
import kotlinx.android.synthetic.main.activity_main4.*
import kotlinx.android.synthetic.main.activity_main6.*
import java.sql.SQLException

class Main6Activity : AppCompatActivity() {
    val nombreBaseDatos = "CONTROL3"
    var listID = ArrayList<String>()
    var spin="" //almacena el valor del spinner
    var motivo=""
    var horaI=""
    var horaF=""
    var listaReportes= emptyList<Reporte>()
    var reporte=Reporte("Mantenimiento", "0:00:00", "0:00:01")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)
        setTitle("Consultar Reportes")

        //------------------configuracion del spinner----------------------------
        var fallas = arrayOf("Todos","Factores ambientales","Mantenimiento","Falla desconocida")

        if (spinner1 != null) {
            val adapter = ArrayAdapter(
                this, R.layout.spinner_item, fallas
            )

            spinner1.adapter = adapter
            spinner1.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    spin = fallas[position]
                    if (spin.equals("Todos")){
                        consultarID()
                    }else {
                        consultarID(spin)
                    }

                }


                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        //--------------------------------fin configuracion spinner----------------------

      //  val reporte = Reporte("Mantenimiento", "8:43:15", "8:50:34")
      //  val reporte1 = Reporte("Ambiental", "8:43:15", "8:50:34")
      //  val reporte2 = Reporte("Descuido", "8:43:15", "8:50:34")
     //   val listaReportes = listOf(reporte,reporte1,reporte2)
     //   val adapter = ReporteAdapterList(this, listaReportes)
       // list.adapter = adapter

    }


    fun consultarID(){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT * FROM REPORTES  "
            var cursor =select.rawQuery(SQL,null)
            if(cursor.count>0){
                var arreglo= ArrayList<String>()
                this.listID =ArrayList<String>()
                cursor.moveToFirst()
                var cantidad =cursor.count -1
                (0..cantidad).forEach {
                     reporte = Reporte(cursor.getString(2), cursor.getString(3), cursor.getString(4))
                    listaReportes=listaReportes+listOf(reporte)
                    listID.add(cursor.getString(0))
                    cursor.moveToNext()
                }

            }
            val adapter = ReporteAdapterList(this, listaReportes)
            list.adapter = adapter
            listaReportes= emptyList()
            select.close()
            baseDatos.close()

        }catch (error: SQLException){

        }
    }
    fun consultarID(id:String){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT * FROM REPORTES WHERE DESCRIPCION= '${id}' "

            var cursor =select.rawQuery(SQL,null)
            if(cursor.count>0){
                var arreglo= ArrayList<String>()
                this.listID =ArrayList<String>()
                cursor.moveToFirst()
                var cantidad =cursor.count -1
                (0..cantidad).forEach {
                    reporte = Reporte(cursor.getString(2), cursor.getString(3), cursor.getString(4))
                    listaReportes=listaReportes+listOf(reporte)
                    listID.add(cursor.getString(0))
                    cursor.moveToNext()
                }

            }
            val adapter = ReporteAdapterList(this, listaReportes)
            list.adapter = adapter
            listaReportes= emptyList()
            select.close()
            baseDatos.close()

        }catch (error: SQLException){

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


