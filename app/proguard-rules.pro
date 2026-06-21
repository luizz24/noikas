# Regras padrão do ProGuard/R8.
# Como o app não usa reflexão nem bibliotecas que exigem regras especiais,
# o arquivo permanece com a configuração padrão do Android Studio.

# Mantém os nomes de linha para facilitar a leitura de stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
