# Média Escolar — App Android (Kotlin)

Aplicativo Android nativo, em Kotlin, para cadastrar matérias escolares,
lançar 3 notas (0 a 10) em cada uma, e calcular automaticamente soma,
média e situação (Aprovado / Reprovado) com base na média mínima 6,0.

## ✅ Funcionalidades implementadas

- Adicionar, editar e remover matérias.
- 3 campos de nota por matéria (0 a 10), com validação:
  - não permite campo vazio;
  - não permite nota negativa ou maior que 10;
  - aceita ponto ou vírgula como separador decimal (ex: `7.5` ou `7,5`).
- Cálculo automático e **em tempo real** (a cada tecla digitada) de:
  - soma das 3 notas;
  - média final;
  - situação: **Aprovado** (média ≥ 6,0) ou **Reprovado**, mostrando
    quantos pontos faltam para atingir a média.
- Persistência local em **SQLite** — os dados não se perdem ao fechar o app.
- Tema escuro, predominantemente preto e roxo.
- Layouts em `ConstraintLayout`/`ScrollView` com espaçamentos que se
  adaptam a telas maiores (pasta `values-sw600dp/` para tablets).
- Código 100% comentado em português.

## 🗂 Estrutura do projeto

```
MediaEscolar/
├── build.gradle                  (config. raiz)
├── settings.gradle
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle               (config. do módulo app)
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/mediaescolar/
        │   ├── MainActivity.kt              (tela: lista de matérias)
        │   ├── AddEditMateriaActivity.kt     (tela: adicionar/editar)
        │   ├── MateriaAdapter.kt             (adapter do RecyclerView)
        │   ├── model/Materia.kt              (regra de negócio: soma/média/situação)
        │   └── data/MateriaDbHelper.kt       (persistência em SQLite)
        └── res/
            ├── layout/      (activity_main, activity_add_edit_materia, item_materia)
            ├── values/      (colors, strings, themes, dimens)
            ├── values-sw600dp/ (dimens maiores para tablets)
            ├── drawable/    (ícones vetoriais: add, edit, delete, launcher)
            ├── mipmap-anydpi-v26/ (ícone adaptativo do app)
            └── xml/         (regras de backup)
```

## 🚀 Opção A — Gerar o APK pronto SEM instalar nada (recomendado)

O projeto já inclui um workflow do **GitHub Actions**
(`.github/workflows/build-apk.yml`) que compila o APK automaticamente
nos servidores do GitHub. Você só precisa de uma conta gratuita do
GitHub e um navegador — nada é instalado no seu computador.

1. Crie uma conta grátis em [github.com](https://github.com) (se ainda não tiver).
2. Clique em **New repository**, dê um nome (ex: `media-escolar`) e
   crie (pode deixar como **Private**).
3. Na página do repositório recém-criado, clique em **"uploading an
   existing file"** e arraste **todo o conteúdo** da pasta
   `MediaEscolar` (incluindo a pasta oculta `.github`) para a janela do
   navegador. Confirme o commit ("Commit changes").
4. Clique na aba **Actions**, no topo do repositório. O workflow
   **"Build APK"** deve iniciar sozinho (se não iniciar, clique nele e
   depois em **"Run workflow"**).
5. Aguarde de 3 a 6 minutos até aparecer o ✅ verde.
6. Clique na execução concluída, role até **Artifacts** e baixe
   **MediaEscolar-apk** (um `.zip` contendo o `app-debug.apk`).
7. Extraia o `.zip`, transfira o `app-debug.apk` para o celular
   (Android 8.0+) e toque nele para instalar (autorize "instalar de
   fontes desconhecidas" se for pedido).

> Este é um APK de **debug**, ideal para testar no seu próprio
> aparelho. Para publicar na Play Store é necessário gerar um APK/AAB
> **assinado** — veja o Passo 4 da Opção B abaixo.

## 🛠 Opção B — Compilar pelo Android Studio (no seu computador)

### Passo 1 — Pré-requisitos
- [Android Studio](https://developer.android.com/studio) (versão Hedgehog/2023.1 ou mais recente).
- JDK 17 (instalado automaticamente junto com o Android Studio).
- Conexão com a internet na primeira abertura (para o Gradle baixar as
  dependências e o `gradle-wrapper.jar`, que não pode ser distribuído
  como texto e por isso não vem pronto na pasta `gradle/wrapper/`).

### Passo 2 — Abrir o projeto
1. Abra o Android Studio.
2. **File → Open...** e selecione a pasta `MediaEscolar` (a pasta que
   contém o arquivo `settings.gradle`).
3. Aguarde o **Gradle Sync** terminar (a barra de progresso na parte
   inferior da tela). O Android Studio detecta automaticamente que falta
   o `gradle-wrapper.jar` e oferece para gerá-lo/baixá-lo — aceite.

### Passo 3 — Gerar o APK (modo debug, para testar no celular)
1. No menu superior: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. Quando terminar, clique no link **"locate"** que aparece no canto
   inferior direito, ou navegue manualmente até:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```
3. Copie esse `.apk` para o celular (Android 8.0 ou superior) e instale
   (pode ser necessário habilitar "Instalar de fontes desconhecidas").

### Passo 4 — (Opcional) Gerar um APK assinado para distribuição
1. **Build → Generate Signed Bundle / APK...**
2. Escolha **APK**, crie (ou selecione) um keystore, preencha a senha e
   siga o assistente até gerar o `app-release.apk`.

### Alternativa via linha de comando
Se preferir compilar sem o Android Studio, instale o Gradle e rode, na
pasta raiz do projeto:
```bash
gradle wrapper --gradle-version 8.4   # gera o gradlew/gradlew.bat e o .jar
./gradlew assembleDebug
```
O APK gerado aparece em `app/build/outputs/apk/debug/app-debug.apk`.

## 📝 Observações técnicas

- **minSdk 26** (Android 8.0+), o que cobre praticamente todos os
  aparelhos em uso atualmente e permite usar ícone adaptativo 100%
  vetorial (sem precisar de imagens PNG).
- O ícone do app pode ser personalizado a qualquer momento pelo
  Android Studio: clique com o botão direito em `res` → **New → Image
  Asset**.
- Para trocar as cores do tema, edite apenas
  `app/src/main/res/values/colors.xml` — todos os layouts referenciam
  essas cores centralizadas, então a alteração se propaga para o app
  inteiro.
