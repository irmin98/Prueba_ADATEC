package mx.edu.ittepic.adatec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main6.*
import kotlinx.android.synthetic.main.item_lista_maquinas.view.*
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    val nombreBaseDatos="CONTROL3"
    var listID =ArrayList<String>()
    var idMaquina=""
    var nombre =""
    var noParte=""
    var idMolde=0
    var lote=""
    var cliente=""
    var horaI="00:00:00"
    var horaF="00:00:00"
    var status=""
    var FechaR="0000/00/00-00:00:00"

    var listaMaquinas= emptyList<Maquina>()
    var maquina=Maquina("nombre", "cliente", "status")

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("Centro de Control")
        //obtenerHora()
        cargarLista()

        agregarM.setOnClickListener {
            var intento = Intent(this,Main2Activity::class.java)
            startActivityForResult(intento,0)

        }


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



    fun actualizarHoraF(id:String){
        consultarID(id)
        try {
            var baseDatos1 =BaseDatos(this,nombreBaseDatos,null,1)

            var actualizar =baseDatos1.writableDatabase
            var SQL ="UPDATE MAQUINAS2  SET NOMBRE='${nombre.toString()}', NOPARTE='${noParte.toString()}',IDMOLDE='${idMolde.toInt()}',LOTE='${lote.toString()}',CLIENTE='${cliente.toString()}',HORAI='${horaI}',HORAF='${obtenerHora()}' WHERE IDMAQUINA= ? "
            var parametros= arrayOf(id)
            actualizar.execSQL(SQL,parametros)

            // mensaje("se Actualizo correctamente")

            actualizar.close()
            baseDatos1.close()


            cargarLista()

        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
    }

  public  fun cargarLista(){
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
                /*    var data ="NOMBRE: ${cursor.getString(1)}  \nNo. Parte: ${cursor.getString(2)} \n" +
                            "Id Molde: ${cursor.getString(3)}"+"\nLote: ${cursor.getString(4)}"+"\nCliente: ${cursor.getString(5)}" +
                    "\nStatus: ${cursor.getString(9)}"
                */
                   // arreglo.add(data)
                    maquina = Maquina(cursor.getString(1), cursor.getString(5), cursor.getString(9))
                    listaMaquinas=listaMaquinas + listOf(maquina)


                    listID.add(cursor.getString(0))
                    cursor.moveToNext()
                }

               // lista.adapter =ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arreglo)
                lista.setOnItemClickListener { parent, view, position, id ->
                    AlertDialog.Builder(this).setTitle("ATENCION")
                        .setMessage("¿QUE DESEAS HACER CON ESTA MAQUINA?")
                        .setPositiveButton("REPORTAR INCIDENTE"){d,i->
                            //dialogo(listID[position])
                           //actualizarHoraI(listID[position])

                            var intento = Intent(this,Main4Activity::class.java)
                            intento.putExtra("id",listID[position].toString())

                            startActivityForResult(intento,0)

                        }
                        .setNegativeButton("CANCELAR"){d,i->
                            //actualizarHoraF(listID[position])
                        }
                        .setNeutralButton("VER"){d,i->
                            consultarID(listID[position])



                            var intento = Intent(this,Main3Activity::class.java)
                            intento.putExtra("idMaquina",listID[position])
                            intento.putExtra("nombre",nombre)
                            intento.putExtra("noParte",noParte)
                            intento.putExtra("idMolde",idMolde)
                            intento.putExtra("lote",lote)
                            intento.putExtra("cliente",cliente)
                            intento.putExtra("horaI",horaI)
                            intento.putExtra("horaF",horaF)
                            intento.putExtra("status",status)


                             startActivityForResult(intento,0)
                        }
                        .show()
                }
            }
            val adapter = MaquinaAdapterList(this, listaMaquinas)

            lista.adapter = adapter

            listaMaquinas= emptyList()
            select.close()
            baseDatos.close()


        }catch (error: SQLException){
            mensaje(error.message.toString())
        }

    }
    fun obtenerHora() : String{
        val c: Calendar = Calendar.getInstance()

        val Hora: String = (
                ""+c.get(Calendar.HOUR_OF_DAY)
                + ":" + c.get(Calendar.MINUTE)
                + ":" + c.get(Calendar.SECOND))
      //  dialogo(fecha)
        return Hora
    }
    fun dialogo(s:String){
        AlertDialog.Builder(this).setMessage(s)
            .setTitle("ATENCION")
            .setPositiveButton("OK"){d,i->}
            .show()
    }
    fun mensaje(mensaje :String){
        AlertDialog.Builder(this).setMessage(mensaje).show()
    }
    fun consultarID(id:String){
        try {
            var baseDatos =BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL ="SELECT * FROM MAQUINAS2 WHERE IDMAQUINA =? "
            var parametros= arrayOf(id)

            var cursor =select.rawQuery(SQL,parametros)
            if(cursor.moveToFirst()){
                //significa que almenos hay una coicidencia con la consulta qur hiciste , si hay resultado

                nombre=cursor.getString(1)
                noParte=cursor.getString(2)
                idMolde=cursor.getInt(3)
                lote=cursor.getString(4)
                cliente=cursor.getString(5)
                horaI=cursor.getString(6)
                horaF=cursor.getString(7)
                status=cursor.getString(9)
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
    fun actualizarHoraI(id:String){
        consultarID(id)
        try {
            var baseDatos1 =BaseDatos(this,nombreBaseDatos,null,1)

            var actualizar =baseDatos1.writableDatabase
            var SQL ="UPDATE MAQUINAS2  SET NOMBRE='${nombre.toString()}', NOPARTE='${noParte.toString()}',IDMOLDE='${idMolde.toInt()}',LOTE='${lote.toString()}',CLIENTE='${cliente.toString()}',HORAI='${obtenerHora()}',HORAF='${horaF}' WHERE IDMAQUINA= ? "
            var parametros= arrayOf(id)
            actualizar.execSQL(SQL,parametros)

           // mensaje("se Actualizo correctamente")

            actualizar.close()
            baseDatos1.close()


           cargarLista()

        }catch (error:SQLException){
            mensaje(error.message.toString())
        }
    }
    fun abrirReporte(){

    }

    override fun onBackPressed() {

    }

}




