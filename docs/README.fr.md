# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · [Español](README.es.md) · [Português](README.pt.md) · **Français** · [Deutsch](README.de.md) · [Italiano](README.it.md) · [Polski](README.pl.md) · [Nederlands](README.nl.md) · [Čeština](README.cs.md) · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** L'application est réservée aux adultes : elle donne accès à un site contenant des contenus pour adultes.
>
> **Projet non officiel.** Il n'est lié ni à PornHub ni à la société propriétaire du site, et n'est ni approuvé ni soutenu par eux. Avant toute utilisation, lisez la [clause de non-responsabilité](#clause-de-non-responsabilité).

## Qu'est-ce que c'est

PrivateTube est un client natif du site PornHub pour **Android TV et Google TV**, écrit en Kotlin (Jetpack Compose for TV, Media3).

Ce n'est ni un navigateur ni un site « emballé » : il n'y a pas de WebView. L'appareil de l'utilisateur demande lui-même les pages publiques du site ; l'application en extrait les titres, les aperçus et les liens vers les flux, puis affiche le tout dans sa propre interface, conçue pour une télécommande ordinaire.

## À quoi ça sert

Utiliser le site sur un téléviseur est peu pratique : éléments minuscules, interface pensée pour la souris et l'écran tactile, fenêtres surgissantes. Un téléviseur a besoin de sa propre interface : grandes vignettes, sélection bien visible, réactivité et commande à la télécommande.

## Réalisé en quelques soirées avec l'aide de l'IA

PrivateTube a été réalisé **littéralement en quelques soirées avec l'aide de l'IA**. Une personne définissait les tâches, vérifiait le résultat et indiquait ce qu'il fallait refaire. L'IA écrivait le code, analysait le balisage des pages, préparait les tests et corrigeait les erreurs.

**Mon avis : les créateurs de PornHub pourraient tout à fait proposer un client officiel de ce genre.** D'après mon expérience, c'est rapide, pas compliqué et cela ne demande pas des milliers de dollars de développement. Ce serait même plus simple pour eux : ils ont un accès direct à leurs données et n'ont pas besoin d'analyser leurs propres pages web. Bien sûr, un produit officiel nécessiterait une assistance et des tests sur divers appareils, mais un client agréable pour téléviseur est une tâche tout à fait réalisable.

Les utilisateurs ne pourront que se réjouir si une application officielle voit le jour et que ce projet devient inutile.

## Fonctionnalités

- Un accueil au fil sans fin, ainsi qu'un menu latéral avec les catégories, les chaînes, les modèles et la recherche.
- De grandes vignettes, des listes qui se chargent automatiquement, une commande complète à la télécommande sans défilement horizontal.
- Un lecteur intégré (HLS) : choix de la qualité, sauts de 30 secondes (avance rapide si le bouton est maintenu), reprise à l'endroit enregistré.
- Un menu par appui long sur une vidéo : reprendre ou recommencer, favoris, abonnement à l'auteur, ses autres vidéos.
- Des rubriques locales « Abonnements », « Favoris » et « Historique ».
- Exportation et importation locales des favoris, des abonnements et de l'historique.
- Un code PIN à l'entrée.
- 13 langues d'interface : anglais, russe, ukrainien, espagnol, portugais (du Brésil), français, allemand, italien, polonais, néerlandais, tchèque, japonais et chinois (simplifié). La langue suit celle du système ou se choisit dans les paramètres.
- Les titres des vidéos proviennent de la version du site dans la même langue. Le site ne traduit pas tous les titres ; en japonais et en chinois, les vidéos traduites sont nettement moins nombreuses.
- La même échelle sur les écrans 720p, 1080p et 4K, deux tailles de texte : moyenne et grande.

## Si le site ne s'ouvre pas : VPN

**Si le site est bloqué dans votre pays ou par votre fournisseur d'accès, ou si les vidéos ne se chargent pas, activez un VPN** sur le téléviseur, le boîtier ou le routeur.

Vérifiez d'abord qu'utiliser un VPN et consulter ce type de contenu est **légal là où vous vous trouvez**. Certains pays imposent des restrictions. Le respect des lois locales incombe à l'utilisateur. Le projet ne fournit pas de VPN, n'est lié à aucun service VPN et ne recommande aucun service en particulier.

## Confidentialité

- L'application n'a besoin d'aucun compte : ni Google, ni compte du site.
- Elle ne contient ni services Google, ni outils d'analyse ou compteurs propres. Le projet n'a pas de serveurs et les auteurs ne reçoivent aucune donnée.
- Les vidéos et les pages du site ne sont pas enregistrées sur le disque. Les aperçus des vidéos sont supprimés à la fermeture de l'application puis de nouveau au démarrage suivant. Les images des catégories, chaînes, modèles et auteurs restent dans le cache (jusqu'à 60 Mo) tant que de nouvelles ne les remplacent pas ou que vous ne videz pas le cache dans les paramètres.
- Les favoris, les abonnements, l'historique et les positions de lecture sont conservés uniquement sur l'appareil.
- Le code PIN est conservé sous forme de sel et d'empreinte. Il protège contre les curieux, pas contre une personne disposant d'un accès complet à l'appareil.

**L'anonymat à 100 % n'existe pas.** Le site, votre fournisseur d'accès, l'administrateur du réseau et le service VPN peuvent toujours voir des informations sur votre connexion, dans la limite de leurs moyens techniques. Le site traite les requêtes selon ses propres règles.

### Sauvegardes

- L'exportation crée dans le dossier « Téléchargements » un fichier du type `user_preferences_26_11_2026_02_49.dat`.
- Il contient les favoris, les abonnements, l'historique et les positions dans les vidéos. Le code PIN et les paramètres de l'application n'y figurent pas.
- Le contenu des sauvegardes est compressé et chiffré : il est illisible dans un éditeur de texte. Mais la clé est intégrée à l'application et visible dans le code source.
- L'importation ajoute ce qui manque et ne supprime rien. L'historique conserve au maximum 100 entrées.
- Le fichier ne disparaît ni quand on vide le cache ni quand on désinstalle l'application. Si vous n'en avez plus besoin, supprimez-le vous-même.

### Autorisations

| Autorisation | Utilité |
|---|---|
| Internet et état du réseau | chargement du catalogue et des vidéos |
| Accès au stockage (Android 10 et antérieur) | uniquement pour exporter et importer la sauvegarde, demandé à la première utilisation |
| Accès à tous les fichiers (Android 11 et plus récent) | uniquement pour importer une sauvegarde d'une installation précédente ou d'un autre appareil ; accordé manuellement, le reste fonctionne sans lui |

## Installation

Android 7.0 ou plus récent est nécessaire.

1. Téléchargez l'APK dans la section **Releases** de ce dépôt.
2. Transférez l'APK sur le téléviseur de la manière qui vous convient, par exemple avec l'application LocalSend depuis un téléphone ou par SAMBA depuis un ordinateur, puis installez-le après avoir autorisé l'installation depuis des sources inconnues.

Vous pouvez aussi installer l'application par ADB (atvTools depuis un téléphone ou ADB App Control depuis un ordinateur).

N'installez que des APK provenant de ce dépôt ou compilés par vous : les auteurs ne répondent pas des versions compilées par d'autres.

## Compilation depuis les sources

Il faut un JDK 17 ou plus récent et le SDK Android (compileSdk 36). Le chemin du SDK se définit dans `local.properties` ou dans la variable `ANDROID_HOME`.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

Sous Windows, utilisez `.\gradlew.bat`. Le fichier obtenu est `app/build/outputs/apk/release/app-release.apk`.

Sans configuration, la version de publication est signée avec la **clé de débogage** de votre ordinateur. Pour la diffuser, créez votre propre clé (`keytool`) et, à côté, un fichier `keystore.properties` :

```properties
storeFile=release.p12
storePassword=votre-mot-de-passe
keyAlias=votre-alias
keyPassword=votre-mot-de-passe
```

Le chemin indiqué dans `storeFile` est relatif au dossier de ce fichier. Indiquez le chemin de `keystore.properties` lui-même dans `local.properties` (`privatetube.signing=...`) ou dans la variable `PRIVATETUBE_SIGNING`. Conservez la clé hors du dépôt et sauvegardez-la. Un APK signé différemment ne s'installera pas par-dessus le précédent : exportez vos données avant de supprimer l'ancienne version.

## Organisation du code

| Dossier dans `app/src/main/java/com/privatetube/app/` | Contenu |
|---|---|
| `core/network` | requêtes vers le site, adresses et User-Agent |
| `core/parsing` | analyse des pages ; les sélecteurs sont réunis dans `ParsingRules.kt` |
| `data` | dépôt de données, stockage local, cache, sauvegarde |
| `domain` | modèles de données |
| `feature` | écrans |
| `ui` | composants communs, gestion du focus, chargement des images |

Si le site change sa mise en page, il suffit le plus souvent de corriger `core/parsing`. Les pages de test de `app/src/test/resources/fixtures/` sont anonymisées. La vérification sur le site réel (`LiveSiteCheck`) ne se lance que manuellement, avec la variable `TV_LIVE=1`.

## Limites connues

- L'application dépend de la mise en page et des règles du site et **peut cesser de fonctionner à tout moment**.
- La connexion à un compte du site n'est pas possible. Les abonnements et les favoris sont locaux et ne sont pas synchronisés avec le site ; il n'y a pas de notifications de nouvelles vidéos.
- Les contenus payants ne sont pas lus.
- Certaines vidéos peuvent être indisponibles dans votre région. Le site peut limiter la fréquence des requêtes.
- Les traductions n'ont pas été relues par des locuteurs natifs.
- Pendant le développement, l'application a été vérifiée par des tests et sur un émulateur (image Android de téléphone avec un écran 1080p). Le fonctionnement sur des modèles précis de téléviseurs et de boîtiers n'est pas garanti.

## Clause de non-responsabilité

En téléchargeant, compilant, installant ou utilisant PrivateTube, vous acceptez les conditions ci-dessous. Si vous ne les acceptez pas, n'utilisez pas le projet.

1. **Projet non officiel.** PrivateTube est un projet indépendant et non commercial. Il n'est pas lié à PornHub, à Aylo ni aux sociétés qui leur sont associées, et n'est ni sponsorisé, ni approuvé, ni soutenu par eux.
2. **Marques.** Les noms PornHub, Aylo et les marques associées appartiennent à leurs titulaires. Ils ne sont mentionnés que pour expliquer avec quel site le programme fonctionne. Le projet ne revendique aucun droit sur eux.
3. **Contenus.** Le dépôt et l'APK ne contiennent ni vidéos, ni photos, ni autres contenus du site. Les auteurs du projet n'hébergent, ne mettent en ligne, ne sélectionnent, ne modèrent ni ne diffusent ces contenus, et le projet n'a pas de serveurs intermédiaires. L'appareil de l'utilisateur reçoit les données directement du site et de ses réseaux de diffusion. Seules les données décrites dans la rubrique « Confidentialité » restent temporairement sur l'appareil. Tous les droits sur les contenus appartiennent à leurs titulaires.
4. **Protections et accès payant.** Le programme n'est pas destiné à contourner les DRM, un accès payant, une authentification ou un CAPTCHA, et n'a pas de fonction de téléchargement de vidéos.
5. **Règles du site.** Le site a ses propres conditions d'utilisation, qui peuvent restreindre l'accès par des programmes tiers. Prenez-en connaissance. La décision d'utiliser le projet et la responsabilité du respect de ces conditions vous incombent.
6. **Lois de votre pays.** Les lois sur les contenus pour adultes, les outils de contournement des blocages, le droit d'auteur et la diffusion de logiciels varient. Il vous appartient de vérifier que le téléchargement, l'utilisation et la diffusion du projet sont légaux là où vous vous trouvez. Si c'est interdit, ne l'utilisez pas et ne le diffusez pas.
7. **Âge.** Seules les personnes âgées d'au moins 18 ans, ou ayant atteint la majorité plus élevée fixée par la loi locale, peuvent utiliser le projet. N'installez pas l'application sur des appareils auxquels des enfants ont accès sans surveillance. L'écran de confirmation de l'âge et le code PIN ne remplacent pas un contrôle parental.
8. **Contenu du site.** Le site et les personnes qui ont publié les contenus en sont responsables, comme de leur légalité, de leurs descriptions et de leur modération. Les auteurs du projet ne contrôlent pas ces contenus. Signalez les contenus illégaux à l'administration du site et aux autorités compétentes.
9. **Aucune garantie.** Le programme est fourni « en l'état », sans aucune garantie expresse ou implicite, notamment de bon fonctionnement, d'adéquation à un usage particulier, de sécurité ou d'absence d'atteinte aux droits de tiers.
10. **Limitation de responsabilité.** Dans toute la mesure permise par la loi, les auteurs et contributeurs du projet ne répondent pas des dommages directs, indirects ou autres, ni des conséquences liées à l'utilisation ou à l'impossibilité d'utiliser le programme, y compris le blocage d'accès, la perte de données, la divulgation d'informations personnelles et les réclamations de tiers.
11. **Versions modifiées.** Les auteurs ne répondent pas des forks, des versions compilées par des tiers ni des modifications faites par d'autres personnes.
12. **Pas un avis juridique.** Ce texte ne constitue pas un avis juridique et ne supprime pas les droits et obligations prévus par la loi applicable.

## Pour les ayants droit

Nous respectons les droits de propriété intellectuelle. Si vous êtes titulaire de droits ou son représentant autorisé et estimez qu'une partie de ce dépôt porte atteinte à vos droits, **créez une Issue** en précisant ce qui porte atteinte aux droits, de quels droits il s'agit et comment vous contacter (ne publiez pas de données personnelles dans l'Issue). Nous examinerons la demande et, si nécessaire, modifierons ou supprimerons les éléments concernés. Cela ne limite pas votre droit de recourir aux procédures officielles de GitHub.

Pour les vidéos et autres contenus du site lui-même, adressez-vous à son administration : le projet n'héberge pas ces contenus et ne peut pas les supprimer.

## Composants tiers

- AndroidX et Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Licence

Le code source est distribué sous [licence MIT](../LICENSE). Elle n'accorde aucun droit sur le contenu de sites tiers ni sur les marques d'autrui.
