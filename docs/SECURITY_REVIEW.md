# Security review — tablet retargeting

Reviewed against the change that removes the vehicle-facing features (instrument cluster, windshield
HUD, vehicle telemetry, steering-wheel keys, interior ambient lighting, the head-unit call popup).
Scope: what the app exposes to other apps and to the network, and what privileges it asks for.

## What this change removes

| Removed | Why it mattered |
| --- | --- |
| `WheelKeyService` accessibility service | It held `BIND_ACCESSIBILITY_SERVICE` and could observe and filter key events for every app on the device, permanently once enabled, to capture steering-wheel keys. Nothing replaces it; the CarPlay window now claims only media keys delivered to it. |
| `CarHotspotSetup.grant` / `check` | They opened a local ADB session and ran `appops set <package> WRITE_SETTINGS allow` and `SYSTEM_ALERT_WINDOW allow` — the app granting itself permissions. The hotspot card now links to Android's own "Modify system settings" screen instead. |
| `BydAdbAccess`, `BydBattery`, `BydWheelSpeed`, `BydTyres`, `BydParkedState`, `BydVehicleCapabilityProbe` | `adb shell service call` readers for vehicle data, and a probe that walked transaction and field IDs looking for readable addresses. |
| `BydAmbientLightClient` / `BydAmbientLightWorker` | A periodic worker writing to the car's lighting service over ADB. |
| `BydCallPopupGuard` / `BydCallPopupTool` | Suppressed another app's window during calls. |
| Audio PCM tap for the lamp envelope | The media renderer no longer copies played PCM into an analyser. |

## What remains, and why

**Local ADB (`LocalAdb`, `BydAdbShell`).** Used by `UsbPermissionSetup`, `IphoneUsbAdbGrant`,
`VpnPreConsent`, `WifiScanPause`, `CarHotspotConfigWrite`/`Read`, `CarHotspotTethering`'s fallback,
`BydBootStartRepair` and `BydBluetoothSuspend`. Every one of these needs the owner to turn on
wireless debugging in developer options *and* approve this app's ADB key in a system dialog, so it
is user-authorised rather than a privilege escalation. It is still the largest remaining surface and
the obvious next reduction: each use is a workaround for a permission the owner can now grant
directly, because a tablet's Settings app is reachable in a way a locked-down head unit's was not.

**`UsbAutoConfirmService`** (accessibility, `BIND_ACCESSIBILITY_SERVICE`). Off by default; the owner
enables it in Android's accessibility settings to have the recurring USB permission dialog confirmed
automatically. Same trade as above: a real convenience with a real privilege. Worth revisiting.

**`MapEmbedService`** (`exported="true"`, no permission). Reviewed and sound: it refuses every
request with `ERROR_DISABLED` until the owner turns on **launcher map sharing**, revokes live
attachments the moment that is turned off, resolves and logs the calling package, requires a
`SurfaceControlViewHost` host token from the caller, and caps attachments both overall and per
calling UID. It is still worth knowing that while the consent is on, any app on the device can bind
and receive the live CarPlay map.

**`SettingsBackupServer`** (plain HTTP on the device). Only runs while the backup page is open, and
is stopped on leaving it. The URL path carries a 16-byte `SecureRandom` token, the pairing code is a
six-digit `SecureRandom` value, and the token comparison is length-checked and constant-time. It
binds all interfaces because the point is to be reachable from a phone on the same network; keep the
page closed otherwise.

**Settings backup allowlist.** `SettingsBackupCodec` travels only the keys it names. Pairing keys
(`pairing.*`), lockdown records, the accessory identity, the authentication target and the hotspot
name and passphrase are excluded, and so is the launcher map-sharing consent. This change dropped
the dead `cluster_*` prefix and `hide_byd_call_popup` from that list and added `map_stream_enabled`;
the exclusions are unchanged.

**Permissions.** `WRITE_SETTINGS` (starting the device hotspot), `REQUEST_INSTALL_PACKAGES` (the
in-app updater), `SYSTEM_ALERT_WINDOW` (the floating map card and launcher bubble),
`RECEIVE_BOOT_COMPLETED` (optional start after boot), microphone and location (Siri and reporting
location to the iPhone). Each backs a feature that is off until the owner turns it on.

**Video playback.** iOS 27 video-in-car used to be gated on a gear reading from the car. A tablet has
no such signal, so it is gated on an explicit **Allow video playback** opt-in that defaults to off,
rather than being reported as always permitted.

## Not addressed here

- The local-ADB and accessibility surfaces above are kept deliberately; removing them is a separate
  change with its own device testing.
- The bundled accessory identity remains extractable from a build that carries one. Unchanged by
  this work, and stated in [compatibility](COMPATIBILITY.md).
- Release signing is supplied by `ANDROID_KEYSTORE_*` environment variables. A build without them
  produces an unsigned release artefact; it does not fall back to a checked-in key.
