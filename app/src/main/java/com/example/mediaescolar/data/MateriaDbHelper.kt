package com.example.mediaescolar.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mediaescolar.model.Materia

/**
 * Responsável por persistir as matérias e notas localmente no dispositivo,
 * usando SQLite (via [SQLiteOpenHelper]). Por usar um banco de dados real
 * em vez de memória, os dados sobrevivem ao fechamento do aplicativo.
 *
 * Todas as operações (inserir, atualizar, remover, listar) abrem e fecham
 * a conexão a cada chamada. Para um app simples como este, isso é
 * suficiente e evita vazamento de conexões abertas.
 */
class MateriaDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "media_escolar.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_MATERIAS = "materias"
        private const val COL_ID = "id"
        private const val COL_NOME = "nome"
        private const val COL_NOTA1 = "nota1"
        private const val COL_NOTA2 = "nota2"
        private const val COL_NOTA3 = "nota3"
    }

    /** Chamado automaticamente na primeira vez que o app roda no dispositivo. */
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MATERIAS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOME TEXT NOT NULL,
                $COL_NOTA1 REAL NOT NULL,
                $COL_NOTA2 REAL NOT NULL,
                $COL_NOTA3 REAL NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    /** Chamado quando DATABASE_VERSION aumenta em uma futura atualização do app. */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MATERIAS")
        onCreate(db)
    }

    /** Insere uma nova matéria no banco e retorna o ID gerado para ela. */
    fun inserir(materia: Materia): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOME, materia.nome)
            put(COL_NOTA1, materia.nota1)
            put(COL_NOTA2, materia.nota2)
            put(COL_NOTA3, materia.nota3)
        }
        val id = db.insert(TABLE_MATERIAS, null, values)
        db.close()
        return id
    }

    /** Atualiza os dados de uma matéria já existente, identificada pelo ID. */
    fun atualizar(materia: Materia): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOME, materia.nome)
            put(COL_NOTA1, materia.nota1)
            put(COL_NOTA2, materia.nota2)
            put(COL_NOTA3, materia.nota3)
        }
        val linhasAfetadas = db.update(
            TABLE_MATERIAS, values, "$COL_ID = ?", arrayOf(materia.id.toString())
        )
        db.close()
        return linhasAfetadas
    }

    /** Remove definitivamente uma matéria pelo ID. */
    fun remover(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_MATERIAS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    /** Retorna todas as matérias salvas, ordenadas por nome (A-Z). */
    fun listarTodas(): MutableList<Materia> {
        val lista = mutableListOf<Materia>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MATERIAS, null, null, null, null, null, "$COL_NOME ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Materia(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                        nome = it.getString(it.getColumnIndexOrThrow(COL_NOME)),
                        nota1 = it.getDouble(it.getColumnIndexOrThrow(COL_NOTA1)),
                        nota2 = it.getDouble(it.getColumnIndexOrThrow(COL_NOTA2)),
                        nota3 = it.getDouble(it.getColumnIndexOrThrow(COL_NOTA3))
                    )
                )
            }
        }
        db.close()
        return lista
    }

    /** Busca uma única matéria pelo ID. Retorna null se não existir. */
    fun buscarPorId(id: Long): Materia? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MATERIAS, null, "$COL_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        var materia: Materia? = null
        cursor.use {
            if (it.moveToFirst()) {
                materia = Materia(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                    nome = it.getString(it.getColumnIndexOrThrow(COL_NOME)),
                    nota1 = it.getDouble(it.getColumnIndexOrThrow(COL_NOTA1)),
                    nota2 = it.getDouble(it.getColumnIndexOrThrow(COL_NOTA2)),
                    nota3 = it.getDouble(it.getColumnIndexOrThrow(COL_NOTA3))
                )
            }
        }
        db.close()
        return materia
    }
}
