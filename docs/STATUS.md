# LockDapp — Reporte de estado del proyecto

**Fecha:** 2026-06-30  
**Build:** `assembleDebug` ✅ — `test` ✅  
**Versión:** 1.0 (versionCode 1)  
**SDK:** minSdk 29 · compileSdk 37 · targetSdk 37

---

## Resumen ejecutivo

El proyecto tiene el núcleo funcional completo en código: datos, motor de bloqueo, runtime de detección y la pantalla de bloqueo. Hay un bug sin confirmar en la detección de apps en el dispositivo físico (bloqueo no dispara pese a permisos OK) que aún no se ha diagnosticado. La UI de horarios y apps —el único vector por el que el usuario puede configurar qué se bloquea y cuándo— no existe todavía; toda la configuración actual viene de datos de prueba hardcodeados.

---

## Estado por fase

| Fase | Descripción | Estado |
|---|---|---|
| 1 | Estructura base, modelos de dominio, sistema de temas, `BlockEngine` puro | ✅ Completa |
| 2 | Capa de datos: Room, DataStore, repositorios | ✅ Completa |
| 3 | Runtime de bloqueo: `AccessibilityService`, `AlarmManager`, `BootReceiver`, onboarding | ✅ Completa (bug abierto) |
| 4 | Overlay fullscreen: `BlockActivity` + `BlockScreen` (pantalla de bloqueo) | ✅ Completa |
| 5 | UI de horarios: lista, editor de `LockSchedule` | ❌ No iniciada |
| 6 | UI de apps y grupos: listado de instaladas, crear `AppGroup` | ❌ No iniciada |
| 7 | Dashboard / pantalla principal real | ❌ No iniciada |
| 8 | Ajustes: tema, válvula de escape, delay | ❌ No iniciada |
| 9 | Importar blocklist JSON (SAF) | ❌ No iniciada |

---

## Archivos implementados

### Dominio
| Archivo | Responsabilidad |
|---|---|
| `domain/model/AppGroup.kt` | Grupo de packages a bloquear |
| `domain/model/LockSchedule.kt` | Horario: grupos + ventanas + días + rango de fechas |
| `domain/model/TimeWindow.kt` | Franja horaria `startMinute..endMinute` |
| `domain/model/InstalledApp.kt` | Modelo de app instalada (para la UI futura) |
| `domain/model/AppTheme.kt` | Enum `MASTIL / DIQUE / LOCKD` |
| `domain/engine/BlockEngine.kt` | Lógica pura de evaluación; `isBlocked()` + `blockInfo()` |

### Datos
| Archivo | Responsabilidad |
|---|---|
| `data/local/AppDatabase.kt` | Room DB con `AppGroupEntity` + `LockScheduleEntity` |
| `data/local/AppGroupEntity.kt` / `LockScheduleEntity.kt` | Entidades Room |
| `data/local/AppGroupDao.kt` / `LockScheduleDao.kt` | DAOs |
| `data/local/LockConverters.kt` | `TypeConverter`s para `List<String>`, `Set<DayOfWeek>`, etc. |
| `data/prefs/SettingsRepository.kt` | DataStore: tema activo, escape on/off, delay, `isFirstRunDone` |
| `data/repository/ScheduleRepository.kt` | CRUD de horarios + grupos + `observeBlockEngine()` |
| `data/repository/AppRepository.kt` | Lista apps instaladas con icono lazy |

### Servicios / runtime
| Archivo | Responsabilidad |
|---|---|
| `service/BlockAccessibilityService.kt` | Núcleo: detecta app en primer plano, llama `showBlockScreen()` |
| `service/AlarmScheduler.kt` | Arma alarmas exactas en los bordes de cada ventana activa hoy |
| `service/AlarmReceiver.kt` | BroadcastReceiver de borde de ventana; valida y lanza bloqueo |
| `service/BootReceiver.kt` | `BOOT_COMPLETED` → re-arma alarmas |
| `service/EscapeVault.kt` | Registro en memoria de accesos temporales (5 min) |

### UI
| Archivo | Responsabilidad |
|---|---|
| `ui/theme/Color.kt` | Tres `ColorScheme` completos (Mástil, Dique, LockD) |
| `ui/theme/Type.kt` | Escala tipográfica (Space Grotesk / Inter / JetBrains Mono — placeholder) |
| `ui/theme/Theme.kt` | `LockAppTheme(theme)` envuelve `MaterialTheme` |
| `ui/theme/Shapes.kt` | Radios por tema |
| `ui/onboarding/OnboardingScreen.kt` | Pantalla de permisos iniciales; 4 tarjetas + botón "Continuar" |
| `ui/block/BlockScreen.kt` | Pantalla de bloqueo: mensaje + info de app + válvula de escape (countdown) |
| `ui/block/BlockActivity.kt` | Aloja `BlockScreen`; recibe extras del service; gestiona EscapeVault |
| `util/PermissionsHelper.kt` | Comprueba y abre ajustes de 4 permisos del sistema |
| `MainActivity.kt` | Routing onboarding ↔ placeholder "Servicio activo" |
| `LockdApplication.kt` | DI manual; seed de prueba 24/7 (YouTube + Instagram, TODO REMOVE) |

### Tests
| Archivo | Cobertura |
|---|---|
| `domain/engine/BlockEngineTest.kt` | Horario recurrente indefinido, por semanas, por mes, bloqueo 24/7, borde exacto de ventana |
| `data/local/LockConvertersTest.kt` | Serialización/deserialización de `TypeConverter`s |

---

## Stack técnico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin | 2.2.10 |
| Build | AGP | 9.2.1 |
| Generación código | KSP | 2.2.10-2.0.2 |
| UI | Jetpack Compose + Material 3 | BOM 2026.06.00 |
| Persistencia | Room | 2.8.4 |
| Preferencias | DataStore Preferences | 2.1.1 |
| Serialización | kotlinx.serialization JSON | 1.11.0 |
| Corrutinas | kotlinx.coroutines | 1.11.0 |

> **Restricción conocida:** KSP 2.2.20+ es incompatible con AGP 9.x en modo built-in Kotlin. Kotlin y KSP están fijados en versiones anteriores hasta que KSP publique una release compatible.

---

## Permisos declarados

| Permiso | Propósito | Cómo se concede |
|---|---|---|
| `QUERY_ALL_PACKAGES` | Listar apps instaladas | Automático (sideload) |
| `PACKAGE_USAGE_STATS` | Detectar app en primer plano (tick 12 s) | Manual en Ajustes → Acceso al uso |
| `SYSTEM_ALERT_WINDOW` | Overlay de bloqueo (fase futura) | Manual en Ajustes → Mostrar sobre otras apps |
| `USE_EXACT_ALARM` | Alarmas precisas en bordes de ventana | Pre-concedido (API 33+) |
| `RECEIVE_BOOT_COMPLETED` | Re-armar alarmas tras reinicio | Automático |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Mantener servicio vivo en segundo plano | Manual en Ajustes → Batería |
| `BIND_ACCESSIBILITY_SERVICE` | El sistema vincula el servicio de accesibilidad | Manual en Ajustes → Accesibilidad |

---

## Bug abierto: bloqueo no dispara en dispositivo

**Síntoma:** YouTube e Instagram se abren sin ser interceptados pese a que los permisos están concedidos y la pantalla "Servicio activo" es visible.

**Causas probables (sin diagnóstico confirmado):**

1. El seed no se insertó — `isFirstRunDone` podría estar en `true` desde una ejecución anterior. Verificar con Logcat o DB Browser.
2. El engine recibe listas vacías — `observeBlockEngine()` emite datos solo cuando Room tiene rows. Si el seed no escribió, el engine queda vacío permanentemente.
3. `event.packageName` no coincide — YouTube puede generar eventos de sistema (Google Play Services) antes de que aparezca el paquete real. Logcat mostraría el valor real.
4. `onServiceConnected()` no se llama — si el servicio no está realmente activo, nada funciona.

**Diagnóstico recomendado:** añadir `Log.d("LockDapp", "pkg=$pkg blocked=${engine.blockInfo(pkg, now)}")` en `onAccessibilityEvent` y filtrar por `LockDapp` en Logcat al abrir YouTube.

---

## Pendiente — próximas fases

### Inmediato: cerrar el bug de bloqueo
Antes de avanzar en UI conviene confirmar que el runtime detecta y bloquea correctamente. Una vez verificado, **eliminar el bloque `seedTestDataIfNeeded()` de `LockdApplication`**.

### Fase 5: UI de horarios (`ui/schedules/`)
- Lista de `LockSchedule` activos con estado on/off
- Editor: nombre, grupo objetivo, ventanas horarias, días de la semana, rango de fechas opcional
- Conectar con `ScheduleRepository` + `AlarmScheduler` al guardar

### Fase 6: UI de apps y grupos (`ui/apps/`)
- Listado de apps instaladas (iconos lazy vía `AppRepository`)
- Crear / editar `AppGroup` (nombre + selección de packages)
- Buscador de apps

### Fase 7: Dashboard
- Resumen de horarios activos ahora mismo
- Acceso rápido a horarios y grupos
- Indicador de estado del servicio

### Fase 8: Ajustes (`ui/settings/`)
- Selector de tema (Mástil / Dique / LockD) con cambio de icono/nombre de launcher
- Toggle válvula de escape + configuración de delay (por ahora 30 s hardcodeado)
- Excepción de batería

### Fase 9: Importar blocklist JSON
- SAF (`ACTION_OPEN_DOCUMENT`), lee JSON de package names
- Añade packages al grupo existente o crea uno nuevo
- Útil para restaurar configuración tras reinstalar

---

## Deuda técnica conocida

| Item | Prioridad | Nota |
|---|---|---|
| Eliminar seed `seedTestDataIfNeeded()` | Alta | Una vez verificado el bloqueo real |
| Fuentes reales (Space Grotesk / Inter / JetBrains Mono) | Media | `Type.kt` usa system fonts como placeholder |
| `BlockScreen` sin tematización completa | Media | Usa `MaterialTheme` genérico; falta aplicar `BlockSpec` por tema |
| `FLAG_ACTIVITY_CLEAR_TASK` en `BlockActivity.newIntent()` | Resuelta | Corregido a `FLAG_ACTIVITY_NEW_TASK` |
| Tests de `BlockEngine.blockInfo()` | Media | `isBlocked()` está cubierto; `blockInfo()` no |
| Cambio de icono/nombre de launcher por tema | Baja | Requiere `<activity-alias>` en el manifest + `PackageManager.setComponentEnabledSetting()` |
