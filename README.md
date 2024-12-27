# Maturitní Projekt: Autíčko ovládané ESP32 a Android aplikací

## Popis projektu

Tento projekt je zaměřen na vytvoření dálkově ovládaného autíčka pomocí mikrokontroléru ESP32 a vlastní Android aplikace. Autíčko bude přijímat příkazy prostřednictvím Bluetooth nebo Wi-Fi a bude možné jej ovládat z mobilní aplikace na zařízení Android.

Cílem tohoto projektu je prakticky demonstrovat znalosti z oblasti mikrokontrolérů, bezdrátové komunikace a vývoje mobilních aplikací.

## Funkcionality

- **ESP32**: Mikrokontrolér zajišťující ovládání autíčka, komunikaci s Android aplikací a řízení motorů.
- **Android aplikace**: Vlastní mobilní aplikace umožňující uživateli ovládat směr, rychlost a další aspekty autíčka.
- **Ovládání**: Autíčko může být ovládáno buď prostřednictvím Bluetooth, nebo Wi-Fi v závislosti na konfiguraci.
- **Reakce**: Ovládání předních/zadních kol, akcelerace, brzdy, případně světla.

## Technologie

- **Hardware**:
  - ESP32 mikrokontrolér
  - DC motory (přední a zadní kola)
  - Motorový driver (např. L298N)
  - Napájení (baterie pro ESP32 a motory)
  - Podvozek autíčka
  - Další periferie jako LED diody pro světla, senzory pro vzdálenost atd.

- **Software**:
  - Vývojová platforma: Arduino IDE (pro programování ESP32)
  - Programovací jazyk: C++ pro ESP32
  - Android aplikace: Kotlin/Java (pro mobilní aplikaci)

## Instalace a nastavení

### 1. Příprava ESP32
1. Stáhněte a nainstalujte [Arduino IDE](https://www.arduino.cc/en/software).
2. Přidejte ESP32 do seznamu podporovaných desek přes `Preferences` v Arduino IDE a nainstalujte balíček.
3. Připojte ESP32 k počítači a nahrajte do něj kód, který najdete v souboru `esp32_car_control.ino`.

### 2. Hardware
1. Připojte ESP32 k driveru motorů (např. L298N) podle schématu v sekci hardware.
2. Připojte motory a baterii.
3. Zajistěte správné propojení Bluetooth/Wi-Fi modulu na ESP32 pro komunikaci s Android aplikací.

### 3. Android Aplikace
1. Otevřete Android Studio a načtěte projekt v adresáři `android_app`.
2. Připojte Android zařízení a spusťte aplikaci.
3. Aplikace umožní připojení k ESP32 přes Bluetooth/Wi-Fi a ovládání autíčka.

## Použití

1. **Zapněte autíčko** a ujistěte se, že ESP32 je v režimu párování.
2. **Spusťte Android aplikaci** a připojte se k autíčku přes Bluetooth nebo Wi-Fi.
3. Ovládejte autíčko pomocí virtuálních tlačítek (pohyb vpřed, vzad, zatáčení vlevo/vpravo, brzdy).
4. V případě potřeby můžete v aplikaci upravit nastavení připojení (rychlost, typ připojení).

## Soubory projektu

- `esp32_car_control.ino`: Hlavní kód pro ESP32.
- `android_app/`: Složka obsahující zdrojový kód Android aplikace.
- `hardware/`: Složka obsahující schéma zapojení hardwaru a další technické detaily.
- `README.md`: Tento soubor s instrukcemi.

## Licence

Tento projekt je chráněn licencí MIT. Můžete jej volně používat, kopírovat a modifikovat za předpokladu, že uvedete původní autora.

---

Doufám, že vám tento návod pomůže při realizaci vlastního autíčka ovládaného pomocí ESP32 a Android aplikace. Pokud máte jakékoliv dotazy, neváhejte mě kontaktovat.



Podměty do dokumentaci
nové verze androidu automaticky mění http na https proto pro funguvání zobrazení streamu musí být vyoučena adresa z toho pravidla 


šablona dokumentace:
- zapojení
- stream kamery a přenos do aplikace
- komunikace tlačítek pro ovládání motorů
- aplikace a vzhled
