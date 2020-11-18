package mx.edu.ittepic.adatec

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_lista_maquinas.view.*

class MaquinaAdapterList (private val mContext: Context, private val listaMaquinas: List<Maquina>) : ArrayAdapter<Maquina>(mContext, 0, listaMaquinas){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_lista_maquinas, parent, false)

        val maquina = listaMaquinas[position]

        layout.nombre.text = maquina.nombre
        layout.cliente.text = maquina.cliente
        layout.status.text = maquina.status

        return layout
    }
}