# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · **Español** · [Português](README.pt.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Italiano](README.it.md) · [Polski](README.pl.md) · [Nederlands](README.nl.md) · [Čeština](README.cs.md) · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** La aplicación es solo para personas adultas: da acceso a un sitio con material para adultos.
>
> **Proyecto no oficial.** No está vinculado a PornHub ni a la empresa propietaria del sitio, y no cuenta con su aprobación ni su apoyo. Antes de usarlo, lee el [descargo de responsabilidad](#descargo-de-responsabilidad).

<img width="1920" height="1048" alt="1" src="https://github.com/user-attachments/assets/b29329cb-7717-4aec-b8c3-54f8219925bf" />

<details>
<summary><b>Más capturas de pantalla</b></summary>
<br>

<img width="1920" height="1048" alt="Screenshot 2" src="https://github.com/user-attachments/assets/8d56b490-c636-45df-a23e-f2c903b6f876" />
<img width="1920" height="1050" alt="Screenshot 3" src="https://github.com/user-attachments/assets/17fcf12e-51a6-4335-bdf5-9f5d08156b2f" />
<img width="1920" height="1050" alt="Screenshot 4" src="https://github.com/user-attachments/assets/25417d21-927a-4a62-8f7d-3307d988d653" />
<img width="1920" height="1053" alt="Screenshot 5" src="https://github.com/user-attachments/assets/7419ce69-33ec-477f-b95d-6c7fc13ec6db" />

</details>

## Qué es

PrivateTube es un cliente nativo de PornHub para **Android TV y Google TV**, escrito en Kotlin (Jetpack Compose for TV, Media3).

No es un navegador ni un sitio web «envuelto»: aquí no hay WebView. El propio dispositivo del usuario solicita las páginas públicas del sitio; la aplicación extrae de ellas títulos, miniaturas y enlaces a las transmisiones, y lo muestra todo en su propia interfaz, pensada para un mando a distancia corriente.

## Para qué sirve

Usar el sitio en un televisor es incómodo: elementos diminutos, una interfaz pensada para ratón y pantalla táctil, ventanas emergentes. Un televisor necesita su propia interfaz: tarjetas grandes, un foco claro, respuesta rápida y manejo con el mando.

## Hecho en un par de tardes con ayuda de la IA

PrivateTube se hizo **literalmente en un par de tardes con ayuda de la IA**. Una persona planteaba las tareas, comprobaba el resultado y decía qué había que rehacer. La IA escribía el código, analizaba el marcado de las páginas, preparaba pruebas y corregía errores.

**Mi opinión: los creadores de PornHub podrían perfectamente hacer un cliente oficial como este.** Según mi experiencia, es rápido, no es difícil y no exige miles de dólares en desarrollo. A ellos les resultaría incluso más fácil: tienen acceso directo a sus datos y no necesitan analizar sus propias páginas web. Por supuesto, un producto oficial requeriría soporte y pruebas en distintos dispositivos, pero un cliente cómodo para televisores es una tarea perfectamente asumible.

Los usuarios solo se alegrarán si aparece una aplicación oficial y este proyecto deja de ser necesario.

## Funciones

- Pantalla de inicio con contenido sin fin y un menú lateral con categorías, canales, modelos y búsqueda.
- Tarjetas grandes, listas que se cargan solas y manejo completo con el mando, sin desplazamiento horizontal.
- Reproductor propio (HLS): elección de calidad, saltos de 30 segundos (avance rápido si se mantiene pulsado el botón) y reanudación desde el punto guardado.
- Menú al mantener pulsado un vídeo: continuar o empezar de nuevo, favoritos, suscripción al autor y sus otros vídeos.
- Secciones locales «Suscripciones», «Favoritos» e «Historial».
- Exportación e importación locales de favoritos, suscripciones e historial.
- Código PIN al entrar.
- 13 idiomas de interfaz: inglés, ruso, ucraniano, español, portugués (de Brasil), francés, alemán, italiano, polaco, neerlandés, checo, japonés y chino (simplificado). El idioma se toma del sistema o se elige en los ajustes.
- Los títulos de los vídeos proceden de la versión del sitio en el mismo idioma. El sitio no traduce todos los títulos; en japonés y chino hay bastantes menos vídeos traducidos.
- La misma escala en pantallas 720p, 1080p y 4K, y dos tamaños de letra: mediano y grande.

## Si el sitio no se abre: VPN

**Si el sitio está bloqueado en tu país o por tu proveedor, o los vídeos no cargan, activa una VPN** en el televisor, en el reproductor multimedia o en el router.

Antes, asegúrate de que usar una VPN y acceder a este tipo de material es **legal en el lugar donde te encuentras**. En algunos países existen restricciones. Cumplir la legislación local es obligación del usuario. El proyecto no ofrece VPN, no está vinculado a ningún servicio de VPN y no recomienda servicios concretos.

## Privacidad

- La aplicación no necesita ninguna cuenta: ni de Google ni del sitio.
- No incluye servicios de Google, ni analíticas o contadores propios. El proyecto no tiene servidores propios y los autores no reciben ningún dato.
- Los vídeos y las páginas del sitio no se guardan en el disco. Las miniaturas de vídeo se borran al salir de la aplicación y otra vez en el siguiente inicio. Las imágenes de categorías, canales, modelos y autores permanecen en la caché (hasta 60 MB) hasta que otras nuevas las sustituyan o vacíes la caché en los ajustes.
- Los favoritos, las suscripciones, el historial y las posiciones de reproducción se guardan solo en el dispositivo.
- El código PIN se guarda como sal y hash. Protege frente a personas ocasionales, no frente a quien tenga acceso total al dispositivo.

**No existe el anonimato al 100 %.** El sitio, tu proveedor de internet, el administrador de la red y el servicio de VPN pueden seguir viendo información sobre tu conexión dentro de sus posibilidades técnicas. El sitio trata las solicitudes según sus propias normas.

### Copias de seguridad

- La exportación crea en la carpeta «Descargas» un archivo del tipo `user_preferences_26_11_2026_02_49.dat`.
- Contiene favoritos, suscripciones, historial y posiciones en los vídeos. El código PIN y los ajustes de la aplicación no se incluyen en la copia.
- El contenido de las copias está comprimido y cifrado, por lo que no se puede leer con un editor de texto. Pero la clave está integrada en la aplicación y es visible en el código fuente.
- La importación añade lo que falta y no borra nada. El historial conserva como máximo 100 entradas.
- El archivo no desaparece al vaciar la caché ni al desinstalar la aplicación. Si ya no lo necesitas, bórralo tú.

### Permisos

| Permiso | Para qué |
|---|---|
| Internet y estado de la red | cargar el catálogo y los vídeos |
| Acceso al almacenamiento (Android 10 y anteriores) | solo para exportar e importar la copia; se pide la primera vez que se usa |
| Acceso a todos los archivos (Android 11 y posteriores) | solo para importar una copia de una instalación anterior o de otro dispositivo; se concede a mano y sin él lo demás funciona |

## Instalación

Se necesita Android 7.0 o posterior.

1. Descarga el APK de la sección **Releases** de este repositorio.
2. Pasa el APK al televisor como te resulte más cómodo, por ejemplo con la aplicación LocalSend desde el teléfono o por SAMBA desde el ordenador, e instálalo tras permitir la instalación desde orígenes desconocidos.

También puedes instalar la aplicación por ADB (atvTools desde el teléfono o ADB App Control desde el ordenador).

Instala solo el APK de este repositorio o uno compilado por ti: los autores no responden de compilaciones ajenas.

## Compilación desde el código fuente

Se necesitan JDK 17 o posterior y Android SDK (compileSdk 36). La ruta al SDK se indica en `local.properties` o en la variable `ANDROID_HOME`.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

En Windows usa `.\gradlew.bat`. El archivo resultante es `app/build/outputs/apk/release/app-release.apk`.

Sin más configuración, la versión final se firma con la **clave de depuración** de tu ordenador. Para distribuirla, crea tu propia clave (`keytool`) y, junto a ella, un archivo `keystore.properties`:

```properties
storeFile=release.p12
storePassword=tu-contraseña
keyAlias=tu-alias
keyPassword=tu-contraseña
```

La ruta de `storeFile` se calcula desde la carpeta de ese archivo. Indica la ruta al propio `keystore.properties` en `local.properties` (`privatetube.signing=...`) o en la variable `PRIVATETUBE_SIGNING`. Guarda la clave fuera del repositorio y haz una copia de seguridad. Un APK con otra firma no se instalará sobre el anterior: exporta tus datos antes de desinstalar la versión antigua.

## Cómo está organizado

| Carpeta en `app/src/main/java/com/privatetube/app/` | Contenido |
|---|---|
| `core/network` | solicitudes al sitio, direcciones y User-Agent |
| `core/parsing` | análisis de páginas; los selectores están reunidos en `ParsingRules.kt` |
| `data` | repositorio, almacenamiento local, caché y copia de seguridad |
| `domain` | modelos de datos |
| `feature` | pantallas |
| `ui` | componentes comunes, foco y carga de imágenes |

Si el sitio cambia su maquetación, en la mayoría de los casos basta con corregir `core/parsing`. Las páginas de prueba de `app/src/test/resources/fixtures/` están anonimizadas. La comprobación contra el sitio real (`LiveSiteCheck`) solo se ejecuta a mano con la variable `TV_LIVE=1`.

## Limitaciones conocidas

- La aplicación depende de la maquetación y las normas del sitio y **puede dejar de funcionar en cualquier momento**.
- No se puede iniciar sesión en una cuenta del sitio. Las suscripciones y los favoritos son locales y no se sincronizan con el sitio; no hay notificaciones de vídeos nuevos.
- El contenido de pago no se reproduce.
- Algunos vídeos pueden no estar disponibles en tu región. El sitio puede limitar la frecuencia de las solicitudes.
- Las traducciones no han sido revisadas por hablantes nativos.
- Durante el desarrollo, la aplicación se comprobó con pruebas y en un emulador (imagen de Android para teléfono con pantalla de 1080p). No se garantiza su funcionamiento en modelos concretos de televisores y reproductores multimedia.

## Descargo de responsabilidad

Al descargar, compilar, instalar o usar PrivateTube aceptas las condiciones siguientes. Si no estás de acuerdo con ellas, no uses el proyecto.

1. **Proyecto no oficial.** PrivateTube es un proyecto independiente y sin ánimo de lucro. No está vinculado a PornHub, Aylo ni a sus empresas asociadas, y no cuenta con su patrocinio, aprobación ni apoyo.
2. **Marcas.** Los nombres PornHub, Aylo y las marcas relacionadas pertenecen a sus titulares. Se mencionan solo para explicar con qué sitio funciona el programa. El proyecto no reclama ningún derecho sobre ellos.
3. **Contenido.** El repositorio y el APK no contienen vídeos, fotografías ni otro material del sitio. Los autores del proyecto no alojan, suben, seleccionan, moderan ni distribuyen ese material, y el proyecto no tiene servidores intermediarios. El dispositivo del usuario recibe los datos directamente del sitio y de sus redes de distribución. En el dispositivo solo quedan temporalmente los datos descritos en la sección «Privacidad». Todos los derechos sobre el material pertenecen a sus titulares.
4. **Protección y acceso de pago.** El programa no está destinado a eludir DRM, accesos de pago, autenticación ni CAPTCHA, y no tiene función de descarga de vídeos.
5. **Normas del sitio.** El sitio tiene sus propias condiciones de uso, que pueden restringir el acceso mediante programas de terceros. Léelas. La decisión de usar el proyecto y la responsabilidad de cumplir esas condiciones son tuyas.
6. **Leyes de tu país.** Las leyes sobre material para adultos, herramientas para evitar bloqueos, derechos de autor y distribución de programas varían. Eres responsable de que descargar, usar y distribuir el proyecto sea legal donde te encuentras. Si está prohibido, no lo uses ni lo distribuyas.
7. **Edad.** Solo pueden usar el proyecto las personas que hayan cumplido 18 años o la mayoría de edad superior que fije la ley local. No instales la aplicación en dispositivos a los que los niños puedan acceder sin supervisión. La pantalla de confirmación de edad y el código PIN no sustituyen el control parental.
8. **Contenido del sitio.** Del material, su legalidad, sus descripciones y su moderación responden el sitio y quienes lo publicaron. Los autores del proyecto no controlan ese material. Denuncia el contenido ilegal a la administración del sitio y a las autoridades competentes.
9. **Sin garantías.** El programa se ofrece «tal cual», sin garantías expresas ni implícitas, incluidas las de funcionamiento, idoneidad para un fin determinado, seguridad y ausencia de infracción de derechos de terceros.
10. **Limitación de responsabilidad.** En la máxima medida permitida por la ley, los autores y colaboradores del proyecto no responden de daños directos, indirectos o de otro tipo ni de las consecuencias relacionadas con el uso o la imposibilidad de usar el programa, incluidos el bloqueo del acceso, la pérdida de datos, la divulgación de información personal y las reclamaciones de terceros.
11. **Versiones modificadas.** Los autores no responden de bifurcaciones, compilaciones ajenas ni cambios hechos por otras personas.
12. **No es asesoramiento jurídico.** Este texto no constituye asesoramiento jurídico ni anula los derechos y obligaciones previstos por la legislación aplicable.

## Para titulares de derechos

Respetamos los derechos de propiedad intelectual. Si eres titular de derechos o su representante autorizado y consideras que alguna parte de este repositorio los infringe, **crea un Issue** indicando qué infringe exactamente los derechos, de qué derechos se trata y cómo contactar contigo (no publiques datos personales en el Issue). Estudiaremos la reclamación y, si es necesario, modificaremos o eliminaremos el material. Esto no limita tu derecho a usar los procedimientos oficiales de GitHub.

Para los vídeos y demás material del propio sitio, dirígete a su administración: el proyecto no aloja ese material y no puede eliminarlo.

## Componentes de terceros

- AndroidX y Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Licencia

El código fuente se distribuye bajo la [licencia MIT](../LICENSE). Esta no concede ningún derecho sobre el contenido de sitios de terceros ni sobre marcas ajenas.
