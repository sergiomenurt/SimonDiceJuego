package com.example.simondicejuego

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

var colorFondo = "" //variable que, una vez seleccionado, guardará el color de fondo

class MainActivity : AppCompatActivity() {
    //val colores = arrayOf("Verde", "Rojo", "Azul", "Amarillo")
    var color = ""
    //var secuencia = arrayOf("")
    var contadorS = 0
    //variable a la que le indicaremos el tamaño de la secuencia de nuestro viewmodel
    var contadorJ = 0
    var puntuacion = 0
    var empezar = false
    var fallo = false
    lateinit var layout : View
    lateinit var layoutroot : View
    lateinit var  botonVerde : Button
    lateinit var  botonRojo : Button
    lateinit var  botonAzul : Button
    lateinit var  botonAmarillo : Button
    lateinit var botonInicio : Button
    lateinit var botonVolverAJugar : Button
    lateinit var botonSalir : Button
    lateinit var mandar: TextView
    lateinit var fallar : TextView
    //variable que recibe el color que se le agrege a la secuencia
    lateinit var ultColor : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val miModelo by viewModels<MyViewModel>()
        //hayamos el layout por su id para cambiar el color con los botones
        layout = findViewById(R.id.layout)
        layoutroot = layout.rootView

        //si la variable colorFondo no esta vacía llamamos a la función colorF
        if(colorFondo.isNotEmpty())
            colorF(colorFondo, layoutroot)

        //Listeners para saber que botón es presionado y cambiar el color de fondo al del botón que se presionó
        botonVerde = findViewById(R.id.Botonverde)
        botonVerde.setOnClickListener {
            layoutroot.setBackgroundColor(ContextCompat.getColor(this, R.color.greenB))
            colorFondo = "verde"
        }

        botonRojo = findViewById(R.id.Botonrojo)
        botonRojo.setOnClickListener {
            layoutroot.setBackgroundColor(ContextCompat.getColor(this, R.color.redB))
            colorFondo = "rojo"
        }

        botonAzul = findViewById(R.id.Botonazul)
        botonAzul.setOnClickListener {
            layoutroot.setBackgroundColor(ContextCompat.getColor(this, R.color.blueB))
            colorFondo = "azul"
        }

        botonAmarillo = findViewById(R.id.Botonamarillo)
        botonAmarillo.setOnClickListener {
            layoutroot.setBackgroundColor(ContextCompat.getColor(this, R.color.yellowB))
            colorFondo = "amarillo"
        }

        //declaración de los textview
        mandar = findViewById(R.id.manda)
        fallar = findViewById(R.id.fallo)

        //botón inicio que al ser pulsado comienza el juego
        botonInicio = findViewById(R.id.Botoninicio)
        botonInicio.setOnClickListener{
            empezar = true
            ultColor = miModelo.elec()
            dice(ultColor)
            empezar(botonInicio)
            jugar() //corrutina
        }

        //botón restart que solo se visualiza si se pierde en el juego
        botonVolverAJugar = findViewById(R.id.VolverAJugar)
        botonVolverAJugar.setOnClickListener {
            val mIntent = intent //creación de un intento
            intent.putExtra("colorFondo", String()) //guardamos la variable colorFondo en el intento
            finish() //finaliza la actividad
            startActivity(mIntent) //vuelve a comenzar la actividad
        }

        //botón salie que solo se visualiza si se pierde en el juego
        botonSalir = findViewById(R.id.salir)
        botonSalir.setOnClickListener{
            exitProcess(0)
        }
    }

    //función que enseña en pantalla el color que se debe presionar
    fun dice(colRan: String){
        if(colRan == "Verde")
            mandar.setText(R.string.SGreen)
        else if(colRan == "Rojo")
            mandar.setText(R.string.SRed)
        else if(colRan == "Azul")
            mandar.setText(R.string.SBlue)
        else
            mandar.setText(R.string.SYellow)
    }

    //función que empieza el juego sacando por pantalla el primer color y esconde el boton de inicio
    fun empezar(boton: Button) {
        boton.visibility = View.GONE
    }

    //función que comprueba que los colores sean presionados en el orden indicado
    fun comprobarSecuencia(view: View, comprobado: MutableList<String>){
        if(comprobado[contadorS] == "Verde"){
            val bPrueba : Button = findViewById(R.id.Botonverde)
            if (bPrueba.id != view.id)
                fallo = true
        }
        if(comprobado[contadorS] == "Rojo"){
            val bPrueba : Button = findViewById(R.id.Botonrojo)
            if (bPrueba.id != view.id)
                fallo = true
        }
        if(comprobado[contadorS] == "Azul"){
            val bPrueba : Button = findViewById(R.id.Botonazul)
            if (bPrueba.id != view.id)
                fallo = true
        }
        if(comprobado[contadorS] == "Amarillo"){
            val bPrueba : Button = findViewById(R.id.Botonamarillo)
            if (bPrueba.id != view.id)
                fallo = true
        }
        //cambiar el valor de la puntuación si se comprueba que la secuencia fue colocada correctamente
        val punt : TextView = findViewById(R.id.puntuacion)
        if(contadorS == contadorJ) {
            contadorS = 0
            puntuacion++
            val sPun = puntuacion.toString()
            punt.text = sPun

            val miModelo by viewModels<MyViewModel>()
            miModelo.livedata_secuencia.observe(
                this,
                //observador para que cada vez que cambie la secuencia cambie el valor de nuestro contadorJ
                Observer(
                    fun(nuevaSecuencia: MutableList<String>){
                        contadorJ = nuevaSecuencia.size
                    }
                )
            )
            //devuelve el nuevo color que se agrega a la secuencia para enseñarlo por pantalla
            ultColor = miModelo.elec()
            dice(ultColor)
        }
        else {
            contadorS++
        }
        if(fallo)
            fallar()
    }

    //función que contiene una corrutina para cambiar la función de los botones para jugar
    @OptIn(DelicateCoroutinesApi::class)
    fun jugar(){
        GlobalScope.launch(Dispatchers.Main){
            val miModelo by viewModels<MyViewModel>()

            botonVerde.setOnClickListener{
                comprobarSecuencia(botonVerde, miModelo.secuencia)
            }
            botonRojo.setOnClickListener{
                comprobarSecuencia(botonRojo, miModelo.secuencia)
            }
            botonAzul.setOnClickListener{
                comprobarSecuencia(botonAzul, miModelo.secuencia)
            }
            botonAmarillo.setOnClickListener{
                comprobarSecuencia(botonAmarillo, miModelo.secuencia)
            }
        }
    }

    //función que se encarga de gestionar lo que pasa cuando el usuario falla o se sale del juego
    fun fallar(){
        botonVerde.visibility = View.GONE
        botonRojo.visibility = View.GONE
        botonAzul.visibility = View.GONE
        botonAmarillo.visibility = View.GONE
        mandar.visibility = View.GONE
        botonVolverAJugar.visibility = View.VISIBLE
        botonSalir.visibility = View.VISIBLE
        fallar.setText(R.string.fallo)
        fallar.visibility = View.VISIBLE
    }

}