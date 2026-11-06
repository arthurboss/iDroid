# First run

A first launch with no chosen iPhone opens the setup guide. It has three steps and can be skipped
at any point; **Settings → Overview → Setup guide** reopens it later.

| Step | What it asks | Where the same control lives afterwards |
| --- | --- | --- |
| 1. Connection | How the iPhone connects: Wi-Fi Direct, this device's own hotspot, an existing Wi-Fi both devices join, or USB | **Settings → Connection → Connection setup** |
| 2. iPhone | Which paired iPhone to use, and the app permissions it needs | **Settings → Connection**, and the home screen's **Choose iPhone** |
| 3. Done | Connect now, open Settings, or finish | — |

## Why the guide keeps a connection step

The upstream project's guide opened on a question about the car: it read the head-unit generation
from the system build name, let the driver correct it, and then offered only the features that
applied to that generation. None of that has meaning on a tablet, so the question is gone.

The connection step stays first because it is the one choice that has no safe default. Wi-Fi Direct
needs no credentials but a single radio also serves the device's Wi-Fi client, so it can stutter
while that client scans. The device's own hotspot is steadier but needs a name and passphrase saved
first, and on some Android versions it needs the owner to allow "Modify system settings". USB needs
a data port. Picking for the owner would send a good share of first runs down a path their hardware
cannot take, with a failure that looks like a bug rather than a choice.

So the guide asks, and makes the question cheap to revisit: step 1 renders the very same controls as
**Settings → Connection → Connection setup**, and says so on the card. Nothing the guide sets is
one-way, and changing the connection later never requires going through the guide again.

## What the guide no longer does

- It detects no vehicle and asks nothing about the device it runs on.
- It offers no per-generation feature list, no `Tested on some cars` or `Experimental` badges and no
  ADB badge. The features those badges described (instrument-cluster output, dashboard calls,
  steering-wheel call keys) are not part of this fork.
- It applies no audio-routing defaults derived from a head-unit generation. Navigation audio routing
  starts on automatic and is changed in **Settings → Audio**.
