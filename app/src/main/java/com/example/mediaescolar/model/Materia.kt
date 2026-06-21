package com.example.mediaescolar.model

/**
 * Representa uma matéria escolar e suas 3 notas.
 *
 * Esta classe concentra toda a regra de negócio do aplicativo: soma,
 * média, situação (aprovado/reprovado) e quantos pontos faltam para a
 * aprovação. Assim, tanto a tela de cadastro quanto a lista principal
 * usam exatamente o mesmo cálculo, evitando duplicação de lógica.
 *
 * @param id ID da matéria no banco de dados local. -1 indica que a
 *           matéria ainda não foi salva (é um cadastro novo).
 * @param nome Nome da matéria (ex: "Matemática").
 * @param nota1 Primeira nota, entre 0.0 e 10.0.
 * @param nota2 Segunda nota, entre 0.0 e 10.0.
 * @param nota3 Terceira nota, entre 0.0 e 10.0.
 */
data class Materia(
    var id: Long = -1,
    var nome: String,
    var nota1: Double,
    var nota2: Double,
    var nota3: Double
) {
    companion object {
        /** Média mínima necessária para o aluno ser considerado aprovado. */
        const val MEDIA_APROVACAO = 6.0
    }

    /** Soma simples das 3 notas lançadas. */
    val soma: Double
        get() = nota1 + nota2 + nota3

    /** Média aritmética das 3 notas. */
    val media: Double
        get() = soma / 3.0

    /** true se a média atingiu (ou superou) 6.0. */
    val aprovado: Boolean
        get() = media >= MEDIA_APROVACAO

    /**
     * Quantos pontos faltam na MÉDIA para o aluno atingir a aprovação.
     * Retorna 0.0 quando o aluno já está aprovado.
     */
    val pontosFaltantes: Double
        get() = if (aprovado) 0.0 else MEDIA_APROVACAO - media
}
