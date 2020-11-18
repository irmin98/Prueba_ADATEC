package mx.edu.ittepic.adatec

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList

class Main2Activity : AppCompatActivity() {
    val nombreBaseDatos="CONTROL3"
    var listID =ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setTitle("Registrar Maquina")
       // dialogo(obtenerFecha())

        insertar.setOnClickListener {

            //valida que esten todos los campos rellenos antes de insertar
            if(validarCampos()){
                insertarMaquina()

            }

        }

    }

    fun insertarMaquina(){


        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)

            var insertar =baseDatos.writableDatabase
            var SQL ="INSERT INTO MAQUINAS2 VALUES(NULL,'${nombreM.text.toString()}','${noParte.text.toString()}','${idMolde.text.toString().toInt()}','${lote.text.toString()}','${cliente.text.toString()}','00:00:00','00:00:00','${obtenerFecha()}','Activo','0','0','0')"

            insertar.execSQL(SQL)
            insertar.close()
            baseDatos.close()

            mensaje("Se insertó correctamente")
            nombreM.setText("")
            noParte.setText("")
            idMolde.setText("")
            lote.setText("")
            cliente.setText("")


        }catch (error: SQLException){
            mensaje(error.message.toString())
        }
    }



    fun cargarLista(){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT * FROM MAQUINAS2"


            var cursor =select.rawQuery(SQL,null)
            if(cursor.count>0){
                var arreglo= ArrayList<String>()
                this.listID =ArrayList<String>()
                cursor.moveToFirst()
                var cantidad =cursor.count -1

                (0..cantidad).forEach {
                    var data ="NOMBRE: ${cursor.getString(1)}  \nNo. Parte: ${cursor.getString(2)} \n" +
                            "Id Molde: ${cursor.getString(3)}"+"\nLote: ${cursor.getString(4)}"+"\nCliente: ${cursor.getString(5)}" +
                            "\nStatus: ${cursor.getString(9)}"

                    arreglo.add(data)
                    listID.add(cursor.getString(0))
                    cursor.moveToNext()
                }
                lista.adapter =
                    ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arreglo)
                lista.setOnItemClickListener { parent, view, position, id ->
                    AlertDialog.Builder(this).setTitle("ATENCION")
                        .setMessage("¿QUE DESAS HACER CON ESTE ITEM?")
                        .setPositiveButton("ELIMINAR"){d,i->

                            //eliminarPorID(listID[position])
                        }
                        .setNeutralButton("ACTUALIZAR"){d,i->
                            // actualizarPorID(listID[position])
                        }
                        .setNegativeButton("CANCELAR"){d,i->}
                        .show()
                }
            }
            select.close()
            baseDatos.close()

        }catch (error: SQLException){
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
    public  fun mensaje(mensaje :String){
        AlertDialog.Builder(this).setMessage(mensaje).show()
    }
    fun dialogo(s:String){
        AlertDialog.Builder(this).setMessage(s)
            .setTitle("ATENCION")
            .setPositiveButton("OK"){d,i->}
            .show()
    }
    fun validarCampos() :Boolean{
        if(nombreM.length()==0){
            dialogo("Campo nombre no tiene datos")
            return false

        }
        if(noParte.length()==0){
            dialogo("Campo numero de parte no tiene datos")
            return false

        }
        if(idMolde.length()==0){
            dialogo("Campo idMolde no tiene datos")
            return false
        }

        if(lote.length()==0){
            dialogo("Campo lote no tiene datos")
            return false
        }
        if(cliente.length()==0){
            dialogo("Campo cliente no tiene datos")
            return false
        }
        return true
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
