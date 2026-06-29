# CLAUDE.md — LockDapp

Contexto del proyecto para Claude Code. Léelo completo antes de tocar código.

---

## Qué es

**LockDapp** es una app **personal** de Android de **autocontrol (self-binding)**. Bloquea el **uso de aplicaciones** según horarios por día, semana o mes. La idea es la de Ulises atándose al mástil: el usuario decide en frío qué apps no quiere poder usar, y la app cumple ese pacto.

- **Un solo usuario, un solo dispositivo** (el teléfono de Daniel). Se instala por **sideload** (`adb install`), **NO** pasa por Google Play.
- Por eso: sin políticas de Play que cumplir, sin declaraciones de permisos, `QUERY_ALL_PACKAGES` es libre.
- **NO es control parental** (no hay app de padre + app de hijo). Es una sola app que se autobloquea.

### Fuera de alcance — no lo implementes
- **Firewall / bloqueo de red / `VpnService`**: descartado por completo. No lo añadas.
- **Backend / sincronización remota**: no hay. Todo es **on-device**.
- **Multi-usuario, cuentas, login**: no aplica.

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Persistencia | Room (datos) + DataStore Preferences (ajustes) |
| Arquitectura | MVVM ligero: `ViewModel` + `StateFlow` + `Repository` + Room DAO |
| Detección de app activa | `AccessibilityService` (+ tick periódico) |
| Overlay de bloqueo | `SYSTEM_ALERT_WINDOW` |
| Persistencia en background | Foreground Service + `BOOT_COMPLETED` receiver |
| Bordes de ventana | `AlarmManager` para reevaluar al inicio/fin de cada franja |
| Anti-desinstalación (opcional) | `DevicePolicyManager` (Device Admin) |

- **compileSdk / targetSdk:** API 36 (último estable). **minSdk:** 29. Es su dispositivo, se puede subir si conviene.
- **DI:** manual / service-locator para v1. No metas Hilt salvo que el proyecto lo justifique.
- **Idioma de la UI:** **español**. Comentarios y nombres de código en inglés (convención estándar).

---

## Arquitectura de paquetes

```
com.lockdapp            (applicationId provisional — confirmar con Daniel)
├── MainActivity.kt
├── LockdApplication.kt
├── data/
│   ├── local/          Room: entities, DAOs, AppDatabase, TypeConverters
│   ├── prefs/          DataStore: tema activo, config de escape, flags
│   └── repository/     ScheduleRepository, AppRepository
├── domain/
│   ├── model/          AppGroup, TimeWindow, LockSchedule, InstalledApp
│   └── engine/         BlockEngine (lógica pura isBlocked)
├── service/
│   ├── BlockAccessibilityService.kt
│   ├── LockForegroundService.kt
│   └── BootReceiver.kt
├── ui/
│   ├── theme/          Theme.kt, Color.kt (los 3 ColorScheme), Type.kt
│   ├── dashboard/
│   ├── schedules/      lista + editor
│   ├── apps/           lista de apps + grupos
│   ├── block/          pantalla de bloqueo (overlay) — la firma
│   └── settings/       temas, escape, importar JSON
└── util/
```

---

## Modelo de datos (la verdad del sistema)

```kotlin
// La blocklist guarda PACKAGE NAMES, nunca la instancia de la app.
// Un package es estable: el usuario puede desinstalar y reinstalar la app
// objetivo mil veces y su package no cambia → el bloqueo revive solo.

data class AppGroup(
    val id: Long,
    val name: String,                 // "Redes sociales", "Juegos"
    val packages: List<String>        // ["com.instagram.android", ...]
)

data class TimeWindow(
    val startMinute: Int,             // 540  = 09:00
    val endMinute: Int                // 1020 = 17:00
)

data class LockSchedule(
    val id: Long,
    val name: String,
    val targetGroupIds: List<Long>,   // qué grupos bloquea
    val windows: List<TimeWindow>,    // [9:00-12:00, 14:00-18:00]
    val daysOfWeek: Set<DayOfWeek>,   // MON..SUN
    val validFrom: LocalDate?,        // null = sin inicio (indefinido)
    val validUntil: LocalDate?,       // null = sin fin (indefinido)
    val enabled: Boolean
)
```

**Motor de evaluación** (en `domain/engine/BlockEngine`, **función pura y testeable**):

```kotlin
fun isBlocked(pkg: String, now: LocalDateTime): Boolean {
    val today = now.toLocalDate()
    val minuteOfDay = now.hour * 60 + now.minute
    return schedules.any { s ->
        s.enabled &&
        (s.validFrom == null  || !today.isBefore(s.validFrom)) &&
        (s.validUntil == null || !today.isAfter(s.validUntil)) &&
        now.dayOfWeek in s.daysOfWeek &&
        s.windows.any { minuteOfDay in it.startMinute until it.endMinute } &&
        s.targetGroupIds.any { gid -> pkg in packagesOf(gid) }
    }
}
```

- Lógica **block-wins**: cualquier regla que coincida → bloqueado. Sin excepciones/prioridades en v1.
- **Cubre los 3 casos de uso**: recurrente indefinido (fechas null), por mes (rango de fechas + días + ventana), por semanas (rango + ventana completa).
- En Room, persiste `List`/`Set` con `TypeConverters` (serializa a JSON con kotlinx.serialization).

---

## Decisiones cerradas (no las reabras sin preguntar)

1. **Nombre clave del proyecto:** LockDapp. En runtime el nombre visible es parte del **sistema de temas** (ver abajo).
2. **Ventanas que cruzan medianoche:** NO se soportan en una sola franja. `start < end` siempre. Un bloqueo nocturno (22:00–06:00) se parte en dos reglas. Decisión consciente para evitar bugs de medianoche.
3. **Válvula de escape:** en la pantalla de bloqueo hay un acceso "Necesito acceso ahora" que impone una **espera de 30s** antes de conceder paso temporal. Debe poder **ocultarse/desactivarse desde Ajustes** (flag en DataStore). El bloqueo perfecto no existe en un dispositivo propio sin root; se diseña la fricción honestamente.
4. **Firewall:** eliminado. (Ver "Fuera de alcance".)
5. **Importar blocklist JSON:** sí, pero es el **último feature**. Vía SAF (`ACTION_OPEN_DOCUMENT`), lee un JSON de package names y los añade a la blocklist. Sirve para recuperar config si se reinstala **LockDapp** misma.

---

## Sistema de temas

3 temas completos, seleccionables en Ajustes. Cada uno cambia **nombre, icono de launcher, paleta y radio de esquinas**. Comparten tipografía y layout.

- **Mástil** — náutico, azul medianoche + acento latón, radio 16dp.
- **Dique** — ingeniería, pizarra + acento teal, radio 8dp.
- **LockD** — daemon Linux, negro OLED + violeta, radio 12dp con glow sutil.

**Las paletas exactas (hex), tipografía, tokens y specs visuales de cada pantalla están en `DESIGN.md`. Úsalo como fuente de verdad para todo lo visual.** Tipografía: Space Grotesk (display/números), Inter (cuerpo), JetBrains Mono (package names y horas).

> Cambiar **nombre/icono del launcher** NO se hace con `setText`: requiere varios `<activity-alias>` en el manifest, cada uno con su `label`/`icon`, activando uno con `PackageManager.setComponentEnabledSetting()`. El icono parpadea unos segundos al cambiar — es esperado, avísalo en la UI.

---

## Comportamiento del runtime — gotchas críticos

- **Detección de app en primer plano:** `BlockAccessibilityService.onAccessibilityEvent` con `TYPE_WINDOW_STATE_CHANGED` da el package activo → consulta `BlockEngine.isBlocked()` → si true, lanza la pantalla de bloqueo (overlay) o un intent a Home.
- **Caso que se escapa:** si el usuario YA está dentro de una app cuando arranca la ventana de bloqueo, no hay evento de cambio de foreground. Por eso el **Foreground Service hace un tick periódico (~30–60s)** que reevalúa la app actual, y se usa `AlarmManager` en los bordes exactos de cada franja.
- **`foregroundServiceType`** debe declararse en el manifest (Android 14+). Usar el tipo apropiado (p. ej. `specialUse` con justificación).
- **`BOOT_COMPLETED`**: el `BootReceiver` reactiva el Foreground Service al reiniciar.
- **Permisos** (todos se conceden manualmente en Ajustes del sistema, con onboarding que los explica uno a uno): Accesibilidad, Usage Access (`PACKAGE_USAGE_STATS`), Overlay (`SYSTEM_ALERT_WINDOW`), excepción de batería, y opcionalmente Device Admin.
- **Listar apps:** `getInstalledApplications` filtrado por `CATEGORY_LAUNCHER` para quitar paquetes de sistema. Iconos en **carga perezosa** (no cargar 80 de golpe).

---

## Comandos

```bash
# Compilar APK debug
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk

# Compilar e instalar en el dispositivo conectado (un paso)
./gradlew installDebug

# Instalar/actualizar manualmente
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Tests unitarios (prioriza cubrir BlockEngine.isBlocked)
./gradlew test

# Lint
./gradlew lint
```

Probar **en el dispositivo físico** de Daniel (USB debugging), no en emulador: accesibilidad, overlay, usage-stats y Device Admin se comportan mejor en hardware real. `JAVA_HOME` debe apuntar a JDK 21.

---

## Convenciones

- Kotlin oficial (4 espacios, `ktlint`-friendly). Compose: composables `PascalCase`, estado arriba (state hoisting), nada de lógica de negocio en composables.
- **Una sola fuente de verdad**: Room para datos, DataStore para ajustes. La UI observa `StateFlow` desde ViewModels.
- `BlockEngine` se mantiene **puro** (sin dependencias de Android) para poder testearlo. Es lo más crítico de cubrir con tests.
- **Copy de la UI en español, sereno, en presente, nunca regañón.** El bloqueo es un pacto, no un castigo (ej.: "Disponible de nuevo a las 18:00", no "Acceso denegado por el sistema").
- Antes de añadir una dependencia pesada (Hilt, librerías de terceros), pregunta.
