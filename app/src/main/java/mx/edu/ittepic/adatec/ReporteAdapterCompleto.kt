package mx.edu.ittepic.adatec

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_lista_maquinas.view.*
import kotlinx.android.synthetic.main.item_lista_reportes.view.*

class ReporteAdapterCompleto (private val mContext: Context, private val listaReportesC: List<ReporteCompleto>) : ArrayAdapter<ReporteCompleto>(mContext, 0, listaReportesC) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout =
            LayoutInflater.from(mContext).inflate(R.layout.item_lista_reportes, parent, false)

        val reporteC = listaReportesC[position]

        layout.motivo.text = reporteC.motivo
        layout.fechaInicio.text = reporteC.fechaInicio
        layout.fechaFin.text = reporteC.fechaFin
        layout.ti.text = reporteC.ti

        return layout
    }
}