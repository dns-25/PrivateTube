# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · [Español](README.es.md) · [Português](README.pt.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Italiano](README.it.md) · [Polski](README.pl.md) · [Nederlands](README.nl.md) · **Čeština** · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** Aplikace je určena pouze dospělým: zpřístupňuje web s obsahem pro dospělé.
>
> **Neoficiální projekt.** Nesouvisí se službou PornHub ani se společností, která web vlastní, a není jimi schválen ani podporován. Před použitím si přečtěte [vyloučení odpovědnosti](#vyloučení-odpovědnosti).

<img width="1920" height="1048" alt="1" src="https://github.com/user-attachments/assets/b29329cb-7717-4aec-b8c3-54f8219925bf" />

<details>
<summary><b>Další snímky obrazovky</b></summary>
<br>

<img width="1920" height="1048" alt="Screenshot 2" src="https://github.com/user-attachments/assets/8d56b490-c636-45df-a23e-f2c903b6f876" />
<img width="1920" height="1050" alt="Screenshot 3" src="https://github.com/user-attachments/assets/17fcf12e-51a6-4335-bdf5-9f5d08156b2f" />
<img width="1920" height="1050" alt="Screenshot 4" src="https://github.com/user-attachments/assets/25417d21-927a-4a62-8f7d-3307d988d653" />
<img width="1920" height="1053" alt="Screenshot 5" src="https://github.com/user-attachments/assets/7419ce69-33ec-477f-b95d-6c7fc13ec6db" />

</details>

## Co to je

PrivateTube je nativní klient webu PornHub pro **Android TV a Google TV**, napsaný v jazyce Kotlin (Jetpack Compose for TV, Media3).

Není to prohlížeč ani web v „obalu“: žádné WebView tu není. Zařízení uživatele si samo vyžádá veřejně dostupné stránky webu, aplikace z nich získá názvy, náhledy a odkazy na streamy a vše zobrazí ve vlastním rozhraní vytvořeném pro obyčejný dálkový ovladač.

## K čemu to je

Používat web na televizi je nepohodlné: drobné prvky, rozhraní pro myš a dotykovou obrazovku, vyskakovací okna. Televize potřebuje vlastní rozhraní: velké karty, zřetelné zvýraznění, rychlou odezvu a ovládání dálkovým ovladačem.

## Vytvořeno za pár večerů s pomocí AI

PrivateTube vznikl **doslova za pár večerů s pomocí AI**. Člověk zadával úkoly, kontroloval výsledek a říkal, co předělat. AI psala kód, rozebírala strukturu stránek, připravovala testy a opravovala chyby.

**Můj názor: tvůrci PornHubu by klidně mohli vytvořit oficiálního klienta podobného tomuto.** Podle mé zkušenosti je to rychlé, není to složité a nevyžaduje to tisíce dolarů na vývoj. Měli by to dokonce jednodušší: mají přímý přístup ke svým datům a nemusí rozebírat vlastní webové stránky. Oficiální produkt by samozřejmě potřeboval podporu a testování na různých zařízeních, ale pohodlný klient pro televizi je zcela zvládnutelný úkol.

Uživatelé budou jen rádi, když se objeví oficiální aplikace a tento projekt přestane být potřeba.

## Funkce

- Domovská obrazovka s nekonečným přehledem a boční nabídka s kategoriemi, kanály, modelkami a hledáním.
- Velké karty, automatické donačítání seznamů, plné ovládání dálkovým ovladačem bez vodorovného posouvání.
- Vlastní přehrávač (HLS): volba kvality, přetáčení po 30 sekundách (při podržení tlačítka rychlé), pokračování od uloženého místa.
- Nabídka po dlouhém stisku videa: pokračovat nebo začít od začátku, oblíbené, odběr autora, jeho další videa.
- Místní sekce „Odběry“, „Oblíbené“ a „Historie“.
- Místní export a import oblíbených, odběrů a historie.
- PIN při vstupu.
- 13 jazyků rozhraní: angličtina, ruština, ukrajinština, španělština, portugalština (brazilská), francouzština, němčina, italština, polština, nizozemština, čeština, japonština a čínština (zjednodušená). Jazyk se řídí systémovým, nebo se volí v nastavení.
- Názvy videí se berou z verze webu ve stejném jazyce. Web nepřekládá všechny názvy; v japonštině a čínštině je přeložených videí znatelně méně.
- Stejné měřítko na obrazovkách 720p, 1080p i 4K, dvě velikosti písma: střední a velká.

## Když se web neotevírá: VPN

**Pokud je web ve vaší zemi nebo u vašeho poskytovatele zablokovaný nebo se videa nenačítají, zapněte VPN** na televizi, TV boxu nebo routeru.

Nejprve se ujistěte, že používat VPN a otevírat takový obsah je **tam, kde se nacházíte, legální**. V některých zemích platí omezení. Dodržovat místní zákony je povinností uživatele. Projekt neposkytuje VPN, není spojen s žádnou službou VPN a nedoporučuje konkrétní služby.

## Soukromí

- Aplikace nepotřebuje žádné účty: ani Google, ani účet na webu.
- Neobsahuje služby Google ani vlastní analytiku a počítadla. Projekt nemá vlastní servery a autoři nedostávají žádná data.
- Videa a stránky webu se neukládají na disk. Náhledy videí se mažou při ukončení aplikace a znovu při dalším spuštění. Obrázky kategorií, kanálů, modelek a autorů zůstávají v mezipaměti (do 60 MB), dokud je nevytlačí nové nebo mezipaměť nevymažete v nastavení.
- Oblíbené, odběry, historie a pozice přehrávání se ukládají pouze v zařízení.
- PIN se ukládá jako sůl a otisk (hash). Chrání před náhodnými lidmi, ne před někým, kdo má k zařízení plný přístup.

**Stoprocentní anonymita neexistuje.** Web, váš poskytovatel internetu, správce sítě i služba VPN mohou v mezích svých technických možností nadále vidět údaje o vašem připojení. Web zpracovává požadavky podle vlastních pravidel.

### Zálohy

- Export vytvoří ve složce „Stažené soubory“ soubor typu `user_preferences_26_11_2026_02_49.dat`.
- Obsahuje oblíbené, odběry, historii sledování a pozice ve videích. PIN ani nastavení aplikace do zálohy nepatří.
- Obsah záloh je komprimovaný a zašifrovaný, v textovém editoru ho tedy nepřečtete. Klíč je ale zabudovaný v aplikaci a viditelný ve zdrojovém kódu.
- Import doplní, co chybí, a nic nemaže. Historie uchovává nejvýše 100 záznamů.
- Soubor nezmizí při vymazání mezipaměti ani při odinstalování aplikace. Pokud už ho nepotřebujete, smažte ho sami.

### Oprávnění

| Oprávnění | K čemu |
|---|---|
| Internet a stav sítě | načítání katalogu a videí |
| Přístup k úložišti (Android 10 a starší) | pouze pro export a import zálohy, vyžádá se při prvním použití |
| Přístup ke všem souborům (Android 11 a novější) | pouze pro import zálohy z předchozí instalace nebo z jiného zařízení; uděluje se ručně, ostatní funkce fungují i bez něj |

## Instalace

Je potřeba Android 7.0 nebo novější.

1. Stáhněte APK ze sekce **Releases** tohoto repozitáře.
2. Přeneste APK do televize způsobem, který vám vyhovuje, například aplikací LocalSend z telefonu nebo přes SAMBA z počítače, a po povolení instalace z neznámých zdrojů ho nainstalujte.

Aplikaci lze nainstalovat také přes ADB (atvTools z telefonu nebo ADB App Control z počítače).

Instalujte pouze APK z tohoto repozitáře nebo vámi sestavené: za cizí sestavení autoři neodpovídají.

## Sestavení ze zdrojového kódu

Potřebujete JDK 17 nebo novější a Android SDK (compileSdk 36). Cesta k SDK se nastavuje v `local.properties` nebo v proměnné `ANDROID_HOME`.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

Ve Windows použijte `.\gradlew.bat`. Výsledný soubor je `app/build/outputs/apk/release/app-release.apk`.

Bez dalšího nastavení se vydání podepisuje **ladicím klíčem** vašeho počítače. Pro distribuci si vytvořte vlastní klíč (`keytool`) a vedle něj soubor `keystore.properties`:

```properties
storeFile=release.p12
storePassword=vaše-heslo
keyAlias=váš-alias
keyPassword=vaše-heslo
```

Cesta v `storeFile` se počítá od složky tohoto souboru. Cestu k samotnému `keystore.properties` uveďte v `local.properties` (`privatetube.signing=...`) nebo v proměnné `PRIVATETUBE_SIGNING`. Klíč uchovávejte mimo repozitář a zálohujte ho. APK s jiným podpisem nelze nainstalovat přes předchozí: před odstraněním staré verze proveďte export.

## Jak je projekt uspořádán

| Složka v `app/src/main/java/com/privatetube/app/` | Obsah |
|---|---|
| `core/network` | požadavky na web, adresy a User-Agent |
| `core/parsing` | rozbor stránek; selektory jsou shromážděny v `ParsingRules.kt` |
| `data` | repozitář, místní úložiště, mezipaměť, záloha |
| `domain` | datové modely |
| `feature` | obrazovky |
| `ui` | společné komponenty, fokus, načítání obrázků |

Pokud web změní strukturu stránek, většinou stačí opravit `core/parsing`. Testovací stránky v `app/src/test/resources/fixtures/` jsou anonymizované. Kontrola na skutečném webu (`LiveSiteCheck`) se spouští pouze ručně s proměnnou `TV_LIVE=1`.

## Známá omezení

- Aplikace závisí na struktuře a pravidlech webu a **může kdykoli přestat fungovat**.
- Přihlášení k účtu na webu není. Odběry a oblíbené jsou místní a s webem se nesynchronizují; upozornění na nová videa nejsou.
- Placený obsah se nepřehrává.
- Některá videa nemusí být ve vaší oblasti dostupná. Web může omezovat četnost požadavků.
- Překlady nezkontrolovali rodilí mluvčí.
- Během vývoje se aplikace ověřovala testy a na emulátoru (telefonní obraz Androidu s obrazovkou 1080p). Fungování na konkrétních modelech televizí a TV boxů není zaručeno.

## Vyloučení odpovědnosti

Stažením, sestavením, instalací nebo používáním aplikace PrivateTube souhlasíte s podmínkami níže. Pokud s nimi nesouhlasíte, projekt nepoužívejte.

1. **Neoficiální projekt.** PrivateTube je nezávislý nekomerční projekt. Nesouvisí se společnostmi PornHub, Aylo ani s firmami s nimi spojenými, není jimi sponzorován, schválen ani podporován.
2. **Ochranné známky.** Názvy PornHub, Aylo a související známky patří jejich vlastníkům. Zmiňují se pouze proto, aby bylo jasné, se kterým webem program pracuje. Projekt si na ně nečiní nárok.
3. **Obsah.** Repozitář ani APK neobsahují videa, fotografie ani jiné materiály webu. Autoři projektu takové materiály neukládají, nenahrávají, nevybírají, nemoderují ani nešíří a projekt nemá žádné zprostředkující servery. Zařízení uživatele dostává data přímo od webu a z jeho distribučních sítí. V zařízení dočasně zůstávají jen údaje popsané v sekci „Soukromí“. Veškerá práva k materiálům patří jejich vlastníkům.
4. **Ochrana a placený přístup.** Program není určen k obcházení DRM, placeného přístupu, přihlášení ani CAPTCHA a nemá funkci stahování videí.
5. **Pravidla webu.** Web má vlastní podmínky používání, které mohou omezovat přístup prostřednictvím programů třetích stran. Seznamte se s nimi. Rozhodnutí používat projekt i odpovědnost za dodržování těchto podmínek jsou na vás.
6. **Zákony vaší země.** Zákony o obsahu pro dospělé, prostředcích k obcházení blokování, autorském právu a šíření programů se liší. Sami odpovídáte za to, že stažení, používání a šíření projektu je tam, kde se nacházíte, legální. Pokud je to zakázáno, projekt nepoužívejte a nešiřte.
7. **Věk.** Projekt smějí používat pouze osoby, kterým je alespoň 18 let nebo které dosáhly vyšší hranice zletilosti stanovené místním zákonem. Neinstalujte aplikaci do zařízení, ke kterým mají děti přístup bez dozoru. Obrazovka pro potvrzení věku a PIN nenahrazují rodičovskou kontrolu.
8. **Obsah webu.** Za materiály, jejich zákonnost, popisy a moderování odpovídá web a ti, kdo je zveřejnili. Autoři projektu tyto materiály nekontrolují. Nezákonný obsah hlaste správě webu a příslušným úřadům.
9. **Bez záruky.** Program je poskytován „tak, jak je“, bez výslovných či předpokládaných záruk, včetně funkčnosti, vhodnosti pro určitý účel, bezpečnosti a neporušování práv třetích osob.
10. **Omezení odpovědnosti.** V maximálním rozsahu povoleném zákonem autoři a přispěvatelé projektu neodpovídají za přímé, nepřímé ani jiné škody a následky spojené s používáním nebo nemožností používat program, včetně zablokování přístupu, ztráty dat, vyzrazení osobních údajů a nároků třetích osob.
11. **Upravené verze.** Autoři neodpovídají za forky, cizí sestavení a změny provedené jinými lidmi.
12. **Nejde o právní poradenství.** Tento text není právní radou a neruší práva a povinnosti vyplývající z příslušných právních předpisů.

## Pro držitele práv

Respektujeme práva k duševnímu vlastnictví. Pokud jste držitelem práv nebo jeho pověřeným zástupcem a domníváte se, že některá část tohoto repozitáře porušuje vaše práva, **vytvořte Issue** s popisem: co přesně práva porušuje, o jaká práva jde a jak vás kontaktovat (do Issue nezveřejňujte osobní údaje). Podnět posoudíme a v případě potřeby materiály upravíme nebo odstraníme. Neomezuje to vaše právo využít oficiální postupy GitHubu.

Ohledně videí a dalších materiálů na samotném webu se obraťte na jeho správu: projekt je nezveřejňuje a odstranit je nemůže.

## Komponenty třetích stran

- AndroidX a Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Licence

Zdrojový kód je šířen pod [licencí MIT](../LICENSE). Ta nedává žádná práva k obsahu cizích webů ani k cizím ochranným známkám.
