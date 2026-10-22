# ⏰ Remindly

> Aplicación Android nativa desarrollada en Java para registrar recordatorios con fecha, hora opcional, imagen, periodicidad y notificaciones locales programadas.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción-del-proyecto)
- [Funcionalidades](#-funcionalidades-principales)
- [Tecnologías](#-tecnologías-usadas)
- [Estructura](#-estructura-principal)
- [Flujo Funcional](#-flujo-funcional-implementado)
- [Programación](#-regla-de-programación-actual)
- [Estado](#-estado-frente-a-la-consigna)
- [Ejecución](#-cómo-ejecutar)

---

## 📝 Descripción del Proyecto

**Remindly** permite crear y administrar recordatorios desde una aplicación Android. Cada recordatorio puede incluir:

✅ Título y descripción  
✅ Fecha obligatoria y hora opcional    
✅ Imagen seleccionada desde la galería     
✅ Periodicidad única o anual   
✅ Anticipación de la notificación: el mismo día, 1 día, 3 días o 1 semana antes    
✅ Edición y eliminación desde una lista tipo tarjeta   
✅ Persistencia local de eventos e imágenes     
✅ Mensaje predeterminado configurable para las notificaciones  
✅ Reposición automática de alarmas después de reiniciar el dispositivo 

Los eventos se guardan localmente en un archivo JSON y las imágenes se copian al almacenamiento interno de la aplicación. No se utiliza una base de datos externa ni una conexión de red.

## ✨ Funcionalidades Principales

### Gestión de recordatorios

- Crear un recordatorio con validación de título y fecha.
- Editar un evento existente al presionar su tarjeta.
- Eliminar un evento y cancelar su notificación.
- Visualizar la fecha, la descripción, una imagen y el tiempo aproximado restante.

### Notificaciones

- Crear canales separados para eventos únicos y anuales.
- Programar alarmas exactas con `AlarmManager`.
- Anticipar la notificación según el plazo seleccionado.
- Personalizar el mensaje base desde la pantalla de ajustes.
- Reprogramar los eventos guardados después de encender el dispositivo.

## 💻 Tecnologías Usadas

| Tecnología | Versión | Uso |
| --- | --- | --- |
| Java | 11 | Lenguaje principal |
| Android SDK | API 34 mínimo / API 37 objetivo | Plataforma Android |
| Android Gradle Plugin | 9.4.1 | Construcción del proyecto |
| AndroidX AppCompat | 1.6.1 | Compatibilidad de componentes |
| Material Components | 1.10.0 | Interfaz de usuario |
| Activity KTX | 1.8.0 | APIs de actividades y resultados |
| ConstraintLayout | 2.1.4 | Diseño de interfaces |
| RecyclerView | Incluido mediante dependencias | Lista de recordatorios |
| AlarmManager | Android SDK | Programación de alarmas |
| NotificationManager | Android SDK | Envío de notificaciones |
| SharedPreferences | Android SDK | Configuración del mensaje predeterminado |
| JSON | Android SDK | Persistencia de eventos |

## 🔐 Permisos Utilizados

| Permiso | Tipo | Propósito |
| --- | --- | --- |
| `POST_NOTIFICATIONS` | Tiempo de ejecución | Mostrar notificaciones en Android 13 y superior |
| `SCHEDULE_EXACT_ALARM` | Instalación | Programar alarmas exactas |
| `RECEIVE_BOOT_COMPLETED` | Instalación | Reprogramar recordatorios al reiniciar el teléfono |

## 📂 Estructura Principal

```text
Remindly/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/remindly/
│       │   │   ├── MainActivity.java
│       │   │   ├── FormActivity.java
│       │   │   ├── SettingsActivity.java
│       │   │   ├── Evento.java
│       │   │   ├── EventoAdapter.java
│       │   │   ├── EventoStorage.java
│       │   │   ├── NotificationHelper.java
│       │   │   ├── AlarmReceiver.java
│       │   │   └── BootReceiver.java
│       │   └── res/
│       │       ├── drawable/
│       │       ├── layout/
│       │       ├── mipmap/
│       │       ├── values/
│       │       └── xml/
│       ├── androidTest/
│       └── test/
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

### Responsabilidad de las clases principales

| Clase | Responsabilidad |
| --- | --- |
| `MainActivity` | Muestra la lista, solicita permisos y gestiona edición/eliminación |
| `FormActivity` | Crea o edita recordatorios y coordina su persistencia |
| `SettingsActivity` | Guarda el mensaje predeterminado de las notificaciones |
| `Evento` | Representa un evento y calcula su próxima ocurrencia |
| `EventoAdapter` | Adapta los eventos para mostrarlos en `RecyclerView` |
| `EventoStorage` | Guarda y carga eventos JSON e imágenes en almacenamiento interno |
| `NotificationHelper` | Crea canales, programa y cancela alarmas |
| `AlarmReceiver` | Construye y publica la notificación cuando se dispara una alarma |
| `BootReceiver` | Reprograma todos los eventos después de reiniciar el dispositivo |

## 🔄 Flujo Funcional Implementado

```text
┌─────────────────────────────────────────────────────────────────────┐
│                    FLUJO DE REMINDLY                                │
├─────────────────────────────────────────────────────────────────────┤
│  1. Iniciar MainActivity                                            │
│     ↓ Crear canales y solicitar permiso de notificaciones           │
│                                                                     │
│  2. Cargar eventos desde eventos.json                               │
│     ↓ Mostrar tarjetas en RecyclerView                              │
│                                                                     │
│  3. Presionar “Agregar recordatorio” o una tarjeta existente        │
│     ↓ Abrir FormActivity en modo creación o edición                 │
│                                                                     │
│  4. Ingresar título, fecha y configuración opcional                 │
│     ↓ Validar campos obligatorios                                   │
│                                                                     │
│  5. Guardar evento                                                  │
│     ↓ Persistir JSON y copiar imagen almacenamiento interno         │
│                                                                     │
│  6. Cancelar alarma anterior y programar nueva alarma               │
│     ↓ AlarmManager invoca AlarmReceiver en la fecha calculada       │
│                                                                     │
│  7. AlarmReceiver publica la notificación                           │
│     ↓ Usa mensaje personalizado almacenado en SharedPreferences     │
│                                                                     │
│  8. Reinicio del dispositivo                                        │
│     ↓ BootReceiver carga los eventos y los vuelve a programar       │
└─────────────────────────────────────────────────────────────────────┘
```

### Pantallas y componentes

| Pantalla / componente | Acción principal |
| --- | --- |
| `MainActivity` | Lista los recordatorios registrados |
| `FormActivity` | Crea, valida y edita recordatorios |
| `SettingsActivity` | Configura el mensaje de notificación |
| `EventoAdapter` | Renderiza las tarjetas de eventos |
| `AlarmReceiver` | Recibe las alarmas y publica notificaciones |
| `BootReceiver` | Restablece las alarmas tras reiniciar el equipo |

## 🧮 Regla de Programación Actual

La aplicación calcula el instante de notificación con la siguiente regla:

```math
disparo = próximaOcurrenciaMillis - (plazoDias × 86 400 000)
```

Si el resultado ya pasó, el evento se programa cinco segundos después del guardado:

```java
if (disparo <= System.currentTimeMillis()) {
    disparo = System.currentTimeMillis() + 5000;
}
```

### Comportamiento adicional

- Si el evento no incluye hora, la próxima ocurrencia se calcula a las **09:00**.
- Si el evento es anual y su fecha ya pasó, se calcula la próxima ocurrencia en un año futuro.
- El plazo puede ser **0, 1, 3 o 7 días**.

**Ejemplo:**

- Fecha del evento: 10/05/2027
- Hora: 18:30
- Anticipación: 3 días
- **Disparo programado: 07/05/2027 a las 18:30**

## 💾 Persistencia Local

| Información | Ubicación |
| --- | --- |
| Lista de eventos | `eventos.json` en almacenamiento interno |
| Imágenes | Archivos `img_<id>_<timestamp>.jpg` en almacenamiento interno |
| Mensaje predeterminado | `SharedPreferences` llamado `ajustes` |
| Alarmas | `AlarmManager` del sistema |

## ✅ Estado frente a la Consigna

### ✔️ Implementado

- [x] Aplicación Android nativa en Java
- [x] Registro de recordatorios con título y fecha obligatorios
- [x] Descripción e imagen opcionales
- [x] Selección de fecha mediante `DatePickerDialog`
- [x] Selección opcional de hora mediante `TimePickerDialog`
- [x] Periodicidad única o anual
- [x] Cuatro opciones de anticipación para la notificación
- [x] Lista visual con tarjetas y tiempo restante aproximado
- [x] Edición y eliminación de recordatorios
- [x] Persistencia local en JSON
- [x] Copia de imágenes al almacenamiento interno
- [x] Canales de notificación para eventos únicos y anuales
- [x] Mensaje predeterminado personalizable
- [x] Reprogramación de alarmas después del reinicio
- [x] Solicitud del permiso `POST_NOTIFICATIONS`

## 🚀 Cómo Ejecutar

### Requisitos Previos

- Android Studio reciente compatible con Android Gradle Plugin 9.4.1
- Android SDK con API 37 instalada
- Dispositivo físico o emulador con Android API 34 o superior
- Conexión a Internet durante la primera sincronización de Gradle

### Ejecutar desde Android Studio

1. Clonar el repositorio:

```bash
   git clone https://github.com/Bearserker10029/Remindly.git
```

2. Abrir Android Studio.
3. Seleccionar **Open** y elegir la carpeta raíz `Remindly`.
4. Esperar la sincronización de Gradle.
5. Seleccionar un dispositivo o emulador.
6. Presionar **Run**.

### Ejecutar desde terminal

**En Linux/macOS:**

```bash
chmod +x gradlew
./gradlew assembleDebug
./gradlew installDebug
```

**En Windows:**

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

### Probar la aplicación

1. Abrir **Remindly** en el dispositivo.
2. Aceptar el permiso de notificaciones.
3. Crear un recordatorio con anticipación **“El mismo día”**.
4. Seleccionar una hora cercana y guardar.
5. Verificar que la notificación se muestre en el tiempo calculado.

> **Nota:** para probar alarmas exactas en dispositivos recientes puede ser necesario activar manualmente el permiso de alarmas y recordatorios en la configuración del sistema.

### Pruebas

```bash
./gradlew test
./gradlew connectedDebugAndroidTest
```

El primer comando ejecuta pruebas unitarias locales. El segundo requiere un dispositivo o emulador conectado para pruebas instrumentadas.

---

## 📚 Recursos Adicionales

- [Documentación oficial de Android](https://developer.android.com/)
- [AlarmManager](https://developer.android.com/reference/android/app/AlarmManager)
- [Notificaciones en Android](https://developer.android.com/develop/ui/views/notifications)
- [RecyclerView](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView)
- [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences)
- [Guía de Gradle para Android](https://developer.android.com/build)

---

## 📄 Licencia

Este proyecto es de uso académico y educativo. Puedes utilizarlo como referencia para aprender desarrollo Android, gestión de alarmas, notificaciones y persistencia local.