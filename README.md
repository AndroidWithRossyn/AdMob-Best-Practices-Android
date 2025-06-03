<h2 align="center">AdMob Best Practices Android</h2>

This repository provides essential tips and implementation examples for integrating AdMob ads effectively in Android apps. Learn how to choose the right ad formats, avoid accidental clicks, follow Google’s ad policies, and preload ads for better performance—all with clean, developer-friendly code.

Best practices for integrating AdMob ads in Android apps (Banner, Interstitial, Rewarded, Native) with clean, reusable code.

<div align="start">
  
<a href="mailto:banrossyn@gmail.com"><img src="https://img.shields.io/badge/Gmail-EA4335.svg?logo=Gmail&logoColor=white"></a>
[![Instagram](https://img.shields.io/badge/Instagram-%23E4405F.svg?logo=Instagram&logoColor=white)](https://instagram.com/rohitraj.khorwal) [![LinkedIn](https://img.shields.io/badge/LinkedIn-%230077B5.svg?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/rohitrajkhorwal/) [![Medium](https://img.shields.io/badge/Medium-12100E?logo=medium&logoColor=white)](https://medium.com/@rohitrajkhorwal) 
<a href="https://t.me/banrossyn" target="_blank"><img src="https://img.shields.io/badge/Telegram-26A5E4.svg?logo=Telegram&logoColor=white"></a>
<a href="https://wa.me/+919694260426/" target="_blank"><img src="https://img.shields.io/badge/WhatsApp-25D366.svg?logo=WhatsApp&logoColor=white">
</a>

</div>

### UI Design Available on Figma:

[![Figma](https://img.shields.io/badge/Figma-%230077B5.svg?logo=figma&logoColor=white)](https://www.figma.com/community/file/1511338188670501612/admob-best-practices-android)

<p align="center">
    <a href="/HIRE_ME.md">
      <img src="./assets/helper-images/graphic-hire-me.png"/>
    </a>
  </p>

[![Contact for Ad Service](https://img.shields.io/badge/Contact-for%20Ad%20Service-blue?style=for-the-badge&logo=googleads)](/HIRE_ME.md)

## 📱 AdMob Demo Ad Unit IDs

**App ID:** `ca-app-pub-3940256099942544~3347511713`

| Ad Format             | Demo Ad Unit ID                          |
| --------------------- | ---------------------------------------- |
| App Open              | `ca-app-pub-3940256099942544/9257395921` |
| Adaptive Banner       | `ca-app-pub-3940256099942544/9214589741` |
| Fixed Size Banner     | `ca-app-pub-3940256099942544/6300978111` |
| Interstitial          | `ca-app-pub-3940256099942544/1033173712` |
| Rewarded Ads          | `ca-app-pub-3940256099942544/5224354917` |
| Rewarded Interstitial | `ca-app-pub-3940256099942544/5354046379` |
| Native                | `ca-app-pub-3940256099942544/2247696110` |
| Native Video          | `ca-app-pub-3940256099942544/1044960115` |

## 📌 About This Repo

I will be adding practical examples and code for all major AdMob ad formats, including:

<table style="width:100%; border-collapse: collapse;" border="1">
  <tr>
    <th style="padding: 10px;">Ad Format</th>
    <th style="padding: 10px;">Description</th>
    <th style="padding: 10px;">Demo</th>
  </tr>

  <!-- Banner Ad - Simple -->
  <tr>
    <td style="text-align: center; padding: 10px;">
      <img src="https://developers.google.com/static/admob/images/format-banner.svg" alt="Banner Ad" width="150" height="100">
    </td>
    <td style="padding: 10px;">
      <strong>Banner Ad - Simple ✅</strong><br/>
       - 
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="./BannerAd-Simple/banner-simple.apk" target="_blank">
        <img src="https://img.shields.io/badge/Demo-Live-green?style=for-the-badge" alt="Demo">
      </a>
      <a href="/BannerAd-Simple/" target="_blank">
        <img src="https://img.shields.io/badge/Source-Code-blue?style=for-the-badge" alt="Source Code">
      </a>
      <a href="https://youtu.be/kjtvKQpqTmo" target="_blank">
        <img src="https://img.shields.io/badge/Watch-Video-red?logo=youtube&style=for-the-badge" alt="YouTube Video">
      </a>
    </td>
  </tr>

  <!-- Banner Ad Collapsible-->
  <tr>
    <td style="text-align: center; padding: 10px;">
      <img src="https://developers.google.com/static/admob/images/format-banner.svg" alt="Banner Ad" width="150" height="100">
    </td>
    <td style="padding: 10px;">
      <strong>Banner Ad - Collapsible ✅</strong><br/>
      -
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="/BannerAd-Collapsible/banner-collapsible.apk" target="_blank">
        <img src="https://img.shields.io/badge/Demo-Live-green?style=for-the-badge" alt="Demo">
      </a>
      <a href="/BannerAd-Collapsible/" target="_blank">
        <img src="https://img.shields.io/badge/Source-Code-blue?style=for-the-badge" alt="Source Code">
      </a>
      <a href="https://youtu.be/rjMCmzDar4o" target="_blank">
        <img src="https://img.shields.io/badge/Watch-Video-red?logo=youtube&style=for-the-badge" alt="YouTube Video">
      </a>
    </td>
  </tr>


  <!-- Banner Ad -->
  <tr>
    <td style="text-align: center; padding: 10px;">
      <img src="https://developers.google.com/static/admob/images/format-banner.svg" alt="Banner Ad" width="150" height="100">
    </td>
    <td style="padding: 10px;">
      <strong>Banner Ad (320×50) ✅ </strong><br/>
      Fixed size banner suitable for phones and tablets.
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="/BannerAd-AdSize-BANNER/app-debug.apk" target="_blank">
        <img src="https://img.shields.io/badge/Demo-Live-green?style=for-the-badge" alt="Demo">
      </a>
      <a href="/BannerAd-AdSize-BANNER/" target="_blank">
        <img src="https://img.shields.io/badge/Source-Code-blue?style=for-the-badge" alt="Source Code">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Watch-Video-red?logo=youtube&style=for-the-badge" alt="YouTube Video">
      </a>
    </td>
  </tr>

  <!-- Interstitial Ad -->
  <tr>
    <td style="text-align: center; padding: 10px;">
      <img src="https://developers.google.com/static/admob/images/format-interstitial.svg" alt="Interstitial Ad" width="150" height="100">
    </td>
    <td style="padding: 10px;">
      <strong>Interstitial Ad</strong><br/>
      Full-screen ad shown at natural app transition points.
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Demo-Live-green?style=for-the-badge" alt="Demo">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Source-Code-blue?style=for-the-badge" alt="Source Code">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Watch-Video-red?logo=youtube&style=for-the-badge" alt="YouTube Video">
      </a>
    </td>
  </tr>

  <!-- Rewarded Ad -->
  <tr>
    <td style="text-align: center; padding: 10px;">
      <img src="https://developers.google.com/static/admob/images/format-rewarded.svg" alt="Rewarded Ad" width="150" height="100">
    </td>
    <td style="padding: 10px;">
      <strong>Rewarded Ad</strong><br/>
      User earns rewards after watching a video or interacting with ad.
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Demo-Live-green?style=for-the-badge" alt="Demo">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Source-Code-blue?style=for-the-badge" alt="Source Code">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Watch-Video-red?logo=youtube&style=for-the-badge" alt="YouTube Video">
      </a>
    </td>
  </tr>

  <!-- App Open Ad -->
  <tr>
    <td style="text-align: center; padding: 10px;">
      <img src="https://developers.google.com/static/admob/images/format-app-open.svg" alt="App Open Ad" width="150" height="100">
    </td>
    <td style="padding: 10px;">
      <strong>App Open Ad</strong><br/>
      Full-screen ad that shows when the app is launched or resumed.
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Demo-Live-green?style=for-the-badge" alt="Demo">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Source-Code-blue?style=for-the-badge" alt="Source Code">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Watch-Video-red?logo=youtube&style=for-the-badge" alt="YouTube Video">
      </a>
    </td>
  </tr>

  <!-- Native Ad -->
  <tr>
    <td style="text-align: center; padding: 10px;">
      <img src="https://developers.google.com/static/admob/images/format-native.svg" alt="Native Ad" width="150" height="100">
    </td>
    <td style="padding: 10px;">
      <strong>Native Ad</strong><br/>
      Custom styled ads that match the look and feel of your app.
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Demo-Live-green?style=for-the-badge" alt="Demo">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Source-Code-blue?style=for-the-badge" alt="Source Code">
      </a>
      <a href="#" target="_blank">
        <img src="https://img.shields.io/badge/Watch-Video-red?logo=youtube&style=for-the-badge" alt="YouTube Video">
      </a>
    </td>
  </tr>

</table>


Each implementation will follow Google's latest policies and monetization strategies to ensure the best results in terms of both user experience and revenue.

Stay tuned for upcoming commits where you'll find easy-to-follow and reusable code for all ad types.

## Find this Repository useful? ❤️

Support it by joining stargazers for this repository. ⭐

Also, [follow me on GitHub](https://github.com/AndroidWithRossyn/) for my next creations! 🤩

<p align="left">
<a href="https://github.com/AndroidWithRossyn?tab=repositories&sort=stargazers"><img alt="All Repositories" title="All Repositories" src="https://custom-icon-badges.demolab.com/badge/-Click%20Here%20For%20All%20My%20Repos-1F222E?style=for-the-badge&logoColor=white&logo=repo"/></a>
  
</p>

## 💖 Support

If you’d like to support this project:

[![Sponsor](https://img.shields.io/badge/Sponsor-❤-red?style=for-the-badge)](https://github.com/sponsors/AndroidWithRossyn)
[![Donate with PayPal](https://img.shields.io/badge/Donate-PayPal-blue.svg?style=for-the-badge&logo=paypal)](https://www.paypal.com/paypalme/banrossyn)

## 🙌 Contribute

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

`Note:` Please review our [Code of Conduct](./CODE_OF_CONDUCT.md) before using this project.

## License

```
Copyright:
~ Rohitraj Khorwal

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=60&section=footer"/>
</p>
