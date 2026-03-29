# Device Specifications

> Generated: 2026-03-20 | Platform: CachyOS Linux

---

## System Overview

| Component | Details |
|---|---|
| **OS** | CachyOS Linux (Arch-based, rolling release) |
| **Kernel** | `6.19.7-1-cachyos` |
| **CPU** | AMD Ryzen 5 5600H with Radeon Graphics |
| **RAM** | 16 GB DDR4 |
| **GPU (iGPU)** | AMD Radeon Vega Series / Cezanne (rev c6) |

---

## Networking

### WiFi Adapter

| Field | Details |
|---|---|
| **Chip** | MediaTek MT7921 802.11ax (Filogic 330) |
| **Interface** | PCIe (`03:00.0`) |
| **PCI ID** | `14c3:7961` |
| **Kernel Driver** | `mt7921e` |
| **Driver Modules** | `mt7921e`, `mt7921_common`, `mt792x_lib`, `mt76_connac_lib`, `mt76`, `mac80211`, `cfg80211` |
| **WiFi Standards** | 802.11 a/b/g/n/ac/ax (WiFi 6) |
| **Bands** | 2.4 GHz, 5 GHz |
| **Max Theoretical TX Power** | 30.0 dBm |
| **Reported TX Power (bugged)** | 3.00 dBm |
| **Interface Name** | `wlan0` |
| **MAC Address** | `d8:80:83:4f:1a:65` |

### Bluetooth

| Field | Details |
|---|---|
| **Chip** | MediaTek Bluetooth Adapter (via Foxconn / Hon Hai) |
| **USB ID** | `0489:e0cd` |
| **Type** | USB BT combo (shared with WiFi chip) |

### Ethernet

| Field | Details |
|---|---|
| **Interface** | `enp2s0` |
| **MAC** | `e4:a8:df:c3:e8:20` |
| **State** | DOWN (no cable detected) |

---

## WiFi Hotspot / AP Mode Capabilities

- ✅ AP mode supported by hardware
- ✅ AP/VLAN mode supported
- ✅ HE (WiFi 6) in AP mode on both 2.4 GHz and 5 GHz
- ✅ VHT (WiFi 5 / 802.11ac) in AP mode
- ✅ HT40/HE40/HE80 on 5 GHz in AP mode
- ⚠️ **TX Power in hotspot mode reports 3.00 dBm (known bug — see below)**

---

## Known Issues

### 🐛 MT7921 Hotspot Zero Signal Strength (txpower = 3 dBm)

**Status:** Confirmed firmware/driver reporting bug in `mt76` / `mt7921e` driver stack.

**Root Cause:**
The `mt7921e` driver misreports the transmit power (`txpower`) as `3.00 dBm` when in managed/AP mode. The chip is physically capable of 30 dBm but the driver does not correctly pass TX power settings from the firmware to the kernel's `cfg80211` layer. This causes:
- Hotspot showing "no signal" or very weak signal on connecting devices
- `iw dev wlan0 info` displaying `txpower 3.00 dBm` instead of the correct value

**Upstream status:** A patch to fix per-band txpower reporting in `mt76` was submitted to the Linux kernel in early 2026 and is pending full integration.

**Workarounds (applied):** See `/etc/modprobe.d/mt7921e.conf` and `/etc/NetworkManager/conf.d/wifi-hotspot.conf`.

---

*Docs auto-generated from live system data. Update after kernel/firmware upgrades.*
