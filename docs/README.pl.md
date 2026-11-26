# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · [Español](README.es.md) · [Português](README.pt.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Italiano](README.it.md) · **Polski** · [Nederlands](README.nl.md) · [Čeština](README.cs.md) · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** Aplikacja jest przeznaczona wyłącznie dla osób pełnoletnich: daje dostęp do serwisu z materiałami dla dorosłych.
>
> **Projekt nieoficjalny.** Nie jest związany z PornHub ani z firmą będącą właścicielem serwisu, nie jest przez nich zatwierdzony ani wspierany. Przed użyciem przeczytaj [wyłączenie odpowiedzialności](#wyłączenie-odpowiedzialności).

## Co to jest

PrivateTube to natywny klient serwisu PornHub dla **Android TV i Google TV**, napisany w Kotlinie (Jetpack Compose for TV, Media3).

To nie jest przeglądarka ani strona w „opakowaniu”: nie ma tu WebView. Urządzenie użytkownika samo pobiera publicznie dostępne strony serwisu, a aplikacja wyciąga z nich tytuły, miniatury i odnośniki do strumieni i pokazuje wszystko we własnym interfejsie, przygotowanym pod zwykłego pilota.

## Po co to

Korzystanie z serwisu na telewizorze jest niewygodne: drobne elementy, interfejs pod mysz i ekran dotykowy, wyskakujące okna. Telewizor potrzebuje własnego interfejsu: dużych kafelków, czytelnego zaznaczenia, szybkiej reakcji i obsługi pilotem.

## Zrobione w kilka wieczorów z pomocą AI

PrivateTube powstał **dosłownie w kilka wieczorów z pomocą AI**. Człowiek stawiał zadania, sprawdzał wynik i mówił, co przerobić. AI pisała kod, analizowała strukturę stron, przygotowywała testy i poprawiała błędy.

**Moim zdaniem twórcy PornHub spokojnie mogliby zrobić oficjalnego klienta tego typu.** Z mojego doświadczenia to szybka, nietrudna praca, która nie wymaga tysięcy dolarów na rozwój. Byłoby im nawet łatwiej: mają bezpośredni dostęp do swoich danych i nie muszą analizować własnych stron. Oczywiście oficjalny produkt wymagałby wsparcia i testów na różnych urządzeniach, ale wygodny klient na telewizor to zadanie w pełni wykonalne.

Użytkownicy tylko się ucieszą, jeśli pojawi się oficjalna aplikacja i ten projekt przestanie być potrzebny.

## Możliwości

- Ekran główny z niekończącą się listą oraz boczne menu z kategoriami, kanałami, modelkami i wyszukiwarką.
- Duże kafelki, automatyczne doczytywanie list, pełna obsługa pilotem bez przewijania w poziomie.
- Własny odtwarzacz (HLS): wybór jakości, przewijanie co 30 sekund (szybkie po przytrzymaniu przycisku), wznawianie od zapisanego miejsca.
- Menu po długim naciśnięciu filmu: wznów lub zacznij od początku, ulubione, subskrypcja autora, inne jego filmy.
- Lokalne sekcje „Subskrypcje”, „Ulubione” i „Historia”.
- Lokalny eksport i import ulubionych, subskrypcji i historii.
- Kod PIN przy wejściu.
- 13 języków interfejsu: angielski, rosyjski, ukraiński, hiszpański, portugalski (brazylijski), francuski, niemiecki, włoski, polski, niderlandzki, czeski, japoński i chiński (uproszczony). Język jest dobierany według systemowego lub wybierany w ustawieniach.
- Tytuły filmów pochodzą z wersji serwisu w tym samym języku. Serwis nie tłumaczy wszystkich tytułów; po japońsku i chińsku przetłumaczonych filmów jest wyraźnie mniej.
- Ta sama skala na ekranach 720p, 1080p i 4K, dwa rozmiary czcionki: średni i duży.

## Jeśli serwis się nie otwiera: VPN

**Jeśli w twoim kraju lub u twojego dostawcy serwis jest zablokowany albo filmy się nie ładują, włącz VPN** na telewizorze, przystawce lub routerze.

Najpierw upewnij się, że korzystanie z VPN i otwieranie takich materiałów jest **legalne tam, gdzie się znajdujesz**. W niektórych krajach obowiązują ograniczenia. Przestrzeganie lokalnego prawa to obowiązek użytkownika. Projekt nie udostępnia VPN, nie jest związany z żadną usługą VPN i nie poleca konkretnych usług.

## Prywatność

- Aplikacja nie wymaga żadnych kont: ani Google, ani konta w serwisie.
- Nie ma w niej usług Google ani własnej analityki i liczników. Projekt nie ma własnych serwerów, a autorzy nie otrzymują żadnych danych.
- Filmy i strony serwisu nie są zapisywane na dysku. Miniatury filmów są usuwane przy wyjściu z aplikacji i ponownie przy następnym uruchomieniu. Obrazy kategorii, kanałów, modelek i autorów pozostają w pamięci podręcznej (do 60 MB), dopóki nie wyprą ich nowe lub nie wyczyścisz jej w ustawieniach.
- Ulubione, subskrypcje, historia i pozycje odtwarzania są przechowywane tylko na urządzeniu.
- Kod PIN jest przechowywany jako sól i skrót. Chroni przed przypadkowymi osobami, a nie przed kimś, kto ma pełny dostęp do urządzenia.

**Stuprocentowej anonimowości nie ma.** Serwis, twój dostawca internetu, administrator sieci i usługa VPN nadal mogą widzieć informacje o twoim połączeniu w granicach swoich możliwości technicznych. Serwis przetwarza zapytania według własnych zasad.

### Kopie zapasowe

- Eksport tworzy w folderze „Pobrane” plik w rodzaju `user_preferences_26_11_2026_02_49.dat`.
- Zawiera ulubione, subskrypcje, historię oglądania i pozycje w filmach. Kod PIN i ustawienia aplikacji nie wchodzą w skład kopii.
- Zawartość kopii jest skompresowana i zaszyfrowana, więc nie da się jej odczytać w edytorze tekstu. Klucz jest jednak wbudowany w aplikację i widoczny w kodzie źródłowym.
- Import dodaje brakujące dane i niczego nie usuwa. Historia przechowuje najwyżej 100 wpisów.
- Plik nie znika po wyczyszczeniu pamięci podręcznej ani po odinstalowaniu aplikacji. Jeśli nie jest już potrzebny, usuń go samodzielnie.

### Uprawnienia

| Uprawnienie | Do czego |
|---|---|
| Internet i stan sieci | pobieranie katalogu i filmów |
| Dostęp do pamięci (Android 10 i starsze) | tylko do eksportu i importu kopii; prośba pojawia się przy pierwszym użyciu |
| Dostęp do wszystkich plików (Android 11 i nowsze) | tylko do importu kopii z poprzedniej instalacji lub z innego urządzenia; nadawany ręcznie, bez niego pozostałe funkcje działają |

## Instalacja

Wymagany jest Android 7.0 lub nowszy.

1. Pobierz APK z sekcji **Releases** tego repozytorium.
2. Przenieś APK na telewizor w dogodny sposób, na przykład aplikacją LocalSend z telefonu lub przez SAMBA z komputera, i zainstaluj, wcześniej zezwalając na instalację z nieznanych źródeł.

Aplikację można też zainstalować przez ADB (atvTools z telefonu lub ADB App Control z komputera).

Instaluj tylko APK z tego repozytorium lub zbudowane samodzielnie: za cudze kompilacje autorzy nie odpowiadają.

## Budowanie ze źródeł

Potrzebne są JDK 17 lub nowszy i Android SDK (compileSdk 36). Ścieżkę do SDK ustawia się w `local.properties` lub w zmiennej `ANDROID_HOME`.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

W systemie Windows użyj `.\gradlew.bat`. Gotowy plik to `app/build/outputs/apk/release/app-release.apk`.

Bez dodatkowej konfiguracji wydanie jest podpisywane **kluczem debugowania** twojego komputera. Do rozpowszechniania utwórz własny klucz (`keytool`), a obok niego plik `keystore.properties`:

```properties
storeFile=release.p12
storePassword=twoje-hasło
keyAlias=twój-alias
keyPassword=twoje-hasło
```

Ścieżka w `storeFile` jest liczona od folderu tego pliku. Ścieżkę do samego `keystore.properties` podaj w `local.properties` (`privatetube.signing=...`) lub w zmiennej `PRIVATETUBE_SIGNING`. Przechowuj klucz poza repozytorium i zrób jego kopię zapasową. APK z innym podpisem nie zainstaluje się na poprzednim: przed usunięciem starej wersji wykonaj eksport.

## Jak to działa

| Folder w `app/src/main/java/com/privatetube/app/` | Zawartość |
|---|---|
| `core/network` | zapytania do serwisu, adresy i User-Agent |
| `core/parsing` | analiza stron; selektory zebrano w `ParsingRules.kt` |
| `data` | repozytorium, lokalne przechowywanie, pamięć podręczna, kopia zapasowa |
| `domain` | modele danych |
| `feature` | ekrany |
| `ui` | wspólne komponenty, fokus, ładowanie obrazów |

Jeśli serwis zmieni układ stron, w większości przypadków wystarczy poprawić `core/parsing`. Strony testowe w `app/src/test/resources/fixtures/` są zanonimizowane. Sprawdzenie na prawdziwym serwisie (`LiveSiteCheck`) uruchamia się tylko ręcznie ze zmienną `TV_LIVE=1`.

## Znane ograniczenia

- Aplikacja zależy od struktury i zasad serwisu i **może w każdej chwili przestać działać**.
- Nie ma logowania do konta w serwisie. Subskrypcje i ulubione są lokalne i nie synchronizują się z serwisem; nie ma powiadomień o nowych filmach.
- Płatne treści nie są odtwarzane.
- Część filmów może być niedostępna w twoim regionie. Serwis może ograniczać częstotliwość zapytań.
- Tłumaczeń nie sprawdzali rodzimi użytkownicy języków.
- W trakcie tworzenia aplikację sprawdzano testami i na emulatorze (telefonowy obraz Androida z ekranem 1080p). Działanie na konkretnych modelach telewizorów i przystawek nie jest gwarantowane.

## Wyłączenie odpowiedzialności

Pobierając, budując, instalując lub używając PrivateTube, akceptujesz poniższe warunki. Jeśli się z nimi nie zgadzasz, nie korzystaj z projektu.

1. **Projekt nieoficjalny.** PrivateTube to niezależny, niekomercyjny projekt. Nie jest związany z PornHub, Aylo ani powiązanymi z nimi spółkami, nie jest przez nie sponsorowany, zatwierdzony ani wspierany.
2. **Znaki towarowe.** Nazwy PornHub, Aylo i powiązane z nimi znaki należą do ich właścicieli. Są wymieniane wyłącznie po to, by wyjaśnić, z jakim serwisem działa program. Projekt nie rości sobie do nich praw.
3. **Treści.** W repozytorium i w APK nie ma filmów, zdjęć ani innych materiałów z serwisu. Autorzy projektu nie hostują, nie przesyłają, nie wybierają, nie moderują ani nie rozpowszechniają takich materiałów, a projekt nie ma serwerów pośredniczących. Urządzenie użytkownika otrzymuje dane bezpośrednio z serwisu i jego sieci dystrybucji. Na urządzeniu tymczasowo pozostają tylko dane opisane w sekcji „Prywatność”. Wszystkie prawa do materiałów należą do ich właścicieli.
4. **Zabezpieczenia i płatny dostęp.** Program nie służy do obchodzenia DRM, płatnego dostępu, logowania ani CAPTCHA i nie ma funkcji pobierania filmów.
5. **Regulamin serwisu.** Serwis ma własne warunki korzystania, które mogą ograniczać dostęp przez programy firm trzecich. Zapoznaj się z nimi. Decyzja o korzystaniu z projektu i odpowiedzialność za przestrzeganie tych warunków należą do ciebie.
6. **Prawo twojego kraju.** Przepisy o materiałach dla dorosłych, narzędziach do obchodzenia blokad, prawie autorskim i rozpowszechnianiu programów są różne. To ty odpowiadasz za to, by pobieranie, używanie i rozpowszechnianie projektu było legalne tam, gdzie jesteś. Jeśli jest zabronione, nie używaj go i nie rozpowszechniaj.
7. **Wiek.** Z projektu mogą korzystać tylko osoby, które ukończyły 18 lat lub osiągnęły wyższy wiek pełnoletności określony lokalnym prawem. Nie instaluj aplikacji na urządzeniach, do których dzieci mają dostęp bez nadzoru. Ekran potwierdzenia wieku i kod PIN nie zastępują kontroli rodzicielskiej.
8. **Zawartość serwisu.** Za materiały, ich legalność, opisy i moderację odpowiadają serwis oraz osoby, które je zamieściły. Autorzy projektu nie kontrolują tych materiałów. Nielegalne treści zgłaszaj administracji serwisu i właściwym organom.
9. **Brak gwarancji.** Program jest udostępniany „w stanie takim, w jakim jest”, bez wyraźnych ani dorozumianych gwarancji, w tym działania, przydatności do określonego celu, bezpieczeństwa i nienaruszania praw osób trzecich.
10. **Ograniczenie odpowiedzialności.** W maksymalnym zakresie dozwolonym przez prawo autorzy i współtwórcy projektu nie odpowiadają za szkody bezpośrednie, pośrednie i inne ani za skutki związane z używaniem lub niemożnością używania programu, w tym zablokowanie dostępu, utratę danych, ujawnienie danych osobowych i roszczenia osób trzecich.
11. **Zmienione wersje.** Autorzy nie odpowiadają za forki, cudze kompilacje i zmiany wprowadzone przez inne osoby.
12. **To nie jest porada prawna.** Ten tekst nie jest poradą prawną i nie uchyla praw i obowiązków wynikających z obowiązujących przepisów.

## Dla właścicieli praw

Szanujemy prawa własności intelektualnej. Jeśli jesteś właścicielem praw lub jego upoważnionym przedstawicielem i uważasz, że jakaś część tego repozytorium narusza twoje prawa, **utwórz Issue** z opisem: co dokładnie narusza prawa, o jakie prawa chodzi i jak się z tobą skontaktować (nie publikuj w Issue danych osobowych). Rozpatrzymy zgłoszenie i w razie potrzeby zmienimy lub usuniemy materiały. Nie ogranicza to twojego prawa do korzystania z oficjalnych procedur GitHub.

W sprawie filmów i innych materiałów w samym serwisie zwróć się do jego administracji: projekt ich nie zamieszcza i nie może ich usunąć.

## Komponenty innych autorów

- AndroidX i Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Licencja

Kod źródłowy jest udostępniany na [licencji MIT](../LICENSE). Nie daje ona żadnych praw do zawartości cudzych stron ani do cudzych znaków towarowych.
