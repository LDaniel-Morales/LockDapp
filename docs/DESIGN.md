# LockDapp — Documento de Diseño e Identidad

> App personal de **autocontrol (self-binding)** para Android. Bloquea el uso de aplicaciones y el tráfico de red según horarios por día, semana o mes. Uso exclusivo en el dispositivo de Daniel (sideload, sin Play Store).

---

## 1. Concepto e identidad

La app nace de una idea vieja y muy humana: **Ulises atándose al mástil** para resistir el canto de las sirenas. No es una herramienta de control parental ni un vigilante externo — eres *tú* quien decide atarte, de antemano y en frío, para protegerte de tu yo impulsivo del futuro.

Esto define el tono de toda la interfaz:

- **No regaña.** No hay "¡Estás perdiendo el tiempo!". El bloqueo es una decisión que ya tomaste; la app solo la cumple.
- **Es un pacto, no un castigo.** El momento del bloqueo se siente sereno y deliberado, no agresivo.
- **Es para una persona técnica.** Daniel es desarrollador y usa Linux (CachyOS). La estética puede permitirse guiños a terminal, daemons y precisión de ingeniería sin diluirlos para un público general.

**Principio rector de copy:** la app habla en segunda persona y en presente. "Bloqueado hasta las 18:00", no "Esta app ha sido bloqueada por el sistema".

---

## 2. Sistema de temas

La app permite cambiar de **tema completo** desde Ajustes. Un tema no es solo un color: cambia el **nombre**, el **icono del launcher**, la **paleta** y un **rasgo de personalidad** (radio de esquinas / acabado). Los tres comparten tipografía, layout y comportamiento — la identidad la cargan color, nombre e icono.

> Nota técnica para implementación: el nombre/icono *dentro* de la app es estado (`DataStore`). El nombre/icono del **launcher** se cambia con varios `activity-alias` en el manifest, activando uno con `PackageManager.setComponentEnabledSetting()`.

### 2.1 Mástil — *el ancla náutica*

| | |
|---|---|
| **Significado** | El mástil al que te atas. Sobrio, marítimo, atemporal. |
| **Personalidad** | Cálido y elegante. Esquinas suaves (radio 16dp). |
| **Icono** | Un mástil vertical con la cuerda de amarre tensa y un nudo. Línea fina en latón sobre azul medianoche. |

```
bg              #0B1426   azul medianoche
surface         #111E33
surfaceElevated #18283F
border          #24344D
textPrimary     #EAF0F7
textSecondary   #9DB0C7
textMuted       #5F7290
accent          #D4A24E   latón / bronce de barco
accentMuted     #8A6A33
blockActive     #E0653C   cobre encendido (estado de bloqueo)
success         #4FB286
```

### 2.2 Dique — *la barrera de ingeniería*

| | |
|---|---|
| **Significado** | Un dique que retiene el flujo. Encaja doble: contiene apps **y** tráfico de red. |
| **Personalidad** | Técnico y preciso. Esquinas marcadas (radio 8dp), sensación de plano de ingeniería. |
| **Icono** | Un muro de contención: barras horizontales apiladas reteniendo una ola. Cian sobre pizarra. |

```
bg              #0D1117   pizarra casi negra (familia GitHub dark)
surface         #161B22
surfaceElevated #1C232D
border          #2A323D
textPrimary     #E6EDF3
textSecondary   #9AA7B4
textMuted       #5C6773
accent          #2DD4BF   cian / teal de agua contenida
accentMuted     #1B7A6F
blockActive     #F4A93C   ámbar de advertencia (compuerta cerrada)
success         #3DD68C
```

### 2.3 LockD — *el daemon*

| | |
|---|---|
| **Significado** | "Lock Daniel" condensado, con sabor a daemon de Linux (`lockd`). Tu mundo CachyOS/Arch. |
| **Personalidad** | Negro real OLED + violeta neón. Esquinas medias (radio 12dp) con un sutil *glow* en el acento. |
| **Icono** | Un candado cuyo arco es un prompt de terminal `>_`, o un candado fundido con un cursor parpadeante. Púrpura sobre negro absoluto. |

```
bg              #0A0A0F   negro real (OLED)
surface         #14121C
surfaceElevated #1E1B2E
border          #2C2740
textPrimary     #ECE8F5
textSecondary   #A39DB8
textMuted       #6B6485
accent          #A855F7   violeta neón
accentMuted     #6B3FA0
blockActive     #F43F5E   rosa/rojo neón
success         #34D399
```

---

## 3. Principios de modo oscuro

Reglas que mantienen la interfaz elegante y no genérica, aplicables a los tres temas:

1. **Sin negro puro en superficies grandes** — excepto LockD, donde el negro absoluto (`#0A0A0F`) es una decisión estética OLED consciente. En Mástil y Dique el fondo es un casi-negro con tinte. El negro puro causa *smearing* en OLED al hacer scroll y aplana la profundidad.
2. **Elevación por luz, no por sombra.** En oscuro las sombras casi no se ven: las superficies elevadas son un punto *más claras* que el fondo (`surface` < `surfaceElevated`).
3. **Acento con cuentagotas.** El color de acento solo en lo que importa: estado activo, toggle encendido, acción principal, número de cuenta regresiva. Todo lo demás vive en grises. Esto es lo que separa "elegante" de "saturado".
4. **`blockActive` es el momento emocional.** Es el único color cálido/de alerta del sistema y solo aparece cuando algo está bloqueado *ahora mismo*. Verlo debe sentirse distinto.

---

## 4. Tipografía

Consistente en los tres temas (la identidad la carga el color, no la fuente):

| Rol | Fuente | Uso |
|---|---|---|
| **Display** | Space Grotesk | Títulos de pantalla, el reloj de cuenta regresiva, números grandes. Técnica y con carácter. |
| **Body** | Inter | Texto de UI, etiquetas, descripciones. Legibilísima en oscuro. |
| **Mono** | JetBrains Mono | Package names (`com.instagram.android`), horas de las ventanas (`09:00–18:00`), datos. Funcional **y** on-brand: refuerza el ADN de desarrollador/daemon. |

**Escala de tipo:** Display 28/22 · Title 18 · Body 15 · Caption 13 · Mono 13. Pesos: Display SemiBold (600), Body Regular/Medium, Mono Regular.

---

## 5. Mapa de pantallas

```
┌─ Onboarding / Permisos        (solo primer arranque)
│
├─ Dashboard / Inicio           ← pantalla raíz, centro emocional
│   ├─ Estado actual ("Todo libre" / "Bloqueando ahora")
│   ├─ Próximo bloqueo
│   └─ Accesos rápidos
│
├─ Horarios (Schedules)
│   ├─ Lista de horarios (con toggle on/off)
│   └─ Editor de horario  ← grupos · ventanas · días · rango de fechas
│
├─ Apps
│   ├─ Lista de apps instaladas (buscador + checkboxes)
│   └─ Grupos de apps ("Redes sociales", "Juegos")
│
├─ Red (firewall)               ← bloqueo de tráfico por app (fase posterior)
│
├─ Pantalla de bloqueo (overlay)  ← LA FIRMA de la app
│
└─ Ajustes
    ├─ Selector de tema (nombre + icono + colores)
    └─ Importar blocklist JSON   (feature final, opcional)
```

---

## 6. Especificación pantalla por pantalla

### 6.1 Onboarding / Permisos
Flujo de primer arranque. La app necesita permisos potentes y hay que pedirlos con honestidad, uno por uno, explicando *por qué*:
- **Accesibilidad** — "para saber qué app abres y poder bloquearla".
- **Acceso de uso** (Usage Access) — "para medir y reevaluar la app activa".
- **Superposición** (overlay) — "para mostrar la pantalla de bloqueo encima".
- **Excepción de batería** — "para que el bloqueo no muera en segundo plano".

Cada permiso es una tarjeta con su explicación y un botón "Conceder" que abre el ajuste del sistema. La pantalla no avanza hasta tenerlos. Tono directo, sin letra chica.

### 6.2 Dashboard / Inicio
El centro emocional. Dos estados:
- **Todo libre:** mensaje sereno en gris, próximo bloqueo programado abajo ("Redes sociales se bloquean hoy a las 09:00").
- **Bloqueando ahora:** aquí entra `blockActive`. Un bloque destacado: "Bloqueando 3 apps · termina en 2h 14m", con la cuenta regresiva en Display grande.

Debajo: lista compacta de horarios activos y un acceso a "Apps" y "Horarios".

### 6.3 Lista de horarios
Cada horario es una fila: nombre, resumen legible ("L–V · 09:00–18:00 · Redes sociales"), y un **toggle**. Si tiene rango de fechas, se ve un chip ("hasta 30 nov"). FAB para crear nuevo.

### 6.4 Editor de horario
El corazón de la lógica (modelo `LockSchedule`). De arriba a abajo:
1. **Nombre** del horario.
2. **Qué bloquea** — selector de grupos de apps (multi).
3. **Ventanas horarias** — una o más franjas (`09:00–12:00`, `14:00–18:00`). Botón "+ añadir franja".
4. **Días** — selector de días de la semana (L M X J V S D).
5. **Vigencia** — opcional: "Desde / Hasta" con date pickers. Vacío = indefinido. *Aquí viven los bloqueos por semanas o meses.*
6. Preview en vivo del resumen legible mientras editas.

> v1: las ventanas viven dentro de un mismo día (`start < end`); un bloqueo nocturno se parte en dos. Decisión consciente: cero bugs de medianoche.

### 6.5 Lista de apps instaladas
Lista con **buscador arriba** y filas con icono (carga perezosa), nombre y **checkbox**. Filtradas por `CATEGORY_LAUNCHER` para quitar paquetes de sistema. Debajo del nombre, el package en Mono y `textMuted`. Estado de selección = en la blocklist.

### 6.6 Grupos de apps
Gestión de grupos (`AppGroup`). Cada grupo: nombre editable + cuántas apps contiene. Entrar a un grupo abre la lista de apps para marcar miembros.

### 6.7 Pantalla de bloqueo (overlay) — **la firma**
El momento más importante de la app. Aparece a pantalla completa cuando abres una app bloqueada. **No es un "ACCESO DENEGADO" agresivo.** Es un recordatorio sereno de un pacto que hiciste:

- Fondo a tope del color del tema, con el icono/identidad del tema.
- Mensaje en presente y en calma: "Te ataste al mástil." / "El dique está cerrado." / "`lockd`: acceso bloqueado." (uno por tema).
- Debajo, el dato concreto: qué app, qué horario lo bloquea, y **hasta cuándo** ("Disponible de nuevo a las 18:00").
- Un solo botón claro: "Volver". Sin botón de "saltar" prominente.
- *Opcional (válvula de escape deliberada):* un enlace discreto "Necesito acceso ahora" que impone fricción (espera de 30s o resolver algo) en vez de hacerlo imposible — porque en tu propio dispositivo el bloqueo perfecto no existe, así que se diseña la fricción honestamente.

### 6.8 Ajustes — Selector de tema
Tres tarjetas grandes, una por tema (Mástil / Dique / LockD), cada una mostrando su **paleta en miniatura, su nombre y su icono**. Tocar una la previsualiza en vivo y la aplica. Aviso sutil de que el icono del launcher parpadeará un momento al cambiar.

### 6.9 Importar blocklist JSON (feature final)
Pantalla simple en Ajustes: botón "Importar blocklist…" que abre el selector de archivos (SAF), lee un JSON de package names y los añade a la lista. Para recuperar la configuración si reinstalas **LockDapp** misma.

---

## 7. Tokens de layout

```
spacing      4 · 8 · 12 · 16 · 24 · 32   (escala base 4)
radius       Mástil 16 · Dique 8 · LockD 12
border       1px, color = token `border`
elevación    por color: surface → surfaceElevated (sin sombra dura)
touch target mínimo 48dp
glow         solo LockD: acento con sombra difusa sutil del propio accent
```

**Stack de UI recomendado:** Jetpack Compose (declarativo y reactivo, similar a Vue: estado → UI). Theming con `MaterialTheme` + `ColorScheme` custom por tema; cambiar de tema en vivo es casi gratis.

---

## 8. Prompts para Claude Design (copiar y pegar)

> Pégalos en orden. El **prompt maestro** primero (establece identidad y tokens); luego cada pantalla. Itera por chat después de cada uno.

### 8.0 — Prompt maestro (pégalo primero)

```
Diseña la identidad visual de una app Android personal de autocontrol
("self-binding") llamada LockDapp, que bloquea apps y tráfico de red por
horarios. Concepto: Ulises atándose al mástil — el usuario se ata a sí mismo
en frío para resistir impulsos. Tono: sereno, deliberado, nunca regañón; es
un pacto, no un castigo. Usuario: desarrollador que usa Linux.

La app tiene un SISTEMA DE 3 TEMAS completos (cambian nombre, icono, paleta y
radio de esquinas). Todos son modo oscuro, elegante y minimalista:

TEMA "Mástil" (náutico, cálido, radio 16):
bg #0B1426 · surface #111E33 · elevated #18283F · border #24344D
text #EAF0F7 / #9DB0C7 / #5F7290 · accent #D4A24E (latón) · blockActive #E0653C

TEMA "Dique" (ingeniería, técnico, radio 8):
bg #0D1117 · surface #161B22 · elevated #1C232D · border #2A323D
text #E6EDF3 / #9AA7B4 / #5C6773 · accent #2DD4BF (teal) · blockActive #F4A93C

TEMA "LockD" (daemon Linux, negro OLED + violeta, radio 12, glow sutil):
bg #0A0A0F · surface #14121C · elevated #1E1B2E · border #2C2740
text #ECE8F5 / #A39DB8 / #6B6485 · accent #A855F7 (violeta) · blockActive #F43F5E

Tipografía: Space Grotesk (títulos/números), Inter (cuerpo), JetBrains Mono
(package names y horas). Reglas: nada de negro puro salvo en LockD; elevación
por luz no por sombra; el acento solo en lo que importa; blockActive solo
cuando algo está bloqueado ahora. Touch targets 48dp. Esquinas según el tema.

Empieza mostrándome una pantalla de muestra (un dashboard) en los 3 temas
lado a lado para validar la identidad antes de seguir.
```

### 8.1 — Dashboard

```
Diseña la pantalla de Inicio/Dashboard de LockDapp en el tema LockD. Muestra
el estado "Bloqueando ahora": un bloque destacado con blockActive que diga
"Bloqueando 3 apps" y una cuenta regresiva grande en Space Grotesk
("termina en 2h 14m"). Debajo, lista compacta de horarios activos con su
resumen legible, y accesos a "Apps" y "Horarios". Sereno, no alarmante.
Dame también la variante del estado "Todo libre" (sin blockActive, en grises,
mostrando el próximo bloqueo programado).
```

### 8.2 — Editor de horario

```
Diseña la pantalla "Editor de horario" de LockDapp en el tema Dique.
Secciones verticales: 1) Nombre del horario. 2) "Qué bloquea": chips de grupos
de apps seleccionables. 3) "Ventanas horarias": franjas tipo 09:00–18:00 en
JetBrains Mono, con botón "+ añadir franja". 4) Selector de días de la semana
(L M X J V S D) como toggles. 5) "Vigencia" opcional con Desde/Hasta (date
pickers) — vacío = indefinido. 6) Un preview en vivo del resumen legible
("L–V · 09:00–18:00 · Redes sociales"). Estética de plano de ingeniería,
esquinas marcadas.
```

### 8.3 — Lista de apps instaladas

```
Diseña la pantalla "Apps" de LockDapp en el tema Mástil. Buscador fijo arriba.
Lista de filas: icono de app a la izquierda, nombre en Inter, el package name
debajo en JetBrains Mono y color muted, y un checkbox a la derecha (marcado =
en la blocklist). Las filas marcadas se distinguen sutilmente con el acento
latón. Limpio, escaneable, esquinas suaves.
```

### 8.4 — Pantalla de bloqueo (la firma)

```
Diseña la pantalla de bloqueo a pantalla completa de LockDapp — la que aparece
al abrir una app bloqueada. Hazla en los 3 temas. NO es agresiva: es un
recordatorio sereno de un pacto que el usuario hizo consigo mismo. Fondo a tope
del color del tema con el icono/identidad del tema. Mensaje en presente y en
calma (Mástil: "Te ataste al mástil." · Dique: "El dique está cerrado." ·
LockD: "lockd: acceso bloqueado."). Debajo: qué app, qué horario la bloquea, y
hasta cuándo ("Disponible de nuevo a las 18:00"). Un solo botón "Volver".
Un enlace muy discreto "Necesito acceso ahora" abajo. Este es el momento
emocional de la app: que se sienta intencional y bonito.
```

### 8.5 — Selector de tema (Ajustes)

```
Diseña la pantalla de Ajustes > Temas de LockDapp. Tres tarjetas grandes
apiladas, una por tema (Mástil, Dique, LockD). Cada tarjeta muestra: el nombre
del tema, su icono de app, y una miniatura de su paleta (3-4 swatches de
color). La tarjeta del tema activo tiene un borde/realce con su propio acento.
Muestra la pantalla renderizada en el tema actualmente activo (elige LockD).
Incluye una nota sutil: "El icono en tu launcher cambiará en unos segundos."
```

### 8.6 — Iconos de app (los 3)

```
Diseña 3 iconos de app Android (adaptive icon, 1024x1024, estética premium,
plana con un toque de profundidad) para los 3 temas de LockDapp:
- "Mástil": un mástil vertical con cuerda de amarre tensa y un nudo, línea fina
  en latón (#D4A24E) sobre azul medianoche (#0B1426).
- "Dique": un muro de contención, barras horizontales reteniendo una ola,
  en teal (#2DD4BF) sobre pizarra (#0D1117).
- "LockD": un candado cuyo arco/cuerpo integra un prompt de terminal ">_" o un
  cursor parpadeante, en violeta (#A855F7) sobre negro (#0A0A0F).
Minimalistas, reconocibles a tamaño pequeño, coherentes entre sí como familia.
```

---

## 9. Decisiones pendientes (para no olvidar)

- **Nombre final / o multi-tema permanente:** los 3 nombres conviven como temas. Decidir si hay un nombre "por defecto" para el primer arranque.
- **Ventanas que cruzan medianoche:** v1 las parte en dos reglas (decidido).
- **Válvula de escape en la pantalla de bloqueo:** ¿sin escape, espera de 30s, o reto? (afecta el tono).
- **Firewall de red (Dique literal):** fase posterior, basado en `VpnService` local sin root (referencia: NetGuard).
- **Import JSON:** feature final.
