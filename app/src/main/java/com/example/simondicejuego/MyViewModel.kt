package com.example.simondicejuego;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random
/*
     * MyViewModel, inicializará y modificará los datos de nuestra aplicación
     */
    class MyViewModel(): ViewModel() {
        //lista que guarda la secuencia del juego
        //es mutable debido a que va a cambiar continuamente
        val secuencia = mutableListOf<String>()
        //se define una MutableLiveData para observar los valores de la secuencia
        val livedata_secuencia = MutableLiveData<MutableList<String>>()

        //se inicializan las variables cuando se instancia la clase
        init {
            livedata_secuencia.value = secuencia
        }

        fun elec(): String{
            secuencia.add(when(Random.nextInt(4) + 1){
                1 -> "Verde"
                2 -> "Rojo"
                3 -> "Azul"
            else -> "Amarillo"
            })
            return secuencia[secuencia.lastIndex]
        }
    }

