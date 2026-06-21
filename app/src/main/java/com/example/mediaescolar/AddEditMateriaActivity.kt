package com.example.mediaescolar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.mediaescolar.data.MateriaDbHelper
import com.example.mediaescolar.databinding.ActivityAddEditMateriaBinding
import com.example.mediaescolar.model.Materia
import java.util.Locale

/**
 * Tela usada tanto para CADASTRAR uma nova matéria quanto para EDITAR uma
 * matéria já existente. O comportamento depende de [materiaId]: se um ID
 * válido foi recebido via Intent, a tela entra em modo de edição.
 *
 * Sempre que o usuário altera qualquer uma das 3 notas, a soma, a média e
 * a situação (Aprovado/Reprovado) são recalculadas e exibidas IMEDIATAMENTE,
 * através de [TextWatcher]s ligados a cada campo de nota.
 */
class AddEditMateriaActivity : AppCompatActivity() {

    companion object {
        /** Chave usada para passar o ID da matéria a ser editada via Intent. */
        const val EXTRA_MATERIA_ID = "extra_materia_id"
    }

    private lateinit var binding: ActivityAddEditMateriaBinding
    private lateinit var dbHelper: MateriaDbHelper

    // ID da matéria sendo editada. -1 (valor padrão) significa que é uma
    // matéria NOVA, ainda não salva no banco.
    private var materiaId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditMateriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MateriaDbHelper(this)

        materiaId = intent.getLongExtra(EXTRA_MATERIA_ID, -1)

        if (materiaId != -1L) {
            // Modo edição: carrega os dados existentes nos campos da tela
            binding.textViewTitulo.text = getString(R.string.editar_materia)
            carregarMateriaExistente(materiaId)
        } else {
            binding.textViewTitulo.text = getString(R.string.nova_materia)
        }

        configurarAtualizacaoEmTempoReal()
        atualizarResultado() // calcula o estado inicial da tela

        binding.buttonSalvar.setOnClickListener { salvarMateria() }
        binding.buttonCancelar.setOnClickListener { finish() }
    }

    /** Busca a matéria no banco de dados e preenche os campos da tela. */
    private fun carregarMateriaExistente(id: Long) {
        val materia = dbHelper.buscarPorId(id) ?: return
        binding.editTextNome.setText(materia.nome)
        binding.editTextNota1.setText(formatarNotaParaEdicao(materia.nota1))
        binding.editTextNota2.setText(formatarNotaParaEdicao(materia.nota2))
        binding.editTextNota3.setText(formatarNotaParaEdicao(materia.nota3))
    }

    /**
     * Formata uma nota para exibição no campo de edição, removendo o ".0"
     * desnecessário quando o valor é inteiro (ex: "8" em vez de "8.00").
     */
    private fun formatarNotaParaEdicao(valor: Double): String {
        return if (valor == valor.toLong().toDouble()) {
            valor.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", valor)
        }
    }

    /** Liga um TextWatcher a cada campo de nota para recalcular tudo em tempo real. */
    private fun configurarAtualizacaoEmTempoReal() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                atualizarResultado()
            }
        }
        binding.editTextNota1.addTextChangedListener(watcher)
        binding.editTextNota2.addTextChangedListener(watcher)
        binding.editTextNota3.addTextChangedListener(watcher)
    }

    /**
     * Converte o texto de um campo de nota para Double. Aceita tanto ponto
     * quanto vírgula como separador decimal (ex: "7.5" ou "7,5"), já que o
     * teclado numérico do Android pode inserir qualquer um dos dois
     * dependendo do idioma do aparelho.
     *
     * Retorna null se o texto estiver vazio ou não for um número válido.
     */
    private fun textoParaNota(texto: String): Double? {
        val textoNormalizado = texto.trim().replace(",", ".")
        if (textoNormalizado.isEmpty()) return null
        return textoNormalizado.toDoubleOrNull()
    }

    /**
     * Recalcula soma, média e situação a cada alteração de nota, mostrando
     * o resultado em tempo real. Enquanto algum campo estiver vazio ou
     * inválido, exibe uma mensagem pedindo para completar as notas em vez
     * de travar ou mostrar um cálculo incorreto.
     */
    private fun atualizarResultado() {
        val nota1 = textoParaNota(binding.editTextNota1.text.toString())
        val nota2 = textoParaNota(binding.editTextNota2.text.toString())
        val nota3 = textoParaNota(binding.editTextNota3.text.toString())

        if (nota1 == null || nota2 == null || nota3 == null) {
            binding.textViewSomaResultado.text = getString(R.string.formato_soma, 0.0)
            binding.textViewMediaResultado.text = getString(R.string.formato_media, 0.0)
            binding.textViewSituacaoResultado.text = getString(R.string.preencha_as_notas)
            binding.textViewSituacaoResultado.setTextColor(getColor(R.color.gray_neutro))
            return
        }

        // Materia "temporária", usada apenas para reaproveitar a lógica de
        // cálculo de soma/média/situação já definida no modelo
        val materiaTemp = Materia(nome = "", nota1 = nota1, nota2 = nota2, nota3 = nota3)

        binding.textViewSomaResultado.text = getString(R.string.formato_soma, materiaTemp.soma)
        binding.textViewMediaResultado.text = getString(R.string.formato_media, materiaTemp.media)

        if (materiaTemp.aprovado) {
            binding.textViewSituacaoResultado.text = getString(R.string.aprovado)
            binding.textViewSituacaoResultado.setTextColor(getColor(R.color.green_aprovado))
        } else {
            binding.textViewSituacaoResultado.text =
                getString(R.string.formato_reprovado, materiaTemp.pontosFaltantes)
            binding.textViewSituacaoResultado.setTextColor(getColor(R.color.red_reprovado))
        }
    }

    /**
     * Valida todos os campos da tela. Retorna true somente se tudo estiver
     * correto; caso contrário, exibe a mensagem de erro apropriada em cada
     * campo inválido e retorna false.
     */
    private fun validarCampos(): Boolean {
        var valido = true

        val nome = binding.editTextNome.text.toString().trim()
        if (nome.isEmpty()) {
            binding.editTextNome.error = getString(R.string.erro_nome_vazio)
            valido = false
        }

        // Usamos "and" sem curto-circuito (valido = X && valido) para garantir
        // que TODOS os campos sejam validados e mostrem seu próprio erro,
        // em vez de parar no primeiro campo inválido encontrado.
        valido = validarCampoNota(binding.editTextNota1) && valido
        valido = validarCampoNota(binding.editTextNota2) && valido
        valido = validarCampoNota(binding.editTextNota3) && valido

        return valido
    }

    /**
     * Valida um único campo de nota:
     * - não pode estar vazio
     * - precisa ser um número válido
     * - precisa estar entre 0 e 10 (inclusive)
     */
    private fun validarCampoNota(editText: EditText): Boolean {
        val texto = editText.text.toString().trim()

        if (texto.isEmpty()) {
            editText.error = getString(R.string.erro_nota_vazia)
            return false
        }

        val nota = textoParaNota(texto)
        if (nota == null) {
            editText.error = getString(R.string.erro_nota_invalida)
            return false
        }

        if (nota < 0.0 || nota > 10.0) {
            editText.error = getString(R.string.erro_nota_fora_intervalo)
            return false
        }

        return true
    }

    /** Valida e salva (insere ou atualiza) a matéria no banco de dados local. */
    private fun salvarMateria() {
        if (!validarCampos()) {
            return // Não salva enquanto houver campos inválidos
        }

        val nome = binding.editTextNome.text.toString().trim()
        val nota1 = textoParaNota(binding.editTextNota1.text.toString())!!
        val nota2 = textoParaNota(binding.editTextNota2.text.toString())!!
        val nota3 = textoParaNota(binding.editTextNota3.text.toString())!!

        val materia = Materia(
            id = materiaId, nome = nome, nota1 = nota1, nota2 = nota2, nota3 = nota3
        )

        if (materiaId == -1L) {
            dbHelper.inserir(materia)
        } else {
            dbHelper.atualizar(materia)
        }

        setResult(RESULT_OK)
        finish()
    }
}
