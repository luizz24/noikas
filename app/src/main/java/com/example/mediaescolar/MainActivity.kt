package com.example.mediaescolar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaescolar.data.MateriaDbHelper
import com.example.mediaescolar.databinding.ActivityMainBinding
import com.example.mediaescolar.model.Materia

/**
 * Tela principal do aplicativo. Mostra a lista de todas as matérias
 * cadastradas, cada uma com suas notas, soma, média e situação final.
 *
 * A partir daqui o usuário pode:
 * - Adicionar uma nova matéria (botão flutuante "+").
 * - Editar uma matéria existente (ícone de lápis no item).
 * - Remover uma matéria existente (ícone de lixeira no item).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: MateriaDbHelper
    private lateinit var adapter: MateriaAdapter

    // Launcher usado para abrir a tela de adicionar/editar matéria.
    // Quando o usuário volta dessa tela (seja salvando ou cancelando),
    // recarregamos a lista para refletir qualquer alteração feita.
    private val addEditLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        carregarMaterias()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MateriaDbHelper(this)

        configurarRecyclerView()

        binding.fabAdicionar.setOnClickListener {
            val intent = Intent(this, AddEditMateriaActivity::class.java)
            addEditLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarrega sempre que a tela volta a ficar visível, garantindo
        // que os dados exibidos estejam sempre atualizados.
        carregarMaterias()
    }

    private fun configurarRecyclerView() {
        adapter = MateriaAdapter(
            mutableListOf(),
            onEditClick = { materia -> abrirEdicao(materia) },
            onDeleteClick = { materia -> confirmarRemocao(materia) }
        )
        binding.recyclerViewMaterias.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMaterias.adapter = adapter
    }

    private fun abrirEdicao(materia: Materia) {
        val intent = Intent(this, AddEditMateriaActivity::class.java)
        intent.putExtra(AddEditMateriaActivity.EXTRA_MATERIA_ID, materia.id)
        addEditLauncher.launch(intent)
    }

    /** Pede confirmação antes de remover, já que a ação não pode ser desfeita. */
    private fun confirmarRemocao(materia: Materia) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirmar_remocao_titulo)
            .setMessage(getString(R.string.confirmar_remocao_mensagem, materia.nome))
            .setPositiveButton(R.string.remover) { _, _ ->
                dbHelper.remover(materia.id)
                carregarMaterias()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    /** Busca a lista atualizada no banco de dados e atualiza a tela. */
    private fun carregarMaterias() {
        val materias = dbHelper.listarTodas()
        adapter.atualizarLista(materias)

        // Alterna entre a lista e a mensagem de "nenhuma matéria cadastrada"
        if (materias.isEmpty()) {
            binding.textViewListaVazia.visibility = View.VISIBLE
            binding.recyclerViewMaterias.visibility = View.GONE
        } else {
            binding.textViewListaVazia.visibility = View.GONE
            binding.recyclerViewMaterias.visibility = View.VISIBLE
        }
    }
}
