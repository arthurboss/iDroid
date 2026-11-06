# Compatibility

This fork is a standalone CarPlay receiver for an ordinary Android tablet or phone. It is not an
Apple-certified CarPlay accessory, and the experimental bundled accessory identity is extractable;
its future acceptance is not guaranteed.

Nothing here reads from or writes to a vehicle. The head-unit features of the upstream project
(instrument-cluster output, windshield HUD, vehicle telemetry over the car's own services,
steering-wheel key capture and interior ambient lighting) are not part of this fork.

| Area | Current scope |
| --- | --- |
| Device | Android 7.0+ (API 24+). Wireless CarPlay needs Wi-Fi Direct or a hotspot the device can bring up; Android 7.x has no local-only hotspot, so use Wi-Fi Direct, the device hotspot or an existing Wi-Fi there. Android 6.0 and older are unsupported. |
| Phone | Standard, non-jailbroken iPhone with CarPlay enabled; device and iOS compatibility vary. |
| Transport | USB (data port, correct host/device role) and wireless (Wi-Fi Direct, the device's own hotspot, or an existing Wi-Fi both devices join). |
| Video | Default H.264 / 30 fps. 60 fps and HEVC raise device-specific demands. |
| Wi-Fi | Auto uses eligible saved/aligned channels; beside a 5 GHz station, explicit 2.4 GHz precedes other 5 GHz and unpinned fallbacks. Manual channels stay explicit; no band or performance guarantee. |
| Evidence | Automated source, unit-test and lint validation on every change, plus install-and-launch verification on a Galaxy Tab S9 Ultra (SM-X916B, Android 16). No end-to-end CarPlay session is claimed for this fork yet; that needs a build carrying an accessory identity. |

## First run

A first launch with no chosen iPhone opens the setup guide: **choose how to connect**, **choose the
iPhone**, **done**. It can be skipped and reopened from **Settings → Overview**.

The guide asks nothing about the device it runs on and detects no vehicle. The connection choice it
offers is the same control as **Settings → Connection → Connection setup**, so whatever is picked
during setup can be changed at any time, and changing it later needs no second trip through the
guide. See [first run](FIRST_RUN.md).

## Settings

- **Display** — resolution, frame rate, icon and text size, appearance, Interface size, the
  settings gesture, external controllers, and the icon and label of CarPlay's own home button.
  Interface size changes this app's controls, not CarPlay geometry.
- **Audio** — stream routing, navigation attenuation and the ordinary music-buffer choice.
- **Navigation** — reporting the device's location to the iPhone.
- **Connection** — transport, automatic connection, USB, hotspot control and permissions.
- **Advanced** — the CarPlay map stream, the launcher bubble, experimental display and media.
- **Diagnostics** — saving and sharing a report.

**Smooth video (experimental)**, **Call echo cancellation (experimental)** and **Clearer call
voices (experimental)** are off by default under **Advanced → Video and audio**. Smooth video adds
adaptive pacing delay, can delay touch response, and uses a SurfaceView path without picture
adjustments; changing it reconnects a running session. The call-processing controls apply at the
next connection, and source and JNI tests do not establish acoustic quality on real hardware.
Rotation, split-screen areas and the side panel are off by default under **Advanced → Display
(experimental)**. Opted-in settings are retained across an in-place update.

## The CarPlay map stream

CarPlay offers a second screen alongside the main one. With **Advanced → CarPlay map stream** on,
this app asks the iPhone for it and nothing shows it directly: copies feed the floating map card,
the launcher map widget and the map embed service (see [launcher
integration](LAUNCHER_INTEGRATION.md)). The stream is a fixed 1280×720 canvas, so there is no panel
geometry to calibrate. The iPhone stops sending it while no copy is on screen.

Sharing the stream with another app is off until the owner turns it on, is withdrawn live when they
turn it off, and is capped per calling app.

## Video playback

iOS 27 "video in car" offers a media URL only to a receiver that reports a stopped vehicle. A tablet
has no gear or speed signal, so **Allow video playback** is the whole answer and stays off until the
owner turns it on. Leave it off wherever the screen is visible to a driver.

## Known limitations

- If the iPhone leaves its current Wi-Fi network but does not join the device hotspot, check that
  Auto-Join is enabled for that hotspot. If selecting it manually starts CarPlay without a password
  prompt, record that distinction in the diagnostic report. Access-point broadcast information
  elements can affect automatic joining even where the credentials and the CarPlay transport work.
  See [automatic hotspot joining](WIRELESS_HOTSPOT_JOIN.md).
- Some devices stutter, particularly under higher video load. A 2.4 GHz link alone does not prove
  the cause: interference, firmware and decoder stalls all contribute. Try default icons, 30 fps and
  a lower resolution, then attach a report.
- Wi-Fi Direct can stutter every few seconds while the device's own Wi-Fi client scans for networks.
  One radio serves both, so each scan takes it off the CarPlay channel and the audio packets sent
  meanwhile are lost; a larger music buffer cannot replace them. The device's own hotspot runs with
  the client off and is the more reliable wireless choice.
- A radio that can join a 5 GHz network may still refuse a 5 GHz Wi-Fi Direct group. The capability
  flag is diagnostic, not proof of group-owner support.
- Some iOS versions do not visibly apply the requested icon and text size. Reconnection is
  implemented; the iPhone still chooses the layout.
- Automatic startup after boot depends on the device's background restrictions.
- Wireless CarPlay cannot start where Android's Bluetooth accepts connections that reach no phone,
  or where the Bluetooth service gives apps no socket manager. The app stops with the cause instead
  of retrying. USB is the route left to try there.
- A device whose Android has no Bluetooth adapter is told so when an iPhone is chosen and when a
  connection is tried, rather than being asked to turn Bluetooth on.
- USB needs a data port and correct host/device-role behaviour. A charging-only port cannot carry
  the transport.
- Calls, Siri, background reconnection, long sessions and future iOS releases need broader testing.

## Reporting a problem

Reproduce the problem, then use **Settings → Diagnostics → Save diagnostic report**. Android 10+
normally saves to **Downloads/DiPlay**; Android 9 uses a document picker. The confirmation names
fallback storage and offers **View report** and **Share**. Attach the reviewed `.txt` to the issue.
Nothing uploads automatically.

Reports record requested frequencies, station association state, fallback failures and
remembered-configuration events. Android 10+ can verify the actual group frequency; Android 9
reports its accepted channel request as unverified and a system default as channel 0. Wi-Fi
credentials and protocol payloads are excluded. A working hotspot is not itself a working CarPlay
session.

Android references: [SupplicantState](https://developer.android.com/reference/android/net/wifi/SupplicantState),
[explicit P2P operating frequency](https://developer.android.com/reference/android/net/wifi/p2p/WifiP2pConfig.Builder#setGroupOperatingFrequency(int)).
