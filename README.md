# PrivateTube

**English** · [Русский](docs/README.ru.md) · [Українська](docs/README.uk.md) · [Español](docs/README.es.md) · [Português](docs/README.pt.md) · [Français](docs/README.fr.md) · [Deutsch](docs/README.de.md)
[Italiano](docs/README.it.md) · [Polski](docs/README.pl.md) · [Nederlands](docs/README.nl.md) · [Čeština](docs/README.cs.md) · [日本語](docs/README.ja.md) · [中文](docs/README.zh.md)

> **18+.** This app is for adults only: it gives access to a website with adult material.
>
> **Unofficial project.** It is not affiliated with PornHub or the company that owns the site, and is not endorsed or supported by them. Read the [disclaimer](#disclaimer) before use.

## What it is

PrivateTube is a native PornHub client for **Android TV and Google TV**, written in Kotlin (Jetpack Compose for TV, Media3).

It is neither a browser nor a website in a wrapper: there is no WebView. The user's device requests the site's publicly available pages itself, and the app extracts titles, previews and stream links from them and shows everything in its own interface, built for an ordinary remote.

## Why it exists

Using the website on a TV is awkward: tiny elements, an interface made for a mouse and a touchscreen, pop-ups. A TV needs its own interface: large cards, clear focus, quick response and remote control.

## Made in a couple of evenings with AI

PrivateTube was made **in literally a couple of evenings with the help of AI**. A human set the tasks, checked the result and said what to redo. The AI wrote the code, analyzed the page markup, prepared tests and fixed bugs.

**My opinion: the creators of PornHub could perfectly well make an official client like this one.** In my experience it is quick, not difficult and does not take thousands of dollars of development. It would be even easier for them: they have direct access to their own data and have no need to parse their own web pages. Of course, an official product would need support and testing on various devices, but a convenient TV client is a very manageable task.

Users will only be glad if an official app appears and this project is no longer needed.

## Features

- A home screen with an endless feed, plus a side menu with categories, channels, models and search.
- Large cards, lists that load more automatically, full remote control with no horizontal scrolling.
- Its own player (HLS): quality selection, 30-second seeking (fast while the button is held), resuming from the saved position.
- A long-press menu on a video: resume or start over, favorites, subscribing to the uploader, their other videos.
- Local “Subscriptions”, “Favorites” and “History” sections.
- Local export and import of favorites, subscriptions and history.
- A PIN code at startup.
- 13 interface languages: English, Russian, Ukrainian, Spanish, Portuguese (Brazilian), French, German, Italian, Polish, Dutch, Czech, Japanese and Chinese (Simplified). The language follows the system one or is chosen in the settings.
- Video titles come from the site's edition in the same language. The site does not translate every title; Japanese and Chinese have noticeably fewer translated videos.
- The same scale on 720p, 1080p and 4K screens, two text sizes: medium and large.

## If the site does not open: VPN

**If the site is blocked in your country or by your provider, or videos do not load, turn on a VPN** on the TV, set-top box or router.

First make sure that using a VPN and opening such material is **legal where you are**. Some countries restrict this. Following local law is the user's responsibility. The project does not provide a VPN, is not affiliated with any VPN service and does not recommend particular services.

## Privacy

- The app needs no accounts at all: neither Google nor a site account.
- It contains no Google services, and no analytics or counters of its own. The project has no servers of its own, and the authors receive no data.
- Videos and site pages are not saved to disk. Video previews are deleted when you leave the app and once more at the next start. Pictures of categories, channels, models and uploaders stay in the cache (up to 60 MB) until newer ones push them out or you clear the cache in the settings.
- Favorites, subscriptions, history and playback positions are stored only on the device.
- The PIN code is stored as a salt and hash. It protects against casual users, not against someone with full access to the device.

**There is no 100% anonymity.** The site, your internet provider, the network administrator and the VPN service can still see information about your connection within their technical capabilities. The site handles requests according to its own rules.

### Backups

- Export creates a file such as `user_preferences_26_11_2026_02_49.dat` in the Downloads folder.
- It contains favorites, subscriptions, watch history and video positions. The PIN code and app settings are not included.
- Backup contents are compressed and encrypted, so they cannot be read in a text editor. However, the key is built into the app and visible in the source code.
- Import adds what is missing and deletes nothing. History keeps no more than 100 entries.
- The file does not disappear when you clear the cache or uninstall the app. If you no longer need it, delete it yourself.

### Permissions

| Permission | Purpose |
|---|---|
| Internet and network state | loading the catalog and videos |
| Storage access (Android 10 and older) | only for exporting and importing a backup, requested on first use |
| All files access (Android 11 and newer) | only to import a backup made by a previous installation or on another device; granted manually, everything else works without it |

## Installation

Android 7.0 or newer is required.

1. Download the APK from the **Releases** section of this repository.
2. Transfer the APK to the TV in whatever way suits you, for example with the LocalSend app from a phone or over SAMBA from a computer, then install it after allowing installation from unknown sources.

You can also install the app over ADB (atvTools from a phone or ADB App Control from a computer).

Install only APKs from this repository or ones you built yourself: the authors are not responsible for other people's builds.

## Building from source

You need JDK 17 or newer and the Android SDK (compileSdk 36). Set the SDK path in `local.properties` or in the `ANDROID_HOME` variable.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

On Windows use `.\gradlew.bat`. The resulting file is `app/build/outputs/apk/release/app-release.apk`.

Without further setup the release is signed with your computer's **debug key**. For distribution, create your own key (`keytool`) and, next to it, a `keystore.properties` file:

```properties
storeFile=release.p12
storePassword=your-password
keyAlias=your-alias
keyPassword=your-password
```

The path in `storeFile` is relative to this file's folder. Set the path to `keystore.properties` itself in `local.properties` (`privatetube.signing=...`) or in the `PRIVATETUBE_SIGNING` variable. Keep the key outside the repository and back it up. An APK with a different signature will not install over the previous one: export your data before removing the old version.

## How it is organized

| Folder in `app/src/main/java/com/privatetube/app/` | Contents |
|---|---|
| `core/network` | requests to the site, addresses and User-Agent |
| `core/parsing` | page parsing; selectors are collected in `ParsingRules.kt` |
| `data` | repository, local storage, cache, backup |
| `domain` | data models |
| `feature` | screens |
| `ui` | shared components, focus, image loading |

If the site changes its markup, in most cases fixing `core/parsing` is enough. The test pages in `app/src/test/resources/fixtures/` are anonymized. The check against the real site (`LiveSiteCheck`) runs only manually, with the variable `TV_LIVE=1`.

## Known limitations

- The app depends on the site's markup and rules and **may stop working at any moment**.
- There is no signing in to a site account. Subscriptions and favorites are local and not synced with the site; there are no notifications about new videos.
- Paid content is not played.
- Some videos may be unavailable in your region. The site may limit the request rate.
- The translations have not been checked by native speakers.
- During development the app was checked with tests and on an emulator (an Android phone image with a 1080p screen). Operation on particular TV and set-top box models is not guaranteed.

## Disclaimer

By downloading, building, installing or using PrivateTube you agree to the terms below. If you do not agree with them, do not use the project.

1. **Unofficial project.** PrivateTube is an independent non-commercial project. It is not affiliated with PornHub, Aylo or their related companies, and is not sponsored, endorsed or supported by them.
2. **Trademarks.** The names PornHub, Aylo and related marks belong to their respective owners. They are mentioned only to explain which site the program works with. The project claims no rights to them.
3. **Content.** The repository and the APK contain no videos, photos or other material from the site. The project's authors do not host, upload, select, moderate or distribute such material, and the project has no intermediary servers. The user's device receives data directly from the site and its delivery networks. Only the data described in the “Privacy” section remains temporarily on the device. All rights to the material belong to its rights holders.
4. **Protection and paid access.** The program is not intended to circumvent DRM, paid access, authorization or CAPTCHA, and has no video downloading function.
5. **Site rules.** The site has its own terms of use, and they may restrict access through third-party programs. Read them. The decision to use the project and the responsibility for complying with those terms rest with you.
6. **Laws of your country.** Laws on adult material, tools for bypassing blocks, copyright and software distribution differ. You are responsible for making sure that downloading, using and distributing the project is legal where you are. If it is prohibited, do not use or distribute it.
7. **Age.** The project may be used only by persons who are at least 18 years old or have reached the higher age of majority set by local law. Do not install the app on devices that children can access unsupervised. The age confirmation screen and the PIN code are no substitute for parental controls.
8. **Site content.** The site and those who posted the material are responsible for it, its legality, descriptions and moderation. The project's authors do not control this material. Report illegal content to the site's administration and to the competent authorities.
9. **No warranty.** The program is provided “as is”, without any express or implied warranties, including of operability, fitness for a particular purpose, security and non-infringement of third-party rights.
10. **Limitation of liability.** To the maximum extent permitted by law, the project's authors and contributors are not liable for direct, indirect or other damages and consequences connected with the use of or inability to use the program, including blocked access, data loss, disclosure of personal information and third-party claims.
11. **Modified versions.** The authors are not responsible for forks, third-party builds and changes made by other people.
12. **Not legal advice.** This text is not legal advice and does not override rights and obligations provided by applicable law.

## For rights holders

We respect intellectual property rights. If you are a rights holder or their authorized representative and believe that some part of this repository infringes your rights, **create an Issue** describing what exactly infringes them, which rights are concerned and how to contact you (do not post personal data in the Issue). We will review the request and change or remove material if necessary. This does not limit your right to use GitHub's official procedures.

About videos and other material on the site itself, contact its administration: the project does not host this material and cannot remove it.

## Third-party components

- AndroidX and Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## License

The source code is distributed under the [MIT License](LICENSE). It grants no rights to the content of third-party sites or to other parties' trademarks.
