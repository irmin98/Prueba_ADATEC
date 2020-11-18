package mx.edu.ittepic.adatec

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {


    override fun onCreate(db: SQLiteDatabase?) {
        //PERMITE CREAR LA ESTRUCUTRA DE TABLAS DE LAS BD SQLite
        //AQUI ES DONDE VOY A ESCRIBIR TODOS LOS CREATE TABLE



        try {

            db!!.execSQL("CREATE TABLE MAQUINAS2 (IDMAQUINA INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR(200), NOPARTE VARCHAR(200),IDMOLDE INT, LOTE VARCHAR(200),CLIENTE VARCHAR(200),HORAI VARCHAR(200),HORAF VARCHAR(200),FECHARE VARCHAR(200),STATUS VARCHAR(10),PA VARCHAR(200),TA VARCHAR(200),TI VARCHAR(200))")
            db!!.execSQL("CREATE TABLE REPORTES (IDREPORTE INTEGER PRIMARY KEY AUTOINCREMENT, IDMAQUINA INTEGER,DESCRIPCION VARCHAR(200),HI VARCHAR(200),HF VARCHARR(200),TIEMPOA VARCHAR(200), FOREIGN KEY(IDMAQUINA) REFERENCES MAQUINAS2 (IDMAQUINA))")


        }catch (error:SQLiteException){

        }





    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}