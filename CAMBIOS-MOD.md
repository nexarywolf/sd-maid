# SD Maid SE — Edición modificada (v2.2.2-rc0)

Basado en el código abierto de SD Maid SE (GPL-3.0) de d4rken: https://github.com/d4rken-org/sdmaid-se
Esta es una **modificación local no oficial**, sin ánimo de redistribución comercial. Respeta el proyecto original y, si te resulta útil, considera apoyar al desarrollador original.

## Cambios incluidos

### 1. Todo desbloqueado (Pro)
- Nueva clase `UpgradeRepoUnlocked` (`app/src/main/java/eu/darken/sdmse/common/upgrade/core/`): todas las funciones Pro activas de fábrica.
- Eliminadas las compras en-app, la verificación de licencia y las pantallas de "Mejorar a Pro".
- Desbloqueado automáticamente: temas premium (Material You, contraste alto), estadísticas/histórico, programador, acciones por lote en AppControl, sesiones ilimitadas en Swiper, deduplicador, filtros personalizados, etc.
- Sin anuncios: la app original ya no contenía anuncios de terceros; los "anuncios" eran en realidad las promo de upgrade, que fueron eliminadas (tarjeta del dashboard, fila de Ajustes, etc.).

### 2. Shizuku como motor principal
- `AdbSettings.useShizuku` activado por defecto.
- Orden de escalada invertido en `LocalGateway` y `EscalatingWalker`: ahora es NORMAL → **Shizuku (ADB)** → ROOT. Con Shizuku autorizado, el escaneo y el borrado usan privilegios de shell (accede a `Android/data`, carpetas del sistema, etc.) sin root.
- Conexión: Ajustes → "Configuración" (Setup) → tarjeta Shizuku, o deja que la app te guíe en el primer escaneo. Necesitas la app Shizuku instalada y ejecutada (por ADB inalámbrico o por root).

### 3. Nuevos descubrimientos del escáner (System Cleaner)
- **Carpetas de papelera** (`TrashBinsFilter`): `.Trash`, `.Trash-0…999`, `.Trashes`, `.trash` (MIUI), `.recycle*`, `$RECYCLE.BIN`, con todo su contenido.
- **Archivos ocultos** (`HiddenFilesFilter`): todo archivo/carpeta cuyo nombre empieza por punto (.), excluyendo funcionales como `.nomedia`, `.placeholder`, y las propias papeleras (las maneja el filtro de papeleras).
- El filtro existente "Trashed files" (`.trashed-*`) ahora viene activado por defecto.
- Interruptores para cada filtro en: System Cleaner → Ajustes.

### 4. Controles, animaciones y loaders
- La UI ya era Compose Material 3 con shimmer/loaders animados, selección múltiple y borrado por lote en todos los resultados; con el desbloqueo Pro, todas las acciones por lote quedan activas sin límites.
- Se mantuvieron y liberaron las transiciones y componentes de progreso existentes.

### 5. Idioma
- Multi-idioma original intacto (los dos filtros nuevos incluyen textos en inglés y español).

## Compilar tú mismo
1. Abre la carpeta en **Android Studio** (JDK 17+).
2. Elige el build variant `fossDebug` o `fossRelease` y ejecuta.
   - `release` firma con la clave debug si no configuras una propia (útil para pruebas).
3. APK listo en `app/build/outputs/apk/foss/debug/` o `.../foss/release/`.

Nota: `gradle.properties` incluye `-Xmx2560m`/`kotlin.daemon.jvmargs` ajustados para máquinas con poca RAM; puedes restaurar `-Xmx4096m` si tu equipo tiene memoria de sobra.

## Instalar el APK
- `ajustes → instalar app desconocida` y abre el APK. Si tenías SD Maid SE original instalada, el paquete es el mismo `eu.darken.sdmse`: **desinstálala primero** (la firma es distinta y Android bloqueará la instalación encima).
