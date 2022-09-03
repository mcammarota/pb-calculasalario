package com.example.pb_tp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val txtSalarioLiquido = this.findViewById<TextView>(R.id.txtSalarioLiquido)
        val txtOutrosDescontos = this.findViewById<TextView>(R.id.txtTotalDescontos)
        val txtPorcentualDescontos = this.findViewById<TextView>(R.id.txtPorcentualDescontos)
        val btnVoltar = this.findViewById<Button>(R.id.btnVoltar)

        val salarioLiquido = intent.getDoubleExtra("salarioLiquido", 0.0)
        val descontos = intent.getDoubleExtra("descontos", 0.0)
        val porcentualDescontos = intent.getDoubleExtra("porcentualDescontos", 0.0)

        val moeda = DecimalFormat.getCurrencyInstance(Locale("pt", "BR"))

        val salarioLiquidoTexto = moeda.format(salarioLiquido)
        val descontosTexto = moeda.format(descontos)
        var porcentualTexto = String.format("%.2f", porcentualDescontos)
        porcentualTexto += "%"

        txtSalarioLiquido.text = salarioLiquidoTexto
        txtOutrosDescontos.text = descontosTexto
        txtPorcentualDescontos.text = porcentualTexto

        btnVoltar.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}