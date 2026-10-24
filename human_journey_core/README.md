# Human Journey Core v0.1 (0.1.0)

Окремий Forge-мод для Minecraft **1.21.8**, Forge **58.1.18**, Tough As Nails **21.8.0.2** і GlitchCore **21.8.0.0**. Баланс контролює сервер; учням не потрібно замінювати JAR після зміни чисел у серверному конфігу.

**Статус: Forge JAR успішно зібрано в GitHub Actions; 6 тестів пройдено, 0 помилок. Ігрові перевірки ще потрібні.** [Успішна збірка](https://github.com/petryshamen/minecraft-education/actions/runs/40066494993), commit `06e795f597619f0056bc375cc239de4563e980b0`. Runtime JAR: `human_journey_core-0.1.0.jar`, SHA-256 `531c7b52eac25796e717f772731623320fa73373a2cba8f042818404a703fd74`. Копія точного артефакту збережена в `releases/original/`. Нічого автоматично не встановлювалося у Minecraft.

## Поведінка

- WARM/HOT додають **додатковий** thirst exhaustion через `ThirstHelper.getThirst(player).addExhaustion(...)` у Survival/Adventure в Overworld та Nether. Базовий TAN exhaustion залишається. NEUTRAL/COLD/ICY не додають Core exhaustion.
- Лише damage type `toughasnails:hyperthermia` блокується в Overworld за замовчуванням. Nether зберігає цей damage. Інші виміри використовують звичайну поведінку TAN.
- Контакт із водою в Overworld швидко прибирає hyperthermia exposure та переводить HOT → WARM → NEUTRAL. За замовчуванням крок кожні 10 ігрових ticks, тобто до 1 секунди для двох кроків при 20 TPS. Дощ не активує додаткове охолодження Core.
- Core ніколи не переводить температуру нижче NEUTRAL, не підвищує COLD/ICY та не змінює холодні цілі TAN. Звичайне подальше охолодження від води у TAN залишається можливим.
- Dehydration, напої, hydration, thirst level, cold effects, fire/lava damage, прогрес, NPC та датапак не переписуються. Менше пиття не обіцяється: спека саме збільшує витрати спраги.
- Creative/Spectator не отримують додаткових витрат чи охолодження Core. Немає client-only класів, mixin або власних пакетів. Зміни температури передає штатна синхронізація TAN на наступному player tick (можлива затримка індикатора приблизно 1 tick).

## Збірка на Windows

1. Розпакуй весь проєкт у звичайну робочу папку. ZIP не встановлюється як мод або датапак.
2. Підготуй **JDK 21** (наприклад Temurin 21). `JavaHome` має містити `bin/java.exe` і `bin/javac.exe`.
3. Відкрий PowerShell у папці проєкту й виконай, підставивши справжній шлях JDK:

```powershell
.\build.ps1 -JavaHome 'C:\path\to\jdk-21'
```

Скрипт перевіряє Java 21, запускає включений Gradle 9.3.1 wrapper, JUnit та build. Перший запуск потребує інтернету для Gradle, Forge, Minecraft, TAN і Maven-залежностей. ForgeGradle зафіксований на 7.0.3 за офіційним MDK. Gradle distribution перевіряється SHA-256.

Кеші, тимчасові дані та виходи скрипт спрямовує в папку проєкту; глобальні змінні середовища відновлює. Це звичайний локальний build script, **не ізольована пісочниця**. Скрипт не встановлює модів, не шукає live-папки гри й не копіює нічого в `mods`.

Після успішної збірки очікується:

```text
build/libs/human_journey_core-0.1.0.jar          ← мод для ручного встановлення
build/libs/human_journey_core-0.1.0-sources.jar  ← лише вихідні файли
build/reports/tests/test/index.html             ← звіт JUnit
```

Ці файли отримані з успішної GitHub Actions збірки. На Linux/macOS із JDK 21: `chmod +x gradlew`, потім `./gradlew --no-daemon test build`.

Проєкт уже розміщено в `petryshamen/minecraft-education/human_journey_core`. Workflow лежить у корені репозиторію: `.github/workflows/human-journey-core.yml`. Він стартує при push змін Core або вручну через **Actions → Build Human Journey Core → Run workflow**, віддає JAR і звіт як artifact та нічого не встановлює. Для локальної збірки спочатку перейди в `human_journey_core`.

## Ручне встановлення після успішної збірки

1. Зупини тестовий сервер/гру та зроби резервну копію світу.
2. Використай Forge 58.1.18, TAN Forge 21.8.0.2 і GlitchCore 21.8.0.0. Fabric/NeoForge JAR не підходять.
3. Вручну поклади тільки `human_journey_core-0.1.0.jar` у `mods` тестового сервера та Forge-профілів учителя й учнів, поряд із TAN/GlitchCore. При оновленні залишай лише одну версію Core.
4. Запусти сервер і перевір лог без помилок завантаження. Не переходь на класний live-сервер, доки не пройдено ігрові перевірки нижче.

Жодних автоматичних змін у live `mods`, світі, датапаку чи конфігах TAN ця поставка не виконує.

## Налаштування на сервері

Після першого запуску Forge створить `<world>/serverconfig/human_journey_core-server.toml`. У singleplayer це `saves/<world>/serverconfig/...`. Назва серверного світу береться з `level-name`, тому папка не обов'язково називається `world`.

Зупини сервер, відредагуй конфіг і запусти знову. Перезбирання або перевстановлення JAR учням не потрібне. `/reload` не використовується як гарантія перезавантаження Forge config. Для нових світів можна вручну покласти шаблон у `defaultconfigs`; для наявного світу редагуй його власний `serverconfig`.

| Ключ у `[temperature]` | Типове значення | Значення |
| --- | ---: | --- |
| `warm_thirst_exhaustion` | 0.10 | Додатковий exhaustion за секунду у WARM; 0–20 |
| `hot_thirst_exhaustion` | 0.50 | Додатковий exhaustion за секунду у HOT; 0–20 |
| `overworld_hyperthermia_damage` | false | Дозволити direct heat damage в Overworld |
| `nether_hyperthermia_damage` | true | Дозволити direct heat damage у Nether |
| `water_heat_relief` | 20 | Мінімальне чисте зменшення exposure за tick у воді; 0–140 |
| `water_cooling_interval_ticks` | 10 | Кількість ticks між кроками HOT → WARM → NEUTRAL; 1–200 |

Exhaustion — **не кількість thirst points**: його обробляє TAN з урахуванням hydration та власних правил. При низькому TPS реальна швидкість змін нижча. `water_heat_relief=0` вимикає всі додаткові водні зміни Core, включно з водним захистом від heat damage; штатна вода TAN продовжує діяти. При додатному значенні вода захищає від hyperthermia damage в Overworld навіть якщо `overworld_hyperthermia_damage=true`.

Приклад конфігу включено в `example-config/`. Не вимикай thirst/temperature у TAN, якщо хочеш тестувати ці функції Core: при вимкненій температурі додаткові витрати теж не застосовуються.

## Перевірка у тестовому світі

JUnit перевіряє чисті правила, але не замінює Forge startup та ігровий тест. **GitHub Actions: 6 тестів пройдено, 0 помилок, 0 пропущених.**

1. **Завантаження:** запусти dedicated test server і підключи клієнт із тими самими модами. Перевір відсутність missing classes/config/eventbus errors.
2. **WARM/HOT:** у Survival з однаковими hydration, спорядженням, активністю й часом порівняй WARM та HOT в Overworld, потім у Nether. HOT має давати більші додаткові витрати. Для контрольної спроби вистав обидва exhaustion у 0 й перезапусти сервер. Через запас hydration ефект не обов'язково відразу видно як втрачені іконки спраги.
3. **Damage type:** в Overworld команда `/damage @s 1 toughasnails:hyperthermia` не повинна забирати здоров'я; у Nether повинна. Виконуй від імені гравця з правами оператора, почекай між ударами. Також дочекайся природної повної hyperthermia в обох вимірах: Overworld захищений, Nether небезпечний. При нормальному рівні спраги ізолюй direct heat damage від dehydration.
4. **Вода:** у HOT із накопиченим exposure зайди у воду Overworld. Exposure має швидко падати; HOT → WARM → NEUTRAL займає до 20 ticks за типовими числами. Перевір також повторний вхід, вихід, смерть/respawn і перехід у Nether. Встанови relief=0 і переконайся, що залишається лише штатна швидкість TAN.
5. **Холод:** порівняй COLD/ICY та їхні ефекти з Core і без нього, у тому числі у холодній воді. Core не повинен гріти холодного гравця чи перешкоджати природному охолодженню нижче NEUTRAL.
6. **Dehydration:** на Normal/Hard залиш гравця без спраги й перевір звичайний thirst damage; можна окремо перевірити `/damage @s 1 toughasnails:thirst`. Вогонь, лава та інші damage types теж мають працювати.
7. **Конфіг:** зміни exhaustion, cooling interval і дозволи damage, перезапусти тільки сервер з тим самим Core JAR. Повтори відповідні перевірки. Після перезапуску клієнти мають користуватися серверними значеннями.
8. **Ролі:** повтори Survival-перевірки з Teacher та Student. Core не робить винятків за тегами; Creative/Spectator обох ролей не отримують додаткових витрат. Сторонні імунітети можуть впливати на результати.

## Структура та обмеження

`config/` — Forge SERVER config; `engine/temperature/HeatPolicy` — чисті правила; `integration/toughasnails/` — TAN API adapter. Перед player tick зменшується exposure, після завершення всіх server ticks застосовується остаточне охолодження й exhaustion. Це не залежить від порядку Forge Player Post і TAN tick hook. Власних збережуваних даних або world migration немає.

Компіляція та JUnit пройдені. Server startup, client sync і геймплей ще не перевірені. Перевірені версії, походження wrapper, SHA-256 TAN і посилання на API описані в `docs/API-EVIDENCE.md`. Сторонні TAN/GlitchCore/Minecraft JAR не вбудовуються у runtime JAR Core.
