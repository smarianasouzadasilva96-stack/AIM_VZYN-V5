# VZYN FF PANEL V10 — Ultimate Visual Edition

Painel Android nativo (Kotlin + Jetpack Compose) com 166 controles reais, sobreposição
flutuante, crosshair de sistema e métricas reais de dispositivo. Sem root, sem injeção de
código no Free Fire: a simulação é local e serve como camada visual de referência.

## Estado

| Item | Valor |
|---|---|
| Versão | 10.0 (versionCode 10) |
| minSdk / targetSdk | 29 (Android 10) / 34 |
| Linguagem | Kotlin 1.9.24, JVM 17 |
| UI | Jetpack Compose + Material 3 |
| Controles | 166 |
| Root | não |
| Permissões | SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE(_SPECIAL_USE), POST_NOTIFICATIONS, VIBRATE |

## Inventário

| Grupo | Controles |
|---|---|
| Aimbot | 30 |
| Aim FOV | 20 |
| ESP | 40 |
| Recoil & Armas | 20 |
| Crosshair Premium | 21 |
| Radar | 13 |
| Configurações | 22 |
| **Total** | **166** |

Nenhum controle é decorativo. Cada TOGGLE, SLIDER, OPTION e COLOR grava no
SettingsRepository e altera a prévia em tempo real; cada ACTION executa um comando real.

## Arquitetura

    core      Spec tipado, catálogo, tradução
    storage   SettingsState + SettingsRepository (DataStore, JSON, perfis)
    vm        ViewModels MVVM (Settings, Perf)
    sim       Renderizadores Canvas por recurso
    perf      Coleta real: RAM, bateria, CPU, storage, rede
    overlay   LifecycleService, bolinha arrastável, crosshair, notificação
    ui        Tema, componentes, navegação, telas

Fluxo: Spec -> ControlRow -> SettingsViewModel -> SettingsRepository -> StateFlow ->
prévia Canvas e overlay. O overlay observa o mesmo fluxo, então qualquer ajuste aplicado
na tela reflete na bolinha e no crosshair sem reiniciar o serviço.

## Dados reais

- RAM: ActivityManager.MemoryInfo
- Bateria e temperatura: ACTION_BATTERY_CHANGED
- Storage: StatFs no diretório de dados
- CPU do processo: Process.getElapsedCpuTime() normalizado por núcleos
- Rede: delta de /proc/self/net/dev
- Heap: Runtime
- FPS: contagem de frames via withFrameNanos
- Dispositivo: Build.MANUFACTURER, MODEL, VERSION.RELEASE, SDK_INT

## Sobreposição

TYPE_APPLICATION_OVERLAY, bolinha arrastável com encaixe nas bordas, painel expansível
(Abrir VZYN, Crosshair, Free Fire, Encerrar), crosshair configurável e notificação
persistente com ação de encerrar. A permissão só é solicitada no botão Permissão.

## Perfis

Criar, renomear, duplicar, excluir, ativar, exportar JSON, importar JSON, restaurar
padrões e histórico de 200 alterações. Persistência via DataStore com gravação debounced.

## Compilação

    git init && git add . && git commit -m "VZYN V10"
    gradle wrapper --gradle-version 8.7
    ./gradlew assembleDebug
    adb install -r app/build/outputs/apk/debug/app-debug.apk

Requer JDK 17, Android SDK Platform 34 e Build-Tools 34.0.0.

## CI

.github/workflows/android.yml configura JDK 17 e Android SDK, gera o wrapper, compila
assembleDebug e publica o APK como artefato.

## Limites conhecidos

- A simulação é local: não lê memória do Free Fire.
- O overlay exige que o app permaneça em execução.
- Sensibilidade, curva e predição são parâmetros de referência para ajuste manual.
