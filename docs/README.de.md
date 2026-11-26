# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · [Español](README.es.md) · [Português](README.pt.md) · [Français](README.fr.md) · **Deutsch** · [Italiano](README.it.md) · [Polski](README.pl.md) · [Nederlands](README.nl.md) · [Čeština](README.cs.md) · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** Die App ist nur für Erwachsene bestimmt: Sie ermöglicht den Zugang zu einer Website mit Inhalten für Erwachsene.
>
> **Inoffizielles Projekt.** Es steht in keiner Verbindung zu PornHub oder zum Unternehmen, dem die Website gehört, und wird von diesen weder gebilligt noch unterstützt. Lies vor der Nutzung den [Haftungsausschluss](#haftungsausschluss).

## Was ist das

PrivateTube ist ein nativer Client der Website PornHub für **Android TV und Google TV**, geschrieben in Kotlin (Jetpack Compose for TV, Media3).

Es ist weder ein Browser noch eine „verpackte“ Website: Es gibt kein WebView. Das Gerät der Nutzerin oder des Nutzers ruft die öffentlich zugänglichen Seiten selbst ab; die App entnimmt ihnen Titel, Vorschaubilder und Stream-Links und zeigt alles in einer eigenen Oberfläche, die für eine gewöhnliche Fernbedienung gemacht ist.

## Wozu das Ganze

Die Website am Fernseher zu nutzen ist unbequem: winzige Elemente, eine Oberfläche für Maus und Touchscreen, Pop-ups. Ein Fernseher braucht eine eigene Oberfläche: große Kacheln, einen gut erkennbaren Fokus, schnelle Reaktion und Bedienung per Fernbedienung.

## In ein paar Abenden mit Hilfe von KI entstanden

PrivateTube ist **buchstäblich in ein paar Abenden mit Hilfe von KI** entstanden. Ein Mensch stellte die Aufgaben, prüfte das Ergebnis und sagte, was überarbeitet werden sollte. Die KI schrieb den Code, analysierte den Aufbau der Seiten, bereitete Tests vor und behob Fehler.

**Meine Meinung: Die Macher von PornHub könnten durchaus selbst einen offiziellen Client wie diesen bauen.** Nach meiner Erfahrung geht das schnell, ist nicht schwierig und erfordert keine Entwicklungskosten in Höhe von Tausenden Dollar. Für sie wäre es sogar einfacher: Sie haben direkten Zugriff auf ihre Daten und müssen ihre eigenen Webseiten nicht auswerten. Natürlich bräuchte ein offizielles Produkt Support und Tests auf verschiedenen Geräten, aber ein bequemer Client für den Fernseher ist eine gut machbare Aufgabe.

Die Nutzer werden sich nur freuen, wenn eine offizielle App erscheint und dieses Projekt überflüssig wird.

## Funktionen

- Startseite mit endlosem Feed sowie ein Seitenmenü mit Kategorien, Kanälen, Models und Suche.
- Große Kacheln, automatisch nachladende Listen, vollständige Bedienung per Fernbedienung ohne horizontales Scrollen.
- Eigener Player (HLS): Qualitätswahl, Spulen in 30-Sekunden-Schritten (schnell, wenn die Taste gehalten wird), Fortsetzen an der gespeicherten Stelle.
- Menü durch langes Drücken auf ein Video: fortsetzen oder von vorn beginnen, Favoriten, den Uploader abonnieren, dessen weitere Videos.
- Lokale Bereiche „Abos“, „Favoriten“ und „Verlauf“.
- Lokaler Export und Import von Favoriten, Abos und Verlauf.
- PIN-Code beim Start.
- 13 Oberflächensprachen: Englisch, Russisch, Ukrainisch, Spanisch, Portugiesisch (Brasilien), Französisch, Deutsch, Italienisch, Polnisch, Niederländisch, Tschechisch, Japanisch und Chinesisch (vereinfacht). Die Sprache richtet sich nach dem System oder wird in den Einstellungen gewählt.
- Videotitel stammen aus der Version der Website in derselben Sprache. Die Website übersetzt nicht alle Titel; auf Japanisch und Chinesisch gibt es deutlich weniger übersetzte Videos.
- Gleiche Skalierung auf 720p-, 1080p- und 4K-Bildschirmen, zwei Schriftgrößen: mittel und groß.

## Wenn sich die Website nicht öffnet: VPN

**Wenn die Website in deinem Land oder bei deinem Anbieter gesperrt ist oder Videos nicht laden, schalte ein VPN ein** – am Fernseher, an der TV-Box oder am Router.

Vergewissere dich vorher, dass die Nutzung eines VPN und das Aufrufen solcher Inhalte **dort, wo du dich befindest, legal ist**. In manchen Ländern gibt es Einschränkungen. Die Einhaltung der örtlichen Gesetze ist Sache der Nutzerin oder des Nutzers. Das Projekt stellt kein VPN bereit, steht mit keinem VPN-Dienst in Verbindung und empfiehlt keine bestimmten Dienste.

## Datenschutz

- Die App benötigt keinerlei Konten: weder Google noch ein Konto der Website.
- Sie enthält keine Google-Dienste und keine eigenen Analyse- oder Zählwerkzeuge. Das Projekt hat keine eigenen Server, und die Autoren erhalten keine Daten.
- Videos und Seiten der Website werden nicht auf dem Datenträger gespeichert. Videovorschaubilder werden beim Verlassen der App und erneut beim nächsten Start gelöscht. Bilder von Kategorien, Kanälen, Models und Uploadern bleiben im Cache (bis 60 MB), bis neue sie verdrängen oder du den Cache in den Einstellungen leerst.
- Favoriten, Abos, Verlauf und Wiedergabepositionen werden nur auf dem Gerät gespeichert.
- Der PIN-Code wird als Salt und Hash gespeichert. Das schützt vor zufälligen Personen, nicht vor jemandem mit vollem Zugriff auf das Gerät.

**Hundertprozentige Anonymität gibt es nicht.** Die Website, dein Internetanbieter, der Netzwerkadministrator und der VPN-Dienst können im Rahmen ihrer technischen Möglichkeiten weiterhin Informationen über deine Verbindung sehen. Die Website verarbeitet Anfragen nach ihren eigenen Regeln.

### Sicherungen

- Der Export legt im Ordner „Downloads“ eine Datei wie `user_preferences_26_11_2026_02_49.dat` an.
- Sie enthält Favoriten, Abos, Wiedergabeverlauf und Positionen in den Videos. PIN-Code und App-Einstellungen sind nicht enthalten.
- Der Inhalt der Sicherungen ist komprimiert und verschlüsselt und daher in einem Texteditor nicht lesbar. Der Schlüssel ist jedoch in die App eingebaut und im Quellcode sichtbar.
- Der Import ergänzt Fehlendes und löscht nichts. Der Verlauf speichert höchstens 100 Einträge.
- Die Datei verschwindet weder beim Leeren des Caches noch beim Deinstallieren der App. Wenn du sie nicht mehr brauchst, lösche sie selbst.

### Berechtigungen

| Berechtigung | Wofür |
|---|---|
| Internet und Netzwerkstatus | Laden des Katalogs und der Videos |
| Speicherzugriff (Android 10 und älter) | nur für Export und Import der Sicherung, wird bei der ersten Nutzung angefragt |
| Zugriff auf alle Dateien (Android 11 und neuer) | nur zum Importieren einer Sicherung aus einer früheren Installation oder von einem anderen Gerät; wird manuell erteilt, alles andere funktioniert auch ohne |

## Installation

Benötigt wird Android 7.0 oder neuer.

1. Lade die APK aus dem Bereich **Releases** dieses Repositorys herunter.
2. Übertrage die APK auf beliebigem Weg auf den Fernseher, zum Beispiel mit der App LocalSend vom Smartphone oder per SAMBA vom Computer, und installiere sie, nachdem du die Installation aus unbekannten Quellen erlaubt hast.

Die App lässt sich auch per ADB installieren (atvTools vom Smartphone oder ADB App Control vom Computer).

Installiere nur APKs aus diesem Repository oder selbst gebaute: Für fremde Builds haften die Autoren nicht.

## Aus dem Quellcode bauen

Benötigt werden JDK 17 oder neuer und das Android SDK (compileSdk 36). Der Pfad zum SDK wird in `local.properties` oder in der Variablen `ANDROID_HOME` angegeben.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

Unter Windows verwende `.\gradlew.bat`. Die fertige Datei ist `app/build/outputs/apk/release/app-release.apk`.

Ohne weitere Einrichtung wird das Release mit dem **Debug-Schlüssel** deines Computers signiert. Für die Weitergabe erstelle einen eigenen Schlüssel (`keytool`) und daneben eine Datei `keystore.properties`:

```properties
storeFile=release.p12
storePassword=dein-passwort
keyAlias=dein-alias
keyPassword=dein-passwort
```

Der Pfad in `storeFile` gilt relativ zum Ordner dieser Datei. Den Pfad zu `keystore.properties` selbst gibst du in `local.properties` (`privatetube.signing=...`) oder in der Variablen `PRIVATETUBE_SIGNING` an. Bewahre den Schlüssel außerhalb des Repositorys auf und sichere ihn. Eine APK mit anderer Signatur lässt sich nicht über die vorherige installieren: Exportiere deine Daten, bevor du die alte Version entfernst.

## Aufbau

| Ordner in `app/src/main/java/com/privatetube/app/` | Inhalt |
|---|---|
| `core/network` | Anfragen an die Website, Adressen und User-Agent |
| `core/parsing` | Auswertung der Seiten; die Selektoren sind in `ParsingRules.kt` gesammelt |
| `data` | Repository, lokale Speicherung, Cache, Sicherung |
| `domain` | Datenmodelle |
| `feature` | Bildschirme |
| `ui` | gemeinsame Komponenten, Fokus, Laden von Bildern |

Wenn die Website ihr Markup ändert, genügt es meist, `core/parsing` anzupassen. Die Testseiten in `app/src/test/resources/fixtures/` sind anonymisiert. Die Prüfung gegen die echte Website (`LiveSiteCheck`) läuft nur manuell mit der Variablen `TV_LIVE=1`.

## Bekannte Einschränkungen

- Die App hängt vom Aufbau und den Regeln der Website ab und **kann jederzeit aufhören zu funktionieren**.
- Eine Anmeldung beim Konto der Website gibt es nicht. Abos und Favoriten sind lokal und werden nicht mit der Website synchronisiert; Benachrichtigungen über neue Videos gibt es nicht.
- Kostenpflichtige Inhalte werden nicht wiedergegeben.
- Manche Videos sind in deiner Region möglicherweise nicht verfügbar. Die Website kann die Häufigkeit der Anfragen begrenzen.
- Die Übersetzungen wurden nicht von Muttersprachlern geprüft.
- Während der Entwicklung wurde die App mit Tests und auf einem Emulator geprüft (Android-Abbild für Smartphones mit 1080p-Bildschirm). Der Betrieb auf bestimmten Fernseher- und TV-Box-Modellen wird nicht garantiert.

## Haftungsausschluss

Wenn du PrivateTube herunterlädst, baust, installierst oder nutzt, stimmst du den folgenden Bedingungen zu. Wenn du nicht zustimmst, nutze das Projekt nicht.

1. **Inoffizielles Projekt.** PrivateTube ist ein unabhängiges, nichtkommerzielles Projekt. Es steht in keiner Verbindung zu PornHub, Aylo oder mit ihnen verbundenen Unternehmen und wird von diesen weder gesponsert noch gebilligt oder unterstützt.
2. **Marken.** Die Namen PornHub, Aylo und die zugehörigen Marken gehören ihren Rechteinhabern. Sie werden nur genannt, um zu erklären, mit welcher Website das Programm arbeitet. Das Projekt beansprucht keine Rechte daran.
3. **Inhalte.** Das Repository und die APK enthalten keine Videos, Fotos oder anderen Materialien der Website. Die Autoren des Projekts hosten, laden, wählen aus, moderieren und verbreiten solche Materialien nicht, und das Projekt hat keine Zwischenserver. Das Gerät erhält die Daten direkt von der Website und ihren Auslieferungsnetzen. Auf dem Gerät verbleiben vorübergehend nur die im Abschnitt „Datenschutz“ beschriebenen Daten. Alle Rechte an den Materialien liegen bei den jeweiligen Rechteinhabern.
4. **Schutzmaßnahmen und Bezahlzugang.** Das Programm ist nicht dazu bestimmt, DRM, Bezahlschranken, Anmeldungen oder CAPTCHAs zu umgehen, und hat keine Funktion zum Herunterladen von Videos.
5. **Regeln der Website.** Die Website hat eigene Nutzungsbedingungen, die den Zugriff durch Programme von Drittanbietern einschränken können. Lies sie. Die Entscheidung zur Nutzung des Projekts und die Verantwortung für die Einhaltung dieser Bedingungen liegen bei dir.
6. **Gesetze deines Landes.** Die Gesetze zu Inhalten für Erwachsene, zu Mitteln zur Umgehung von Sperren, zum Urheberrecht und zur Verbreitung von Software unterscheiden sich. Du bist selbst dafür verantwortlich, dass Herunterladen, Nutzen und Weitergeben des Projekts an deinem Aufenthaltsort legal sind. Ist es verboten, nutze und verbreite es nicht.
7. **Alter.** Das Projekt dürfen nur Personen nutzen, die mindestens 18 Jahre alt sind oder das höhere, gesetzlich vor Ort festgelegte Volljährigkeitsalter erreicht haben. Installiere die App nicht auf Geräten, auf die Kinder unbeaufsichtigt zugreifen können. Die Altersabfrage und der PIN-Code ersetzen keine Kindersicherung.
8. **Inhalte der Website.** Für die Materialien, ihre Rechtmäßigkeit, Beschreibungen und Moderation sind die Website und diejenigen verantwortlich, die sie eingestellt haben. Die Autoren des Projekts kontrollieren diese Materialien nicht. Melde illegale Inhalte der Verwaltung der Website und den zuständigen Behörden.
9. **Keine Gewährleistung.** Das Programm wird „wie besehen“ bereitgestellt, ohne ausdrückliche oder stillschweigende Gewährleistungen, auch nicht für Funktionsfähigkeit, Eignung für einen bestimmten Zweck, Sicherheit oder Freiheit von Rechtsverletzungen gegenüber Dritten.
10. **Haftungsbeschränkung.** Im größtmöglichen gesetzlich zulässigen Umfang haften die Autoren und Mitwirkenden des Projekts nicht für direkte, indirekte oder sonstige Schäden und Folgen, die mit der Nutzung oder Nichtnutzbarkeit des Programms zusammenhängen, einschließlich Zugangssperren, Datenverlust, Offenlegung persönlicher Informationen und Ansprüchen Dritter.
11. **Geänderte Versionen.** Für Forks, fremde Builds und Änderungen durch andere Personen haften die Autoren nicht.
12. **Keine Rechtsberatung.** Dieser Text ist keine Rechtsberatung und hebt die im anwendbaren Recht vorgesehenen Rechte und Pflichten nicht auf.

## Für Rechteinhaber

Wir achten die Rechte an geistigem Eigentum. Wenn du Rechteinhaber oder dessen bevollmächtigte Vertretung bist und meinst, dass ein Teil dieses Repositorys deine Rechte verletzt, **erstelle ein Issue** mit folgenden Angaben: was genau die Rechte verletzt, um welche Rechte es geht und wie man dich erreichen kann (veröffentliche im Issue keine persönlichen Daten). Wir prüfen die Anfrage und ändern oder entfernen die Materialien bei Bedarf. Dein Recht, die offiziellen Verfahren von GitHub zu nutzen, wird dadurch nicht eingeschränkt.

Bei Videos und anderen Materialien auf der Website selbst wende dich an deren Verwaltung: Das Projekt stellt diese Materialien nicht bereit und kann sie nicht entfernen.

## Komponenten von Drittanbietern

- AndroidX und Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Lizenz

Der Quellcode wird unter der [MIT-Lizenz](../LICENSE) verbreitet. Sie gewährt keinerlei Rechte an Inhalten fremder Websites oder an fremden Marken.
