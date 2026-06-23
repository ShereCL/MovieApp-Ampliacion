# 🎬 MovieApp

Aplicación Android nativa desarrollada en **Java** para descubrir, seguir y comentar películas y series, construida sobre la API de **TMDB**. Permite registrar lo que vas viendo a modo de diario personal, guardar pendientes, recibir recomendaciones según tus gustos y consultar estadísticas de tu actividad.

Este repositorio es una **ampliación** del proyecto MovieApp original, con nuevas funcionalidades de seguimiento, comentarios, recomendaciones y estadísticas.

## ✨ Funcionalidades

### Exploración de contenido
- Listados de **películas**, **series** y **personas** (actores/equipo) desde TMDB.
- Ficha de detalle con sinopsis, póster, fecha de estreno, valoración y reproducción del **tráiler** directamente desde YouTube.
- Ficha de persona con biografía expandible, fecha y lugar de nacimiento, y listado de obras conocidas (`combined_credits`).

### Seguimiento (diario de visualización)
- Registro de películas/series ya vistas, con fecha de visualización, puntuación (0–5 con medios puntos) y una foto de "recuerdo" asociada.
- Búsqueda y precarga de título desde la ficha de detalle al añadir un nuevo seguimiento.
- Resolución automática del género de la obra para alimentar estadísticas y recomendaciones.
- Listado con búsqueda por título, filtros avanzados (rango de fechas, rango de puntuación) y ordenación (fecha o puntuación, ascendente/descendente).
- Eliminación de elementos mediante swipe.

### Pendientes (watchlist)
- Guarda películas y series para ver más adelante.
- Eliminación mediante swipe y acceso directo a la ficha de detalle.

### Personas favoritas
- Guarda actores, directores u otros perfiles como favoritos desde su ficha de detalle.
- Visualización en el perfil del usuario.

### Comentarios
- Comentarios en tiempo real por película/serie (Firestore `addSnapshotListener`), con contador y mensaje cuando no hay ninguno todavía.

### Recomendaciones personalizadas
- Calcula el género más repetido en tu historial de seguimiento y consulta `discover/movie` o `discover/tv` en TMDB para sugerir títulos de ese género.
- Excluye automáticamente los títulos que ya están en tu seguimiento (máximo 10 resultados).

### Estadísticas
- Total de películas y series vistas, puntuación media, mes más activo y tipo de contenido predominante.
- Gráfico combinado (barras + línea de tendencia) con la actividad mensual del año en curso, usando **MPAndroidChart**.

### Perfil y ajustes
- Perfil con nombre, biografía editable y foto, organizado en pestañas (Perfil / Estadísticas).
- Cambio de idioma (ES/EN) y de tema (claro/oscuro) aplicados en caliente.
- Subida de foto de perfil a **Supabase Storage**.
- Autenticación con email/contraseña y con Google (Firebase Auth), recuperación de contraseña y cierre de sesión.

## 🛠️ Stack técnico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java |
| Arquitectura | MVVM (ViewModel + LiveData) |
| UI | Fragments + ViewBinding, Navigation Component, Material Components 3 |
| Red | Retrofit2 + OkHttp + Gson |
| Backend de datos | Firebase Firestore |
| Autenticación | Firebase Authentication (email/contraseña y Google Sign-In) |
| Almacenamiento de imágenes | Supabase Storage |
| Imágenes | Glide |
| Gráficos | MPAndroidChart |
| API de contenido | [TMDB API](https://www.themoviedb.org/documentation/api) |

## 📂 Estructura del proyecto

```
app/src/main/java/com/example/movieapp/
├── data/
│   ├── auth/          # Autenticación con Firebase
│   ├── model/         # Modelos (Movie, Person, SeguimientoFS, PendienteFS, Comentario...)
│   ├── network/        # ApiService (TMDB), Retrofit, interceptores
│   ├── repository/      # Repositorios (TMDB, Firestore, recomendaciones...)
│   ├── supabase/       # Cliente y API de Supabase Storage
│   └── util/          # CustomToast, CustomAlertDialog, utilidades varias
└── ui/
    ├── auth/           # Login y registro
    ├── detail/         # Fragments de cada pantalla (Explorar, Seguimiento, Pendientes, Perfil, Ajustes, Estadísticas...)
    ├── list/           # Adapters de RecyclerView
    └── viewModel/        # ViewModels de cada pantalla
```

## 🚀 Cómo ejecutar el proyecto

1. Clona el repositorio.
2. Ábrelo en **Android Studio** (compileSdk 36, minSdk 24).
3. Añade tu propio archivo `google-services.json` en `app/` (proyecto de Firebase con Authentication y Firestore habilitados).
4. Configura tu API key de TMDB y tus credenciales de Supabase.
5. Sincroniza Gradle y ejecuta la app en un emulador o dispositivo físico.

## 📌 Estado del proyecto

Proyecto en desarrollo activo como ampliación de funcionalidades sobre la versión base de MovieApp.
