package ar.com.develup.tateti.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ar.com.develup.tateti.R
import ar.com.develup.tateti.adaptadores.AdaptadorPartidas
import ar.com.develup.tateti.modelo.Constantes
import ar.com.develup.tateti.modelo.Partida
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.actividad_partidas.*

class ActividadPartidas : AppCompatActivity() {

    companion object {
        private const val TAG = "ActividadPartidas"
    }

    private lateinit var adaptadorPartidas: AdaptadorPartidas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_partidas)
        adaptadorPartidas = AdaptadorPartidas(this)
        partidas.layoutManager = LinearLayoutManager(this)
        partidas.adapter = adaptadorPartidas
        nuevaPartida.setOnClickListener { nuevaPartida() }
        cerrarSesion.setOnClickListener { cerrarSesion() }
    }

    override fun onResume() {
        super.onResume()
        // TODO-06-DATABASE
        // Obtener una referencia a la base de datos, suscribirse a los cambios en Constantes.TABLA_PARTIDAS
        // y agregar como ChildEventListener el listenerTablaPartidas definido mas abajo
        FirebaseDatabase.getInstance().reference.child(Constantes.TABLA_PARTIDAS).addChildEventListener(listenerTablaPartidas)
    }

    fun nuevaPartida() {
        val intent = Intent(this, ActividadPartida::class.java)
        startActivity(intent)

        //Eventos de Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Nueva Partida")
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    private val listenerTablaPartidas: ChildEventListener = object : ChildEventListener {


        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            Log.i(TAG, "onChildAdded: $dataSnapshot")
            // Obtener el valor del dataSnapshot
            val partida = dataSnapshot.getValue(Partida::class.java)
            // Asignar el valor del campo "key" del dataSnapshot
            partida!!.id = dataSnapshot.key!!
            adaptadorPartidas.agregarPartida(partida)
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            Log.i(TAG, "onChildChanged: $s")
            // Obtener el valor del dataSnapshot
            val partida = dataSnapshot.getValue(Partida::class.java)
            // Asignar el valor del campo "key" del dataSnapshot
            partida!!.id = dataSnapshot.key!!
            adaptadorPartidas.partidaCambio(partida)
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            Log.i(TAG, "onChildRemoved: ")
            // Obtener el valor del dataSnapshot
            val partida = dataSnapshot.getValue(Partida::class.java)
            // Asignar el valor del campo "key" del dataSnapshot
            partida!!.id = dataSnapshot.key!!
            adaptadorPartidas.remover(partida)
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            Log.i(TAG, "onChildMoved: $s")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.i(TAG, "onCancelled: ")
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, ActividadInicial::class.java)
        startActivity(intent)

        //Eventos en FireBase
        val bundle = Bundle()
        val se_deslogueo = "se_deslogueo"
        bundle.putString("se_deslogueo", se_deslogueo)
        FirebaseAnalytics.getInstance(this).logEvent("desloguearse", bundle)
    }
}