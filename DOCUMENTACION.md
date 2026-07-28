# LG Extended - Documentación Completa

## Tabla de Contenidos

1. [Resumen del Proyecto](#1-resumen-del-proyecto)
2. [Arquitectura](#2-arquitectura)
3. [Características y Hooks](#3-características-y-hooks)
4. [Componentes UI](#4-componentes-ui)
5. [Gestión de Datos](#5-gestión-de-datos)
6. [Integración con Root](#6-integración-con-root)
7. [Configuración del Build](#7-configuración-del-build)
8. [Estructura de Archivos](#8-estructura-de-archivos)
9. [Dependencias](#9-dependencias)
10. [Guía de Instalación](#10-guía-de-instalación)
11. [Troubleshooting](#11-troubleshooting)

---

## 1. Resumen del Proyecto

**LG Extended** es un módulo Xposed diseñado específicamente para dispositivos LG (principalmente LG V60) que permite personalizar y modificar various aspectos del sistema sin necesidad de modificar el firmware. El módulo interactúa con múltiples procesos del sistema para aplicar cambios en tiempo real.

### Información General

| Propiedad | Valor |
|-----------|-------|
| **Nombre** | LG Extended |
| **Paquete** | `com.zxerox.lg_extended` |
| **Versión** | 1.0 (versionCode: 1) |
| **Autor** | ZxeroX |
| **SDK Mínimo** | Android 10 (API 29) |
| **SDK Objetivo** | Android 15 (API 35) |
| **Framework** | Xposed API 82+ |
| **Requisitos** | LSPosed o similar + Root (Magisk/KernelSU/APatch) |

### Descripción

LG Extended proporciona una suite de mods para dispositivos LG que incluye:

- **Personalización del icono de batería** con múltiples estilos (iOS 26, iOS 17, OneUI 8, OneUI 9)
- **Cambio de DPI por aplicación** para ajustar la densidad de pantalla individualmente
- **Estilo iOS para recientes** que modifica el diseño del multitasking
- **Bypass de FLAG_SECURE** para permitir capturas de pantalla en apps restringidas
- **Personalización de iconos en Ajustes** con iconos estilo OneUI
- **Tarjeta de perfil personalizada** en la pantalla principal de Ajustes

---

## 2. Arquitectura

### Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────────┐
│                        LG Extended App                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      MainHook.java                          │   │
│  │         (Punto de entrada Xposed - IXposedHookLoadPackage)   │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│          ┌───────────────────┼───────────────────┐                  │
│          │                   │                   │                  │
│          ▼                   ▼                   ▼                  │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐          │
│  │  BatteryHook  │  │   DpiHook     │  │ RecentsHook   │          │
│  │  (SystemUI)   │  │  (Todas las   │  │ (Launcher3)   │          │
│  │               │  │   apps)       │  │               │          │
│  └───────────────┘  └───────────────┘  └───────────────┘          │
│          │                   │                   │                  │
│          │           ┌───────────────┐           │                  │
│          │           │FlagSecureHook │           │                  │
│          │           │   (android)   │           │                  │
│          │           └───────────────┘           │                  │
│          │                   │                   │                  │
│          │           ┌───────────────┐           │                  │
│          │           │ SettingsHook  │           │                  │
│          │           │(Ajustes)      │           │                  │
│          │           └───────────────┘           │                  │
│          │                                       │                  │
│  ┌───────┴───────────────────────────────────────┴───────────┐    │
│  │                     ModPrefs (ContentProvider)             │    │
│  │              Almacenamiento centralizado de prefs          │    │
│  └───────────────────────────────────────────────────────────┘    │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────┐    │
│  │                    UI Layer (Activities)                   │    │
│  │  MainActivity | BatteryStyleActivity | DpiActivity | ...  │    │
│  └───────────────────────────────────────────────────────────┘    │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────┐    │
│  │                  Root Utils (libsu)                        │    │
│  │            DeviceInfoProvider | RootUtils                   │    │
│  └───────────────────────────────────────────────────────────┘    │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Ciclo de Vida del Hook

1. **Inicialización**: `MainHook.initZygote()` almacena la ruta del módulo
2. **Carga**: `MainHook.handleLoadPackage()` detecta el paquete objetivo
3. **Registro**: Cada hook se registra para su paquete específico
4. **Intercepción**: Los hooks modifican el comportamiento en tiempo real
5. **Persistencia**: Las preferencias se almacenan vía `ModPrefs` ContentProvider

### Patrón de Comunicación

El módulo utiliza un **ContentProvider** (`ModPrefs`) como sistema de comunicación entre:
- La app de configuración (UI)
- Los hooks activos en diferentes procesos del sistema

```
┌──────────────────┐     ContentResolver      ┌──────────────────┐
│  MainActivity    │ ───────────────────────▶ │    ModPrefs      │
│  (UI Process)    │ ◀─────────────────────── │ (Module Process) │
└──────────────────┘     Query/Insert         └──────────────────┘
                                │
                                ▼
┌──────────────────┐     ContentObserver      ┌──────────────────┐
│  BatteryHook     │ ◀─────────────────────── │    ModPrefs      │
│  (SystemUI)      │     onChange()           │                  │
└──────────────────┘                          └──────────────────┘
```

---

## 3. Características y Hooks

### 3.1 BatteryHook - Personalización del Icono de Batería

**Paquete objetivo**: `com.android.systemui` / `com.lge.systemui`

**Descripción**: Reemplaza el icono de batería nativo de LG por un icono personalizado con múltiples estilos y colores configurables.

#### Estilos Disponibles

| Estilo | Descripción |
|--------|-------------|
| `ONEUI_8` | Estilo pill redondeado (default) |
| `ONEUI_9` | Estilo con círculo de carga |
| `IOS_26` | Estilo iOS con bolt integrado |
| `IOS_17` | Estilo iOS con borde y relleno |

#### Estados y Colores

| Estado | Color Fondo Default | Color Texto Default |
|--------|---------------------|---------------------|
| Normal | `#1C1C1E` | Blanco |
| Cargando | `#34C759` | Blanco |
| Batería Baja (≤20%) | `#FF3B30` | Blanco |

#### Implementación Técnica

```java
// BatteryHook.java - Clave del hook
XposedHelpers.findAndHookMethod(
    "com.lge.systemui.widget.LGBatteryMeterView",
    lpparam.classLoader,
    "onAttachedToWindow",
    new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) {
            // 1. Ocultar vista original
            original.setVisibility(View.GONE);
            
            // 2. Crear BatteryIconView personalizado
            BatteryIconView nueva = new BatteryIconView(context);
            
            // 3. Insertar en el padre
            padre.addView(nueva, padre.indexOfChild(original), params);
            
            // 4. Registrar observer para cambios en tiempo real
            context.getContentResolver().registerContentObserver(
                Uri.parse("content://com.zxerox.lg_extended.prefs/prefs"),
                true, observer
            );
        }
    }
);
```

#### Preferencias Almacenadas

| Key | Tipo | Descripción |
|-----|------|-------------|
| `battery_style` | String | Estilo seleccionado (ONEUI_8, ONEUI_9, IOS_26, IOS_17) |
| `battery_color_fondo` | int | Color de fondo normal |
| `battery_color_texto` | int | Color de texto normal |
| `battery_color_fondo_cargando` | int | Color de fondo cargando |
| `battery_color_texto_cargando` | int | Color de texto cargando |
| `battery_color_fondo_bajo` | int | Color de fondo batería baja |
| `battery_color_texto_bajo` | int | Color de texto batería baja |

---

### 3.2 DpiHook - Cambio de DPI por Aplicación

**Paquete objetivo**: Todas las aplicaciones (excepto la propia LG Extended)

**Descripción**: Permite modificar la densidad de pantalla (DPI) de cada aplicación individualmente, útil para ajustar el tamaño de la interfaz.

#### Implementación Técnica

```java
// DpiHook.java
XposedHelpers.findAndHookMethod(
    "android.content.res.ResourcesImpl",
    lpparam.classLoader,
    "updateConfiguration",
    Configuration.class,
    DisplayMetrics.class,
    "android.content.res.CompatibilityInfo",
    new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            // Leer DPI guardado para esta app
            if (dpiCache <= 0) return;
            
            // Modificar Configuration
            config.densityDpi = dpiCache;
            
            // Modificar DisplayMetrics
            metrics.densityDpi = dpiCache;
            metrics.density = dpiCache * 0.00625f;
        }
    }
);
```

#### Flujo de Datos

```
┌──────────────────┐
│  DpiActivity     │
│  (Seleccionar    │
│   DPI por app)   │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  ModPrefs        │
│  (Almacenar      │
│   paquete: dpi)  │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  DpiHook         │
│  (Aplicar en     │
│   cada app)      │
└──────────────────┘
```

#### Preferencias Almacenadas

| Key | Tipo | Descripción |
|-----|------|-------------|
| `{package_name}` | int | DPI específico para esa app (0 = default) |

---

### 3.3 RecentsHook - Estilo iOS para Multitasking

**Paquete objetivo**: `com.lge.launcher3` / `com.android.launcher3`

**Descripción**: Modifica la vista de recientes para implementar un estilo similar al multitasking de iOS con efectos de pila y escala.

#### Características

- **Efecto de pila**: Las apps se apilan con efecto de profundidad
- **Escalamiento progresivo**: Las apps más alejadas del centro son más pequeñas
- **Transparencia de headers**: Los títulos se desvanecen al alejarse
- **Animación suave**: Transiciones fluidas al hacer scroll

#### Implementación Técnica

```java
// RecentsHook.java - Múltiples hooks
hookSilently(classLoader, "TaskView", "setFullscreenProgress", ...);
hookSilently(classLoader, "TaskView", "setDimAlpha", ...);
hookSilently(classLoader, "TaskView", "onFinishInflate", ...);
hookSilently(classLoader, "RecentsView", "updateStackLayout", ...);
hookSilently(classLoader, "RecentsView", "updateStackProperties", scrollHook);
hookSilently(classLoader, "RecentsView", "updateCurveProperties", scrollHook);
```

#### Parámetros de Diseño

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| `STACK_GAP` | 200f | Distancia entre apps en pila |
| `stackGapAhead` | 70% del spacing | Distancia hacia adelante |
| Scale inicial | 1.0 - 0.96 | Escala para primeras 4 apps |
| Scale mínimo | 0.60 | Escala mínima para apps lejanas |

#### Preferencias Almacenadas

| Key | Tipo | Descripción |
|-----|------|-------------|
| `recents_enabled` | boolean | Habilitar estilo iOS en recientes |

---

### 3.4 FlagSecureHook - Bypass de Restricciones

**Paquete objetivo**: `android` (proceso del sistema)

**Descripción**: Desactiva FLAG_SECURE que impide capturas de pantalla en aplicaciones que lo implementan (apps bancarias, streaming, etc.).

#### Implementación Técnica

```java
// FlagSecureHook.java
Class<?> windowStateClass = XposedHelpers.findClass(
    "com.android.server.wm.WindowState",
    lpparam.classLoader
);

XposedHelpers.findAndHookMethod(
    windowStateClass,
    "isSecureLocked",
    new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            prefs.reload();
            boolean bypassActivo = prefs.getBoolean("bypass_flag_secure", true);
            if (bypassActivo) {
                param.setResult(false); // Siempre retorna false
            }
        }
    }
);
```

#### Preferencias Almacenadas

| Key | Tipo | Descripción |
|-----|------|-------------|
| `bypass_flag_secure` | boolean | Habilitar bypass (default: true) |

---

### 3.5 SettingsHook - Personalización de Ajustes

**Paquete objetivo**: `com.android.settings`

**Descripción**: Realiza modificaciones extensas a la app de Ajustes de Android incluyendo:

1. **Tarjeta de perfil personalizada** en la pantalla principal
2. **Reemplazo de iconos** con iconos estilo OneUI
3. **Eliminación de divisores** para un look más limpio
4. **Bloqueo de tinte de iconos** para mantener colores originales

#### Tarjeta de Perfil

```java
// SettingsHook.java - Perfil personalizado
View customCard = buildCustomCardView(context, name, phrase, base64Avatar);

// Crear LayoutPreference
Object pref = XposedHelpers.newInstance(layoutPrefClass,
    new Class[]{Context.class, View.class}, context, customCard);

XposedHelpers.callMethod(pref, "setKey", CARD_KEY);
XposedHelpers.callMethod(pref, "setOrder", -999); // Siempre arriba
XposedHelpers.callMethod(pref, "setSelectable", false);
XposedHelpers.callMethod(screen, "addPreference", pref);
```

#### Mapeo de Iconos

| Key de Preferencia | Icono |
|-------------------|-------|
| `top_level_network` | `ic_red_e_internet` |
| `top_level_connected_devices` | `ic_bluetooth` |
| `top_level_sound` | `ic_sonido` |
| `top_level_notification` | `ic_notificaciones` |
| `top_level_display` | `ic_pantalla` |
| `top_level_theme` | `ic_fondo_pantalla_y_tema` |
| `top_level_security` | `ic_pantalla_de_bloqueo_y_seguridad` |
| `top_level_privacy` | `ic_privacidad` |
| `top_level_location` | `ic_ubicacion` |
| `top_level_useful_features` | `ic_extensiones` |
| `top_level_apps_and_notifs` | `ic_aplicaciones` |
| `top_level_digital_wellbeing` | `ic_bienestar_digital` |
| `top_level_battery` | `ic_bateria` |
| `top_level_storage` | `ic_almacenamiento` |
| `top_level_emergency` | `ic_seguridad_y_emergencia` |
| `top_level_accounts` | `ic_cuentas` |
| `top_level_system` | `ic_sistema` |
| `top_level_accessibility` | `ic_accesibilidad` |

#### Eliminación de Divisores

```java
// Bloquear setDivider y setDividerHeight
XposedBridge.hookAllMethods(prefFragmentClass, "setDivider", 
    XC_MethodReplacement.DO_NOTHING);
XposedBridge.hookAllMethods(prefFragmentClass, "setDividerHeight", 
    new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            param.args[0] = 0;
        }
    });

// Bloquear addItemDecoration en RecyclerView
XposedBridge.hookAllMethods(recyclerViewClass, "addItemDecoration", 
    new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            Object decor = param.args[0];
            if (decor != null && className.contains("divider")) {
                param.setResult(null);
            }
        }
    });

// Bloquear HIGHelper.isShowDivider
XposedBridge.hookAllMethods(lgHigHelperClass, "isShowDivider", 
    XC_MethodReplacement.returnConstant(false));
```

#### Preferencias Almacenadas

| Key | Tipo | Descripción |
|-----|------|-------------|
| `hook_settings_icons` | boolean | Reemplazar iconos de ajustes |
| `profile_name` | String | Nombre del perfil |
| `profile_phrase` | String | Frase del perfil |
| `profile_avatar_base64` | String | Avatar en Base64 |

---

## 4. Componentes UI

### 4.1 MainActivity - Pantalla Principal

**Descripción**: Activity principal con navegación por tabs (bottom navigation).

#### Tabs

| Tab | Layout | Función |
|-----|--------|---------|
| Inicio | `tab_inicio.xml` | Estado del módulo, info del dispositivo |
| Hooks | `tab_hooks.xml` | Gestión de hooks activos |
| Logs | `tab_logs.xml` | Visualización de logs del módulo |
| Ajustes | `tab_settings.xml` | Configuración (próximamente) |

#### Funcionalidades del Tab Inicio

- **Estado de LSPosed**: Verifica si hay hooks activos
- **Estado de Root**: Detecta Magisk/KernelSU/APatch
- **Info del dispositivo**: Modelo, versión Android, kernel, arquitectura
- **Botón reiniciar SystemUI**: Reinicia SystemUI para aplicar cambios

### 4.2 BatteryStyleActivity - Selector de Estilo de Batería

**Descripción**: Permite seleccionar el estilo del icono de batería y personalizar colores.

#### Estilos con Preview en Tiempo Real

```
┌─────────────────────────────────────────────────┐
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  │
│  │ IOS 26  │  │ IOS 17  │  │ OneUI 9 │  │ OneUI 8 │  │
│  │  [75]   │  │  [75]   │  │  [75]   │  │  [75]   │  │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘  │
│                                                         │
│  [Personalizar Colores]  ← BottomSheet con 6 opciones  │
└─────────────────────────────────────────────────────────┘
```

#### Selector de Colores (BottomSheet)

| Estado | Opciones |
|--------|----------|
| Normal | Fondo Normal, Texto Normal |
| Cargando | Fondo Cargando, Texto Cargando |
| Batería Baja | Fondo Batería Baja, Texto Batería Baja |

### 4.3 DpiActivity - Selector de DPI

**Descripción**: Lista de apps instaladas con opción de cambiar DPI individualmente.

#### Características

- **Lista de apps**: Muestra todas las apps no-sistema
- **DPI actual**: Muestra el DPI configurado o "Default"
- **Dialog de edición**: Input numérico para nuevo DPI
- **Reinicio automático**: Fuerza cierre de la app para aplicar cambios

### 4.4 BypassActivity - Bypass de Seguridad

**Descripción**: Switch para habilitar/deshabilitar el bypass de FLAG_SECURE.

### 4.5 CustomizeSettingsActivity - Personalización de Ajustes

**Descripción**: Editor de perfil para la tarjeta personalizada en Ajustes.

#### Características

- **Preview en tiempo real**: Muestra cambios mientras se editan
- **Selector de avatar**: Permite elegir imagen de galería
- **Avatar por defecto**: Genera inicial del nombre con fondo circular
- **Límite de tamaño**: Avatar redimensionado a 256x256px máximo

### 4.6 BatteryIconView - Vista Personalizada de Batería

**Descripción**: Componente de vista personalizada que dibuja el icono de batería.

```java
public class BatteryIconView extends View {
    public enum Estilo {
        IOS_26,
        ONEUI_9,
        ONEUI_8,
        IOS_17
    }
    
    // Métodos principales
    public void actualizarEstado(int nivel, boolean cargando);
    public void setEstilo(Estilo nuevoEstilo);
    public void setColoresNormal(int colorFondo, int colorTexto);
    public void setColoresCargando(int colorFondo, int colorTexto);
    public void setColoresBateriaBaja(int colorFondo, int colorTexto);
}
```

---

## 5. Gestión de Datos

### 5.1 ModPrefs - ContentProvider Centralizado

**Autoridad**: `com.zxerox.lg_extended.prefs`
**URI**: `content://com.zxerox.lg_extended.prefs/prefs`

#### Operaciones

| Operación | Método | Descripción |
|-----------|--------|-------------|
| Leer | `query()` | Lee una preferencia por key |
| Escribir | `insert()` | Escribe o actualiza una preferencia |

#### Formato de ContentValues

```java
ContentValues values = new ContentValues();
values.put("key", "nombre_preferencia");
values.put("type", "string|int|boolean");
values.put("value", "valor");
contentResolver.insert(PREFS_URI, values);
```

#### Lectura de Preferencias

```java
Cursor c = contentResolver.query(
    PREFS_URI,
    new String[]{"key_name"},    // projection
    "type",                       // selection (tipo de dato)
    new String[]{"default_value"}, // selectionArgs (valor por defecto)
    null
);
if (c != null && c.moveToFirst()) {
    String value = c.getString(0);
    c.close();
}
```

### 5.2 Sistema de Logs

**Clase**: `LogWriter`

#### Características

- **Almacenamiento**: En SharedPreferences vía ModPrefs
- **Formato**: `YYYY-MM-DD HH:mm:ss | NIVEL | mensaje`
- **Niveles**: `OK`, `ERR`, `INFO`
- **Límite**: 200 entradas máximo (rotación automática)

#### Estructura de LogEntry

```java
public static class LogEntry {
    public String timestamp;
    public String level;
    public String message;
}
```

#### Uso en Hooks

```java
// En BatteryHook.java
LogWriter.write(ctx, "OK", "BatteryHook", packageName, true);

// Output: "2024-01-15 10:30:45 | OK | BatteryHook applied in com.android.systemui"
```

### 5.3 Persistencia de Datos

| Dato | Ubicación | Formato |
|------|-----------|---------|
| Preferencias | `shared_prefs/lg_extended_prefs.xml` | XML SharedPreferences |
| Logs | Misma ubicación (via ModPrefs) | String con newlines |
| Avatar | Misma ubicación | Base64 encoded string |

---

## 6. Integración con Root

### 6.1 Dependencia libsu

```gradle
implementation 'com.github.topjohnwu.libsu:core:5.2.2'
```

### 6.2 DeviceInfoProvider

**Descripción**: Obtiene información del dispositivo usando comandos root.

#### Datos Obtenidos

| Dato | Fuente |
|------|--------|
| Modelo | `Build.MANUFACTURER + " " + Build.MODEL` |
| Versión Android | `Build.VERSION.RELEASE` |
| Build Number | `Build.DISPLAY` |
| Arquitectura | `Build.SUPPORTED_ABIS[0]` |
| Versión Kernel | `uname -r` (via root) |
| Root Manager | Detección automática |

#### Detección de Root Manager

```java
// Prioridad de detección:
1. `su -v` → busca "magisk", "kernelsu", "apatch"
2. `/data/adb/ksu` → KernelSU
3. `/data/adb/ap` → APatch
4. `/data/adb/magisk` → Magisk
5. `su --version` → Root genérico
```

### 6.3 RootUtils

```java
// Verificar root
RootUtils.tieneRoot(); // boolean

// Reiniciar SystemUI
RootUtils.reiniciarSystemUI(() -> {
    // Callback después del reinicio
});
// Equivale a: killall com.android.systemui
```

### 6.4 Uso de Root en la App

| Función | Comando Root |
|---------|--------------|
| Reiniciar SystemUI | `killall com.android.systemui` |
| Forzar cierre de app | `am force-stop {package}` |
| Verificar root | `Shell.getShell().isRoot()` |
| Info kernel | `uname -r` |

---

## 7. Configuración del Build

### 7.1 Archivos de Build

```
LG_Extended/
├── build.gradle                    # Build script raíz
├── app/
│   └── build.gradle               # Build script del módulo
├── settings.gradle                 # Configuración de dependencias
├── gradle.properties               # Propiedades de Gradle
└── gradle/
    └── libs.versions.toml          # Catálogo de versiones
```

### 7.2 Configuración del Módulo

```gradle
// app/build.gradle
android {
    namespace 'com.zxerox.lg_extended'
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId "com.zxerox.lg_extended"
        minSdk 29
        targetSdk 36
        versionCode 1
        versionName "1.0"
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
}
```

### 7.3 Dependencias Principales

```gradle
dependencies {
    // Xposed API
    compileOnly 'de.robv.android.xposed:api:82'
    
    // Root (libsu)
    implementation 'com.github.topjohnwu.libsu:core:5.2.2'
    
    // Color Picker
    implementation 'com.github.skydoves:colorpickerview:2.3.0'
    
    // AndroidX
    implementation libs.activity.ktx
    implementation libs.appcompat
    implementation libs.constraintlayout
    implementation libs.material
}
```

### 7.4 Configuración Xposed

```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="xposedmodule"
    android:value="true" />
<meta-data
    android:name="xposedminversion"
    android:value="82" />
<meta-data
    android:name="xposeddescription"
    android:value="LG Extended - Suite de mods para LG V60" />
```

```properties
# xposed_init
com.zxerox.lg_extended.MainHook
```

### 7.5 Repositorios

```gradle
// settings.gradle
repositories {
    google()
    mavenCentral()
    maven { url 'https://api.xposed.info/' }
    maven { url 'https://jitpack.io' }
}
```

---

## 8. Estructura de Archivos

### 8.1 Árbol del Proyecto

```
LG_Extended/
├── app/
│   ├── build.gradle
│   ├── libs/                          # Xposed API jar
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── assets/
│           │   └── xposed_init        # Entry point para Xposed
│           ├── java/
│           │   └── com/zxerox/lg_extended/
│           │       ├── MainHook.java           # Entry point principal
│           │       ├── hooks/
│           │       │   ├── BatteryHook.java    # Icono de batería
│           │       │   ├── DpiHook.java        # DPI por app
│           │       │   ├── FlagSecureHook.java # Bypass FLAG_SECURE
│           │       │   ├── RecentsHook.java    # Estilo iOS recientes
│           │       │   └── SettingsHook.java   # Personalización Ajustes
│           │       ├── log/
│           │       │   ├── LogAdapter.java     # Adapter RecyclerView
│           │       │   └── LogWriter.java      # Escritura de logs
│           │       ├── prefs/
│           │       │   └── ModPrefs.java       # ContentProvider
│           │       ├── root/
│           │       │   ├── DeviceInfoProvider.java  # Info del dispositivo
│           │       │   └── RootUtils.java      # Utilidades root
│           │       ├── ui/
│           │       │   ├── AppAdapter.java     # Adapter para lista de apps
│           │       │   ├── BatteryStyleActivity.java  # Selector estilo batería
│           │       │   ├── BypassActivity.java # Toggle bypass
│           │       │   ├── CustomizeSettingsActivity.java  # Editor perfil
│           │       │   ├── DpiActivity.java    # Selector DPI
│           │       │   ├── IosStyleActivity.java # Toggle estilo iOS
│           │       │   └── MainActivity.java   # Activity principal
│           │       └── views/
│           │           └── BatteryIconView.java # Vista personalizada batería
│           ├── keepRules/
│           │   └── rules.keep                 # ProGuard/R8 rules
│           └── res/
│               ├── drawable/                   # Iconos y drawables
│               ├── layout/                     # Layouts de activities
│               ├── mipmap-*/                   # Iconos de launcher
│               ├── values/
│               │   ├── colors.xml              # Colores
│               │   ├── strings.xml             # Strings
│               │   └── themes.xml              # Temas
│               ├── values-night/               # Tema oscuro
│               └── xml/
│                   ├── backup_rules.xml
│                   └── data_extraction_rules.xml
├── iconos_ajustes_oneui_muted/        # Iconos SVG para Ajustes
├── xml icons/                         # Iconos adicionales
├── hook iconos.txt                    # Código de referencia (JADX)
├── nuevohook.txt                      # Investigación de hooks
├── build.gradle
├── settings.gradle
├── gradle.properties
└── gradlew / gradlew.bat
```

### 8.2 Archivos de Recursos

#### Layouts Principales

| Archivo | Descripción |
|---------|-------------|
| `activity_main_new.xml` | MainActivity con bottom navigation |
| `activity_battery_style.xml` | Selector de estilo de batería |
| `activity_bypass.xml` | Toggle de bypass |
| `activity_customize_settings.xml` | Editor de perfil |
| `activity_dpi.xml` | Lista de apps para DPI |
| `activity_ios_style.xml` | Toggle estilo iOS |
| `tab_inicio.xml` | Tab de inicio |
| `tab_hooks.xml` | Tab de hooks |
| `tab_logs.xml` | Tab de logs |
| `tab_settings.xml` | Tab de ajustes |

#### Drawables Principales

| Categoría | Archivos |
|-----------|----------|
| Iconos Settings | `ic_accesibilidad.xml`, `ic_bateria.xml`, `ic_bluetooth.xml`, etc. |
| Iconos UI | `ic_home.xml`, `ic_hook.xml`, `ic_log.xml`, `ic_settings.xml` |
| Backgrounds | `bg_card_section.xml`, `bg_main_glass.xml`, `bg_nav_floating.xml` |
| Selectores | `row_selected.xml`, `row_default.xml`, `nav_selector.xml` |

---

## 9. Dependencias

### 9.1 Dependencias Principales

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| `de.robv.android.xposed:api` | 82 | Framework Xposed |
| `com.github.topjohnwu.libsu:core` | 5.2.2 | Operaciones root |
| `com.github.skydoves:colorpickerview` | 2.3.0 | Selector de colores |
| `androidx.activity:activity-ktx` | - | Extensiones Activity |
| `androidx.appcompat:appcompat` | - |Compatibilidad hacia atrás |
| `com.google.android.material:material` | - | Componentes Material Design |

### 9.2 Dependencias de Compilación

| Dependencia | Tipo |
|-------------|------|
| `*.jar` en `libs/` | compileOnly |
| Xposed API | compileOnly |

> **Nota**: Xposed API es `compileOnly` porque se proporciona por el framework en runtime.

---

## 10. Guía de Instalación

### 10.1 Requisitos Previos

1. **Dispositivo LG** (preferiblemente LG V60)
2. **Android 10+** (API 29+)
3. **Root** via Magisk, KernelSU o APatch
4. **LSPosed** (o similar framework Xposed)

### 10.2 Pasos de Instalación

#### Paso 1: Instalar LSPosed

```bash
# Si usas Magisk:
# Descargar LSPosed desde GitHub
# Instalar vía Magisk Manager
# Habilitar en /data/adb/lspd/

# Si usas KernelSU:
# LSPosed suele venir preinstalado
```

#### Paso 2: Compilar o Descargar el Módulo

```bash
# Opción A: Compilar desde código fuente
./gradlew assembleRelease

# Opción B: Descargar APK precompilada
# (Descargar desde releases)
```

#### Paso 3: Instalar el Módulo

```bash
# Instalar APK como app normal
adb install app-release.apk
```

#### Paso 4: Habilitar en LSPosed

1. Abrir **LSPosed**
2. Ir a **Modules**
3. Buscar **LG Extended**
4. Habilitar el módulo
5. Seleccionar alcance:
   - ☑️ `com.android.systemui`
   - ☑️ `com.lge.launcher3` / `com.android.launcher3`
   - ☑️ `android`
   - ☑️ `com.android.settings`

#### Paso 5: Reiniciar

```bash
# Reiniciar dispositivo o
adb shell reboot
```

### 10.5 Uso

1. Abrir **LG Extended**
2. Ir a **Hooks** para configurar cada mod
3. Los cambios se aplican en tiempo real (algunos requieren reinicio de SystemUI)

---

## 11. Troubleshooting

### 11.1 Problemas Comunes

| Problema | Causa | Solución |
|----------|-------|----------|
| Módulo no aparece en LSPosed | No instalado correctamente | Reinstalar APK y habilitar en LSPosed |
| Hooks no funcionan | Alcance incorrecto | Verificar que todos los paquetes estén seleccionados |
| Icono batería no cambia | SystemUI no reiniciado | Usar botón "Restart System UI" o reboot |
| DPI no se aplica | App no en alcance | Asegurar que la app esté en el scope de LSPosed |
| Crash al abrir Ajustes | Conflicto con hooks | Deshabilitar hooks temporalmente |

### 11.2 Logs

Los logs del módulo se pueden visualizar en:
- **LG Extended** → Tab **Logs**
- **LSPosed** → Logs
- **Logcat** con filtro: `LG_Extended`

### 11.3 Debug

```bash
# Ver logs del módulo
adb logcat | grep "LG_Extended"

# Ver logs de Xposed
adb logcat | grep "XposedBridge"

# Forzar reinicio de SystemUI
adb shell killall com.android.systemui

# Ver preferencias del módulo
adb shell cat /data/data/com.zxerox.lg_extended/shared_prefs/lg_extended_prefs.xml
```

---

## Apendice A: Mapeo de Paquetes

| Paquete | Descripción | Hooks |
|---------|-------------|-------|
| `com.android.systemui` | SystemUI | BatteryHook |
| `com.lge.systemui` | LG SystemUI | BatteryHook |
| `com.lge.launcher3` | LG Launcher | RecentsHook |
| `com.android.launcher3` | AOSP Launcher | RecentsHook |
| `android` | Proceso del sistema | FlagSecureHook |
| `com.android.settings` | Ajustes | SettingsHook |
| `com.zxerox.lg_extended` | Esta app | (Ninguno - excluida) |

---

## Apendice B: Referencia de Preferencias

| Key | Tipo | Default | Descripción |
|-----|------|---------|-------------|
| `battery_style` | String | ONEUI_8 | Estilo de batería |
| `battery_color_fondo` | int | #1C1C1E | Color fondo normal |
| `battery_color_texto` | int | Blanco | Color texto normal |
| `battery_color_fondo_cargando` | int | #34C759 | Color fondo cargando |
| `battery_color_texto_cargando` | int | Blanco | Color texto cargando |
| `battery_color_fondo_bajo` | int | #FF3B30 | Color fondo batería baja |
| `battery_color_texto_bajo` | int | Blanco | Color texto batería baja |
| `recents_enabled` | boolean | false | Estilo iOS recientes |
| `bypass_flag_secure` | boolean | true | Bypass FLAG_SECURE |
| `hook_settings_icons` | boolean | false | Reemplazar iconos ajustes |
| `profile_name` | String | LG V60 User | Nombre perfil |
| `profile_phrase` | String | Stock is a suggestion | Frase perfil |
| `profile_avatar_base64` | String | "" | Avatar Base64 |
| `hook_active_battery` | boolean | false | BatteryHook activo (auto) |
| `hook_active_dpi` | boolean | false | DpiHook activo (auto) |
| `hook_active_recents` | boolean | false | RecentsHook activo (auto) |
| `hook_active_flagsecure` | boolean | false | FlagSecureHook activo (auto) |
| `hook_active_settings` | boolean | false | SettingsHook activo (auto) |

---

## Apendice C: Créditos y Licencias

### Autor
- **ZxeroX** - Desarrollador principal

### Dependencias con Licencia

| Librería | Licencia |
|----------|----------|
| Xposed Framework | Apache License 2.0 |
| libsu | Apache License 2.0 |
| ColorPickerView | Apache License 2.0 |
| Material Components | Apache License 2.0 |

---

*Documentación generada el: 27 de Julio, 2026*
*Versión del documento: 1.0*
