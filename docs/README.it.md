# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · [Español](README.es.md) · [Português](README.pt.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · **Italiano** · [Polski](README.pl.md) · [Nederlands](README.nl.md) · [Čeština](README.cs.md) · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** L'app è destinata esclusivamente ai maggiorenni: dà accesso a un sito con materiale per adulti.
>
> **Progetto non ufficiale.** Non è collegato a PornHub né alla società proprietaria del sito, e non è approvato né supportato da loro. Prima dell'uso leggi l'[esclusione di responsabilità](#esclusione-di-responsabilità).

## Che cos'è

PrivateTube è un client nativo del sito PornHub per **Android TV e Google TV**, scritto in Kotlin (Jetpack Compose for TV, Media3).

Non è un browser né un sito «impacchettato»: non c'è alcuna WebView. È il dispositivo dell'utente a richiedere le pagine pubbliche del sito; l'app ne ricava titoli, anteprime e collegamenti ai flussi e mostra tutto in un'interfaccia propria, pensata per un normale telecomando.

## A cosa serve

Usare il sito sul televisore è scomodo: elementi minuscoli, un'interfaccia pensata per mouse e touchscreen, finestre popup. Un televisore ha bisogno di un'interfaccia propria: schede grandi, selezione ben visibile, risposta rapida e comandi da telecomando.

## Realizzato in un paio di serate con l'aiuto dell'IA

PrivateTube è stato realizzato **letteralmente in un paio di serate con l'aiuto dell'IA**. Una persona assegnava i compiti, controllava il risultato e diceva cosa rifare. L'IA scriveva il codice, analizzava il markup delle pagine, preparava i test e correggeva gli errori.

**La mia opinione: i creatori di PornHub potrebbero benissimo realizzare un client ufficiale come questo.** Per la mia esperienza è un lavoro rapido, non difficile e che non richiede migliaia di dollari di sviluppo. Per loro sarebbe perfino più semplice: hanno accesso diretto ai propri dati e non devono analizzare le proprie pagine web. Certo, un prodotto ufficiale richiederebbe assistenza e prove su dispositivi diversi, ma un client comodo per la TV è un compito del tutto affrontabile.

Gli utenti non potranno che essere contenti se uscirà un'app ufficiale e questo progetto non servirà più.

## Funzioni

- Schermata Home con un flusso senza fine e un menu laterale con categorie, canali, modelle e ricerca.
- Schede grandi, elenchi che si caricano da soli, comandi completi da telecomando senza scorrimento orizzontale.
- Lettore proprio (HLS): scelta della qualità, salti di 30 secondi (rapidi tenendo premuto il tasto), ripresa dal punto salvato.
- Menu con pressione prolungata su un video: riprendi o ricomincia, preferiti, iscrizione all'autore, i suoi altri video.
- Sezioni locali «Iscrizioni», «Preferiti» e «Cronologia».
- Esportazione e importazione locali di preferiti, iscrizioni e cronologia.
- Codice PIN all'ingresso.
- 13 lingue dell'interfaccia: inglese, russo, ucraino, spagnolo, portoghese (brasiliano), francese, tedesco, italiano, polacco, olandese, ceco, giapponese e cinese (semplificato). La lingua segue quella di sistema o si sceglie nelle impostazioni.
- I titoli dei video provengono dalla versione del sito nella stessa lingua. Il sito non traduce tutti i titoli; in giapponese e cinese i video tradotti sono molti meno.
- La stessa scala su schermi 720p, 1080p e 4K, due dimensioni del testo: media e grande.

## Se il sito non si apre: VPN

**Se nel tuo paese o presso il tuo provider il sito è bloccato, o i video non si caricano, attiva una VPN** sul televisore, sul box TV o sul router.

Prima assicurati che usare una VPN e accedere a materiali di questo tipo sia **legale nel luogo in cui ti trovi**. In alcuni paesi esistono restrizioni. Rispettare le leggi locali è dovere dell'utente. Il progetto non fornisce VPN, non è collegato ad alcun servizio VPN e non consiglia servizi specifici.

## Privacy

- L'app non richiede alcun account: né Google né un account del sito.
- Non contiene servizi Google, né strumenti di analisi o contatori propri. Il progetto non ha server propri e gli autori non ricevono alcun dato.
- I video e le pagine del sito non vengono salvati su disco. Le anteprime dei video vengono cancellate all'uscita dall'app e di nuovo all'avvio successivo. Le immagini di categorie, canali, modelle e autori restano nella cache (fino a 60 MB) finché non vengono sostituite da quelle nuove o non svuoti la cache nelle impostazioni.
- Preferiti, iscrizioni, cronologia e posizioni di visione sono conservati solo sul dispositivo.
- Il codice PIN è conservato come salt e hash. Protegge da persone occasionali, non da chi ha accesso completo al dispositivo.

**L'anonimato al 100% non esiste.** Il sito, il tuo provider internet, l'amministratore di rete e il servizio VPN possono comunque vedere informazioni sulla tua connessione, nei limiti delle loro capacità tecniche. Il sito gestisce le richieste secondo le proprie regole.

### Copie di backup

- L'esportazione crea nella cartella «Download» un file del tipo `user_preferences_26_11_2026_02_49.dat`.
- Contiene preferiti, iscrizioni, cronologia e posizioni nei video. Il codice PIN e le impostazioni dell'app non sono inclusi.
- Il contenuto dei backup è compresso e cifrato, quindi non è leggibile in un editor di testo. La chiave però è incorporata nell'app e visibile nel codice sorgente.
- L'importazione aggiunge ciò che manca e non cancella nulla. La cronologia conserva al massimo 100 voci.
- Il file non scompare svuotando la cache o disinstallando l'app. Se non ti serve più, cancellalo tu.

### Autorizzazioni

| Autorizzazione | A cosa serve |
|---|---|
| Internet e stato della rete | caricamento del catalogo e dei video |
| Accesso alla memoria (Android 10 e precedenti) | solo per esportare e importare il backup, richiesto al primo utilizzo |
| Accesso a tutti i file (Android 11 e successivi) | solo per importare il backup di un'installazione precedente o di un altro dispositivo; si concede manualmente e senza di essa il resto funziona |

## Installazione

Serve Android 7.0 o successivo.

1. Scarica l'APK dalla sezione **Releases** di questo repository.
2. Trasferisci l'APK sul televisore nel modo che preferisci, per esempio con l'app LocalSend dal telefono o tramite SAMBA dal computer, e installalo dopo aver consentito l'installazione da origini sconosciute.

Puoi installare l'app anche tramite ADB (atvTools dal telefono o ADB App Control dal computer).

Installa solo APK di questo repository o compilati da te: gli autori non rispondono delle build altrui.

## Compilazione dai sorgenti

Servono JDK 17 o successivo e l'Android SDK (compileSdk 36). Il percorso dell'SDK si imposta in `local.properties` o nella variabile `ANDROID_HOME`.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

Su Windows usa `.\gradlew.bat`. Il file risultante è `app/build/outputs/apk/release/app-release.apk`.

Senza ulteriori impostazioni, la release viene firmata con la **chiave di debug** del tuo computer. Per distribuirla, crea una tua chiave (`keytool`) e accanto un file `keystore.properties`:

```properties
storeFile=release.p12
storePassword=la-tua-password
keyAlias=il-tuo-alias
keyPassword=la-tua-password
```

Il percorso in `storeFile` è relativo alla cartella di quel file. Indica il percorso di `keystore.properties` in `local.properties` (`privatetube.signing=...`) o nella variabile `PRIVATETUBE_SIGNING`. Conserva la chiave fuori dal repository e fanne una copia di backup. Un APK con una firma diversa non si installa sopra il precedente: esporta i dati prima di rimuovere la vecchia versione.

## Com'è organizzato

| Cartella in `app/src/main/java/com/privatetube/app/` | Contenuto |
|---|---|
| `core/network` | richieste al sito, indirizzi e User-Agent |
| `core/parsing` | analisi delle pagine; i selettori sono raccolti in `ParsingRules.kt` |
| `data` | repository, archiviazione locale, cache, backup |
| `domain` | modelli di dati |
| `feature` | schermate |
| `ui` | componenti comuni, focus, caricamento delle immagini |

Se il sito cambia il markup, nella maggior parte dei casi basta correggere `core/parsing`. Le pagine di prova in `app/src/test/resources/fixtures/` sono anonimizzate. La verifica sul sito reale (`LiveSiteCheck`) si avvia solo manualmente con la variabile `TV_LIVE=1`.

## Limiti noti

- L'app dipende dal markup e dalle regole del sito e **può smettere di funzionare in qualsiasi momento**.
- Non è possibile accedere a un account del sito. Iscrizioni e preferiti sono locali e non si sincronizzano con il sito; non ci sono notifiche sui nuovi video.
- I contenuti a pagamento non vengono riprodotti.
- Alcuni video potrebbero non essere disponibili nella tua regione. Il sito può limitare la frequenza delle richieste.
- Le traduzioni non sono state controllate da madrelingua.
- Durante lo sviluppo l'app è stata verificata con test e su un emulatore (immagine Android per telefono con schermo 1080p). Il funzionamento su specifici modelli di televisori e box TV non è garantito.

## Esclusione di responsabilità

Scaricando, compilando, installando o usando PrivateTube accetti le condizioni seguenti. Se non le accetti, non usare il progetto.

1. **Progetto non ufficiale.** PrivateTube è un progetto indipendente e non commerciale. Non è collegato a PornHub, Aylo o società a essi collegate, e non è sponsorizzato, approvato né supportato da loro.
2. **Marchi.** I nomi PornHub, Aylo e i marchi correlati appartengono ai rispettivi titolari. Sono citati solo per spiegare con quale sito funziona il programma. Il progetto non rivendica alcun diritto su di essi.
3. **Contenuti.** Il repository e l'APK non contengono video, foto o altri materiali del sito. Gli autori del progetto non ospitano, caricano, selezionano, moderano né distribuiscono tali materiali, e il progetto non ha server intermediari. Il dispositivo dell'utente riceve i dati direttamente dal sito e dalle sue reti di distribuzione. Sul dispositivo restano temporaneamente solo i dati descritti nella sezione «Privacy». Tutti i diritti sui materiali appartengono ai rispettivi titolari.
4. **Protezioni e accesso a pagamento.** Il programma non è destinato ad aggirare DRM, accesso a pagamento, autenticazione o CAPTCHA e non ha una funzione per scaricare i video.
5. **Regole del sito.** Il sito ha condizioni d'uso proprie, che possono limitare l'accesso tramite programmi di terze parti. Leggile. La decisione di usare il progetto e la responsabilità del rispetto di tali condizioni spettano a te.
6. **Leggi del tuo paese.** Le leggi sui materiali per adulti, sugli strumenti per aggirare i blocchi, sul diritto d'autore e sulla distribuzione di software variano. Sei tu a dover garantire che scaricare, usare e distribuire il progetto sia legale dove ti trovi. Se è vietato, non usarlo e non distribuirlo.
7. **Età.** Il progetto può essere usato solo da chi ha compiuto 18 anni o ha raggiunto la maggiore età più alta stabilita dalla legge locale. Non installare l'app su dispositivi accessibili ai bambini senza sorveglianza. La schermata di conferma dell'età e il codice PIN non sostituiscono il controllo parentale.
8. **Contenuti del sito.** Dei materiali, della loro legalità, delle descrizioni e della moderazione rispondono il sito e chi li ha pubblicati. Gli autori del progetto non controllano tali materiali. Segnala i contenuti illegali all'amministrazione del sito e alle autorità competenti.
9. **Nessuna garanzia.** Il programma è fornito «così com'è», senza garanzie esplicite o implicite, comprese quelle di funzionamento, idoneità a uno scopo specifico, sicurezza e assenza di violazioni di diritti di terzi.
10. **Limitazione di responsabilità.** Nella misura massima consentita dalla legge, gli autori e i collaboratori del progetto non rispondono di danni diretti, indiretti o di altro tipo né delle conseguenze legate all'uso o all'impossibilità di usare il programma, inclusi blocco dell'accesso, perdita di dati, divulgazione di informazioni personali e pretese di terzi.
11. **Versioni modificate.** Gli autori non rispondono di fork, build di terzi e modifiche fatte da altre persone.
12. **Non è una consulenza legale.** Questo testo non è una consulenza legale e non annulla i diritti e gli obblighi previsti dalla legge applicabile.

## Per i titolari dei diritti

Rispettiamo i diritti di proprietà intellettuale. Se sei un titolare di diritti o un suo rappresentante autorizzato e ritieni che una parte di questo repository violi i tuoi diritti, **crea una Issue** indicando che cosa esattamente li viola, di quali diritti si tratta e come contattarti (non pubblicare dati personali nella Issue). Esamineremo la richiesta e, se necessario, modificheremo o rimuoveremo il materiale. Ciò non limita il tuo diritto di usare le procedure ufficiali di GitHub.

Per i video e gli altri materiali presenti sul sito stesso, rivolgiti alla sua amministrazione: il progetto non ospita quei materiali e non può rimuoverli.

## Componenti di terze parti

- AndroidX e Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Licenza

Il codice sorgente è distribuito con [licenza MIT](../LICENSE). Essa non concede alcun diritto sui contenuti di siti di terzi né su marchi altrui.
