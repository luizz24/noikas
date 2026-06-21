package com.example.mediaescolar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaescolar.databinding.ItemMateriaBinding
import com.example.mediaescolar.model.Materia

/**
 * Adapter responsável por exibir a lista de matérias no RecyclerView da
 * tela principal. Cada item mostra: nome, as 3 notas, soma, média e a
 * situação final do aluno (Aprovado ou Reprovado + pontos que faltam).
 *
 * @param onEditClick chamado quando o usuário toca no ícone de editar.
 * @param onDeleteClick chamado quando o usuário toca no ícone de remover.
 */
class MateriaAdapter(
    private var materias: MutableList<Materia>,
    private val onEditClick: (Materia) -> Unit,
    private val onDeleteClick: (Materia) -> Unit
) : RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder>() {

    inner class MateriaViewHolder(val binding: ItemMateriaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val binding = ItemMateriaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MateriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        val materia = materias[position]
        val context = holder.itemView.context

        holder.binding.textViewNomeMateria.text = materia.nome
        holder.binding.textViewNotas.text = context.getString(
            R.string.formato_notas, materia.nota1, materia.nota2, materia.nota3
        )
        holder.binding.textViewSoma.text = context.getString(
            R.string.formato_soma, materia.soma
        )
        holder.binding.textViewMedia.text = context.getString(
            R.string.formato_media, materia.media
        )

        // Situação final: verde quando aprovado, vermelho com os pontos
        // que faltam quando reprovado
        if (materia.aprovado) {
            holder.binding.textViewSituacao.text = context.getString(R.string.aprovado)
            holder.binding.textViewSituacao.setTextColor(
                context.getColor(R.color.green_aprovado)
            )
        } else {
            holder.binding.textViewSituacao.text = context.getString(
                R.string.formato_reprovado, materia.pontosFaltantes
            )
            holder.binding.textViewSituacao.setTextColor(
                context.getColor(R.color.red_reprovado)
            )
        }

        holder.binding.buttonEditar.setOnClickListener { onEditClick(materia) }
        holder.binding.buttonRemover.setOnClickListener { onDeleteClick(materia) }
    }

    override fun getItemCount(): Int = materias.size

    /** Substitui a lista exibida pelos dados mais recentes vindos do banco. */
    fun atualizarLista(novaLista: List<Materia>) {
        materias = novaLista.toMutableList()
        notifyDataSetChanged()
    }
}
