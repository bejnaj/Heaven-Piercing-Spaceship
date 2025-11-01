# Heaven Piercing SpaceShip

## Descripción

Heaven Piercing SpaceShip es un juego de acción espacial desarrollado en Java utilizando la librería LibGDX. El jugador controla una nave-taladro que debe enfrentarse a diferentes tipos de enemigos, esquivar obstáculos y disparar para sobrevivir el mayor tiempo posible. Esta nave puede conectarse y controlar partes enemigas para mejorar temporalmente su poder. El objetivo es ascender hacia el espacio, derrotando enemigos y destruyendo naves nodrizas en distintos niveles.

## Cómo compilar y ejecutar

Este juego ha sido desarrollado en Java y puede ejecutarse utilizando IntelliJ IDEA, Eclipse, o cualquier entorno que soporte proyectos Java y LibGDX.

### Requisitos previos:

- Tener instalado Java JDK 17 o superior
- Tener instalado IntelliJ IDEA o Eclipse.
- Librería LibGDX configurada en el proyecto (ya incluida en los archivos del proyecto).
- Opcional: Gradle para gestionar dependencias y compilación.

### Pasos para compilar y ejecutar:

1. **Descarga y descomprime el** archivo `.zip` en una carpeta de tu elección.
2. **Abre el proyecto en tu IDE**
    - En IntelliJ IDEA: File > Open > [carpeta del proyecto].
    - En Eclipse: File > Import > Existing Gradle Project.

3. **Compila el código**
    - Si usas Gradle, abre la terminal del IDE y ejecuta: 
        ```
        ./gradlew build
        ```
    - Si no, asegúrate de compilar todas las clases Java.
        
4. **Ejecuta el programa**
    - Ejecuta la clase principal SpaceNavigation.java para iniciar el juego.
    - Se abrirá una ventana con la pantalla principal del juego.
        

## Funcionalidades

### Funcionando correctamente:

- Controlar la nave del jugador con movimientos horizontales y verticales.
- Disparar proyectiles para atacar enemigos.
- Enemigos con distintos tipos y comportamientos.
- Gestión de vidas y colisiones con enemigos y balas.

### Problemas conocidos:

- Al corresponder a una versión inicial del proyecto, aún hacen falta implementar más enemigos, mejorar la visualización y pulir los distintos apartados.

### A mejorar:

- Implementar distintos niveles y oleadas de enemigos.
- Añadir que enemigos específicos puedan disparar.

## Ejemplo de uso

**Iniciar el juego**

- El juego presenta una pantalla de inicio donde debe presionar cualquier botón para comenzar.

**Pantalla del nivel**

- En esta pantalla se encuentra nuestra nave y los distintos enemigos correspondientes al primer nivel, además en el HUD se encuentran las vidas, ronda (nivel), highscore (corresponde al puntaje más alto obtenido mientras el juego este ejecutado) y score (puntaje de la partida actual).

**Controles y Enfrentamiento**

- Usa las teclas de dirección para mover la nave y la barra espaciadora para disparar, los enemigos aparecen automáticamente y se mueven con distintos patrones, además, luego de dejarlos a poca vida, cambiará su Sprite y el jugador podrá acoplarse a la nave enemiga acercandoce a esta, mejorando la velocidad o el tipo de disparo del jugador, dependiendo del enemigo. 

**Sobrevivir el mayor tiempo posible**

- El juego continúa generando enemigos y proyectiles hasta que la nave del jugador pierda todas sus vidas.
