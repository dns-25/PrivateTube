# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · [Español](README.es.md) · [Português](README.pt.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Italiano](README.it.md) · [Polski](README.pl.md) · **Nederlands** · [Čeština](README.cs.md) · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** De app is uitsluitend bedoeld voor volwassenen: ze geeft toegang tot een website met materiaal voor volwassenen.
>
> **Onofficieel project.** Het is niet verbonden met PornHub of het bedrijf dat eigenaar is van de website, en wordt door hen niet goedgekeurd of ondersteund. Lees vóór gebruik de [disclaimer](#disclaimer).

<img width="1920" height="1048" alt="1" src="https://github.com/user-attachments/assets/b29329cb-7717-4aec-b8c3-54f8219925bf" />

<details>
<summary><b>Meer screenshots</b></summary>
<br>

<img width="1920" height="1048" alt="Screenshot 2" src="https://github.com/user-attachments/assets/8d56b490-c636-45df-a23e-f2c903b6f876" />
<img width="1920" height="1050" alt="Screenshot 3" src="https://github.com/user-attachments/assets/17fcf12e-51a6-4335-bdf5-9f5d08156b2f" />
<img width="1920" height="1050" alt="Screenshot 4" src="https://github.com/user-attachments/assets/25417d21-927a-4a62-8f7d-3307d988d653" />
<img width="1920" height="1053" alt="Screenshot 5" src="https://github.com/user-attachments/assets/7419ce69-33ec-477f-b95d-6c7fc13ec6db" />

</details>

## Wat het is

PrivateTube is een native client van de website PornHub voor **Android TV en Google TV**, geschreven in Kotlin (Jetpack Compose for TV, Media3).

Het is geen browser en geen ‘ingepakte’ website: er is geen WebView. Het apparaat van de gebruiker vraagt zelf de openbaar toegankelijke pagina's van de website op; de app haalt daar titels, voorbeeldafbeeldingen en links naar de streams uit en toont alles in een eigen interface die voor een gewone afstandsbediening is gemaakt.

## Waarom

De website op een tv gebruiken is onhandig: piepkleine elementen, een interface voor muis en touchscreen, pop-ups. Een tv heeft een eigen interface nodig: grote kaarten, een duidelijke focus, snelle reactie en bediening met de afstandsbediening.

## Gemaakt in een paar avonden met hulp van AI

PrivateTube is **letterlijk in een paar avonden met hulp van AI** gemaakt. Een mens gaf de opdrachten, controleerde het resultaat en zei wat er anders moest. De AI schreef de code, analyseerde de opmaak van de pagina's, maakte tests en verhielp fouten.

**Mijn mening: de makers van PornHub zouden heel goed zelf zo'n officiële client kunnen maken.** Naar mijn ervaring is het snel, niet moeilijk en kost de ontwikkeling geen duizenden dollars. Voor hen zou het zelfs eenvoudiger zijn: ze hebben rechtstreeks toegang tot hun eigen gegevens en hoeven hun eigen webpagina's niet te ontleden. Natuurlijk zou een officieel product ondersteuning en tests op verschillende apparaten vragen, maar een prettige tv-client is een goed haalbare opdracht.

Gebruikers zullen alleen maar blij zijn als er een officiële app komt en dit project niet meer nodig is.

## Mogelijkheden

- Een startscherm met een eindeloze feed en een zijmenu met categorieën, kanalen, modellen en zoeken.
- Grote kaarten, lijsten die vanzelf verder laden, volledige bediening met de afstandsbediening zonder horizontaal scrollen.
- Eigen speler (HLS): kwaliteitskeuze, spoelen per 30 seconden (snel als je de knop ingedrukt houdt), verder kijken vanaf de bewaarde plek.
- Menu bij lang indrukken op een video: hervatten of opnieuw beginnen, favorieten, abonneren op de maker, diens andere video's.
- Lokale onderdelen ‘Abonnementen’, ‘Favorieten’ en ‘Geschiedenis’.
- Lokaal exporteren en importeren van favorieten, abonnementen en geschiedenis.
- Pincode bij het openen.
- 13 talen voor de interface: Engels, Russisch, Oekraïens, Spaans, Portugees (Braziliaans), Frans, Duits, Italiaans, Pools, Nederlands, Tsjechisch, Japans en Chinees (vereenvoudigd). De taal volgt die van het systeem of kies je in de instellingen.
- Videotitels komen uit de versie van de website in dezelfde taal. De website vertaalt niet alle titels; in het Japans en Chinees zijn er duidelijk minder vertaalde video's.
- Dezelfde schaal op 720p-, 1080p- en 4K-schermen, twee lettergroottes: normaal en groot.

## Als de website niet opent: VPN

**Is de website in jouw land of bij jouw provider geblokkeerd, of laden video's niet, zet dan een VPN aan** op de tv, het mediakastje of de router.

Controleer eerst of het gebruik van een VPN en het openen van zulk materiaal **legaal is waar je bent**. In sommige landen gelden beperkingen. Het naleven van de plaatselijke wetten is de verantwoordelijkheid van de gebruiker. Het project levert geen VPN, is met geen enkele VPN-dienst verbonden en beveelt geen specifieke diensten aan.

## Privacy

- De app heeft geen enkel account nodig: niet van Google en niet van de website.
- Er zitten geen Google-diensten in en ook geen eigen analyse of tellers. Het project heeft geen eigen servers en de makers ontvangen geen gegevens.
- Video's en pagina's van de website worden niet op schijf bewaard. Videovoorbeelden worden gewist bij het afsluiten van de app en nogmaals bij de volgende start. Afbeeldingen van categorieën, kanalen, modellen en makers blijven in de cache (tot 60 MB) totdat nieuwe ze verdringen of je de cache in de instellingen leegt.
- Favorieten, abonnementen, geschiedenis en kijkposities worden alleen op het apparaat bewaard.
- De pincode wordt bewaard als salt en hash. Dat beschermt tegen toevallige personen, niet tegen iemand met volledige toegang tot het apparaat.

**100% anonimiteit bestaat niet.** De website, je internetprovider, de netwerkbeheerder en de VPN-dienst kunnen binnen hun technische mogelijkheden nog steeds informatie over je verbinding zien. De website verwerkt verzoeken volgens haar eigen regels.

### Reservekopieën

- Exporteren maakt in de map ‘Downloads’ een bestand zoals `user_preferences_26_11_2026_02_49.dat`.
- Het bevat favorieten, abonnementen, kijkgeschiedenis en posities in video's. De pincode en de app-instellingen horen niet bij de kopie.
- De inhoud van reservekopieën is gecomprimeerd en versleuteld en daardoor niet leesbaar in een teksteditor. De sleutel zit echter in de app en is zichtbaar in de broncode.
- Importeren voegt toe wat ontbreekt en verwijdert niets. De geschiedenis bewaart hooguit 100 items.
- Het bestand verdwijnt niet bij het legen van de cache of het verwijderen van de app. Heb je het niet meer nodig, verwijder het dan zelf.

### Machtigingen

| Machtiging | Waarvoor |
|---|---|
| Internet en netwerkstatus | het laden van de catalogus en video's |
| Toegang tot opslag (Android 10 en ouder) | alleen voor het exporteren en importeren van de kopie; wordt bij het eerste gebruik gevraagd |
| Toegang tot alle bestanden (Android 11 en nieuwer) | alleen om een kopie van een eerdere installatie of van een ander apparaat te importeren; je verleent die handmatig, zonder deze machtiging werkt de rest ook |

## Installatie

Android 7.0 of nieuwer is vereist.

1. Download de APK uit het onderdeel **Releases** van deze repository.
2. Zet de APK op de tv op een manier die je prettig vindt, bijvoorbeeld met de app LocalSend vanaf een telefoon of via SAMBA vanaf een computer, en installeer hem nadat je installatie uit onbekende bronnen hebt toegestaan.

Je kunt de app ook via ADB installeren (atvTools vanaf een telefoon of ADB App Control vanaf een computer).

Installeer alleen APK's uit deze repository of zelf gebouwde: voor builds van anderen zijn de makers niet verantwoordelijk.

## Bouwen vanuit de broncode

Je hebt JDK 17 of nieuwer en de Android SDK nodig (compileSdk 36). Het pad naar de SDK stel je in `local.properties` of in de variabele `ANDROID_HOME` in.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

Gebruik op Windows `.\gradlew.bat`. Het resultaat is `app/build/outputs/apk/release/app-release.apk`.

Zonder verdere instellingen wordt de release ondertekend met de **debugsleutel** van je computer. Maak voor verspreiding een eigen sleutel (`keytool`) en daarnaast een bestand `keystore.properties`:

```properties
storeFile=release.p12
storePassword=jouw-wachtwoord
keyAlias=jouw-alias
keyPassword=jouw-wachtwoord
```

Het pad in `storeFile` geldt vanaf de map van dat bestand. Geef het pad naar `keystore.properties` zelf op in `local.properties` (`privatetube.signing=...`) of in de variabele `PRIVATETUBE_SIGNING`. Bewaar de sleutel buiten de repository en maak er een reservekopie van. Een APK met een andere handtekening kan niet over de vorige worden geïnstalleerd: exporteer je gegevens voordat je de oude versie verwijdert.

## Hoe het in elkaar zit

| Map in `app/src/main/java/com/privatetube/app/` | Inhoud |
|---|---|
| `core/network` | verzoeken aan de website, adressen en User-Agent |
| `core/parsing` | ontleden van pagina's; de selectors staan bij elkaar in `ParsingRules.kt` |
| `data` | repository, lokale opslag, cache, reservekopie |
| `domain` | gegevensmodellen |
| `feature` | schermen |
| `ui` | gedeelde componenten, focus, laden van afbeeldingen |

Als de website haar opmaak wijzigt, is het meestal genoeg om `core/parsing` aan te passen. De testpagina's in `app/src/test/resources/fixtures/` zijn geanonimiseerd. De controle op de echte website (`LiveSiteCheck`) wordt alleen handmatig gestart met de variabele `TV_LIVE=1`.

## Bekende beperkingen

- De app is afhankelijk van de opmaak en de regels van de website en **kan op elk moment ophouden te werken**.
- Inloggen op een account van de website kan niet. Abonnementen en favorieten zijn lokaal en worden niet met de website gesynchroniseerd; er zijn geen meldingen over nieuwe video's.
- Betaalde inhoud wordt niet afgespeeld.
- Sommige video's zijn mogelijk niet beschikbaar in jouw regio. De website kan de frequentie van verzoeken beperken.
- De vertalingen zijn niet door moedertaalsprekers gecontroleerd.
- Tijdens de ontwikkeling is de app gecontroleerd met tests en op een emulator (Android-image voor telefoons met een 1080p-scherm). De werking op specifieke modellen tv's en mediakastjes wordt niet gegarandeerd.

## Disclaimer

Door PrivateTube te downloaden, te bouwen, te installeren of te gebruiken ga je akkoord met onderstaande voorwaarden. Ga je er niet mee akkoord, gebruik het project dan niet.

1. **Onofficieel project.** PrivateTube is een onafhankelijk, niet-commercieel project. Het is niet verbonden met PornHub, Aylo of aan hen gelieerde bedrijven, en wordt door hen niet gesponsord, goedgekeurd of ondersteund.
2. **Merken.** De namen PornHub, Aylo en de bijbehorende merken zijn eigendom van hun rechthebbenden. Ze worden alleen genoemd om uit te leggen met welke website het programma werkt. Het project maakt er geen aanspraak op.
3. **Inhoud.** De repository en de APK bevatten geen video's, foto's of ander materiaal van de website. De makers van het project hosten, uploaden, selecteren, modereren en verspreiden zulk materiaal niet, en het project heeft geen tussenservers. Het apparaat van de gebruiker ontvangt de gegevens rechtstreeks van de website en haar distributienetwerken. Op het apparaat blijven alleen tijdelijk de gegevens achter die in het onderdeel ‘Privacy’ zijn beschreven. Alle rechten op het materiaal berusten bij de rechthebbenden.
4. **Beveiliging en betaalde toegang.** Het programma is niet bedoeld om DRM, betaalde toegang, inloggen of CAPTCHA te omzeilen en heeft geen functie om video's te downloaden.
5. **Regels van de website.** De website heeft eigen gebruiksvoorwaarden, die toegang via programma's van derden kunnen beperken. Lees ze. De beslissing om het project te gebruiken en de verantwoordelijkheid voor het naleven van die voorwaarden liggen bij jou.
6. **Wetten van jouw land.** Wetten over materiaal voor volwassenen, middelen om blokkades te omzeilen, auteursrecht en de verspreiding van software verschillen. Je bent er zelf verantwoordelijk voor dat downloaden, gebruiken en verspreiden van het project legaal is waar je bent. Is het verboden, gebruik en verspreid het dan niet.
7. **Leeftijd.** Het project mag alleen worden gebruikt door personen van minstens 18 jaar of van de hogere meerderjarigheidsleeftijd die de plaatselijke wet bepaalt. Installeer de app niet op apparaten waar kinderen zonder toezicht bij kunnen. Het scherm voor leeftijdsbevestiging en de pincode vervangen geen ouderlijk toezicht.
8. **Inhoud van de website.** Voor het materiaal, de rechtmatigheid, de beschrijvingen en de moderatie ervan zijn de website en de plaatsers verantwoordelijk. De makers van het project hebben geen controle over dit materiaal. Meld illegale inhoud bij het beheer van de website en bij de bevoegde instanties.
9. **Geen garantie.** Het programma wordt geleverd ‘zoals het is’, zonder uitdrukkelijke of stilzwijgende garanties, waaronder die van werking, geschiktheid voor een bepaald doel, veiligheid en het niet schenden van rechten van derden.
10. **Beperking van aansprakelijkheid.** Voor zover de wet dat maximaal toestaat, zijn de makers en bijdragers van het project niet aansprakelijk voor directe, indirecte of andere schade en gevolgen in verband met het gebruik of het niet kunnen gebruiken van het programma, waaronder blokkering van toegang, verlies van gegevens, openbaarmaking van persoonlijke informatie en claims van derden.
11. **Gewijzigde versies.** De makers zijn niet verantwoordelijk voor forks, builds van derden en wijzigingen door anderen.
12. **Geen juridisch advies.** Deze tekst is geen juridisch advies en doet niet af aan de rechten en plichten uit de toepasselijke wetgeving.

## Voor rechthebbenden

Wij respecteren intellectuele-eigendomsrechten. Ben je rechthebbende of diens gemachtigde en vind je dat een deel van deze repository je rechten schendt, **maak dan een Issue aan** met een beschrijving: wat precies de rechten schendt, om welke rechten het gaat en hoe we contact met je kunnen opnemen (plaats geen persoonsgegevens in de Issue). We beoordelen de melding en passen het materiaal zo nodig aan of verwijderen het. Dit beperkt je recht om de officiële procedures van GitHub te gebruiken niet.

Voor video's en ander materiaal op de website zelf neem je contact op met het beheer ervan: het project plaatst dat materiaal niet en kan het niet verwijderen.

## Onderdelen van derden

- AndroidX en Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Licentie

De broncode wordt verspreid onder de [MIT-licentie](../LICENSE). Die geeft geen enkel recht op de inhoud van websites van derden of op merken van anderen.
