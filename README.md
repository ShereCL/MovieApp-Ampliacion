# 🎬 MovieApp - Ampliación

Aplicación Android desarrollada como ampliación del proyecto MovieApp. Permite explorar películas, consultar información detallada y buscar títulos utilizando una API externa de cine.

---

## 📱 Características

✅ Visualización de películas populares y recientes.  
✅ Búsqueda de películas por nombre.  
✅ Pantalla de detalles con información completa:
- Título
- Sinopsis
- Puntuación
- Idioma
- Imagen/poster

✅ Arquitectura MVVM.  
✅ Consumo de API REST mediante Retrofit.  
✅ Interfaz moderna y fluida.

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Descripción |
|------------|-------------|
| Kotlin / Java | Lenguaje principal |
| MVVM | Arquitectura del proyecto |
| Retrofit | Consumo de API REST |
| Coroutines | Operaciones asíncronas |
| LiveData | Observación de datos |
| ViewModel | Gestión del estado UI |
| RecyclerView | Listados de películas |
| Material Design | Diseño de la interfaz |

---

## 🏗️ Arquitectura

El proyecto sigue el patrón **MVVM (Model - View - ViewModel)**:

```text
UI
│
├── ViewModel
│
├── Repository
│
└── API / Data Source
```

Esta arquitectura permite separar responsabilidades y facilita el mantenimiento y escalabilidad del proyecto.

---

## 📂 Estructura del proyecto

```text
MovieApp-Ampliacion/
│
├── app/
│   ├── data/
│   │   ├── api/
│   │   ├── model/
│   │   └── repository/
│   │
│   ├── ui/
│   │   ├── home/
│   │   ├── details/
│   │   └── search/
│   │
│   ├── viewmodel/
│   └── utils/
│
├── gradle/
├── build.gradle
└── settings.gradle
```

---

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/ShereCL/MovieApp-Ampliacion.git
```

### 2. Abrir en Android Studio

Selecciona:

```text
File → Open → MovieApp-Ampliacion
```

### 3. Sincronizar Gradle

Espera a que Android Studio descargue todas las dependencias.

### 4. Configurar la API Key

Si el proyecto utiliza TMDB, añade tu clave API en el archivo correspondiente:

```kotlin
const val API_KEY = "TU_API_KEY"
```

Puedes obtener una API Key en:

https://www.themoviedb.org/settings/api

### 5. Ejecutar la aplicación

Selecciona un emulador o dispositivo físico y pulsa **Run ▶️**.

---

## 🔎 Funcionalidades principales

### 🏠 Inicio

Muestra las películas más populares o recientes obtenidas desde la API.

### 🔍 Búsqueda

Permite buscar películas escribiendo su nombre.

### 🎞️ Detalles

Muestra información detallada de la película seleccionada:

- Poster
- Título
- Descripción
- Valoración
- Idioma original

---

## 📸 Capturas de pantalla

Añade aquí las capturas de tu aplicación:

| Inicio | Búsqueda | Detalles |
|-------|---------|---------|
| ![](screenshots/home.png) | ![](screenshots/search.png) | ![](screenshots/detail.png) |

---

## 🌐 API utilizada

La aplicación consume datos de:

**The Movie Database (TMDB)**

https://www.themoviedb.org/

Endpoints utilizados:

- Películas populares
- Películas recientes
- Búsqueda de películas
- Detalles de una película

---

## 👩‍💻 Autor

**ShereCL**

GitHub:

https://github.com/ShereCL

---

## 📄 Licencia

Proyecto desarrollado con fines educativos y de aprendizaje.

---

⭐ Si te ha gustado este proyecto, no olvides darle una estrella al repositorio.
