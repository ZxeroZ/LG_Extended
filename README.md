<div align="center">
  <img src="icono.png" width="150" style="border-radius: 20%; box-shadow: 0 4px 8px rgba(0,0,0,0.2);" />
  
  <br><br>
  
  # LG Extended
  
  <h3><i>"Stock is a suggestion"</i></h3>

  <p>
    <b>An open-source UI customizer for the LG V60</b>
  </p>

  <br>

  <!-- Badges -->
  <a href="#"><img src="https://img.shields.io/badge/Device-LG_V60_Exclusive-0a58ca?style=for-the-badge&logo=lg&logoColor=white" alt="LG V60"></a>
  <a href="#"><img src="https://img.shields.io/badge/Android-13-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 13"></a>
  <a href="#"><img src="https://img.shields.io/badge/Root-Magisk_%7C_KernelSU-DF342A?style=for-the-badge" alt="Root"></a>
  <a href="#"><img src="https://img.shields.io/badge/Module-LSPosed-F29C38?style=for-the-badge" alt="LSPosed"></a>
  <br>
  <a href="#"><img src="https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=github" alt="Build"></a>
  <a href="#"><img src="https://img.shields.io/badge/Version-1.0.0-blueviolet?style=for-the-badge" alt="Version"></a>
  <a href="#"><img src="https://img.shields.io/badge/License-GPL_v3-yellow?style=for-the-badge" alt="License"></a>

</div>

<hr>

## About

**LG Extended** is an Xposed/LSPosed module built specifically for the LG V60. It hooks directly into the system framework and stock LG apps to replace and improve various UI elements on the fly.

## Features

- **iOS-Style Recents:** Modifies the multitasking view with thicker cards, 26dp rounded corners, overlapping animations, and fading titles.
- **OneUI Settings Layout:** Replaces the default settings list with floating cards grouped by categories, using custom muted vector icons.
- **Auto Sort App Drawer:** Automatically triggers the alphabetical sorting in the LG Launcher whenever a new app is installed.
- **Battery Icon Mods:** Removes the default battery percentage text and injects custom SVG battery icons directly into the status bar.
- **DPI Override:** Forces custom screen densities system-wide.
- **Instant Apply:** Automatically force-closes the target app (like Settings or Launcher) when a mod is toggled so changes apply immediately.

<br>

<div align="center">
  <i>(Add screenshots here)</i><br><br>
</div>

<br>

## Requirements

- **Device:** LG V60 ThinQ
- **ROM:** Stock LG Android 13 **OR** Thunder OS (v1.2 or older)
- **Root:** Magisk, KernelSU, or APatch
- **Framework:** LSPosed

## Installation

1. Download the latest `app-release.apk` from the Releases page.
2. Install the app on your LG V60.
3. Open the app and grant Superuser (Root) permissions.
4. Open the LSPosed Manager.
5. Go to Modules, find **LG Extended**, and enable it.
6. Make sure the target apps (Settings, System UI, Inicio/Launcher) are checked.
7. Reboot the device.
8. Open LG Extended and toggle the features you want.

## Contributing

Pull Requests are welcome. If you want to add new hooks, fix bugs, or add new icons, feel free to submit a PR. 

If you find a bug (like layout issues or things not applying correctly), open an Issue with your ROM version and steps to reproduce.

---

<div align="center">
  Developed by <b>ZxeroZ</b>
</div>
