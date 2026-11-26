# PrivateTube

[English](../README.md) · [Русский](README.ru.md) · [Українська](README.uk.md) · [Español](README.es.md) · **Português** · [Français](README.fr.md) · [Deutsch](README.de.md) · [Italiano](README.it.md) · [Polski](README.pl.md) · [Nederlands](README.nl.md) · [Čeština](README.cs.md) · [日本語](README.ja.md) · [中文](README.zh.md)

> **18+.** O aplicativo é destinado apenas a maiores de idade: ele dá acesso a um site com material adulto.
>
> **Projeto não oficial.** Não tem vínculo com o PornHub nem com a empresa proprietária do site, e não é aprovado nem apoiado por eles. Antes de usar, leia a [isenção de responsabilidade](#isenção-de-responsabilidade).

<img width="1920" height="1048" alt="1" src="https://github.com/user-attachments/assets/b29329cb-7717-4aec-b8c3-54f8219925bf" />

<details>
<summary><b>Mais capturas de tela</b></summary>
<br>

<img width="1920" height="1048" alt="Screenshot 2" src="https://github.com/user-attachments/assets/8d56b490-c636-45df-a23e-f2c903b6f876" />
<img width="1920" height="1050" alt="Screenshot 3" src="https://github.com/user-attachments/assets/17fcf12e-51a6-4335-bdf5-9f5d08156b2f" />
<img width="1920" height="1050" alt="Screenshot 4" src="https://github.com/user-attachments/assets/25417d21-927a-4a62-8f7d-3307d988d653" />
<img width="1920" height="1053" alt="Screenshot 5" src="https://github.com/user-attachments/assets/7419ce69-33ec-477f-b95d-6c7fc13ec6db" />

</details>

## O que é

O PrivateTube é um cliente nativo do PornHub para **Android TV e Google TV**, escrito em Kotlin (Jetpack Compose for TV, Media3).

Não é um navegador nem um site em uma “casca”: não há WebView. O próprio aparelho do usuário solicita as páginas públicas do site, e o aplicativo extrai delas títulos, miniaturas e links das transmissões e mostra tudo em uma interface própria, feita para um controle remoto comum.

## Para que serve

Usar o site na TV é desconfortável: elementos minúsculos, interface pensada para mouse e tela de toque, janelas pop-up. A TV precisa de uma interface própria: cartões grandes, foco claro, resposta rápida e controle pelo controle remoto.

## Feito em algumas noites com a ajuda de IA

O PrivateTube foi feito **literalmente em algumas noites com a ajuda de IA**. Uma pessoa definia as tarefas, conferia o resultado e dizia o que refazer. A IA escrevia o código, analisava a marcação das páginas, preparava testes e corrigia erros.

**Minha opinião: os criadores do PornHub poderiam muito bem fazer um cliente oficial como este.** Pela minha experiência, é rápido, não é difícil e não exige milhares de dólares em desenvolvimento. Para eles seria ainda mais fácil: têm acesso direto aos próprios dados e não precisam analisar as próprias páginas. Claro, um produto oficial precisaria de suporte e de testes em vários aparelhos, mas um cliente confortável para TV é uma tarefa perfeitamente viável.

Os usuários só vão ficar felizes se surgir um aplicativo oficial e este projeto deixar de ser necessário.

## Recursos

- Tela inicial com um feed sem fim e um menu lateral com categorias, canais, modelos e pesquisa.
- Cartões grandes, listas que carregam sozinhas e controle total pelo controle remoto, sem rolagem horizontal.
- Player próprio (HLS): escolha de qualidade, avanço de 30 em 30 segundos (rápido ao segurar o botão) e retomada do ponto salvo.
- Menu ao pressionar e segurar um vídeo: continuar ou começar do início, favoritos, inscrição no autor e outros vídeos dele.
- Seções locais “Inscrições”, “Favoritos” e “Histórico”.
- Exportação e importação locais de favoritos, inscrições e histórico.
- Código PIN para entrar.
- 13 idiomas de interface: inglês, russo, ucraniano, espanhol, português (do Brasil), francês, alemão, italiano, polonês, holandês, tcheco, japonês e chinês (simplificado). O idioma segue o do sistema ou é escolhido nas configurações.
- Os títulos dos vídeos vêm da versão do site no mesmo idioma. O site não traduz todos os títulos; em japonês e chinês há bem menos vídeos traduzidos.
- A mesma escala em telas 720p, 1080p e 4K e dois tamanhos de fonte: médio e grande.

## Se o site não abrir: VPN

**Se o site estiver bloqueado no seu país ou pelo seu provedor, ou se os vídeos não carregarem, ative uma VPN** na TV, no aparelho de TV ou no roteador.

Antes, certifique-se de que usar VPN e acessar esse tipo de material é **legal onde você está**. Alguns países têm restrições. Cumprir as leis locais é obrigação do usuário. O projeto não fornece VPN, não tem vínculo com nenhum serviço de VPN e não recomenda serviços específicos.

## Privacidade

- O aplicativo não precisa de nenhuma conta: nem do Google, nem do site.
- Não há serviços do Google, nem análises ou contadores do próprio aplicativo. O projeto não tem servidores próprios, e os autores não recebem nenhum dado.
- Vídeos e páginas do site não são salvos no disco. As miniaturas de vídeo são apagadas ao sair do aplicativo e novamente na próxima abertura. As imagens de categorias, canais, modelos e autores ficam no cache (até 60 MB) até serem substituídas por novas ou até você limpar o cache nas configurações.
- Favoritos, inscrições, histórico e posições de reprodução ficam armazenados somente no aparelho.
- O código PIN é armazenado como sal e hash. Ele protege contra pessoas ocasionais, não contra quem tem acesso total ao aparelho.

**Não existe anonimato 100%.** O site, seu provedor de internet, o administrador da rede e o serviço de VPN ainda podem ver informações sobre sua conexão dentro das possibilidades técnicas deles. O site trata as solicitações conforme as próprias regras.

### Cópias de segurança

- A exportação cria na pasta “Downloads” um arquivo como `user_preferences_26_11_2026_02_49.dat`.
- Ele contém favoritos, inscrições, histórico e posições nos vídeos. O código PIN e as configurações do aplicativo não entram na cópia.
- O conteúdo das cópias é compactado e criptografado, então não dá para lê-lo em um editor de texto. Mas a chave está embutida no aplicativo e visível no código-fonte.
- A importação adiciona o que falta e não apaga nada. O histórico guarda no máximo 100 registros.
- O arquivo não desaparece ao limpar o cache nem ao desinstalar o aplicativo. Se não precisar mais dele, apague-o você mesmo.

### Permissões

| Permissão | Para quê |
|---|---|
| Internet e estado da rede | carregar o catálogo e os vídeos |
| Acesso ao armazenamento (Android 10 e anteriores) | apenas para exportar e importar a cópia; é pedida no primeiro uso |
| Acesso a todos os arquivos (Android 11 e posteriores) | apenas para importar uma cópia de uma instalação anterior ou de outro aparelho; é concedida manualmente, e sem ela o restante funciona |

## Instalação

É necessário Android 7.0 ou mais recente.

1. Baixe o APK na seção **Releases** deste repositório.
2. Transfira o APK para a TV do jeito que for mais prático, por exemplo com o aplicativo LocalSend no celular ou por SAMBA a partir do computador, e instale depois de permitir a instalação de fontes desconhecidas.

Também é possível instalar o aplicativo via ADB (atvTools pelo celular ou ADB App Control pelo computador).

Instale somente APKs deste repositório ou compilados por você: os autores não respondem por compilações de terceiros.

## Compilação a partir do código-fonte

São necessários JDK 17 ou mais recente e o Android SDK (compileSdk 36). O caminho do SDK é definido em `local.properties` ou na variável `ANDROID_HOME`.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

No Windows use `.\gradlew.bat`. O arquivo gerado é `app/build/outputs/apk/release/app-release.apk`.

Sem configuração, a versão de lançamento é assinada com a **chave de depuração** do seu computador. Para distribuir, crie sua própria chave (`keytool`) e, junto dela, um arquivo `keystore.properties`:

```properties
storeFile=release.p12
storePassword=sua-senha
keyAlias=seu-alias
keyPassword=sua-senha
```

O caminho em `storeFile` é relativo à pasta desse arquivo. Informe o caminho do próprio `keystore.properties` em `local.properties` (`privatetube.signing=...`) ou na variável `PRIVATETUBE_SIGNING`. Guarde a chave fora do repositório e faça uma cópia de segurança dela. Um APK com outra assinatura não é instalado por cima do anterior: exporte seus dados antes de remover a versão antiga.

## Como está organizado

| Pasta em `app/src/main/java/com/privatetube/app/` | Conteúdo |
|---|---|
| `core/network` | solicitações ao site, endereços e User-Agent |
| `core/parsing` | análise das páginas; os seletores estão reunidos em `ParsingRules.kt` |
| `data` | repositório, armazenamento local, cache e cópia de segurança |
| `domain` | modelos de dados |
| `feature` | telas |
| `ui` | componentes comuns, foco e carregamento de imagens |

Se o site mudar o layout, na maioria dos casos basta corrigir `core/parsing`. As páginas de teste em `app/src/test/resources/fixtures/` são anonimizadas. A verificação no site real (`LiveSiteCheck`) só é executada manualmente, com a variável `TV_LIVE=1`.

## Limitações conhecidas

- O aplicativo depende do layout e das regras do site e **pode parar de funcionar a qualquer momento**.
- Não há login em conta do site. Inscrições e favoritos são locais e não sincronizam com o site; não há notificações de vídeos novos.
- Conteúdo pago não é reproduzido.
- Alguns vídeos podem estar indisponíveis na sua região. O site pode limitar a frequência das solicitações.
- As traduções não foram revisadas por falantes nativos.
- Durante o desenvolvimento, o aplicativo foi verificado com testes e em um emulador (imagem de Android para celular com tela de 1080p). O funcionamento em modelos específicos de TVs e aparelhos de TV não é garantido.

## Isenção de responsabilidade

Ao baixar, compilar, instalar ou usar o PrivateTube, você concorda com as condições abaixo. Se não concordar com elas, não use o projeto.

1. **Projeto não oficial.** O PrivateTube é um projeto independente e sem fins comerciais. Não tem vínculo com o PornHub, a Aylo nem empresas ligadas a eles, e não é patrocinado, aprovado nem apoiado por eles.
2. **Marcas.** Os nomes PornHub, Aylo e as marcas relacionadas pertencem aos respectivos titulares. São citados apenas para explicar com qual site o programa funciona. O projeto não reivindica direitos sobre eles.
3. **Conteúdo.** O repositório e o APK não contêm vídeos, fotos nem outros materiais do site. Os autores do projeto não hospedam, enviam, selecionam, moderam nem distribuem esses materiais, e o projeto não tem servidores intermediários. O aparelho do usuário recebe os dados diretamente do site e de suas redes de distribuição. No aparelho ficam temporariamente apenas os dados descritos na seção “Privacidade”. Todos os direitos sobre os materiais pertencem aos seus titulares.
4. **Proteção e acesso pago.** O programa não se destina a contornar DRM, acesso pago, autenticação ou CAPTCHA e não tem função de baixar vídeos.
5. **Regras do site.** O site tem termos de uso próprios, que podem restringir o acesso por programas de terceiros. Leia-os. A decisão de usar o projeto e a responsabilidade por cumprir esses termos são suas.
6. **Leis do seu país.** As leis sobre material adulto, ferramentas para contornar bloqueios, direitos autorais e distribuição de programas variam. Cabe a você garantir que baixar, usar e distribuir o projeto seja legal onde você está. Se for proibido, não o use nem o distribua.
7. **Idade.** Só podem usar o projeto pessoas com pelo menos 18 anos ou com a maioridade mais alta estabelecida pela lei local. Não instale o aplicativo em aparelhos a que crianças tenham acesso sem supervisão. A tela de confirmação de idade e o código PIN não substituem o controle parental.
8. **Conteúdo do site.** O site e quem publicou os materiais respondem por eles, por sua legalidade, descrições e moderação. Os autores do projeto não controlam esses materiais. Denuncie conteúdo ilegal à administração do site e às autoridades competentes.
9. **Sem garantias.** O programa é fornecido “como está”, sem garantias expressas ou implícitas, inclusive de funcionamento, adequação a um fim específico, segurança e não violação de direitos de terceiros.
10. **Limitação de responsabilidade.** Na máxima extensão permitida por lei, os autores e colaboradores do projeto não respondem por danos diretos, indiretos ou outros nem por consequências ligadas ao uso ou à impossibilidade de uso do programa, incluindo bloqueio de acesso, perda de dados, divulgação de informações pessoais e reclamações de terceiros.
11. **Versões modificadas.** Os autores não respondem por forks, compilações de terceiros nem alterações feitas por outras pessoas.
12. **Não é consultoria jurídica.** Este texto não é consultoria jurídica e não anula direitos e obrigações previstos na legislação aplicável.

## Para titulares de direitos

Respeitamos os direitos de propriedade intelectual. Se você é titular de direitos ou representante autorizado e acredita que alguma parte deste repositório viola seus direitos, **crie uma Issue** descrevendo o que exatamente os viola, quais direitos estão envolvidos e como entrar em contato com você (não publique dados pessoais na Issue). Analisaremos o pedido e, se necessário, alteraremos ou removeremos o material. Isso não limita seu direito de usar os procedimentos oficiais do GitHub.

Sobre vídeos e outros materiais do próprio site, procure a administração dele: o projeto não hospeda esses materiais e não pode removê-los.

## Componentes de terceiros

- AndroidX e Jetpack (Compose, TV Material, Media3, DataStore, Navigation, Lifecycle, Activity, Core) — Apache License 2.0
- Kotlin, kotlinx.coroutines, kotlinx.serialization — Apache License 2.0
- OkHttp — Apache License 2.0
- Coil — Apache License 2.0
- jsoup — MIT License
- Gradle Wrapper — Apache License 2.0

## Licença

O código-fonte é distribuído sob a [licença MIT](../LICENSE). Ela não concede nenhum direito sobre o conteúdo de sites de terceiros nem sobre marcas alheias.
