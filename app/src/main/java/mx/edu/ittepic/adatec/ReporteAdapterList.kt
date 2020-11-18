package mx.edu.ittepic.adatec

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_lista_maquinas.view.*
import kotlinx.android.synthetic.main.item_lista_reportes.view.*

class ReporteAdapterList (private val mContext: Context, private val listaReportes: List<Reporte>) : ArrayAdapter<Reporte>(mContext, 0, listaReportes){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_lista_reportes, parent, false)

        val maquina = listaReportes[position]

        layout.motivo.text = maquina.motivo
        layout.fechaInicio.text = maquina.fechaInicio
        layout.fechaFin.text = maquina.fechaFin

        return layout
    }


}