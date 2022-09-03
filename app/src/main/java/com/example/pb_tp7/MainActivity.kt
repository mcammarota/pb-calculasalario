package com.example.pb_tp7

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    val STORAGE_PERMISSION_CODE = 111

    val filename = "dados.txt"

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCalcular = this.findViewById<Button>(R.id.btnCalcular)
        val btnApagar = this.findViewById<Button>(R.id.btnApagar)
        val txtSalarioBruto = this.findViewById<EditText>(R.id.txtSalarioBruto)
        val txtDependentes = this.findViewById<EditText>(R.id.txtDependentes)
        val txtPensaoAlimenticia = this.findViewById<EditText>(R.id.txtPensaoAlimenticia)
        val txtPlanoSaude = this.findViewById<EditText>(R.id.txtPlanoSaude)
        val txtOutrosDescontos = this.findViewById<EditText>(R.id.txtOutrosDescontos)

        val file = File("/data/user/0/com.example.pb_tp7/files/$filename")

        btnCalcular.setOnClickListener(){
            val salarioBruto = txtSalarioBruto.text.toString()
            val dependentes = txtDependentes.text.toString()
            val pensao = txtPensaoAlimenticia.text.toString()
            val planoSaude = txtPlanoSaude.text.toString()
            val outrosDescontos = txtOutrosDescontos.text.toString()
            if(txtSalarioBruto.text.toString().toDouble() < 0 || txtSalarioBruto.text.toString().isEmpty()){
                Toast.makeText(this, "Favor informar valor do salário bruto.", Toast.LENGTH_LONG).show()
            }
            else{
                val salarioLiquido =
                    calculaSalarioLiquido(salarioBruto, dependentes, pensao, planoSaude, outrosDescontos)

                if (file.exists()) {
                    Toast.makeText(this, "Arquivo já existe. Favor excluir antes de criar novamente.", Toast.LENGTH_LONG).show()
                } else{
                    salvarArquivo(salarioBruto, dependentes, pensao, planoSaude, outrosDescontos)

                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("salarioLiquido", salarioLiquido.get(0))
                    intent.putExtra("descontos", salarioLiquido.get(1))
                    intent.putExtra("porcentualDescontos", salarioLiquido.get(2))
                    startActivity(intent)
                }
            }
        }

        btnApagar.setOnClickListener() {
            if (file.exists()) {
                if (file.delete()) {
                    Toast.makeText(this, "Arquivo excluído com sucesso", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Arquivo inexistente.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun calculaSalarioLiquido(txtSalarioBruto: String, txtDependentes: String, txtPensaoAlimenticia: String, txtPlanoSaude: String, txtOutrosDescontos: String) : List<Double>{

        var inss = 0.0
        var irpf = 0.0

        //INSS
        val salarioBruto = txtSalarioBruto.toDouble()

        if(salarioBruto <= 1659.38)
            inss = salarioBruto * 0.08
        else if ((salarioBruto > 1659.38) && salarioBruto <= 2765.66)
            inss = salarioBruto * 0.09
        else if ((salarioBruto > 2765.66) && salarioBruto <= 5531.31)
            inss = salarioBruto * 0.11
        else
            inss = 608.44

        //IRPF
        if(salarioBruto <= 1903.98)
            irpf = 0.0
        else if((salarioBruto > 1903.98) && salarioBruto <= 2826.65)
            irpf = salarioBruto * 0.075
        else if((salarioBruto > 2765.65) && salarioBruto <= 3751.05)
            irpf = salarioBruto * 0.15
        else if((salarioBruto > 3751.06) && salarioBruto <= 4664.68)
            irpf = salarioBruto * 0.225
        else
            irpf = salarioBruto * 0.275

        //Pensão
        val pensaoAlimenticia = txtPensaoAlimenticia.toString().toDouble()

        //Plano de Saúde
        val qtdDependentes = txtDependentes.toString().toInt()
        val planoSaude = txtPlanoSaude.toString().toDouble()
        val valorPlanoTotal = planoSaude + (qtdDependentes * 189.59)

        //Outros Descontos
        val outrosDescontos = txtOutrosDescontos.toString().toDouble()

        //Total Descontos
        val totalDescontos = inss + irpf + pensaoAlimenticia + valorPlanoTotal + outrosDescontos

        //Salário Líquido
        val salarioLiquido = salarioBruto - totalDescontos

        //Porcentual descontos
        val porcentualDescontos = (totalDescontos / salarioBruto) * 100

        val lista = arrayListOf(salarioLiquido, totalDescontos, porcentualDescontos)

        return lista
    }

    private fun escreverTexto (salarioBruto: String, dependentes: String,
                               pensao: String, planoSaude: String, outrosDescontos: String): String {
        val dataAtual = Date().toString()
        val texto = "Salário Bruto: $salarioBruto\n" +
                "Dependentes: $dependentes\n" +
                "Pensão: $pensao\n" +
                "Plano de Saúde: $planoSaude\n + " +
                "Outros Descontos: $outrosDescontos\n + " +
                "Data cadastro: $dataAtual\n"

        return texto
    }

    private fun salvarArquivo(salarioBruto: String, dependentes: String,
                              pensao: String, planoSaude: String, outrosDescontos: String) {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    STORAGE_PERMISSION_CODE
                )
            }
        } else {
            val fileOutputStream = openFileOutput(filename, MODE_APPEND)
            fileOutputStream.write(
                escreverTexto(salarioBruto, dependentes, pensao, planoSaude, outrosDescontos).toByteArray())
            fileOutputStream.close()
        }
    }


    @SuppressLint("WrongViewCast")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permissão de gravação na memório externa concedida",
                    Toast.LENGTH_LONG
                ).show()
                val salarioBruto = findViewById<EditText>(R.id.lblSalarioBruto)
                val dependentes = findViewById<EditText>(R.id.lblDependentes)
                val pensao = findViewById<EditText>(R.id.txtPensaoAlimenticia)
                val planoSaude = findViewById<EditText>(R.id.lblPlanoSaude)
                val outrosDescontos = findViewById<EditText>(R.id.lblOutrosDescontos)
                val fileOutputStream = openFileOutput(filename, MODE_APPEND)
                fileOutputStream.write(
                    escreverTexto(
                        salarioBruto.text.toString(),
                        dependentes.text.toString(),
                        pensao.text.toString(),
                        planoSaude.text.toString(),
                        outrosDescontos.text.toString())
                        .toByteArray())
                fileOutputStream.close()
            }
            else{
                Toast.makeText(this, "Permissão negada.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}
